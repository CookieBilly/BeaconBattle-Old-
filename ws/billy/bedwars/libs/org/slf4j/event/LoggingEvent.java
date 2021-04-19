

package ws.billy.bedwars.libs.org.slf4j.event;

import ws.billy.bedwars.libs.org.slf4j.Marker;

public interface LoggingEvent
{
    Level getLevel();
    
    Marker getMarker();
    
    String getLoggerName();
    
    String getMessage();
    
    String getThreadName();
    
    Object[] getArgumentArray();
    
    long getTimeStamp();
    
    Throwable getThrowable();
}
