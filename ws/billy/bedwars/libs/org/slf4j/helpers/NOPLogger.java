

package ws.billy.bedwars.libs.org.slf4j.helpers;

public class NOPLogger extends MarkerIgnoringBase
{
    private static final long serialVersionUID = -517220405410904473L;
    public static final NOPLogger NOP_LOGGER;
    
    protected NOPLogger() {
    }
    
    @Override
    public String getName() {
        return "NOP";
    }
    
    public final boolean isTraceEnabled() {
        return false;
    }
    
    public final void trace(final String s) {
    }
    
    public final void trace(final String s, final Object o) {
    }
    
    public final void trace(final String s, final Object o, final Object o2) {
    }
    
    public final void trace(final String s, final Object... array) {
    }
    
    public final void trace(final String s, final Throwable t) {
    }
    
    public final boolean isDebugEnabled() {
        return false;
    }
    
    public final void debug(final String s) {
    }
    
    public final void debug(final String s, final Object o) {
    }
    
    public final void debug(final String s, final Object o, final Object o2) {
    }
    
    public final void debug(final String s, final Object... array) {
    }
    
    public final void debug(final String s, final Throwable t) {
    }
    
    public final boolean isInfoEnabled() {
        return false;
    }
    
    public final void info(final String s) {
    }
    
    public final void info(final String s, final Object o) {
    }
    
    public final void info(final String s, final Object o, final Object o2) {
    }
    
    public final void info(final String s, final Object... array) {
    }
    
    public final void info(final String s, final Throwable t) {
    }
    
    public final boolean isWarnEnabled() {
        return false;
    }
    
    public final void warn(final String s) {
    }
    
    public final void warn(final String s, final Object o) {
    }
    
    public final void warn(final String s, final Object o, final Object o2) {
    }
    
    public final void warn(final String s, final Object... array) {
    }
    
    public final void warn(final String s, final Throwable t) {
    }
    
    public final boolean isErrorEnabled() {
        return false;
    }
    
    public final void error(final String s) {
    }
    
    public final void error(final String s, final Object o) {
    }
    
    public final void error(final String s, final Object o, final Object o2) {
    }
    
    public final void error(final String s, final Object... array) {
    }
    
    public final void error(final String s, final Throwable t) {
    }
    
    static {
        NOP_LOGGER = new NOPLogger();
    }
}
