

package ws.billy.bedwars.libs.com.zaxxer.hikari;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import java.security.AccessControlException;
import java.util.concurrent.ThreadLocalRandom;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.codahale.metrics.health.HealthCheckRegistry;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.UtilityElf;
import ws.billy.bedwars.libs.com.zaxxer.hikari.util.PropertyElf;
import ws.billy.bedwars.libs.com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.Properties;
import javax.sql.DataSource;
import ws.billy.bedwars.libs.org.slf4j.Logger;

public class HikariConfig implements HikariConfigMXBean
{
    private static final Logger LOGGER;
    private static final char[] ID_CHARACTERS;
    private static final long CONNECTION_TIMEOUT;
    private static final long VALIDATION_TIMEOUT;
    private static final long IDLE_TIMEOUT;
    private static final long MAX_LIFETIME;
    private static final int DEFAULT_POOL_SIZE = 10;
    private static boolean unitTest;
    private volatile String catalog;
    private volatile long connectionTimeout;
    private volatile long validationTimeout;
    private volatile long idleTimeout;
    private volatile long leakDetectionThreshold;
    private volatile long maxLifetime;
    private volatile int maxPoolSize;
    private volatile int minIdle;
    private volatile String username;
    private volatile String password;
    private long initializationFailTimeout;
    private String connectionInitSql;
    private String connectionTestQuery;
    private String dataSourceClassName;
    private String dataSourceJndiName;
    private String driverClassName;
    private String exceptionOverrideClassName;
    private String jdbcUrl;
    private String poolName;
    private String schema;
    private String transactionIsolationName;
    private boolean isAutoCommit;
    private boolean isReadOnly;
    private boolean isIsolateInternalQueries;
    private boolean isRegisterMbeans;
    private boolean isAllowPoolSuspension;
    private DataSource dataSource;
    private Properties dataSourceProperties;
    private ThreadFactory threadFactory;
    private ScheduledExecutorService scheduledExecutor;
    private MetricsTrackerFactory metricsTrackerFactory;
    private Object metricRegistry;
    private Object healthCheckRegistry;
    private Properties healthCheckProperties;
    private volatile boolean sealed;
    
    public HikariConfig() {
        this.dataSourceProperties = new Properties();
        this.healthCheckProperties = new Properties();
        this.minIdle = -1;
        this.maxPoolSize = -1;
        this.maxLifetime = HikariConfig.MAX_LIFETIME;
        this.connectionTimeout = HikariConfig.CONNECTION_TIMEOUT;
        this.validationTimeout = HikariConfig.VALIDATION_TIMEOUT;
        this.idleTimeout = HikariConfig.IDLE_TIMEOUT;
        this.initializationFailTimeout = 1L;
        this.isAutoCommit = true;
        final String property = System.getProperty("hikaricp.configurationFile");
        if (property != null) {
            this.loadProperties(property);
        }
    }
    
    public HikariConfig(final Properties properties) {
        this();
        PropertyElf.setTargetFromProperties(this, properties);
    }
    
    public HikariConfig(final String s) {
        this();
        this.loadProperties(s);
    }
    
    @Override
    public String getCatalog() {
        return this.catalog;
    }
    
