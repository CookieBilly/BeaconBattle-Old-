

package ws.billy.bedwars.libs.com.zaxxer.hikari.pool;

import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.IMetricsTracker;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.sql.SQLTransientConnectionException;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.ClockSource;
import java.util.Properties;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.DriverDataSource;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.PropertyElf;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.UtilityElf;
import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.util.concurrent.Executor;
import ws.billy.bedwars.libs.com.zaxxer.hikari.SQLExceptionOverride;
import java.util.concurrent.atomic.AtomicReference;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariConfig;
import ws.billy.bedwars.libs.org.slf4j.Logger;

abstract class PoolBase
{
    private final Logger logger;
    public final HikariConfig config;
    IMetricsTrackerDelegate metricsTracker;
    protected final String poolName;
    volatile String catalog;
    final AtomicReference<Exception> lastConnectionFailure;
    long connectionTimeout;
    long validationTimeout;
    SQLExceptionOverride exceptionOverride;
    private static final String[] RESET_STATES;
    private static final int UNINITIALIZED = -1;
    private static final int TRUE = 1;
    private static final int FALSE = 0;
    private int networkTimeout;
    private int isNetworkTimeoutSupported;
    private int isQueryTimeoutSupported;
    private int defaultTransactionIsolation;
    private int transactionIsolation;
    private Executor netTimeoutExecutor;
    private DataSource dataSource;
    private final String schema;
    private final boolean isReadOnly;
    private final boolean isAutoCommit;
    private final boolean isUseJdbc4Validation;
    private final boolean isIsolateInternalQueries;
    private volatile boolean isValidChecked;
    
    PoolBase(final HikariConfig config) {
        this.logger = LoggerFactory.getLogger(PoolBase.class);
        this.config = config;
        this.networkTimeout = -1;
        this.catalog = config.getCatalog();
        this.schema = config.getSchema();
        this.isReadOnly = config.isReadOnly();
        this.isAutoCommit = config.isAutoCommit();
        this.exceptionOverride = UtilityElf.createInstance(config.getExceptionOverrideClassName(), SQLExceptionOverride.class, new Object[0]);
        this.transactionIsolation = UtilityElf.getTransactionIsolation(config.getTransactionIsolation());
        this.isQueryTimeoutSupported = -1;
        this.isNetworkTimeoutSupported = -1;
        this.isUseJdbc4Validation = (config.getConnectionTestQuery() == null);
        this.isIsolateInternalQueries = config.isIsolateInternalQueries();
        this.poolName = config.getPoolName();
        this.connectionTimeout = config.getConnectionTimeout();
        this.validationTimeout = config.getValidationTimeout();
        this.lastConnectionFailure = new AtomicReference<Exception>();
        this.initializeDataSource();
    }
    
    @Override
    public String toString() {
        return this.poolName;
    }
    
    abstract void recycle(final PoolEntry p0);
    
    void quietlyCloseConnection(final Connection connection, final String s) {
        if (connection != null) {
            try {
                this.logger.debug("{} - Closing connection {}: {}", this.poolName, connection, s);
                try {
                    this.setNetworkTimeout(connection, TimeUnit.SECONDS.toMillis(15L));
                }
                catch (SQLException ex2) {}
                finally {
                    connection.close();
                }
            }
            catch (Exception ex) {
                this.logger.debug("{} - Closing connection {} failed", this.poolName, connection, ex);
            }
        }
    }
    
