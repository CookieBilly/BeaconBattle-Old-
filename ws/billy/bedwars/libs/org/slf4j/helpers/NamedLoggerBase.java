

package ws.billy.bedwars.libs.org.slf4j.helpers;

import ws.billy.bedwars.libs.org.slf4j.LoggerFactory;
import java.io.Serializable;
import ws.billy.bedwars.libs.org.slf4j.Logger;

abstract class NamedLoggerBase implements Logger, Serializable
{
    private static final long serialVersionUID = 7535258609338176893L;
    protected String name;
    
    public String getName() {
        return this.name;
    }
    
    protected Object readResolve() {
        return LoggerFactory.getLogger(this.getName());
    }
}
