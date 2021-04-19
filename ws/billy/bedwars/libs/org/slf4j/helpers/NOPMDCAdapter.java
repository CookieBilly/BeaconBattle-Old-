

package ws.billy.bedwars.libs.org.slf4j.helpers;

import java.util.Map;
import ws.billy.bedwars.libs.org.slf4j.spi.MDCAdapter;

public class NOPMDCAdapter implements MDCAdapter
{
    public void clear() {
    }
    
    public String get(final String s) {
        return null;
    }
    
    public void put(final String s, final String s2) {
    }
    
    public void remove(final String s) {
    }
    
    public Map<String, String> getCopyOfContextMap() {
        return null;
    }
    
    public void setContextMap(final Map<String, String> map) {
    }
}
