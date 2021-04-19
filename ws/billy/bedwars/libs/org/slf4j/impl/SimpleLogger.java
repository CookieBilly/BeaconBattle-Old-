

package ws.billy.bedwars.libs.org.slf4j.impl;

import ws.billy.bedwars.libs.org.slf4j.event.LoggingEvent;
import ws.billy.bedwars.libs.org.slf4j.helpers.FormattingTuple;
import ws.billy.bedwars.libs.org.slf4j.helpers.MessageFormatter;
import java.util.Date;
import java.io.PrintStream;
import ws.billy.bedwars.libs.org.slf4j.helpers.MarkerIgnoringBase;

public class SimpleLogger extends MarkerIgnoringBase
{
    private static final long serialVersionUID = -632788891211436180L;
    private static long START_TIME;
    protected static final int LOG_LEVEL_TRACE = 0;
    protected static final int LOG_LEVEL_DEBUG = 10;
    protected static final int LOG_LEVEL_INFO = 20;
    protected static final int LOG_LEVEL_WARN = 30;
    protected static final int LOG_LEVEL_ERROR = 40;
    protected static final int LOG_LEVEL_OFF = 50;
    private static boolean INITIALIZED;
    static SimpleLoggerConfiguration CONFIG_PARAMS;
    protected int currentLogLevel;
    private transient String shortLogName;
    public static final String SYSTEM_PREFIX = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.";
    public static final String LOG_KEY_PREFIX = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.log.";
    public static final String CACHE_OUTPUT_STREAM_STRING_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.cacheOutputStream";
    public static final String WARN_LEVEL_STRING_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.warnLevelString";
    public static final String LEVEL_IN_BRACKETS_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.levelInBrackets";
    public static final String LOG_FILE_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.logFile";
    public static final String SHOW_SHORT_LOG_NAME_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showShortLogName";
    public static final String SHOW_LOG_NAME_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showLogName";
    public static final String SHOW_THREAD_NAME_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showThreadName";
    public static final String DATE_TIME_FORMAT_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.dateTimeFormat";
    public static final String SHOW_DATE_TIME_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.showDateTime";
    public static final String DEFAULT_LOG_LEVEL_KEY = "ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.defaultLogLevel";
    
    static void lazyInit() {
        if (SimpleLogger.INITIALIZED) {
            return;
        }
        SimpleLogger.INITIALIZED = true;
        init();
    }
    
    static void init() {
        (SimpleLogger.CONFIG_PARAMS = new SimpleLoggerConfiguration()).init();
    }
    
    SimpleLogger(final String name) {
        this.currentLogLevel = 20;
        this.shortLogName = null;
        this.name = name;
        final String recursivelyComputeLevelString = this.recursivelyComputeLevelString();
        if (recursivelyComputeLevelString != null) {
            this.currentLogLevel = SimpleLoggerConfiguration.stringToLevel(recursivelyComputeLevelString);
        }
        else {
            this.currentLogLevel = SimpleLogger.CONFIG_PARAMS.defaultLogLevel;
        }
    }
    
    String recursivelyComputeLevelString() {
        String s = this.name;
        String stringProperty = null;
        for (int endIndex = s.length(); stringProperty == null && endIndex > -1; stringProperty = SimpleLogger.CONFIG_PARAMS.getStringProperty("ws.billy.BeaconBattle.libs.org.slf4j.simpleLogger.log." + s, null), endIndex = String.valueOf(s).lastIndexOf(".")) {
            s = s.substring(0, endIndex);
        }
        return stringProperty;
    }
    
    private void log(final int n, final String str, final Throwable t) {
        if (!this.isLevelEnabled(n)) {
            return;
        }
        final StringBuilder sb = new StringBuilder(32);
        if (SimpleLogger.CONFIG_PARAMS.showDateTime) {
            if (SimpleLogger.CONFIG_PARAMS.dateFormatter != null) {
                sb.append(this.getFormattedDate());
                sb.append(' ');
            }
            else {
                sb.append(System.currentTimeMillis() - SimpleLogger.START_TIME);
                sb.append(' ');
            }
        }
        if (SimpleLogger.CONFIG_PARAMS.showThreadName) {
            sb.append('[');
            sb.append(Thread.currentThread().getName());
            sb.append("] ");
        }
        if (SimpleLogger.CONFIG_PARAMS.levelInBrackets) {
            sb.append('[');
        }
        sb.append(this.renderLevel(n));
        if (SimpleLogger.CONFIG_PARAMS.levelInBrackets) {
            sb.append(']');
        }
        sb.append(' ');
        if (SimpleLogger.CONFIG_PARAMS.showShortLogName) {
            if (this.shortLogName == null) {
                this.shortLogName = this.computeShortName();
            }
            sb.append(String.valueOf(this.shortLogName)).append(" - ");
        }
        else if (SimpleLogger.CONFIG_PARAMS.showLogName) {
            sb.append(String.valueOf(this.name)).append(" - ");
        }
        sb.append(str);
        this.write(sb, t);
    }
    
    protected String renderLevel(final int i) {
        switch (i) {
            case 0: {
                return "TRACE";
            }
            case 10: {
                return "DEBUG";
            }
            case 20: {
                return "INFO";
            }
            case 30: {
                return SimpleLogger.CONFIG_PARAMS.warnLevelString;
            }
            case 40: {
                return "ERROR";
            }
            default: {
                throw new IllegalStateException("Unrecognized level [" + i + "]");
            }
        }
    }
    
