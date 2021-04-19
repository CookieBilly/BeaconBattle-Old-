

package ws.billy.bedwars.libs.org.slf4j;

import java.util.Enumeration;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import ws.billy.bedwars.libs.org.slf4j.event.SubstituteLoggingEvent;
import java.util.ArrayList;
import java.util.Iterator;
import ws.billy.bedwars.libs.org.slf4j.helpers.SubstituteLogger;
import java.net.URL;
import java.util.Set;
import ws.billy.bedwars.libs.org.slf4j.helpers.Util;
import ws.billy.bedwars.libs.org.slf4j.impl.StaticLoggerBinder;
import ws.billy.bedwars.libs.org.slf4j.helpers.NOPLoggerFactory;
import ws.billy.bedwars.libs.org.slf4j.helpers.SubstituteLoggerFactory;

public final class LoggerFactory
{
    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
    static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
    static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
    static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
    static final String LOGGER_NAME_MISMATCH_URL = "http://www.slf4j.org/codes.html#loggerNameMismatch";
    static final String REPLAY_URL = "http://www.slf4j.org/codes.html#replay";
    static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final int UNINITIALIZED = 0;
    static final int ONGOING_INITIALIZATION = 1;
    static final int FAILED_INITIALIZATION = 2;
    static final int SUCCESSFUL_INITIALIZATION = 3;
    static final int NOP_FALLBACK_INITIALIZATION = 4;
    static volatile int INITIALIZATION_STATE;
    static final SubstituteLoggerFactory SUBST_FACTORY;
    static final NOPLoggerFactory NOP_FALLBACK_FACTORY;
    static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
    static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
    static boolean DETECT_LOGGER_NAME_MISMATCH;
    private static final String[] API_COMPATIBILITY_LIST;
    private static String STATIC_LOGGER_BINDER_PATH;
    
    private LoggerFactory() {
    }
    
    static void reset() {
        LoggerFactory.INITIALIZATION_STATE = 0;
    }
    
    private static final void performInitialization() {
        bind();
        if (LoggerFactory.INITIALIZATION_STATE == 3) {
            versionSanityCheck();
        }
    }

    private static final void bind() {
        try {
            Set<URL> possibleStaticLoggerBinderPathSet = null;
            if (!isAndroid()) {
                possibleStaticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
                reportMultipleBindingAmbiguity(possibleStaticLoggerBinderPathSet);
            }
            StaticLoggerBinder.getSingleton();
            LoggerFactory.INITIALIZATION_STATE = 3;
            reportActualBinding(possibleStaticLoggerBinderPathSet);
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            if (messageContainsOrgSlf4jImplStaticLoggerBinder(noClassDefFoundError.getMessage())) {
                LoggerFactory.INITIALIZATION_STATE = 4;
                Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
                Util.report("Defaulting to no-operation (NOP) logger implementation");
                Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
                return;
            }
            failedBinding(noClassDefFoundError);
            throw noClassDefFoundError;
        }
        catch (NoSuchMethodError noSuchMethodError) {
            final String message = noSuchMethodError.getMessage();
            if (message != null && message.contains("StaticLoggerBinder.getSingleton()")) {
                LoggerFactory.INITIALIZATION_STATE = 2;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x.");
            }
            throw noSuchMethodError;
        }
        catch (Exception cause) {
            failedBinding(cause);
            throw new IllegalStateException("Unexpected initialization failure", cause);
        }
        finally {
            postBindCleanUp();
        }
    }
    
    private static void postBindCleanUp() {
        fixSubstituteLoggers();
        replayEvents();
        LoggerFactory.SUBST_FACTORY.clear();
    }
    
    private static void fixSubstituteLoggers() {
        synchronized (LoggerFactory.SUBST_FACTORY) {
            LoggerFactory.SUBST_FACTORY.postInitialization();
            for (final SubstituteLogger substituteLogger : LoggerFactory.SUBST_FACTORY.getLoggers()) {
                substituteLogger.setDelegate(getLogger(substituteLogger.getName()));
            }
        }
    }
    
    static void failedBinding(final Throwable t) {
        LoggerFactory.INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }
    
    private static void replayEvents() {
        final LinkedBlockingQueue<SubstituteLoggingEvent> eventQueue = LoggerFactory.SUBST_FACTORY.getEventQueue();
        final int size = eventQueue.size();
        int n = 0;
        final ArrayList<SubstituteLoggingEvent> c = new ArrayList<SubstituteLoggingEvent>(128);
        while (eventQueue.drainTo(c, 128) != 0) {
            for (final SubstituteLoggingEvent substituteLoggingEvent : c) {
                replaySingleEvent(substituteLoggingEvent);
                if (n++ == 0) {
                    emitReplayOrSubstituionWarning(substituteLoggingEvent, size);
                }
            }
            c.clear();
        }
    }
    
    private static void emitReplayOrSubstituionWarning(final SubstituteLoggingEvent substituteLoggingEvent, final int n) {
        if (substituteLoggingEvent.getLogger().isDelegateEventAware()) {
            emitReplayWarning(n);
        }
        else if (!substituteLoggingEvent.getLogger().isDelegateNOP()) {
            emitSubstitutionWarning();
        }
    }
    
