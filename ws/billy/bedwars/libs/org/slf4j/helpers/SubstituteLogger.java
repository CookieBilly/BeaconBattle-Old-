

package ws.billy.bedwars.libs.org.slf4j.helpers;

import java.lang.reflect.InvocationTargetException;
import ws.billy.bedwars.libs.org.slf4j.event.LoggingEvent;
import ws.billy.bedwars.libs.org.slf4j.Marker;
import ws.billy.bedwars.libs.org.slf4j.event.SubstituteLoggingEvent;
import java.util.Queue;
import ws.billy.bedwars.libs.org.slf4j.event.EventRecodingLogger;
import java.lang.reflect.Method;
import ws.billy.bedwars.libs.org.slf4j.Logger;

public class SubstituteLogger implements Logger
{
    private final String name;
    private volatile Logger _delegate;
    private Boolean delegateEventAware;
    private Method logMethodCache;
    private EventRecodingLogger eventRecodingLogger;
    private Queue<SubstituteLoggingEvent> eventQueue;
    private final boolean createdPostInitialization;
    
    public SubstituteLogger(final String name, final Queue<SubstituteLoggingEvent> eventQueue, final boolean createdPostInitialization) {
        this.name = name;
        this.eventQueue = eventQueue;
        this.createdPostInitialization = createdPostInitialization;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isTraceEnabled() {
        return this.delegate().isTraceEnabled();
    }
    
    public void trace(final String s) {
        this.delegate().trace(s);
    }
    
    public void trace(final String s, final Object o) {
        this.delegate().trace(s, o);
    }
    
    public void trace(final String s, final Object o, final Object o2) {
        this.delegate().trace(s, o, o2);
    }
    
    public void trace(final String s, final Object... array) {
        this.delegate().trace(s, array);
    }
    
    public void trace(final String s, final Throwable t) {
        this.delegate().trace(s, t);
    }
    
    public boolean isTraceEnabled(final Marker marker) {
        return this.delegate().isTraceEnabled(marker);
    }
    
    public void trace(final Marker marker, final String s) {
        this.delegate().trace(marker, s);
    }
    
    public void trace(final Marker marker, final String s, final Object o) {
        this.delegate().trace(marker, s, o);
    }
    
    public void trace(final Marker marker, final String s, final Object o, final Object o2) {
        this.delegate().trace(marker, s, o, o2);
    }
    
    public void trace(final Marker marker, final String s, final Object... array) {
        this.delegate().trace(marker, s, array);
    }
    
    public void trace(final Marker marker, final String s, final Throwable t) {
        this.delegate().trace(marker, s, t);
    }
    
    public boolean isDebugEnabled() {
        return this.delegate().isDebugEnabled();
    }
    
    public void debug(final String s) {
        this.delegate().debug(s);
    }
    
    public void debug(final String s, final Object o) {
        this.delegate().debug(s, o);
    }
    
    public void debug(final String s, final Object o, final Object o2) {
        this.delegate().debug(s, o, o2);
    }
    
    public void debug(final String s, final Object... array) {
        this.delegate().debug(s, array);
    }
    
    public void debug(final String s, final Throwable t) {
        this.delegate().debug(s, t);
    }
    
    public boolean isDebugEnabled(final Marker marker) {
        return this.delegate().isDebugEnabled(marker);
    }
    
    public void debug(final Marker marker, final String s) {
        this.delegate().debug(marker, s);
    }
    
    public void debug(final Marker marker, final String s, final Object o) {
        this.delegate().debug(marker, s, o);
    }
    
    public void debug(final Marker marker, final String s, final Object o, final Object o2) {
        this.delegate().debug(marker, s, o, o2);
    }
    
    public void debug(final Marker marker, final String s, final Object... array) {
        this.delegate().debug(marker, s, array);
    }
    
    public void debug(final Marker marker, final String s, final Throwable t) {
        this.delegate().debug(marker, s, t);
    }
    
    public boolean isInfoEnabled() {
        return this.delegate().isInfoEnabled();
    }
    
    public void info(final String s) {
        this.delegate().info(s);
    }
    
    public void info(final String s, final Object o) {
        this.delegate().info(s, o);
    }
    
    public void info(final String s, final Object o, final Object o2) {
        this.delegate().info(s, o, o2);
    }
    
    public void info(final String s, final Object... array) {
        this.delegate().info(s, array);
    }
    
    public void info(final String s, final Throwable t) {
        this.delegate().info(s, t);
    }
    
    public boolean isInfoEnabled(final Marker marker) {
        return this.delegate().isInfoEnabled(marker);
    }
    
    public void info(final Marker marker, final String s) {
        this.delegate().info(marker, s);
    }
    
    public void info(final Marker marker, final String s, final Object o) {
        this.delegate().info(marker, s, o);
    }
    
    public void info(final Marker marker, final String s, final Object o, final Object o2) {
        this.delegate().info(marker, s, o, o2);
    }
    
    public void info(final Marker marker, final String s, final Object... array) {
        this.delegate().info(marker, s, array);
    }
    
    public void info(final Marker marker, final String s, final Throwable t) {
        this.delegate().info(marker, s, t);
    }
    
    public boolean isWarnEnabled() {
        return this.delegate().isWarnEnabled();
    }
    
    public void warn(final String s) {
        this.delegate().warn(s);
    }
    
    public void warn(final String s, final Object o) {
        this.delegate().warn(s, o);
    }
    
    public void warn(final String s, final Object o, final Object o2) {
        this.delegate().warn(s, o, o2);
    }
    
    public void warn(final String s, final Object... array) {
        this.delegate().warn(s, array);
    }
    
    public void warn(final String s, final Throwable t) {
        this.delegate().warn(s, t);
    }
    
    public boolean isWarnEnabled(final Marker marker) {
        return this.delegate().isWarnEnabled(marker);
    }
    
    public void warn(final Marker marker, final String s) {
        this.delegate().warn(marker, s);
    }
    
    public void warn(final Marker marker, final String s, final Object o) {
        this.delegate().warn(marker, s, o);
    }
    
    public void warn(final Marker marker, final String s, final Object o, final Object o2) {
        this.delegate().warn(marker, s, o, o2);
    }
    
    public void warn(final Marker marker, final String s, final Object... array) {
        this.delegate().warn(marker, s, array);
    }
    
    public void warn(final Marker marker, final String s, final Throwable t) {
        this.delegate().warn(marker, s, t);
    }
    
    public boolean isErrorEnabled() {
        return this.delegate().isErrorEnabled();
    }
    
    public void error(final String s) {
        this.delegate().error(s);
    }
    
    public void error(final String s, final Object o) {
        this.delegate().error(s, o);
    }
    
    public void error(final String s, final Object o, final Object o2) {
        this.delegate().error(s, o, o2);
    }
    
    public void error(final String s, final Object... array) {
        this.delegate().error(s, array);
    }
    
    public void error(final String s, final Throwable t) {
        this.delegate().error(s, t);
    }
    
    public boolean isErrorEnabled(final Marker marker) {
        return this.delegate().isErrorEnabled(marker);
    }
    
    public void error(final Marker marker, final String s) {
        this.delegate().error(marker, s);
    }
    
    public void error(final Marker marker, final String s, final Object o) {
        this.delegate().error(marker, s, o);
    }
    
    public void error(final Marker marker, final String s, final Object o, final Object o2) {
        this.delegate().error(marker, s, o, o2);
    }
    
    public void error(final Marker marker, final String s, final Object... array) {
        this.delegate().error(marker, s, array);
    }
    
    public void error(final Marker marker, final String s, final Throwable t) {
        this.delegate().error(marker, s, t);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass() && this.name.equals(((SubstituteLogger)o).name));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    Logger delegate() {
        if (this._delegate != null) {
            return this._delegate;
        }
        if (this.createdPostInitialization) {
            return NOPLogger.NOP_LOGGER;
        }
        return this.getEventRecordingLogger();
    }
    
    private Logger getEventRecordingLogger() {
        if (this.eventRecodingLogger == null) {
            this.eventRecodingLogger = new EventRecodingLogger(this, this.eventQueue);
        }
        return this.eventRecodingLogger;
    }
    
    public void setDelegate(final Logger delegate) {
        this._delegate = delegate;
    }
    
    public boolean isDelegateEventAware() {
        if (this.delegateEventAware != null) {
            return this.delegateEventAware;
        }
        try {
            this.logMethodCache = this._delegate.getClass().getMethod("log", LoggingEvent.class);
            this.delegateEventAware = Boolean.TRUE;
        }
        catch (NoSuchMethodException ex) {
            this.delegateEventAware = Boolean.FALSE;
        }
        return this.delegateEventAware;
    }
    
    public void log(final LoggingEvent loggingEvent) {
        if (this.isDelegateEventAware()) {
            try {
                this.logMethodCache.invoke(this._delegate, loggingEvent);
            }
            catch (IllegalAccessException ex) {}
            catch (IllegalArgumentException ex2) {}
            catch (InvocationTargetException ex3) {}
        }
    }
    
    public boolean isDelegateNull() {
        return this._delegate == null;
    }
    
    public boolean isDelegateNOP() {
        return this._delegate instanceof NOPLogger;
    }
}
