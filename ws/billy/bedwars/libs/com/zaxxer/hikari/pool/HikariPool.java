

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import java.util.List;
import java.sql.SQLTransientConnectionException;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.PoolStats;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.Callable;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.dropwizard.CodahaleHealthChecker;
import com.codahale.metrics.health.HealthCheckRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import com.codahale.metrics.MetricRegistry;
import java.util.concurrent.ExecutorService;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.concurrent.ThreadFactory;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ClockSource;

import java.util.concurrent.BlockingQueue;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.UtilityElf;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariConfig;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.SuspendResumeLock;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Collection;
import ws.billy.bedwars.libs.org.slf4j.Logger;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ConcurrentBag;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariPoolMXBean;

public final class HikariPool extends PoolBase implements HikariPoolMXBean, ConcurrentBag.IBagStateListener
{
    private final Logger logger;
    public static final int POOL_NORMAL = 0;
    public static final int POOL_SUSPENDED = 1;
    public static final int POOL_SHUTDOWN = 2;
    public volatile int poolState;
    private final long aliveBypassWindowMs;
    private final long housekeepingPeriodMs;
    private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
    private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
    private final PoolEntryCreator poolEntryCreator;
    private final PoolEntryCreator postFillPoolEntryCreator;
    private final Collection<Runnable> addConnectionQueueReadOnlyView;
    private final ThreadPoolExecutor addConnectionExecutor;
    private final ThreadPoolExecutor closeConnectionExecutor;
    private final ConcurrentBag<PoolEntry> connectionBag;
    private final ProxyLeakTaskFactory leakTaskFactory;
    private final SuspendResumeLock suspendResumeLock;
    private final ScheduledExecutorService houseKeepingExecutorService;
    private ScheduledFuture<?> houseKeeperTask;
    