    void write(final StringBuilder sb, final Throwable t) {
        final PrintStream targetPrintStream = SimpleLogger.CONFIG_PARAMS.outputChoice.getTargetPrintStream();
        targetPrintStream.println(sb.toString());
        this.writeThrowable(t, targetPrintStream);
        targetPrintStream.flush();
    }
    
    protected void writeThrowable(final Throwable t, final PrintStream s) {
        if (t != null) {
            t.printStackTrace(s);
        }
    }
    
    private String getFormattedDate() {
        final Date date = new Date();
        final String format;
        synchronized (SimpleLogger.CONFIG_PARAMS.dateFormatter) {
            format = SimpleLogger.CONFIG_PARAMS.dateFormatter.format(date);
        }
        return format;
    }
    
    private String computeShortName() {
        return this.name.substring(this.name.lastIndexOf(".") + 1);
    }
    
    private void formatAndLog(final int n, final String s, final Object o, final Object o2) {
        if (!this.isLevelEnabled(n)) {
            return;
        }
        final FormattingTuple format = MessageFormatter.format(s, o, o2);
        this.log(n, format.getMessage(), format.getThrowable());
    }
    
    private void formatAndLog(final int n, final String s, final Object... array) {
        if (!this.isLevelEnabled(n)) {
            return;
        }
        final FormattingTuple arrayFormat = MessageFormatter.arrayFormat(s, array);
        this.log(n, arrayFormat.getMessage(), arrayFormat.getThrowable());
    }
    
    protected boolean isLevelEnabled(final int n) {
        return n >= this.currentLogLevel;
    }
    
    public boolean isTraceEnabled() {
        return this.isLevelEnabled(0);
    }
    
    public void trace(final String s) {
        this.log(0, s, null);
    }
    
    public void trace(final String s, final Object o) {
        this.formatAndLog(0, s, o, null);
    }
    
    public void trace(final String s, final Object o, final Object o2) {
        this.formatAndLog(0, s, o, o2);
    }
    
    public void trace(final String s, final Object... array) {
        this.formatAndLog(0, s, array);
    }
    
    public void trace(final String s, final Throwable t) {
        this.log(0, s, t);
    }
    
    public boolean isDebugEnabled() {
        return this.isLevelEnabled(10);
    }
    
    public void debug(final String s) {
        this.log(10, s, null);
    }
    
    public void debug(final String s, final Object o) {
        this.formatAndLog(10, s, o, null);
    }
    
    public void debug(final String s, final Object o, final Object o2) {
        this.formatAndLog(10, s, o, o2);
    }
    
    public void debug(final String s, final Object... array) {
        this.formatAndLog(10, s, array);
    }
    
    public void debug(final String s, final Throwable t) {
        this.log(10, s, t);
    }
    
    public boolean isInfoEnabled() {
        return this.isLevelEnabled(20);
    }
    
    public void info(final String s) {
        this.log(20, s, null);
    }
    
    public void info(final String s, final Object o) {
        this.formatAndLog(20, s, o, null);
    }
    
    public void info(final String s, final Object o, final Object o2) {
        this.formatAndLog(20, s, o, o2);
    }
    
    public void info(final String s, final Object... array) {
        this.formatAndLog(20, s, array);
    }
    
    public void info(final String s, final Throwable t) {
        this.log(20, s, t);
    }
    
    public boolean isWarnEnabled() {
        return this.isLevelEnabled(30);
    }
    
    public void warn(final String s) {
        this.log(30, s, null);
    }
    
    public void warn(final String s, final Object o) {
        this.formatAndLog(30, s, o, null);
    }
    
    public void warn(final String s, final Object o, final Object o2) {
        this.formatAndLog(30, s, o, o2);
    }
    
    public void warn(final String s, final Object... array) {
        this.formatAndLog(30, s, array);
    }
    
    public void warn(final String s, final Throwable t) {
        this.log(30, s, t);
    }
    
    public boolean isErrorEnabled() {
        return this.isLevelEnabled(40);
    }
    
    public void error(final String s) {
        this.log(40, s, null);
    }
    
    public void error(final String s, final Object o) {
        this.formatAndLog(40, s, o, null);
    }
    
    public void error(final String s, final Object o, final Object o2) {
        this.formatAndLog(40, s, o, o2);
    }
    
    public void error(final String s, final Object... array) {
        this.formatAndLog(40, s, array);
    }
    
    public void error(final String s, final Throwable t) {
        this.log(40, s, t);
    }
    
    public void log(final LoggingEvent loggingEvent) {
        final int int1 = loggingEvent.getLevel().toInt();
        if (!this.isLevelEnabled(int1)) {
            return;
        }
        this.log(int1, MessageFormatter.arrayFormat(loggingEvent.getMessage(), loggingEvent.getArgumentArray(), loggingEvent.getThrowable()).getMessage(), loggingEvent.getThrowable());
    }
    
    static {
        SimpleLogger.START_TIME = System.currentTimeMillis();
        SimpleLogger.INITIALIZED = false;
        SimpleLogger.CONFIG_PARAMS = null;
    }
}
