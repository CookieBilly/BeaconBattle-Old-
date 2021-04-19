

package ws.billy.bedwars.libs.org.slf4j.event;

import ws.billy.bedwars.libs.org.slf4j.helpers.MessageFormatter;
import ws.billy.bedwars.libs.org.slf4j.Marker;
import java.util.Queue;
import ws.billy.bedwars.libs.org.slf4j.helpers.SubstituteLogger;
import ws.billy.bedwars.libs.org.slf4j.Logger;

public class EventRecodingLogger implements Logger
{
    String name;
    SubstituteLogger logger;
    Queue<SubstituteLoggingEvent> eventQueue;
    static final boolean RECORD_ALL_EVENTS = true;
    
    public EventRecodingLogger(final SubstituteLogger logger, final Queue<SubstituteLoggingEvent> eventQueue) {
        this.logger = logger;
        this.name = logger.getName();
        this.eventQueue = eventQueue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isTraceEnabled() {
        return true;
    }
    
    public void trace(final String s) {
        this.recordEvent_0Args(Level.TRACE, null, s, null);
    }
    
    public void trace(final String s, final Object o) {
        this.recordEvent_1Args(Level.TRACE, null, s, o);
    }
    
    public void trace(final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.TRACE, null, s, o, o2);
    }
    
    public void trace(final String s, final Object... array) {
        this.recordEventArgArray(Level.TRACE, null, s, array);
    }
    
    public void trace(final String s, final Throwable t) {
        this.recordEvent_0Args(Level.TRACE, null, s, t);
    }
    
    public boolean isTraceEnabled(final Marker marker) {
        return true;
    }
    
    public void trace(final Marker marker, final String s) {
        this.recordEvent_0Args(Level.TRACE, marker, s, null);
    }
    
    public void trace(final Marker marker, final String s, final Object o) {
        this.recordEvent_1Args(Level.TRACE, marker, s, o);
    }
    
