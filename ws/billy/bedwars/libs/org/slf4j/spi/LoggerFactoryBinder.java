

package ws.billy.bedwars.libs.org.slf4j.spi;

import ws.billy.bedwars.libs.org.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder
{
    ILoggerFactory getLoggerFactory();
    
    String getLoggerFactoryClassStr();
}