    public HikariPool(final HikariConfig hikariConfig) {
        super(hikariConfig);
        this.logger = LoggerFactory.getLogger(HikariPool.class);
        this.aliveBypassWindowMs = Long.getLong("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L));
        this.housekeepingPeriodMs = Long.getLong("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L));
        this.poolEntryCreator = new PoolEntryCreator(null);
        this.postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
        this.connectionBag = new ConcurrentBag<PoolEntry>(this);
        this.suspendResumeLock = (hikariConfig.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK);
        this.houseKeepingExecutorService = this.initializeHouseKeepingExecutorService();
        this.checkFailFast();
        if (hikariConfig.getMetricsTrackerFactory() != null) {
            this.setMetricsTrackerFactory(hikariConfig.getMetricsTrackerFactory());
        }
        else {
            this.setMetricRegistry(hikariConfig.getMetricRegistry());
        }
        this.setHealthCheckRegistry(hikariConfig.getHealthCheckRegistry());
        this.handleMBeans(this, true);
        final ThreadFactory threadFactory = hikariConfig.getThreadFactory();
        final int maximumPoolSize = hikariConfig.getMaximumPoolSize();
        final LinkedBlockingQueue c = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
        this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection((Collection<? extends Runnable>)c);
        this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor((BlockingQueue<Runnable>)c, this.poolName + " connection adder", threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(maximumPoolSize, this.poolName + " connection closer", threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.leakTaskFactory = new ProxyLeakTaskFactory(hikariConfig.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
        this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
        if (Boolean.getBoolean("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.blockUntilFilled") && hikariConfig.getInitializationFailTimeout() > 1L) {
            this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
            this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
            while (ClockSource.elapsedMillis(ClockSource.currentTime()) < hikariConfig.getInitializationFailTimeout() && this.getTotalConnections() < hikariConfig.getMinimumIdle()) {
                UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L));
            }
            this.addConnectionExecutor.setCorePoolSize(1);
            this.addConnectionExecutor.setMaximumPoolSize(1);
        }
    }
    
    public Connection getConnection() {
        return this.getConnection(this.connectionTimeout);
    }
    
    public Connection getConnection(final long n) {
        this.suspendResumeLock.acquire();
        final long currentTime = ClockSource.currentTime();
        try {
            long n2 = n;
            do {
                final PoolEntry poolEntry = this.connectionBag.borrow(n2, TimeUnit.MILLISECONDS);
                if (poolEntry == null) {
                    break;
                }
                final long currentTime2 = ClockSource.currentTime();
                if (!poolEntry.isMarkedEvicted() && (ClockSource.elapsedMillis(poolEntry.lastAccessed, currentTime2) <= this.aliveBypassWindowMs || this.isConnectionAlive(poolEntry.connection))) {
                    this.metricsTracker.recordBorrowStats(poolEntry, currentTime);
                    return poolEntry.createProxyConnection(this.leakTaskFactory.schedule(poolEntry), currentTime2);
                }
                this.closeConnection(poolEntry, poolEntry.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
                n2 = n - ClockSource.elapsedMillis(currentTime);
            } while (n2 > 0L);
            this.metricsTracker.recordBorrowTimeoutStats(currentTime);
            throw this.createTimeoutException(currentTime);
        }
        catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new SQLException(this.poolName + " - Interrupted during connection acquisition", cause);
        }
        finally {
            this.suspendResumeLock.release();
        }
    }
    
    public synchronized void shutdown() {
        try {
            this.poolState = 2;
            if (this.addConnectionExecutor == null) {
                return;
            }
            this.logPoolState("Before shutdown ");
            if (this.houseKeeperTask != null) {
                this.houseKeeperTask.cancel(false);
                this.houseKeeperTask = null;
            }
            this.softEvictConnections();
            this.addConnectionExecutor.shutdown();
            this.addConnectionExecutor.awaitTermination(this.getLoginTimeout(), TimeUnit.SECONDS);
            this.destroyHouseKeepingExecutorService();
            this.connectionBag.close();
            final ThreadPoolExecutor threadPoolExecutor = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config.getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
            try {
                final long currentTime = ClockSource.currentTime();
                do {
                    this.abortActiveConnections(threadPoolExecutor);
                    this.softEvictConnections();
                } while (this.getTotalConnections() > 0 && ClockSource.elapsedMillis(currentTime) < TimeUnit.SECONDS.toMillis(10L));
            }
            finally {
                threadPoolExecutor.shutdown();
                threadPoolExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            }
            this.shutdownNetworkTimeoutExecutor();
            this.closeConnectionExecutor.shutdown();
            this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
        }
        finally {
            this.logPoolState("After shutdown ");
            this.handleMBeans(this, false);
            this.metricsTracker.close();
        }
    }
    
    public void evictConnection(final Connection connection) {
        final ProxyConnection proxyConnection = (ProxyConnection)connection;
        proxyConnection.cancelLeakTask();
        try {
            this.softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
        }
        catch (SQLException ex) {}
    }
    
    public void setMetricRegistry(final Object o) {
        if (o != null && UtilityElf.safeIsAssignableFrom(o, "com.codahale.metrics.MetricRegistry")) {
            this.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory((MetricRegistry)o));
        }
        else if (o != null && UtilityElf.safeIsAssignableFrom(o, "io.micrometer.core.instrument.MeterRegistry")) {
            this.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory((MeterRegistry)o));
        }
        else {
            this.setMetricsTrackerFactory(null);
        }
    }
    
    public void setMetricsTrackerFactory(final MetricsTrackerFactory metricsTrackerFactory) {
        if (metricsTrackerFactory != null) {
            this.metricsTracker = new MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), this.getPoolStats()));
        }
        else {
            this.metricsTracker = new NopMetricsTrackerDelegate();
        }
    }
    
    public void setHealthCheckRegistry(final Object o) {
        if (o != null) {
            CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)o);
        }
    }
    
    @Override
    public void addBagItem(final int i) {
        if (i - this.addConnectionQueueReadOnlyView.size() >= 0) {
            this.addConnectionExecutor.submit((Callable<Object>)this.poolEntryCreator);
        }
        else {
            this.logger.debug("{} - Add connection elided, waiting {}, queue {}", this.poolName, i, this.addConnectionQueueReadOnlyView.size());
        }
    }
    
    @Override
    public int getActiveConnections() {
        return this.connectionBag.getCount(1);
    }
    
    @Override
    public int getIdleConnections() {
        return this.connectionBag.getCount(0);
    }
    
    @Override
    public int getTotalConnections() {
        return this.connectionBag.size();
    }
    
    @Override
    public int getThreadsAwaitingConnection() {
        return this.connectionBag.getWaitingThreadCount();
    }
    
    @Override
    public void softEvictConnections() {
        this.connectionBag.values().forEach(poolEntry -> this.softEvictConnection(poolEntry, "(connection evicted)", false));
    }
    
    @Override
    public synchronized void suspendPool() {
        if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
            throw new IllegalStateException(this.poolName + " - is not suspendable");
        }
        if (this.poolState != 1) {
            this.suspendResumeLock.suspend();
            this.poolState = 1;
        }
    }
    
    @Override
    public synchronized void resumePool() {
        if (this.poolState == 1) {
            this.poolState = 0;
            this.fillPool();
            this.suspendResumeLock.resume();
        }
    }
    
    void logPoolState(final String... array) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", this.poolName, (array.length > 0) ? array[0] : "", this.getTotalConnections(), this.getActiveConnections(), this.getIdleConnections(), this.getThreadsAwaitingConnection());
        }
    }
    
    @Override
    void recycle(final PoolEntry poolEntry) {
        this.metricsTracker.recordConnectionUsage(poolEntry);
        this.connectionBag.requite(poolEntry);
    }
    
    void closeConnection(final PoolEntry poolEntry, final String s) {
        if (this.connectionBag.remove(poolEntry)) {
            this.closeConnectionExecutor.execute(() -> {
                this.quietlyCloseConnection(poolEntry.close(), s);
                if (this.poolState == 0) {
                    this.fillPool();
                }
            });
        }
    }
    
    int[] getPoolStateCounts() {
        return this.connectionBag.getStateCounts();
    }
    
    private PoolEntry createPoolEntry() {
        try {
            final PoolEntry poolEntry = this.newPoolEntry();
            final long maxLifetime = this.config.getMaxLifetime();
            if (maxLifetime > 0L) {
                poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(() -> {
                    if (this.softEvictConnection(poolEntry, "(connection has passed maxLifetime)", (boolean)(0 != 0))) {
                        this.addBagItem(this.connectionBag.getWaitingThreadCount());
                    }
                    return;
                }, maxLifetime - ((maxLifetime > 10000L) ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L), TimeUnit.MILLISECONDS));
            }
            return poolEntry;
        }
        catch (ConnectionSetupException newValue) {
            if (this.poolState == 0) {
                this.logger.error("{} - Error thrown while acquiring connection from data source", this.poolName, newValue.getCause());
                this.lastConnectionFailure.set(newValue);
            }
        }
        catch (Exception ex) {
            if (this.poolState == 0) {
                this.logger.debug("{} - Cannot acquire connection from data source", this.poolName, ex);
            }
        }
        return null;
    }
    
    private synchronized void fillPool() {
        final int n = Math.min(this.config.getMaximumPoolSize() - this.getTotalConnections(), this.config.getMinimumIdle() - this.getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
        if (n <= 0) {
            this.logger.debug("{} - Fill pool skipped, pool is at sufficient level.", this.poolName);
        }
        for (int i = 0; i < n; ++i) {
            this.addConnectionExecutor.submit((Callable<Object>)((i < n - 1) ? this.poolEntryCreator : this.postFillPoolEntryCreator));
        }
    }
    
    private void abortActiveConnections(final ExecutorService executorService) {
        for (final PoolEntry poolEntry : this.connectionBag.values(1)) {
            final Connection close = poolEntry.close();
            try {
                close.abort(executorService);
            }
            catch (Throwable t) {
                this.quietlyCloseConnection(close, "(connection aborted during shutdown)");
            }
            finally {
                this.connectionBag.remove(poolEntry);
            }
        }
    }
    
    private void checkFailFast() {
        final long initializationFailTimeout = this.config.getInitializationFailTimeout();
        if (initializationFailTimeout < 0L) {
            return;
        }
        do {
            final PoolEntry poolEntry = this.createPoolEntry();
            if (poolEntry != null) {
                if (this.config.getMinimumIdle() > 0) {
                    this.connectionBag.add(poolEntry);
                    this.logger.debug("{} - Added connection {}", this.poolName, poolEntry.connection);
                }
                else {
                    this.quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
                }
                return;
            }
            if (this.getLastConnectionFailure() instanceof ConnectionSetupException) {
                this.throwPoolInitializationException(this.getLastConnectionFailure().getCause());
            }
            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
        } while (ClockSource.elapsedMillis(ClockSource.currentTime()) < initializationFailTimeout);
        if (initializationFailTimeout > 0L) {
            this.throwPoolInitializationException(this.getLastConnectionFailure());
        }
    }
    
    private void throwPoolInitializationException(final Throwable t) {
        this.logger.error("{} - Exception during pool initialization.", this.poolName, t);
        this.destroyHouseKeepingExecutorService();
        throw new PoolInitializationException(t);
    }
    
    private boolean softEvictConnection(final PoolEntry poolEntry, final String s, final boolean b) {
        poolEntry.markEvicted();
        if (b || this.connectionBag.reserve(poolEntry)) {
            this.closeConnection(poolEntry, s);
            return true;
        }
        return false;
    }
    
    private ScheduledExecutorService initializeHouseKeepingExecutorService() {
        if (this.config.getScheduledExecutor() == null) {
            final Object o;
            final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, Optional.ofNullable(this.config.getThreadFactory()).orElseGet(() -> {
                new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", (boolean)(1 != 0));
                return o;
            }), new ThreadPoolExecutor.DiscardPolicy());
            scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
            return scheduledThreadPoolExecutor;
        }
        return this.config.getScheduledExecutor();
    }
    
    private void destroyHouseKeepingExecutorService() {
        if (this.config.getScheduledExecutor() == null) {
            this.houseKeepingExecutorService.shutdownNow();
        }
    }
    
    private PoolStats getPoolStats() {
        return new PoolStats(TimeUnit.SECONDS.toMillis(1L)) {
            @Override
            protected void update() {
                this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
                this.idleConnections = HikariPool.this.getIdleConnections();
                this.totalConnections = HikariPool.this.getTotalConnections();
                this.activeConnections = HikariPool.this.getActiveConnections();
                this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
                this.minConnections = HikariPool.this.config.getMinimumIdle();
            }
        };
    }
    
    private SQLException createTimeoutException(final long startTime) {
        this.logPoolState("Timeout failure ");
        this.metricsTracker.recordConnectionTimeout();
        String sqlState = null;
        final Exception lastConnectionFailure = this.getLastConnectionFailure();
        if (lastConnectionFailure instanceof SQLException) {
            sqlState = ((SQLException)lastConnectionFailure).getSQLState();
        }
        final SQLTransientConnectionException ex = new SQLTransientConnectionException(this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(startTime) + "ms.", sqlState, lastConnectionFailure);
        if (lastConnectionFailure instanceof SQLException) {
            ex.setNextException((SQLException)lastConnectionFailure);
        }
        return ex;
    }
    
    private final class PoolEntryCreator implements Callable<Boolean>
    {
        private final String loggingPrefix;
        
        PoolEntryCreator(final String loggingPrefix) {
            this.loggingPrefix = loggingPrefix;
        }
        
        @Override
        public Boolean call() {
            long min = 250L;
            while (HikariPool.this.poolState == 0 && this.shouldCreateAnotherConnection()) {
                final PoolEntry access$100 = HikariPool.this.createPoolEntry();
                if (access$100 != null) {
                    HikariPool.this.connectionBag.add(access$100);
                    HikariPool.this.logger.debug("{} - Added connection {}", HikariPool.this.poolName, access$100.connection);
                    if (this.loggingPrefix != null) {
                        HikariPool.this.logPoolState(this.loggingPrefix);
                    }
                    return Boolean.TRUE;
                }
                if (this.loggingPrefix != null) {
                    HikariPool.this.logger.debug("{} - Connection add failed, sleeping with backoff: {}ms", HikariPool.this.poolName, min);
                }
                UtilityElf.quietlySleep(min);
                min = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)(min * 1.5)));
            }
            return Boolean.FALSE;
        }
        
        private synchronized boolean shouldCreateAnotherConnection() {
            return HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this.connectionBag.getWaitingThreadCount() > 0 || HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle());
        }
    }
    
    private final class HouseKeeper implements Runnable
    {
        private volatile long previous;
        
        private HouseKeeper() {
            this.previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
        }
        
        @Override
        public void run() {
            try {
                HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
                HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
                HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
                HikariPool.this.catalog = ((HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog)) ? HikariPool.this.config.getCatalog() : HikariPool.this.catalog);
                final long idleTimeout = HikariPool.this.config.getIdleTimeout();
                final long currentTime = ClockSource.currentTime();
                if (ClockSource.plusMillis(currentTime, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
                    HikariPool.this.logger.warn("{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", HikariPool.this.poolName, ClockSource.elapsedDisplayString(this.previous, currentTime));
                    this.previous = currentTime;
                    HikariPool.this.softEvictConnections();
                    return;
                }
                if (currentTime > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L)) {
                    HikariPool.this.logger.warn("{} - Thread starvation or clock leap detected (housekeeper delta={}).", HikariPool.this.poolName, ClockSource.elapsedDisplayString(this.previous, currentTime));
                }
                this.previous = currentTime;
                String s = "Pool ";
                if (idleTimeout > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
                    HikariPool.this.logPoolState("Before cleanup ");
                    s = "After cleanup  ";
                    final List<PoolEntry> values = (List<PoolEntry>)HikariPool.this.connectionBag.values(0);
                    int n = values.size() - HikariPool.this.config.getMinimumIdle();
                    for (final PoolEntry poolEntry : values) {
                        if (n > 0 && ClockSource.elapsedMillis(poolEntry.lastAccessed, currentTime) > idleTimeout && HikariPool.this.connectionBag.reserve(poolEntry)) {
                            HikariPool.this.closeConnection(poolEntry, "(connection has passed idleTimeout)");
                            --n;
                        }
                    }
                }
                HikariPool.this.logPoolState(s);
                HikariPool.this.fillPool();
            }
            catch (Exception ex) {
                HikariPool.this.logger.error("Unexpected exception in housekeeping task", ex);
            }
        }
    }
    
    public static class PoolInitializationException extends RuntimeException
    {
        private static final long serialVersionUID = 929872118275916520L;
        
        public PoolInitializationException(final Throwable cause) {
            super("Failed to initialize pool: " + cause.getMessage(), cause);
        }
    }
}
