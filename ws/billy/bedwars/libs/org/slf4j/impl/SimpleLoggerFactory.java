

package ws.billy.bedwars.libs.org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.libs.org.slf4j.Logger;
import java.util.concurrent.ConcurrentMap;
import ws.billy.bedwars.libs.org.slf4j.ILoggerFactory;

public class SimpleLoggerFactory implements ILoggerFactory
{
    ConcurrentMap<String, Logger> loggerMap;
    
    public SimpleLoggerFactory() {
        this.loggerMap = new ConcurrentHashMap<String, Logger>();
        SimpleLogger.lazyInit();
    }
    
    public Logger getLogger(final String s) {
        final Logger logger = this.loggerMap.get(s);
        if (logger != null) {
            return logger;
        }
        final SimpleLogger simpleLogger = new SimpleLogger(s);
        final SimpleLogger simpleLogger2 = this.loggerMap.putIfAbsent(s, simpleLogger);
        return (simpleLogger2 == null) ? simpleLogger : simpleLogger2;
    }
    
    void reset() {
        this.loggerMap.clear();
    }
}