    private static void replaySingleEvent(final SubstituteLoggingEvent substituteLoggingEvent) {
        if (substituteLoggingEvent == null) {
            return;
        }
        final SubstituteLogger logger = substituteLoggingEvent.getLogger();
        final String name = logger.getName();
        if (logger.isDelegateNull()) {
            throw new IllegalStateException("Delegate logger cannot be null at this state.");
        }
        if (!logger.isDelegateNOP()) {
            if (logger.isDelegateEventAware()) {
                logger.log(substituteLoggingEvent);
            }
            else {
                Util.report(name);
            }
        }
    }
    
    private static void emitSubstitutionWarning() {
        Util.report("The following set of substitute loggers may have been accessed");
        Util.report("during the initialization phase. Logging calls during this");
        Util.report("phase were not honored. However, subsequent logging calls to these");
        Util.report("loggers will work as normally expected.");
        Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
    }
    
    private static void emitReplayWarning(final int i) {
        Util.report("A number (" + i + ") of logging calls during the initialization phase have been intercepted and are");
        Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
        Util.report("See also http://www.slf4j.org/codes.html#replay");
    }
    
    private static final void versionSanityCheck() {
        try {
            final String requested_API_VERSION = StaticLoggerBinder.REQUESTED_API_VERSION;
            boolean b = false;
            final String[] api_COMPATIBILITY_LIST = LoggerFactory.API_COMPATIBILITY_LIST;
            for (int length = api_COMPATIBILITY_LIST.length, i = 0; i < length; ++i) {
                if (requested_API_VERSION.startsWith(api_COMPATIBILITY_LIST[i])) {
                    b = true;
                }
            }
            if (!b) {
                Util.report("The requested version " + requested_API_VERSION + " by your slf4j binding is not compatible with " + Arrays.asList(LoggerFactory.API_COMPATIBILITY_LIST).toString());
                Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
            }
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        catch (Throwable t) {
            Util.report("Unexpected problem occured during version sanity check", t);
        }
    }
    
    static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        final LinkedHashSet<URL> set = new LinkedHashSet<URL>();
        try {
            final ClassLoader classLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> enumeration;
            if (classLoader == null) {
                enumeration = ClassLoader.getSystemResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            else {
                enumeration = classLoader.getResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            while (enumeration.hasMoreElements()) {
                set.add(enumeration.nextElement());
            }
        }
        catch (IOException ex) {
            Util.report("Error getting resources from path", ex);
        }
        return set;
    }
    
    private static boolean isAmbiguousStaticLoggerBinderPathSet(final Set<URL> set) {
        return set.size() > 1;
    }
    
    private static void reportMultipleBindingAmbiguity(final Set<URL> set) {
        if (isAmbiguousStaticLoggerBinderPathSet(set)) {
            Util.report("Class path contains multiple SLF4J bindings.");
            final Iterator<URL> iterator = set.iterator();
            while (iterator.hasNext()) {
                Util.report("Found binding in [" + iterator.next() + "]");
            }
            Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
        }
    }
    
    private static boolean isAndroid() {
        final String safeGetSystemProperty = Util.safeGetSystemProperty("java.vendor.url");
        return safeGetSystemProperty != null && safeGetSystemProperty.toLowerCase().contains("android");
    }
    
    private static void reportActualBinding(final Set<URL> set) {
        if (set != null && isAmbiguousStaticLoggerBinderPathSet(set)) {
            Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
        }
    }
    
    public static Logger getLogger(final String s) {
        return getILoggerFactory().getLogger(s);
    }
    
    public static Logger getLogger(final Class<?> clazz) {
        final Logger logger = getLogger(clazz.getName());
        if (LoggerFactory.DETECT_LOGGER_NAME_MISMATCH) {
            final Class<?> callingClass = Util.getCallingClass();
            if (callingClass != null && nonMatchingClasses(clazz, callingClass)) {
                Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", logger.getName(), callingClass.getName()));
                Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
            }
        }
        return logger;
    }
    
    private static boolean nonMatchingClasses(final Class<?> clazz, final Class<?> clazz2) {
        return !clazz2.isAssignableFrom(clazz);
    }
    
    public static ILoggerFactory getILoggerFactory() {
        if (LoggerFactory.INITIALIZATION_STATE == 0) {
            synchronized (LoggerFactory.class) {
                if (LoggerFactory.INITIALIZATION_STATE == 0) {
                    LoggerFactory.INITIALIZATION_STATE = 1;
                    performInitialization();
                }
            }
        }
        switch (LoggerFactory.INITIALIZATION_STATE) {
            case 3: {
                return StaticLoggerBinder.getSingleton().getLoggerFactory();
            }
            case 4: {
                return LoggerFactory.NOP_FALLBACK_FACTORY;
            }
            case 2: {
                throw new IllegalStateException("org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
            }
            case 1: {
                return LoggerFactory.SUBST_FACTORY;
            }
            default: {
                throw new IllegalStateException("Unreachable code");
            }
        }
    }
    
    static {
        LoggerFactory.INITIALIZATION_STATE = 0;
        SUBST_FACTORY = new SubstituteLoggerFactory();
        NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
        LoggerFactory.DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
        API_COMPATIBILITY_LIST = new String[] { "1.6", "1.7" };
    }
}
