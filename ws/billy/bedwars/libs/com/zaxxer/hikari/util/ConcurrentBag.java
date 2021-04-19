

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.concurrent.locks.LockSupport;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws.billy.bedwars.libs.org.slf4j.Logger;

public class ConcurrentBag<T extends IConcurrentBagEntry> implements AutoCloseable
{
    private static final Logger LOGGER;
    private final CopyOnWriteArrayList<T> sharedList;
    private final boolean weakThreadLocals;
    private final ThreadLocal<List<Object>> threadList;
    private final IBagStateListener listener;
    private final AtomicInteger waiters;
    private volatile boolean closed;
    private final SynchronousQueue<T> handoffQueue;
    
    public ConcurrentBag(final IBagStateListener listener) {
        this.listener = listener;
        this.weakThreadLocals = this.useWeakThreadLocals();
        this.handoffQueue = new SynchronousQueue<T>(true);
        this.waiters = new AtomicInteger();
        this.sharedList = new CopyOnWriteArrayList<T>();
        if (this.weakThreadLocals) {
            this.threadList = ThreadLocal.withInitial(() -> new ArrayList(16));
        }
        else {
            this.threadList = ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
        }
    }
    
    public T borrow(long nanos, final TimeUnit timeUnit) {
        final List<Object> list = this.threadList.get();
        for (int i = list.size() - 1; i >= 0; --i) {
            final IConcurrentBagEntry remove = list.remove(i);
            final IConcurrentBagEntry concurrentBagEntry = this.weakThreadLocals ? ((WeakReference<IConcurrentBagEntry>)remove).get() : ((T)remove);
            if (concurrentBagEntry != null && concurrentBagEntry.compareAndSet(0, 1)) {
                return (T)concurrentBagEntry;
            }
        }
        final int incrementAndGet = this.waiters.incrementAndGet();
        try {
            for (final IConcurrentBagEntry concurrentBagEntry2 : this.sharedList) {
                if (concurrentBagEntry2.compareAndSet(0, 1)) {
                    if (incrementAndGet > 1) {
                        this.listener.addBagItem(incrementAndGet - 1);
                    }
                    return (T)concurrentBagEntry2;
                }
            }
            this.listener.addBagItem(incrementAndGet);
            nanos = timeUnit.toNanos(nanos);
            do {
                final long currentTime = ClockSource.currentTime();
                final IConcurrentBagEntry concurrentBagEntry3 = this.handoffQueue.poll(nanos, TimeUnit.NANOSECONDS);
                if (concurrentBagEntry3 == null || concurrentBagEntry3.compareAndSet(0, 1)) {
                    return (T)concurrentBagEntry3;
                }
                nanos -= ClockSource.elapsedNanos(currentTime);
            } while (nanos > 10000L);
            return null;
        }
        finally {
            this.waiters.decrementAndGet();
        }
    }
    
    public void requite(final T t) {
        t.setState(0);
        int n = 0;
        while (this.waiters.get() > 0) {
            if (t.getState() != 0 || this.handoffQueue.offer(t)) {
                return;
            }
            if ((n & 0xFF) == 0xFF) {
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
            }
            else {
                Thread.yield();
            }
            ++n;
        }
        final List<Object> list = this.threadList.get();
        if (list.size() < 50) {
            list.add(this.weakThreadLocals ? new WeakReference((T)t) : t);
        }
    }
    
    public void add(final T t) {
        if (this.closed) {
            ConcurrentBag.LOGGER.info("ConcurrentBag has been closed, ignoring add()");
            throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
        }
        this.sharedList.add(t);
        while (this.waiters.get() > 0 && t.getState() == 0 && !this.handoffQueue.offer(t)) {
            Thread.yield();
        }
    }
    
    public boolean remove(final T o) {
        if (!o.compareAndSet(1, -1) && !o.compareAndSet(-2, -1) && !this.closed) {
            ConcurrentBag.LOGGER.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", o);
            return false;
        }
        final boolean remove = this.sharedList.remove(o);
        if (!remove && !this.closed) {
            ConcurrentBag.LOGGER.warn("Attempt to remove an object from the bag that does not exist: {}", o);
        }
        this.threadList.get().remove(o);
        return remove;
    }
    
    @Override
    public void close() {
        this.closed = true;
    }
    
    public List<T> values(final int n) {
        final List<? super Object> list = this.sharedList.stream().filter(concurrentBagEntry -> concurrentBagEntry.getState() == n).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        Collections.reverse(list);
        return (List<T>)list;
    }
    
    public List<T> values() {
        return (List<T>)this.sharedList.clone();
    }
    
    public boolean reserve(final T t) {
        return t.compareAndSet(0, -2);
    }
    
    public void unreserve(final T e) {
        if (e.compareAndSet(-2, 0)) {
            while (this.waiters.get() > 0 && !this.handoffQueue.offer(e)) {
                Thread.yield();
            }
        }
        else {
            ConcurrentBag.LOGGER.warn("Attempt to relinquish an object to the bag that was not reserved: {}", e);
        }
    }
    
    public int getWaitingThreadCount() {
        return this.waiters.get();
    }
    
    public int getCount(final int n) {
        int n2 = 0;
        final Iterator<T> iterator = this.sharedList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getState() == n) {
                ++n2;
            }
        }
        return n2;
    }
    
    public int[] getStateCounts() {
        final int[] array = new int[6];
        for (final IConcurrentBagEntry concurrentBagEntry : this.sharedList) {
            final int[] array2 = array;
            final int state = concurrentBagEntry.getState();
            ++array2[state];
        }
        array[4] = this.sharedList.size();
        array[5] = this.waiters.get();
        return array;
    }
    
    public int size() {
        return this.sharedList.size();
    }
    
    public void dumpState() {
        this.sharedList.forEach(concurrentBagEntry -> ConcurrentBag.LOGGER.info(concurrentBagEntry.toString()));
    }
    
    private boolean useWeakThreadLocals() {
        try {
            if (System.getProperty("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.useWeakReferences") != null) {
                return Boolean.getBoolean("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.useWeakReferences");
            }
            return this.getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException ex) {
            return true;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
    }
    
    public interface IBagStateListener
    {
        void addBagItem(final int p0);
    }
    
    public interface IConcurrentBagEntry
    {
        public static final int STATE_NOT_IN_USE = 0;
        public static final int STATE_IN_USE = 1;
        public static final int STATE_REMOVED = -1;
        public static final int STATE_RESERVED = -2;
        
        boolean compareAndSet(final int p0, final int p1);
        
        void setState(final int p0);
        
        int getState();
    }
}