    @Override
    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }
    
    @Override
    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    @Override
    public void setConnectionTimeout(final long connectionTimeout) {
        if (connectionTimeout == 0L) {
            this.connectionTimeout = 2147483647L;
        }
        else {
            if (connectionTimeout < 250L) {
                throw new IllegalArgumentException("connectionTimeout cannot be less than 250ms");
            }
            this.connectionTimeout = connectionTimeout;
        }
    }
    
    @Override
    public long getIdleTimeout() {
        return this.idleTimeout;
    }
    
    @Override
    public void setIdleTimeout(final long idleTimeout) {
        if (idleTimeout < 0L) {
            throw new IllegalArgumentException("idleTimeout cannot be negative");
        }
        this.idleTimeout = idleTimeout;
    }
    
    @Override
    public long getLeakDetectionThreshold() {
        return this.leakDetectionThreshold;
    }
    
    @Override
    public void setLeakDetectionThreshold(final long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }
    
    @Override
    public long getMaxLifetime() {
        return this.maxLifetime;
    }
    
    @Override
    public void setMaxLifetime(final long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }
    
    @Override
    public int getMaximumPoolSize() {
        return this.maxPoolSize;
    }
    
    @Override
    public void setMaximumPoolSize(final int maxPoolSize) {
        if (maxPoolSize < 1) {
            throw new IllegalArgumentException("maxPoolSize cannot be less than 1");
        }
        this.maxPoolSize = maxPoolSize;
    }
    
    @Override
    public int getMinimumIdle() {
        return this.minIdle;
    }
    
    @Override
    public void setMinimumIdle(final int minIdle) {
        if (minIdle < 0) {
            throw new IllegalArgumentException("minimumIdle cannot be negative");
        }
        this.minIdle = minIdle;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public void setUsername(final String username) {
        this.username = username;
    }
    
    @Override
    public long getValidationTimeout() {
        return this.validationTimeout;
    }
    
    @Override
    public void setValidationTimeout(final long validationTimeout) {
        if (validationTimeout < 250L) {
            throw new IllegalArgumentException("validationTimeout cannot be less than 250ms");
        }
        this.validationTimeout = validationTimeout;
    }
    
    public String getConnectionTestQuery() {
        return this.connectionTestQuery;
    }
    
    public void setConnectionTestQuery(final String connectionTestQuery) {
        this.checkIfSealed();
        this.connectionTestQuery = connectionTestQuery;
    }
    
    public String getConnectionInitSql() {
        return this.connectionInitSql;
    }
    
    public void setConnectionInitSql(final String connectionInitSql) {
        this.checkIfSealed();
        this.connectionInitSql = connectionInitSql;
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.checkIfSealed();
        this.dataSource = dataSource;
    }
    
    public String getDataSourceClassName() {
        return this.dataSourceClassName;
    }
    
    public void setDataSourceClassName(final String dataSourceClassName) {
        this.checkIfSealed();
        this.dataSourceClassName = dataSourceClassName;
    }
    
    public void addDataSourceProperty(final String key, final Object value) {
        this.checkIfSealed();
        this.dataSourceProperties.put(key, value);
    }
    
    public String getDataSourceJNDI() {
        return this.dataSourceJndiName;
    }
    
    public void setDataSourceJNDI(final String dataSourceJndiName) {
        this.checkIfSealed();
        this.dataSourceJndiName = dataSourceJndiName;
    }
    
    public Properties getDataSourceProperties() {
        return this.dataSourceProperties;
    }
    
    public void setDataSourceProperties(final Properties t) {
        this.checkIfSealed();
        this.dataSourceProperties.putAll(t);
    }
    
    public String getDriverClassName() {
        return this.driverClassName;
    }
    
    public void setDriverClassName(final String s) {
        this.checkIfSealed();
        Class<?> clazz = this.attemptFromContextLoader(s);
        try {
            if (clazz == null) {
                clazz = this.getClass().getClassLoader().loadClass(s);
                HikariConfig.LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", s, this.getClass().getClassLoader());
            }
        }
        catch (ClassNotFoundException ex) {
            HikariConfig.LOGGER.error("Failed to load driver class {} from HikariConfig class classloader {}", s, this.getClass().getClassLoader());
        }
        if (clazz == null) {
            throw new RuntimeException("Failed to load driver class " + s + " in either of HikariConfig class loader or Thread context classloader");
        }
        try {
            clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            this.driverClassName = s;
        }
        catch (Exception cause) {
            throw new RuntimeException("Failed to instantiate class " + s, cause);
        }
    }
    
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }
    
    public void setJdbcUrl(final String jdbcUrl) {
        this.checkIfSealed();
        this.jdbcUrl = jdbcUrl;
    }
    
    public boolean isAutoCommit() {
        return this.isAutoCommit;
    }
    
    public void setAutoCommit(final boolean isAutoCommit) {
        this.checkIfSealed();
        this.isAutoCommit = isAutoCommit;
    }
    
    public boolean isAllowPoolSuspension() {
        return this.isAllowPoolSuspension;
    }
    
    public void setAllowPoolSuspension(final boolean isAllowPoolSuspension) {
        this.checkIfSealed();
        this.isAllowPoolSuspension = isAllowPoolSuspension;
    }
    
    public long getInitializationFailTimeout() {
        return this.initializationFailTimeout;
    }
    
    public void setInitializationFailTimeout(final long initializationFailTimeout) {
        this.checkIfSealed();
        this.initializationFailTimeout = initializationFailTimeout;
    }
    
    public boolean isIsolateInternalQueries() {
        return this.isIsolateInternalQueries;
    }
    
    public void setIsolateInternalQueries(final boolean isIsolateInternalQueries) {
        this.checkIfSealed();
        this.isIsolateInternalQueries = isIsolateInternalQueries;
    }
    
    public MetricsTrackerFactory getMetricsTrackerFactory() {
        return this.metricsTrackerFactory;
    }
    
    public void setMetricsTrackerFactory(final MetricsTrackerFactory metricsTrackerFactory) {
        if (this.metricRegistry != null) {
            throw new IllegalStateException("cannot use setMetricsTrackerFactory() and setMetricRegistry() together");
        }
        this.metricsTrackerFactory = metricsTrackerFactory;
    }
    
    public Object getMetricRegistry() {
        return this.metricRegistry;
    }
    
    public void setMetricRegistry(Object objectOrPerformJndiLookup) {
        if (this.metricsTrackerFactory != null) {
            throw new IllegalStateException("cannot use setMetricRegistry() and setMetricsTrackerFactory() together");
        }
        if (objectOrPerformJndiLookup != null) {
            objectOrPerformJndiLookup = this.getObjectOrPerformJndiLookup(objectOrPerformJndiLookup);
            if (!UtilityElf.safeIsAssignableFrom(objectOrPerformJndiLookup, "com.codahale.metrics.MetricRegistry") && !UtilityElf.safeIsAssignableFrom(objectOrPerformJndiLookup, "io.micrometer.core.instrument.MeterRegistry")) {
                throw new IllegalArgumentException("Class must be instance of com.codahale.metrics.MetricRegistry or io.micrometer.core.instrument.MeterRegistry");
            }
        }
        this.metricRegistry = objectOrPerformJndiLookup;
    }
    
    public Object getHealthCheckRegistry() {
        return this.healthCheckRegistry;
    }
    
    public void setHealthCheckRegistry(Object objectOrPerformJndiLookup) {
        this.checkIfSealed();
        if (objectOrPerformJndiLookup != null) {
            objectOrPerformJndiLookup = this.getObjectOrPerformJndiLookup(objectOrPerformJndiLookup);
            if (!(objectOrPerformJndiLookup instanceof HealthCheckRegistry)) {
                throw new IllegalArgumentException("Class must be an instance of com.codahale.metrics.health.HealthCheckRegistry");
            }
        }
        this.healthCheckRegistry = objectOrPerformJndiLookup;
    }
    
    public Properties getHealthCheckProperties() {
        return this.healthCheckProperties;
    }
    
    public void setHealthCheckProperties(final Properties t) {
        this.checkIfSealed();
        this.healthCheckProperties.putAll(t);
    }
    
    public void addHealthCheckProperty(final String key, final String value) {
        this.checkIfSealed();
        this.healthCheckProperties.setProperty(key, value);
    }
    
    public boolean isReadOnly() {
        return this.isReadOnly;
    }
    
    public void setReadOnly(final boolean isReadOnly) {
        this.checkIfSealed();
        this.isReadOnly = isReadOnly;
    }
    
    public boolean isRegisterMbeans() {
        return this.isRegisterMbeans;
    }
    
    public void setRegisterMbeans(final boolean isRegisterMbeans) {
        this.checkIfSealed();
        this.isRegisterMbeans = isRegisterMbeans;
    }
    
    @Override
    public String getPoolName() {
        return this.poolName;
    }
    
    public void setPoolName(final String poolName) {
        this.checkIfSealed();
        this.poolName = poolName;
    }
    
    public ScheduledExecutorService getScheduledExecutor() {
        return this.scheduledExecutor;
    }
    
    public void setScheduledExecutor(final ScheduledExecutorService scheduledExecutor) {
        this.checkIfSealed();
        this.scheduledExecutor = scheduledExecutor;
    }
    
    public String getTransactionIsolation() {
        return this.transactionIsolationName;
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public void setSchema(final String schema) {
        this.checkIfSealed();
        this.schema = schema;
    }
    
    public String getExceptionOverrideClassName() {
        return this.exceptionOverrideClassName;
    }
    
    public void setExceptionOverrideClassName(final String s) {
        this.checkIfSealed();
        Class<?> clazz = this.attemptFromContextLoader(s);
        try {
            if (clazz == null) {
                clazz = this.getClass().getClassLoader().loadClass(s);
                HikariConfig.LOGGER.debug("SQLExceptionOverride class {} found in the HikariConfig class classloader {}", s, this.getClass().getClassLoader());
            }
        }
        catch (ClassNotFoundException ex) {
            HikariConfig.LOGGER.error("Failed to load SQLExceptionOverride class {} from HikariConfig class classloader {}", s, this.getClass().getClassLoader());
        }
        if (clazz == null) {
            throw new RuntimeException("Failed to load SQLExceptionOverride class " + s + " in either of HikariConfig class loader or Thread context classloader");
        }
        try {
            clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            this.exceptionOverrideClassName = s;
        }
        catch (Exception cause) {
            throw new RuntimeException("Failed to instantiate class " + s, cause);
        }
    }
    
    public void setTransactionIsolation(final String transactionIsolationName) {
        this.checkIfSealed();
        this.transactionIsolationName = transactionIsolationName;
    }
    
    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }
    
    public void setThreadFactory(final ThreadFactory threadFactory) {
        this.checkIfSealed();
        this.threadFactory = threadFactory;
    }
    
    void seal() {
        this.sealed = true;
    }
    
    public void copyStateTo(final HikariConfig obj) {
        for (final Field field : HikariConfig.class.getDeclaredFields()) {
            if (!Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    field.set(obj, field.get(this));
                }
                catch (Exception cause) {
                    throw new RuntimeException("Failed to copy HikariConfig state: " + cause.getMessage(), cause);
                }
            }
        }
        obj.sealed = false;
    }
    
    private Class<?> attemptFromContextLoader(final String name) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                final Class<?> loadClass = contextClassLoader.loadClass(name);
                HikariConfig.LOGGER.debug("Driver class {} found in Thread context class loader {}", name, contextClassLoader);
                return loadClass;
            }
            catch (ClassNotFoundException ex) {
                HikariConfig.LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", name, contextClassLoader, this.getClass().getClassLoader());
            }
        }
        return null;
    }
    
    public void validate() {
        if (this.poolName == null) {
            this.poolName = this.generatePoolName();
        }
        else if (this.isRegisterMbeans && this.poolName.contains(":")) {
            throw new IllegalArgumentException("poolName cannot contain ':' when used with JMX");
        }
        this.catalog = UtilityElf.getNullIfEmpty(this.catalog);
        this.connectionInitSql = UtilityElf.getNullIfEmpty(this.connectionInitSql);
        this.connectionTestQuery = UtilityElf.getNullIfEmpty(this.connectionTestQuery);
        this.transactionIsolationName = UtilityElf.getNullIfEmpty(this.transactionIsolationName);
        this.dataSourceClassName = UtilityElf.getNullIfEmpty(this.dataSourceClassName);
        this.dataSourceJndiName = UtilityElf.getNullIfEmpty(this.dataSourceJndiName);
        this.driverClassName = UtilityElf.getNullIfEmpty(this.driverClassName);
        this.jdbcUrl = UtilityElf.getNullIfEmpty(this.jdbcUrl);
        if (this.dataSource != null) {
            if (this.dataSourceClassName != null) {
                HikariConfig.LOGGER.warn("{} - using dataSource and ignoring dataSourceClassName.", this.poolName);
            }
        }
        else if (this.dataSourceClassName != null) {
            if (this.driverClassName != null) {
                HikariConfig.LOGGER.error("{} - cannot use driverClassName and dataSourceClassName together.", this.poolName);
                throw new IllegalStateException("cannot use driverClassName and dataSourceClassName together.");
            }
            if (this.jdbcUrl != null) {
                HikariConfig.LOGGER.warn("{} - using dataSourceClassName and ignoring jdbcUrl.", this.poolName);
            }
        }
        else if (this.jdbcUrl == null) {
            if (this.dataSourceJndiName == null) {
                if (this.driverClassName != null) {
                    HikariConfig.LOGGER.error("{} - jdbcUrl is required with driverClassName.", this.poolName);
                    throw new IllegalArgumentException("jdbcUrl is required with driverClassName.");
                }
                HikariConfig.LOGGER.error("{} - dataSource or dataSourceClassName or jdbcUrl is required.", this.poolName);
                throw new IllegalArgumentException("dataSource or dataSourceClassName or jdbcUrl is required.");
            }
        }
        this.validateNumerics();
        if (HikariConfig.LOGGER.isDebugEnabled() || HikariConfig.unitTest) {
            this.logConfiguration();
        }
    }
    
    private void validateNumerics() {
        if (this.maxLifetime != 0L && this.maxLifetime < TimeUnit.SECONDS.toMillis(30L)) {
            HikariConfig.LOGGER.warn("{} - maxLifetime is less than 30000ms, setting to default {}ms.", this.poolName, HikariConfig.MAX_LIFETIME);
            this.maxLifetime = HikariConfig.MAX_LIFETIME;
        }
        if (this.leakDetectionThreshold > 0L && !HikariConfig.unitTest && (this.leakDetectionThreshold < TimeUnit.SECONDS.toMillis(2L) || (this.leakDetectionThreshold > this.maxLifetime && this.maxLifetime > 0L))) {
            HikariConfig.LOGGER.warn("{} - leakDetectionThreshold is less than 2000ms or more than maxLifetime, disabling it.", this.poolName);
            this.leakDetectionThreshold = 0L;
        }
        if (this.connectionTimeout < 250L) {
            HikariConfig.LOGGER.warn("{} - connectionTimeout is less than 250ms, setting to {}ms.", this.poolName, HikariConfig.CONNECTION_TIMEOUT);
            this.connectionTimeout = HikariConfig.CONNECTION_TIMEOUT;
        }
        if (this.validationTimeout < 250L) {
            HikariConfig.LOGGER.warn("{} - validationTimeout is less than 250ms, setting to {}ms.", this.poolName, HikariConfig.VALIDATION_TIMEOUT);
            this.validationTimeout = HikariConfig.VALIDATION_TIMEOUT;
        }
        if (this.maxPoolSize < 1) {
            this.maxPoolSize = 10;
        }
        if (this.minIdle < 0 || this.minIdle > this.maxPoolSize) {
            this.minIdle = this.maxPoolSize;
        }
        if (this.idleTimeout + TimeUnit.SECONDS.toMillis(1L) > this.maxLifetime && this.maxLifetime > 0L && this.minIdle < this.maxPoolSize) {
            HikariConfig.LOGGER.warn("{} - idleTimeout is close to or more than maxLifetime, disabling it.", this.poolName);
            this.idleTimeout = 0L;
        }
        else if (this.idleTimeout != 0L && this.idleTimeout < TimeUnit.SECONDS.toMillis(10L) && this.minIdle < this.maxPoolSize) {
            HikariConfig.LOGGER.warn("{} - idleTimeout is less than 10000ms, setting to default {}ms.", this.poolName, HikariConfig.IDLE_TIMEOUT);
            this.idleTimeout = HikariConfig.IDLE_TIMEOUT;
        }
        else if (this.idleTimeout != HikariConfig.IDLE_TIMEOUT && this.idleTimeout != 0L && this.minIdle == this.maxPoolSize) {
            HikariConfig.LOGGER.warn("{} - idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.", this.poolName);
        }
    }
    
    private void checkIfSealed() {
        if (this.sealed) {
            throw new IllegalStateException("The configuration of the pool is sealed once started. Use HikariConfigMXBean for runtime changes.");
        }
    }
    
    private void logConfiguration() {
        HikariConfig.LOGGER.debug("{} - configuration:", this.poolName);
        for (final String s : new TreeSet<String>(PropertyElf.getPropertyNames(HikariConfig.class))) {
            try {
                Object o = PropertyElf.getProperty(s, this);
                if ("dataSourceProperties".equals(s)) {
                    final Properties copyProperties = PropertyElf.copyProperties(this.dataSourceProperties);
                    copyProperties.setProperty("password", "<masked>");
                    o = copyProperties;
                }
                if ("initializationFailTimeout".equals(s) && this.initializationFailTimeout == Long.MAX_VALUE) {
                    o = "infinite";
                }
                else if ("transactionIsolation".equals(s) && this.transactionIsolationName == null) {
                    o = "default";
                }
                else if (s.matches("scheduledExecutorService|threadFactory") && o == null) {
                    o = "internal";
                }
                else if (s.contains("jdbcUrl") && o instanceof String) {
                    o = ((String)o).replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
                }
                else if (s.contains("password")) {
                    o = "<masked>";
                }
                else if (o instanceof String) {
                    o = "\"" + o + "\"";
                }
                else if (o == null) {
                    o = "none";
                }
                HikariConfig.LOGGER.debug((s + "................................................").substring(0, 32) + o);
            }
            catch (Exception ex) {}
        }
    }
    
    private void loadProperties(final String str) {
        final File file = new File(str);
        try {
            final InputStream inStream = file.isFile() ? new FileInputStream(file) : this.getClass().getResourceAsStream(str);
            try {
                if (inStream == null) {
                    throw new IllegalArgumentException("Cannot find property file: " + str);
                }
                final Properties properties = new Properties();
                properties.load(inStream);
                PropertyElf.setTargetFromProperties(this, properties);
                if (inStream != null) {
                    inStream.close();
                }
            }
            catch (Throwable t) {
                if (inStream != null) {
                    try {
                        inStream.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (IOException cause) {
            throw new RuntimeException("Failed to read property file", cause);
        }
    }
    
    private String generatePoolName() {
        try {
            synchronized (System.getProperties()) {
                final String value = String.valueOf(Integer.getInteger("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.pool_number", 0) + 1);
                System.setProperty("ws.billy.BeaconBattle.libs.com.zaxxer.hikari.pool_number", value);
                return "HikariPool-" + value;
            }
        }
        catch (AccessControlException ex) {
            final ThreadLocalRandom current = ThreadLocalRandom.current();
            final StringBuilder sb = new StringBuilder("HikariPool-");
            for (int i = 0; i < 4; ++i) {
                sb.append(HikariConfig.ID_CHARACTERS[current.nextInt(62)]);
            }
            HikariConfig.LOGGER.info("assigned random pool name '{}' (security manager prevented access to system properties)", sb);
            return sb.toString();
        }
    }
    
    private Object getObjectOrPerformJndiLookup(final Object o) {
        if (o instanceof String) {
            try {
                return new InitialContext().lookup((String)o);
            }
            catch (NamingException cause) {
                throw new IllegalArgumentException(cause);
            }
        }
        return o;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(HikariConfig.class);
        ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
        VALIDATION_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
        IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10L);
        MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30L);
        HikariConfig.unitTest = false;
    }
}
