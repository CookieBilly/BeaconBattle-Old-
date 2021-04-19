

package ws.billy.bedwars.libs.org.slf4j.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.InputStream;
import ws.billy.bedwars.libs.org.slf4j.helpers.Util;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.text.DateFormat;

public class SimpleLoggerConfiguration
{
    private static final String CONFIGURATION_FILE = "simplelogger.properties";
    static int DEFAULT_LOG_LEVEL_DEFAULT;
    int defaultLogLevel;
    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    boolean showDateTime;
    private static final String DATE_TIME_FORMAT_STR_DEFAULT;
    private static String dateTimeFormatStr;
    DateFormat dateFormatter;
    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    boolean showThreadName;
    static final boolean SHOW_LOG_NAME_DEFAULT = true;
    boolean showLogName;
    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    boolean showShortLogName;
    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    boolean levelInBrackets;
    private static String LOG_FILE_DEFAULT;
    private String logFile;
    OutputChoice outputChoice;
    private static final boolean CACHE_OUTPUT_STREAM_DEFAULT = false;
    private boolean cacheOutputStream;
    private static final String WARN_LEVELS_STRING_DEFAULT = "WARN";
    String warnLevelString;
    private final Properties properties;
    
    public SimpleLoggerConfiguration() {
        this.defaultLogLevel = SimpleLoggerConfiguration.DEFAULT_LOG_LEVEL_DEFAULT;
        this.showDateTime = false;
        this.dateFormatter = null;
        this.showThreadName = true;
        this.showLogName = true;
        this.showShortLogName = false;
        this.levelInBrackets = false;
        this.logFile = SimpleLoggerConfiguration.LOG_FILE_DEFAULT;
        this.outputChoice = null;
        this.cacheOutputStream = false;
        this.warnLevelString = "WARN";
        this.properties = new Properties();
    }
    
    void init() {
        this.loadProperties();
        final String stringProperty = this.getStringProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.defaultLogLevel", null);
        if (stringProperty != null) {
            this.defaultLogLevel = stringToLevel(stringProperty);
        }
        this.showLogName = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showLogName", true);
        this.showShortLogName = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showShortLogName", false);
        this.showDateTime = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showDateTime", false);
        this.showThreadName = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showThreadName", true);
        SimpleLoggerConfiguration.dateTimeFormatStr = this.getStringProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.dateTimeFormat", SimpleLoggerConfiguration.DATE_TIME_FORMAT_STR_DEFAULT);
        this.levelInBrackets = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.levelInBrackets", false);
        this.warnLevelString = this.getStringProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.warnLevelString", "WARN");
        this.logFile = this.getStringProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.logFile", this.logFile);
        this.cacheOutputStream = this.getBooleanProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.cacheOutputStream", false);
        this.outputChoice = computeOutputChoice(this.logFile, this.cacheOutputStream);
        if (SimpleLoggerConfiguration.dateTimeFormatStr != null) {
            try {
                this.dateFormatter = new SimpleDateFormat(SimpleLoggerConfiguration.dateTimeFormatStr);
            }
            catch (IllegalArgumentException ex) {
                Util.report("Bad date format in simplelogger.properties; will output relative time", ex);
            }
        }
    }
    
    private void loadProperties() {
        final InputStream inStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            public InputStream run() {
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                if (contextClassLoader != null) {
                    return contextClassLoader.getResourceAsStream("simplelogger.properties");
                }
                return ClassLoader.getSystemResourceAsStream("simplelogger.properties");
            }
        });
        if (null != inStream) {
            try {
                this.properties.load(inStream);
            }
            catch (IOException ex) {}
            finally {
                try {
                    inStream.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    String getStringProperty(final String s, final String s2) {
        final String stringProperty = this.getStringProperty(s);
        return (stringProperty == null) ? s2 : stringProperty;
    }
    
    boolean getBooleanProperty(final String s, final boolean b) {
        final String stringProperty = this.getStringProperty(s);
        return (stringProperty == null) ? b : "true".equalsIgnoreCase(stringProperty);
    }
    
    String getStringProperty(final String s) {
        String property = null;
        try {
            property = System.getProperty(s);
        }
        catch (SecurityException ex) {}
        return (property == null) ? this.properties.getProperty(s) : property;
    }
    
    static int stringToLevel(final String s) {
        if ("trace".equalsIgnoreCase(s)) {
            return 0;
        }
        if ("debug".equalsIgnoreCase(s)) {
            return 10;
        }
        if ("info".equalsIgnoreCase(s)) {
            return 20;
        }
        if ("warn".equalsIgnoreCase(s)) {
            return 30;
        }
        if ("error".equalsIgnoreCase(s)) {
            return 40;
        }
        if ("off".equalsIgnoreCase(s)) {
            return 50;
        }
        return 20;
    }
    
    private static OutputChoice computeOutputChoice(final String s, final boolean b) {
        if ("System.err".equalsIgnoreCase(s)) {
            if (b) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_ERR);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
        }
        else if ("System.out".equalsIgnoreCase(s)) {
            if (b) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_OUT);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_OUT);
        }
        else {
            try {
                return new OutputChoice(new PrintStream(new FileOutputStream(s)));
            }
            catch (FileNotFoundException ex) {
                Util.report("Could not open [" + s + "]. Defaulting to System.err", ex);
                return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
            }
        }
    }
    
    static {
        SimpleLoggerConfiguration.DEFAULT_LOG_LEVEL_DEFAULT = 20;
        DATE_TIME_FORMAT_STR_DEFAULT = null;
        SimpleLoggerConfiguration.dateTimeFormatStr = SimpleLoggerConfiguration.DATE_TIME_FORMAT_STR_DEFAULT;
        SimpleLoggerConfiguration.LOG_FILE_DEFAULT = "System.err";
    }
}