    boolean isConnectionAlive(final Connection connection) {
        try {
            try {
                this.setNetworkTimeout(connection, this.validationTimeout);
                final int n = (int)Math.max(1000L, this.validationTimeout) / 1000;
                if (this.isUseJdbc4Validation) {
                    return connection.isValid(n);
                }
                final Statement statement = connection.createStatement();
                try {
                    if (this.isNetworkTimeoutSupported != 1) {
                        this.setQueryTimeout(statement, n);
                    }
                    statement.execute(this.config.getConnectionTestQuery());
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            finally {
                this.setNetworkTimeout(connection, this.networkTimeout);
                if (this.isIsolateInternalQueries && !this.isAutoCommit) {
                    connection.rollback();
                }
            }
            return true;
        }
        catch (Exception newValue) {
            this.lastConnectionFailure.set(newValue);
            this.logger.warn("{} - Failed to validate connection {} ({}). Possibly consider using a shorter maxLifetime value.", this.poolName, connection, newValue.getMessage());
            return false;
        }
    }
    
    Exception getLastConnectionFailure() {
        return this.lastConnectionFailure.get();
    }
    
    public DataSource getUnwrappedDataSource() {
        return this.dataSource;
    }
    
    PoolEntry newPoolEntry() {
        return new PoolEntry(this.newConnection(), this, this.isReadOnly, this.isAutoCommit);
    }
    
    void resetConnectionState(final Connection connection, final ProxyConnection proxyConnection, final int n) {
        int n2 = 0;
        if ((n & 0x1) != 0x0 && proxyConnection.getReadOnlyState() != this.isReadOnly) {
            connection.setReadOnly(this.isReadOnly);
            n2 |= 0x1;
        }
        if ((n & 0x2) != 0x0 && proxyConnection.getAutoCommitState() != this.isAutoCommit) {
            connection.setAutoCommit(this.isAutoCommit);
            n2 |= 0x2;
        }
        if ((n & 0x4) != 0x0 && proxyConnection.getTransactionIsolationState() != this.transactionIsolation) {
            connection.setTransactionIsolation(this.transactionIsolation);
            n2 |= 0x4;
        }
        if ((n & 0x8) != 0x0 && this.catalog != null && !this.catalog.equals(proxyConnection.getCatalogState())) {
            connection.setCatalog(this.catalog);
            n2 |= 0x8;
        }
        if ((n & 0x10) != 0x0 && proxyConnection.getNetworkTimeoutState() != this.networkTimeout) {
            this.setNetworkTimeout(connection, this.networkTimeout);
            n2 |= 0x10;
        }
        if ((n & 0x20) != 0x0 && this.schema != null && !this.schema.equals(proxyConnection.getSchemaState())) {
            connection.setSchema(this.schema);
            n2 |= 0x20;
        }
        if (n2 != 0 && this.logger.isDebugEnabled()) {
            this.logger.debug("{} - Reset ({}) on connection {}", this.poolName, this.stringFromResetBits(n2), connection);
        }
    }
    
    void shutdownNetworkTimeoutExecutor() {
        if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
        }
    }
    
    long getLoginTimeout() {
        try {
            return (this.dataSource != null) ? this.dataSource.getLoginTimeout() : TimeUnit.SECONDS.toSeconds(5L);
        }
        catch (SQLException ex) {
            return TimeUnit.SECONDS.toSeconds(5L);
        }
    }
    
    void handleMBeans(final HikariPool hikariPool, final boolean b) {
        if (!this.config.isRegisterMbeans()) {
            return;
        }
        try {
            final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            final ObjectName objectName = new ObjectName("ws.billy.BeaconBattle.libs.com.zaxxer.hikari:type=PoolConfig (" + this.poolName + ")");
            final ObjectName objectName2 = new ObjectName("ws.billy.BeaconBattle.libs.com.zaxxer.hikari:type=Pool (" + this.poolName + ")");
            if (b) {
                if (!platformMBeanServer.isRegistered(objectName)) {
                    platformMBeanServer.registerMBean(this.config, objectName);
                    platformMBeanServer.registerMBean(hikariPool, objectName2);
                }
                else {
                    this.logger.error("{} - JMX name ({}) is already registered.", this.poolName, this.poolName);
                }
            }
            else if (platformMBeanServer.isRegistered(objectName)) {
                platformMBeanServer.unregisterMBean(objectName);
                platformMBeanServer.unregisterMBean(objectName2);
            }
        }
        catch (Exception ex) {
            this.logger.warn("{} - Failed to {} management beans.", this.poolName, b ? "register" : "unregister", ex);
        }
    }
    
    private void initializeDataSource() {
        final String jdbcUrl = this.config.getJdbcUrl();
        final String username = this.config.getUsername();
        final String password = this.config.getPassword();
        final String dataSourceClassName = this.config.getDataSourceClassName();
        final String driverClassName = this.config.getDriverClassName();
        final String dataSourceJNDI = this.config.getDataSourceJNDI();
        final Properties dataSourceProperties = this.config.getDataSourceProperties();
        DataSource dataSource = this.config.getDataSource();
        if (dataSourceClassName != null && dataSource == null) {
            dataSource = UtilityElf.createInstance(dataSourceClassName, DataSource.class, new Object[0]);
            PropertyElf.setTargetFromProperties(dataSource, dataSourceProperties);
        }
        else if (jdbcUrl != null && dataSource == null) {
            dataSource = new DriverDataSource(jdbcUrl, driverClassName, dataSourceProperties, username, password);
        }
        else if (dataSourceJNDI != null && dataSource == null) {
            try {
                dataSource = (DataSource)new InitialContext().lookup(dataSourceJNDI);
            }
            catch (NamingException ex) {
                throw new HikariPool.PoolInitializationException(ex);
            }
        }
        if (dataSource != null) {
            this.setLoginTimeout(dataSource);
            this.createNetworkTimeoutExecutor(dataSource, dataSourceClassName, jdbcUrl);
        }
        this.dataSource = dataSource;
    }
    
    private Connection newConnection() {
        final long currentTime = ClockSource.currentTime();
        Connection connection = null;
        try {
            final String username = this.config.getUsername();
            final String password = this.config.getPassword();
            connection = ((username == null) ? this.dataSource.getConnection() : this.dataSource.getConnection(username, password));
            if (connection == null) {
                throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
            }
            this.setupConnection(connection);
            this.lastConnectionFailure.set(null);
            return connection;
        }
        catch (Exception newValue) {
            if (connection != null) {
                this.quietlyCloseConnection(connection, "(Failed to create/setup connection)");
            }
            else if (this.getLastConnectionFailure() == null) {
                this.logger.debug("{} - Failed to create/setup connection: {}", this.poolName, newValue.getMessage());
            }
            this.lastConnectionFailure.set(newValue);
            throw newValue;
        }
        finally {
            if (this.metricsTracker != null) {
                this.metricsTracker.recordConnectionCreated(ClockSource.elapsedMillis(currentTime));
            }
        }
    }
    
    private void setupConnection(final Connection connection) {
        try {
            if (this.networkTimeout == -1) {
                this.networkTimeout = this.getAndSetNetworkTimeout(connection, this.validationTimeout);
            }
            else {
                this.setNetworkTimeout(connection, this.validationTimeout);
            }
            if (connection.isReadOnly() != this.isReadOnly) {
                connection.setReadOnly(this.isReadOnly);
            }
            if (connection.getAutoCommit() != this.isAutoCommit) {
                connection.setAutoCommit(this.isAutoCommit);
            }
            this.checkDriverSupport(connection);
            if (this.transactionIsolation != this.defaultTransactionIsolation) {
                connection.setTransactionIsolation(this.transactionIsolation);
            }
            if (this.catalog != null) {
                connection.setCatalog(this.catalog);
            }
            if (this.schema != null) {
                connection.setSchema(this.schema);
            }
            this.executeSql(connection, this.config.getConnectionInitSql(), true);
            this.setNetworkTimeout(connection, this.networkTimeout);
        }
        catch (SQLException ex) {
            throw new ConnectionSetupException(ex);
        }
    }
    
    private void checkDriverSupport(final Connection connection) {
        if (!this.isValidChecked) {
            this.checkValidationSupport(connection);
            this.checkDefaultIsolation(connection);
            this.isValidChecked = true;
        }
    }
    
    private void checkValidationSupport(final Connection connection) {
        try {
            if (this.isUseJdbc4Validation) {
                connection.isValid(1);
            }
            else {
                this.executeSql(connection, this.config.getConnectionTestQuery(), false);
            }
        }
        catch (Exception | AbstractMethodError ex) {
            final AbstractMethodError abstractMethodError2;
            final AbstractMethodError abstractMethodError = abstractMethodError2;
            this.logger.error("{} - Failed to execute{} connection test query ({}).", this.poolName, this.isUseJdbc4Validation ? " isValid() for connection, configure" : "", abstractMethodError.getMessage());
            throw abstractMethodError;
        }
    }
    
    private void checkDefaultIsolation(final Connection connection) {
        try {
            this.defaultTransactionIsolation = connection.getTransactionIsolation();
            if (this.transactionIsolation == -1) {
                this.transactionIsolation = this.defaultTransactionIsolation;
            }
        }
        catch (SQLException ex) {
            this.logger.warn("{} - Default transaction isolation level detection failed ({}).", this.poolName, ex.getMessage());
            if (ex.getSQLState() != null && !ex.getSQLState().startsWith("08")) {
                throw ex;
            }
        }
    }
    
    private void setQueryTimeout(final Statement statement, final int queryTimeout) {
        if (this.isQueryTimeoutSupported != 0) {
            try {
                statement.setQueryTimeout(queryTimeout);
                this.isQueryTimeoutSupported = 1;
            }
            catch (Exception ex) {
                if (this.isQueryTimeoutSupported == -1) {
                    this.isQueryTimeoutSupported = 0;
                    this.logger.info("{} - Failed to set query timeout for statement. ({})", this.poolName, ex.getMessage());
                }
            }
        }
    }
    
    private int getAndSetNetworkTimeout(final Connection connection, final long n) {
        if (this.isNetworkTimeoutSupported != 0) {
            try {
                final int networkTimeout = connection.getNetworkTimeout();
                connection.setNetworkTimeout(this.netTimeoutExecutor, (int)n);
                this.isNetworkTimeoutSupported = 1;
                return networkTimeout;
            }
            catch (Exception | AbstractMethodError ex) {
                final AbstractMethodError abstractMethodError2;
                final AbstractMethodError abstractMethodError = abstractMethodError2;
                if (this.isNetworkTimeoutSupported == -1) {
                    this.isNetworkTimeoutSupported = 0;
                    this.logger.info("{} - Driver does not support get/set network timeout for connections. ({})", this.poolName, abstractMethodError.getMessage());
                    if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
                        this.logger.warn("{} - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.", this.poolName);
                    }
                    else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
                        this.logger.warn("{} - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.", this.poolName);
                    }
                }
            }
        }
        return 0;
    }
    
    private void setNetworkTimeout(final Connection connection, final long n) {
        if (this.isNetworkTimeoutSupported == 1) {
            connection.setNetworkTimeout(this.netTimeoutExecutor, (int)n);
        }
    }
    
    private void executeSql(final Connection connection, final String s, final boolean b) {
        if (s != null) {
            final Statement statement = connection.createStatement();
            try {
                statement.execute(s);
                if (statement != null) {
                    statement.close();
                }
            }
            catch (Throwable t) {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
            if (this.isIsolateInternalQueries && !this.isAutoCommit) {
                if (b) {
                    connection.commit();
                }
                else {
                    connection.rollback();
                }
            }
        }
    }
    
    private void createNetworkTimeoutExecutor(final DataSource dataSource, final String s, final String s2) {
        if ((s != null && s.contains("Mysql")) || (s2 != null && s2.contains("mysql")) || (dataSource != null && dataSource.getClass().getName().contains("Mysql"))) {
            this.netTimeoutExecutor = new SynchronousExecutor();
        }
        else {
            final ThreadFactory threadFactory = this.config.getThreadFactory();
            final ThreadPoolExecutor netTimeoutExecutor = (ThreadPoolExecutor)Executors.newCachedThreadPool((threadFactory != null) ? threadFactory : new UtilityElf.DefaultThreadFactory(this.poolName + " network timeout executor", true));
            netTimeoutExecutor.setKeepAliveTime(15L, TimeUnit.SECONDS);
            netTimeoutExecutor.allowCoreThreadTimeOut(true);
            this.netTimeoutExecutor = netTimeoutExecutor;
        }
    }
    
    private void setLoginTimeout(final DataSource dataSource) {
        if (this.connectionTimeout != 2147483647L) {
            try {
                dataSource.setLoginTimeout(Math.max(1, (int)TimeUnit.MILLISECONDS.toSeconds(500L + this.connectionTimeout)));
            }
            catch (Exception ex) {
                this.logger.info("{} - Failed to set login timeout for data source. ({})", this.poolName, ex.getMessage());
            }
        }
    }
    
    private String stringFromResetBits(final int n) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PoolBase.RESET_STATES.length; ++i) {
            if ((n & 1 << i) != 0x0) {
                sb.append(PoolBase.RESET_STATES[i]).append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
    
    static {
        RESET_STATES = new String[] { "readOnly", "autoCommit", "isolation", "catalog", "netTimeout", "schema" };
    }
    
    static class ConnectionSetupException extends Exception
    {
        private static final long serialVersionUID = 929872118275916521L;
        
        ConnectionSetupException(final Throwable cause) {
            super(cause);
        }
    }
    
    private static class SynchronousExecutor implements Executor
    {
        @Override
        public void execute(final Runnable runnable) {
            try {
                runnable.run();
            }
            catch (Exception ex) {
                LoggerFactory.getLogger(PoolBase.class).debug("Failed to execute: {}", runnable, ex);
            }
        }
    }
    
    interface IMetricsTrackerDelegate extends AutoCloseable
    {
        default void recordConnectionUsage(final PoolEntry poolEntry) {
        }
        
        default void recordConnectionCreated(final long connectionCreatedMillis) {
        }
        
        default void recordBorrowTimeoutStats(final long startTime) {
        }
        
        default void recordBorrowStats(final PoolEntry poolEntry, final long startTime) {
        }
        
        default void recordConnectionTimeout() {
        }
        
        default void close() {
        }
    }
    
    static class MetricsTrackerDelegate implements IMetricsTrackerDelegate
    {
        final IMetricsTracker tracker;
        
        MetricsTrackerDelegate(final IMetricsTracker tracker) {
            this.tracker = tracker;
        }
        
        @Override
        public void recordConnectionUsage(final PoolEntry poolEntry) {
            this.tracker.recordConnectionUsageMillis(poolEntry.getMillisSinceBorrowed());
        }
        
        @Override
        public void recordConnectionCreated(final long connectionCreatedMillis) {
            this.tracker.recordConnectionCreatedMillis(connectionCreatedMillis);
        }
        
        @Override
        public void recordBorrowTimeoutStats(final long startTime) {
            this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime));
        }
        
        @Override
        public void recordBorrowStats(final PoolEntry poolEntry, final long startTime) {
            final long currentTime = ClockSource.currentTime();
            poolEntry.lastBorrowed = currentTime;
            this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(startTime, currentTime));
        }
        
        @Override
        public void recordConnectionTimeout() {
            this.tracker.recordConnectionTimeout();
        }
        
        @Override
        public void close() {
            this.tracker.close();
        }
    }
    
    static final class NopMetricsTrackerDelegate implements IMetricsTrackerDelegate
    {
    }
}
