

package ws.billy.bedwars.libs.org.slf4j.helpers;

import ws.billy.bedwars.libs.org.slf4j.Marker;
import ws.billy.bedwars.libs.org.slf4j.Logger;

public abstract class MarkerIgnoringBase extends NamedLoggerBase implements Logger
{
    private static final long serialVersionUID = 9044267456635152283L;
    
    public boolean isTraceEnabled(final Marker marker) {
        return this.isTraceEnabled();
    }
    
    public void trace(final Marker marker, final String s) {
        this.trace(s);
    }
    
    public void trace(final Marker marker, final String s, final Object o) {
        this.trace(s, o);
    }
    
    public void trace(final Marker marker, final String s, final Object o, final Object o2) {
        this.trace(s, o, o2);
    }
    
    public void trace(final Marker marker, final String s, final Object... array) {
        this.trace(s, array);
    }
    
    public void trace(final Marker marker, final String s, final Throwable t) {
        this.trace(s, t);
    }
    
    public boolean isDebugEnabled(final Marker marker) {
        return this.isDebugEnabled();
    }
    
    public void debug(final Marker marker, final String s) {
        this.debug(s);
    }
    
    public void debug(final Marker marker, final String s, final Object o) {
        this.debug(s, o);
    }
    
    public void debug(final Marker marker, final String s, final Object o, final Object o2) {
        this.debug(s, o, o2);
    }
    
    public void debug(final Marker marker, final String s, final Object... array) {
        this.debug(s, array);
    }
    
    public void debug(final Marker marker, final String s, final Throwable t) {
        this.debug(s, t);
    }
    
    public boolean isInfoEnabled(final Marker marker) {
        return this.isInfoEnabled();
    }
    
    public void info(final Marker marker, final String s) {
        this.info(s);
    }
    
    public void info(final Marker marker, final String s, final Object o) {
        this.info(s, o);
    }
    
    public void info(final Marker marker, final String s, final Object o, final Object o2) {
        this.info(s, o, o2);
    }
    
    public void info(final Marker marker, final String s, final Object... array) {
        this.info(s, array);
    }
    
    public void info(final Marker marker, final String s, final Throwable t) {
        this.info(s, t);
    }
    
    public boolean isWarnEnabled(final Marker marker) {
        return this.isWarnEnabled();
    }
    
    public void warn(final Marker marker, final String s) {
        this.warn(s);
    }
    
    public void warn(final Marker marker, final String s, final Object o) {
        this.warn(s, o);
    }
    
    public void warn(final Marker marker, final String s, final Object o, final Object o2) {
        this.warn(s, o, o2);
    }
    
    public void warn(final Marker marker, final String s, final Object... array) {
        this.warn(s, array);
    }
    
    public void warn(final Marker marker, final String s, final Throwable t) {
        this.warn(s, t);
    }
    
    public boolean isErrorEnabled(final Marker marker) {
        return this.isErrorEnabled();
    }
    
    public void error(final Marker marker, final String s) {
        this.error(s);
    }
    
    public void error(final Marker marker, final String s, final Object o) {
        this.error(s, o);
    }
    
    public void error(final Marker marker, final String s, final Object o, final Object o2) {
        this.error(s, o, o2);
    }
    
    public void error(final Marker marker, final String s, final Object... array) {
        this.error(s, array);
    }
    
    public void error(final Marker marker, final String s, final Throwable t) {
        this.error(s, t);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.getName() + ")";
    }
}
