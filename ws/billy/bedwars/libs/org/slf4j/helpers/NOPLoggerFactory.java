

package ws.billy.bedwars.libs.org.slf4j.helpers;

import ws.billy.bedwars.libs.org.slf4j.Logger;
import ws.billy.bedwars.libs.org.slf4j.ILoggerFactory;

public class NOPLoggerFactory implements ILoggerFactory
{
    public Logger getLogger(final String s) {
        return NOPLogger.NOP_LOGGER;
    }
}
