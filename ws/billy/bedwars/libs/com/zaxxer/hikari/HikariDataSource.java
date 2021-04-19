

package ws.billy.bedwars.libs.com.zaxxer.hikari;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import java.io.PrintWriter;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLException;
import java.sql.Connection;
import ws.billy.bedwars.libs.com.zaxxer.hikari.pool.HikariPool;
import java.util.concurrent.atomic.AtomicBoolean;
import ws.billy.bedwars.libs.org.slf4j.Logger;

import java.io.Closeable;
import javax.sql.DataSource;

public class HikariDataSource extends HikariConfig implements DataSource, Closeable
{
    private static final Logger LOGGER;
    private final AtomicBoolean isShutdown;
    private final HikariPool fastPathPool;
    private volatile HikariPool pool;
    
    public HikariDataSource() {
        this.isShutdown = new AtomicBoolean();
        this.fastPathPool = null;
    }
    
    public HikariDataSource(final HikariConfig hikariConfig) {
        this.isShutdown = new AtomicBoolean();
        hikariConfig.validate();
        hikariConfig.copyStateTo(this);
        HikariDataSource.LOGGER.info("{} - Starting...", hikariConfig.getPoolName());
        final HikariPool hikariPool = new HikariPool(this);
        this.fastPathPool = hikariPool;
        this.pool = hikariPool;
        HikariDataSource.LOGGER.info("{} - Start completed.", hikariConfig.getPoolName());
        this.seal();
    }
    
    @Override
    public Connection getConnection() {
        if (this.isClosed()) {
            throw new SQLException("HikariDataSource " + this + " has been closed.");
        }
        if (this.fastPathPool != null) {
            return this.fastPathPool.getConnection();
        }
        HikariPool hikariPool = this.pool;
        if (hikariPool == null) {
            synchronized (this) {
                hikariPool = this.pool;
                if (hikariPool == null) {
                    this.validate();
                    HikariDataSource.LOGGER.info("{} - Starting...", this.getPoolName());
                    try {
                        hikariPool = (this.pool = new HikariPool(this));
                        this.seal();
                    }
                    catch (HikariPool.PoolInitializationException ex) {
                        if (ex.getCause() instanceof SQLException) {
                            throw (SQLException)ex.getCause();
                        }
                        throw ex;
                    }
                    HikariDataSource.LOGGER.info("{} - Start completed.", this.getPoolName());
                }
            }
        }
        return hikariPool.getConnection();
    }
    
    @Override
    public Connection getConnection(final String s, final String s2) {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public PrintWriter getLogWriter() {
        final HikariPool pool = this.pool;
        return (pool != null) ? pool.getUnwrappedDataSource().getLogWriter() : null;
    }
    
    @Override
    public void setLogWriter(final PrintWriter logWriter) {
        final HikariPool pool = this.pool;
        if (pool != null) {
            pool.getUnwrappedDataSource().setLogWriter(logWriter);
        }
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) {
        final HikariPool pool = this.pool;
        if (pool != null) {
            pool.getUnwrappedDataSource().setLoginTimeout(loginTimeout);
        }
    }
    
    @Override
    public int getLoginTimeout() {
        final HikariPool pool = this.pool;
        return (pool != null) ? pool.getUnwrappedDataSource().getLoginTimeout() : 0;
    }
    
    @Override
    public java.util.logging.Logger getParentLogger() {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public <T> T unwrap(final Class<T> obj) {
        if (obj.isInstance(this)) {
            return (T)this;
        }
        final HikariPool pool = this.pool;
        if (pool != null) {
            final DataSource unwrappedDataSource = pool.getUnwrappedDataSource();
            if (obj.isInstance(unwrappedDataSource)) {
                return (T)unwrappedDataSource;
            }
            if (unwrappedDataSource != null) {
                return unwrappedDataSource.unwrap(obj);
            }
        }
        throw new SQLException("Wrapped DataSource is not an instance of " + obj);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) {
        if (clazz.isInstance(this)) {
            return true;
        }
        final HikariPool pool = this.pool;
        if (pool != null) {
            final DataSource unwrappedDataSource = pool.getUnwrappedDataSource();
            if (clazz.isInstance(unwrappedDataSource)) {
                return true;
            }
            if (unwrappedDataSource != null) {
                return unwrappedDataSource.isWrapperFor(clazz);
            }
        }
        return false;
    }
    
    @Override
    public void setMetricRegistry(final Object metricRegistry) {
        final boolean b = this.getMetricRegistry() != null;
        super.setMetricRegistry(metricRegistry);
        final HikariPool pool = this.pool;
        if (pool != null) {
            if (b) {
                throw new IllegalStateException("MetricRegistry can only be set one time");
            }
            pool.setMetricRegistry(super.getMetricRegistry());
        }
    }
    
    @Override
    public void setMetricsTrackerFactory(final MetricsTrackerFactory metricsTrackerFactory) {
        final boolean b = this.getMetricsTrackerFactory() != null;
        super.setMetricsTrackerFactory(metricsTrackerFactory);
        final HikariPool pool = this.pool;
        if (pool != null) {
            if (b) {
                throw new IllegalStateException("MetricsTrackerFactory can only be set one time");
            }
            pool.setMetricsTrackerFactory(super.getMetricsTrackerFactory());
        }
    }
    
    @Override
    public void setHealthCheckRegistry(final Object healthCheckRegistry) {
        final boolean b = this.getHealthCheckRegistry() != null;
        super.setHealthCheckRegistry(healthCheckRegistry);
        final HikariPool pool = this.pool;
        if (pool != null) {
            if (b) {
                throw new IllegalStateException("HealthCheckRegistry can only be set one time");
            }
            pool.setHealthCheckRegistry(super.getHealthCheckRegistry());
        }
    }
    
    public boolean isRunning() {
        return this.pool != null && this.pool.poolState == 0;
    }
    
    public HikariPoolMXBean getHikariPoolMXBean() {
        return this.pool;
    }
    
    public HikariConfigMXBean getHikariConfigMXBean() {
        return this;
    }
    
    public void evictConnection(final Connection connection) {
        final HikariPool pool;
        if (!this.isClosed() && (pool = this.pool) != null && connection.getClass().getName().startsWith("ws.billy.BeaconBattle.libs.com.zaxxer.hikari")) {
            pool.evictConnection(connection);
        }
    }
    
    @Override
    public void close() {
        if (this.isShutdown.getAndSet(true)) {
            return;
        }
        final HikariPool pool = this.pool;
        if (pool != null) {
            try {
                HikariDataSource.LOGGER.info("{} - Shutdown initiated...", this.getPoolName());
                pool.shutdown();
                HikariDataSource.LOGGER.info("{} - Shutdown completed.", this.getPoolName());
            }
            catch (InterruptedException ex) {
                HikariDataSource.LOGGER.warn("{} - Interrupted during closing", this.getPoolName(), ex);
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public boolean isClosed() {
        return this.isShutdown.get();
    }
    
    @Override
    public String toString() {
        return "HikariDataSource (" + this.pool + ")";
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(HikariDataSource.class);
    }
}
