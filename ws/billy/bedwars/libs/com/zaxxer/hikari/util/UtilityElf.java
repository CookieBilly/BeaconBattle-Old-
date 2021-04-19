

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;

public final class UtilityElf
{
    public static String getNullIfEmpty(final String s) {
        return (s == null) ? null : (s.trim().isEmpty() ? null : s.trim());
    }
    
    public static void quietlySleep(final long n) {
        try {
            Thread.sleep(n);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static boolean safeIsAssignableFrom(final Object o, final String className) {
        try {
            return Class.forName(className).isAssignableFrom(o.getClass());
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static <T> T createInstance(final String name, final Class<T> clazz, final Object... initargs) {
        if (name == null) {
            return null;
        }
        try {
            final Class<?> loadClass = UtilityElf.class.getClassLoader().loadClass(name);
            if (initargs.length == 0) {
                return clazz.cast(loadClass.newInstance());
            }
            final Class[] parameterTypes = new Class[initargs.length];
            for (int i = 0; i < initargs.length; ++i) {
                parameterTypes[i] = initargs[i].getClass();
            }
            return clazz.cast(loadClass.getConstructor((Class<?>[])parameterTypes).newInstance(initargs));
        }
        catch (Exception cause) {
            throw new RuntimeException(cause);
        }
    }
    
    public static ThreadPoolExecutor createThreadPoolExecutor(final int capacity, final String s, ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        if (threadFactory == null) {
            threadFactory = new DefaultThreadFactory(s, true);
        }
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(capacity), threadFactory, handler);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
    
    public static ThreadPoolExecutor createThreadPoolExecutor(final BlockingQueue<Runnable> workQueue, final String s, ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        if (threadFactory == null) {
            threadFactory = new DefaultThreadFactory(s, true);
        }
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, workQueue, threadFactory, handler);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        return threadPoolExecutor;
    }
    
    public static int getTransactionIsolation(final String str) {
        if (str != null) {
            try {
                return IsolationLevel.valueOf(str.toUpperCase(Locale.ENGLISH)).getLevelId();
            }
            catch (IllegalArgumentException ex) {
                try {
                    final int int1 = Integer.parseInt(str);
                    for (final IsolationLevel isolationLevel : IsolationLevel.values()) {
                        if (isolationLevel.getLevelId() == int1) {
                            return isolationLevel.getLevelId();
                        }
                    }
                    throw new IllegalArgumentException("Invalid transaction isolation value: " + str);
                }
                catch (NumberFormatException cause) {
                    throw new IllegalArgumentException("Invalid transaction isolation value: " + str, cause);
                }
            }
        }
        return -1;
    }
    
    public static final class DefaultThreadFactory implements ThreadFactory
    {
        private final String threadName;
        private final boolean daemon;
        
        public DefaultThreadFactory(final String threadName, final boolean daemon) {
            this.threadName = threadName;
            this.daemon = daemon;
        }
        
        @Override
        public Thread newThread(final Runnable target) {
            final Thread thread = new Thread(target, this.threadName);
            thread.setDaemon(this.daemon);
            return thread;
        }
    }
}