    public void trace(final Marker marker, final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.TRACE, marker, s, o, o2);
    }
    
    public void trace(final Marker marker, final String s, final Object... array) {
        this.recordEventArgArray(Level.TRACE, marker, s, array);
    }
    
    public void trace(final Marker marker, final String s, final Throwable t) {
        this.recordEvent_0Args(Level.TRACE, marker, s, t);
    }
    
    public boolean isDebugEnabled() {
        return true;
    }
    
    public void debug(final String s) {
        this.recordEvent_0Args(Level.DEBUG, null, s, null);
    }
    
    public void debug(final String s, final Object o) {
        this.recordEvent_1Args(Level.DEBUG, null, s, o);
    }
    
    public void debug(final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.DEBUG, null, s, o, o2);
    }
    
    public void debug(final String s, final Object... array) {
        this.recordEventArgArray(Level.DEBUG, null, s, array);
    }
    
    public void debug(final String s, final Throwable t) {
        this.recordEvent_0Args(Level.DEBUG, null, s, t);
    }
    
    public boolean isDebugEnabled(final Marker marker) {
        return true;
    }
    
    public void debug(final Marker marker, final String s) {
        this.recordEvent_0Args(Level.DEBUG, marker, s, null);
    }
    
    public void debug(final Marker marker, final String s, final Object o) {
        this.recordEvent_1Args(Level.DEBUG, marker, s, o);
    }
    
    public void debug(final Marker marker, final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.DEBUG, marker, s, o, o2);
    }
    
    public void debug(final Marker marker, final String s, final Object... array) {
        this.recordEventArgArray(Level.DEBUG, marker, s, array);
    }
    
    public void debug(final Marker marker, final String s, final Throwable t) {
        this.recordEvent_0Args(Level.DEBUG, marker, s, t);
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
    
    public void info(final String s) {
        this.recordEvent_0Args(Level.INFO, null, s, null);
    }
    
    public void info(final String s, final Object o) {
        this.recordEvent_1Args(Level.INFO, null, s, o);
    }
    
    public void info(final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.INFO, null, s, o, o2);
    }
    
    public void info(final String s, final Object... array) {
        this.recordEventArgArray(Level.INFO, null, s, array);
    }
    
    public void info(final String s, final Throwable t) {
        this.recordEvent_0Args(Level.INFO, null, s, t);
    }
    
    public boolean isInfoEnabled(final Marker marker) {
        return true;
    }
    
    public void info(final Marker marker, final String s) {
        this.recordEvent_0Args(Level.INFO, marker, s, null);
    }
    
    public void info(final Marker marker, final String s, final Object o) {
        this.recordEvent_1Args(Level.INFO, marker, s, o);
    }
    
    public void info(final Marker marker, final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.INFO, marker, s, o, o2);
    }
    
    public void info(final Marker marker, final String s, final Object... array) {
        this.recordEventArgArray(Level.INFO, marker, s, array);
    }
    
    public void info(final Marker marker, final String s, final Throwable t) {
        this.recordEvent_0Args(Level.INFO, marker, s, t);
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public void warn(final String s) {
        this.recordEvent_0Args(Level.WARN, null, s, null);
    }
    
    public void warn(final String s, final Object o) {
        this.recordEvent_1Args(Level.WARN, null, s, o);
    }
    
    public void warn(final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.WARN, null, s, o, o2);
    }
    
    public void warn(final String s, final Object... array) {
        this.recordEventArgArray(Level.WARN, null, s, array);
    }
    
    public void warn(final String s, final Throwable t) {
        this.recordEvent_0Args(Level.WARN, null, s, t);
    }
    
    public boolean isWarnEnabled(final Marker marker) {
        return true;
    }
    
    public void warn(final Marker marker, final String s) {
        this.recordEvent_0Args(Level.WARN, marker, s, null);
    }
    
    public void warn(final Marker marker, final String s, final Object o) {
        this.recordEvent_1Args(Level.WARN, marker, s, o);
    }
    
    public void warn(final Marker marker, final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.WARN, marker, s, o, o2);
    }
    
    public void warn(final Marker marker, final String s, final Object... array) {
        this.recordEventArgArray(Level.WARN, marker, s, array);
    }
    
    public void warn(final Marker marker, final String s, final Throwable t) {
        this.recordEvent_0Args(Level.WARN, marker, s, t);
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    public void error(final String s) {
        this.recordEvent_0Args(Level.ERROR, null, s, null);
    }
    
    public void error(final String s, final Object o) {
        this.recordEvent_1Args(Level.ERROR, null, s, o);
    }
    
    public void error(final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.ERROR, null, s, o, o2);
    }
    
    public void error(final String s, final Object... array) {
        this.recordEventArgArray(Level.ERROR, null, s, array);
    }
    
    public void error(final String s, final Throwable t) {
        this.recordEvent_0Args(Level.ERROR, null, s, t);
    }
    
    public boolean isErrorEnabled(final Marker marker) {
        return true;
    }
    
    public void error(final Marker marker, final String s) {
        this.recordEvent_0Args(Level.ERROR, marker, s, null);
    }
    
    public void error(final Marker marker, final String s, final Object o) {
        this.recordEvent_1Args(Level.ERROR, marker, s, o);
    }
    
    public void error(final Marker marker, final String s, final Object o, final Object o2) {
        this.recordEvent2Args(Level.ERROR, marker, s, o, o2);
    }
    
    public void error(final Marker marker, final String s, final Object... array) {
        this.recordEventArgArray(Level.ERROR, marker, s, array);
    }
    
    public void error(final Marker marker, final String s, final Throwable t) {
        this.recordEvent_0Args(Level.ERROR, marker, s, t);
    }
    
    private void recordEvent_0Args(final Level level, final Marker marker, final String s, final Throwable t) {
        this.recordEvent(level, marker, s, null, t);
    }
    
    private void recordEvent_1Args(final Level level, final Marker marker, final String s, final Object o) {
        this.recordEvent(level, marker, s, new Object[] { o }, null);
    }
    
    private void recordEvent2Args(final Level level, final Marker marker, final String s, final Object o, final Object o2) {
        if (o2 instanceof Throwable) {
            this.recordEvent(level, marker, s, new Object[] { o }, (Throwable)o2);
        }
        else {
            this.recordEvent(level, marker, s, new Object[] { o, o2 }, null);
        }
    }
    
    private void recordEventArgArray(final Level level, final Marker marker, final String s, final Object[] array) {
        final Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(array);
        if (throwableCandidate != null) {
            this.recordEvent(level, marker, s, MessageFormatter.trimmedCopy(array), throwableCandidate);
        }
        else {
            this.recordEvent(level, marker, s, array, null);
        }
    }
    
    private void recordEvent(final Level level, final Marker marker, final String message, final Object[] argumentArray, final Throwable throwable) {
        final SubstituteLoggingEvent substituteLoggingEvent = new SubstituteLoggingEvent();
        substituteLoggingEvent.setTimeStamp(System.currentTimeMillis());
        substituteLoggingEvent.setLevel(level);
        substituteLoggingEvent.setLogger(this.logger);
        substituteLoggingEvent.setLoggerName(this.name);
        substituteLoggingEvent.setMarker(marker);
        substituteLoggingEvent.setMessage(message);
        substituteLoggingEvent.setThreadName(Thread.currentThread().getName());
        substituteLoggingEvent.setArgumentArray(argumentArray);
        substituteLoggingEvent.setThrowable(throwable);
        this.eventQueue.add(substituteLoggingEvent);
    }
}
