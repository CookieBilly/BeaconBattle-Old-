

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ClockSource;
import java.sql.Statement;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.FastList;
import java.util.concurrent.ScheduledFuture;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import ws.billy.bedwars.libs.org.slf4j.Logger;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ConcurrentBag;

final class PoolEntry implements ConcurrentBag.IConcurrentBagEntry
{
    private static final Logger LOGGER;
    private static final AtomicIntegerFieldUpdater<PoolEntry> stateUpdater;
    Connection connection;
    long lastAccessed;
    long lastBorrowed;
    private volatile int state;
    private volatile boolean evict;
    private volatile ScheduledFuture<?> endOfLife;
    private final FastList<Statement> openStatements;
    private final HikariPool hikariPool;
    private final boolean isReadOnly;
    private final boolean isAutoCommit;
    
    PoolEntry(final Connection connection, final PoolBase poolBase, final boolean isReadOnly, final boolean isAutoCommit) {
        this.state = 0;
        this.connection = connection;
        this.hikariPool = (HikariPool)poolBase;
        this.isReadOnly = isReadOnly;
        this.isAutoCommit = isAutoCommit;
        this.lastAccessed = ClockSource.currentTime();
        this.openStatements = new FastList<Statement>(Statement.class, 16);
    }
    
    void recycle(final long lastAccessed) {
        if (this.connection != null) {
            this.lastAccessed = lastAccessed;
            this.hikariPool.recycle(this);
        }
    }
    
    void setFutureEol(final ScheduledFuture<?> endOfLife) {
        this.endOfLife = endOfLife;
    }
    
    Connection createProxyConnection(final ProxyLeakTask proxyLeakTask, final long n) {
        return ProxyFactory.getProxyConnection(this, this.connection, this.openStatements, proxyLeakTask, n, this.isReadOnly, this.isAutoCommit);
    }
    
    void resetConnectionState(final ProxyConnection proxyConnection, final int n) {
        this.hikariPool.resetConnectionState(this.connection, proxyConnection, n);
    }
    
    String getPoolName() {
        return this.hikariPool.toString();
    }
    
    boolean isMarkedEvicted() {
        return this.evict;
    }
    
    void markEvicted() {
        this.evict = true;
    }
    
    void evict(final String s) {
        this.hikariPool.closeConnection(this, s);
    }
    
    long getMillisSinceBorrowed() {
        return ClockSource.elapsedMillis(this.lastBorrowed);
    }
    
    PoolBase getPoolBase() {
        return this.hikariPool;
    }
    
    @Override
    public String toString() {
        return this.connection + ", accessed " + ClockSource.elapsedDisplayString(this.lastAccessed, ClockSource.currentTime()) + " ago, " + this.stateToString();
    }
    
    @Override
    public int getState() {
        return PoolEntry.stateUpdater.get(this);
    }
    
    @Override
    public boolean compareAndSet(final int n, final int n2) {
        return PoolEntry.stateUpdater.compareAndSet(this, n, n2);
    }
    
    @Override
    public void setState(final int n) {
        PoolEntry.stateUpdater.set(this, n);
    }
    
    Connection close() {
        final ScheduledFuture<?> endOfLife = this.endOfLife;
        if (endOfLife != null && !endOfLife.isDone() && !endOfLife.cancel(false)) {
            PoolEntry.LOGGER.warn("{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}", this.getPoolName(), this.connection);
        }
        final Connection connection = this.connection;
        this.connection = null;
        this.endOfLife = null;
        return connection;
    }
    
    private String stateToString() {
        switch (this.state) {
            case 1: {
                return "IN_USE";
            }
            case 0: {
                return "NOT_IN_USE";
            }
            case -1: {
                return "REMOVED";
            }
            case -2: {
                return "RESERVED";
            }
            default: {
                return "Invalid";
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(PoolEntry.class);
        stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolEntry.class, "state");
    }
}
