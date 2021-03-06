

package ws.billy.bedwars.libs.org.slf4j.event;

public enum Level
{
    ERROR(40, "ERROR"), 
    WARN(30, "WARN"), 
    INFO(20, "INFO"), 
    DEBUG(10, "DEBUG"), 
    TRACE(0, "TRACE");
    
    private int levelInt;
    private String levelStr;
    
    private Level(final int levelInt, final String levelStr) {
        this.levelInt = levelInt;
        this.levelStr = levelStr;
    }
    
    public int toInt() {
        return this.levelInt;
    }
    
    @Override
    public String toString() {
        return this.levelStr;
    }
}
