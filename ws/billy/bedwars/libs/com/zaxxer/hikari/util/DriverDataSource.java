

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import java.sql.SQLFeatureNotSupportedException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Map;
import java.sql.Driver;
import java.util.Properties;
import ws.billy.bedwars.libs.org.slf4j.Logger;

import javax.sql.DataSource;

public final class DriverDataSource implements DataSource
{
    private static final Logger LOGGER;
    private static final String PASSWORD = "password";
    private static final String USER = "user";
    private final String jdbcUrl;
    private final Properties driverProperties;
    private Driver driver;
    
    public DriverDataSource(final String s, final String s2, final Properties properties, final String defaultValue, final String defaultValue2) {
        this.jdbcUrl = s;
        this.driverProperties = new Properties();
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            this.driverProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }
        if (defaultValue != null) {
            this.driverProperties.put("user", this.driverProperties.getProperty("user", defaultValue));
        }
        if (defaultValue2 != null) {
            this.driverProperties.put("password", this.driverProperties.getProperty("password", defaultValue2));
        }
        if (s2 != null) {
            final Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                final Driver driver = drivers.nextElement();
                if (driver.getClass().getName().equals(s2)) {
                    this.driver = driver;
                    break;
                }
            }
            if (this.driver == null) {
                DriverDataSource.LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", s2);
                Class<?> clazz = null;
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    if (contextClassLoader != null) {
                        try {
                            clazz = contextClassLoader.loadClass(s2);
                            DriverDataSource.LOGGER.debug("Driver class {} found in Thread context class loader {}", s2, contextClassLoader);
                        }
                        catch (ClassNotFoundException ex2) {
                            DriverDataSource.LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", s2, contextClassLoader, this.getClass().getClassLoader());
                        }
                    }
                    if (clazz == null) {
                        clazz = this.getClass().getClassLoader().loadClass(s2);
                        DriverDataSource.LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", s2, this.getClass().getClassLoader());
                    }
                }
                catch (ClassNotFoundException ex3) {
                    DriverDataSource.LOGGER.debug("Failed to load driver class {} from HikariConfig class classloader {}", s2, this.getClass().getClassLoader());
                }
                if (clazz != null) {
                    try {
                        this.driver = (Driver)clazz.newInstance();
                    }
                    catch (Exception ex) {
                        DriverDataSource.LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", s2, ex);
                    }
                }
            }
        }
        final String replaceAll = s.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
        try {
            if (this.driver == null) {
                this.driver = DriverManager.getDriver(s);
                DriverDataSource.LOGGER.debug("Loaded driver with class name {} for jdbcUrl={}", this.driver.getClass().getName(), replaceAll);
            }
            else if (!this.driver.acceptsURL(s)) {
                throw new RuntimeException("Driver " + s2 + " claims to not accept jdbcUrl, " + replaceAll);
            }
        }
        catch (SQLException cause) {
            throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + replaceAll, cause);
        }
    }
    
    @Override
    public Connection getConnection() {
        return this.driver.connect(this.jdbcUrl, this.driverProperties);
    }
    
    @Override
    public Connection getConnection(final String s, final String value) {
        final Properties properties = (Properties)this.driverProperties.clone();
        if (s != null) {
            properties.put("user", s);
            if (properties.containsKey("username")) {
                properties.put("username", s);
            }
        }
        if (value != null) {
            properties.put("password", value);
        }
        return this.driver.connect(this.jdbcUrl, properties);
    }
    
    @Override
    public PrintWriter getLogWriter() {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void setLogWriter(final PrintWriter printWriter) {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }
    
    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }
    
    @Override
    public java.util.logging.Logger getParentLogger() {
        return this.driver.getParentLogger();
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) {
        return false;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
    }
}
