

package ws.billy.bedwars.libs.org.slf4j.spi;

import ws.billy.bedwars.libs.org.slf4j.IMarkerFactory;

public interface MarkerFactoryBinder
{
    IMarkerFactory getMarkerFactory();
    
    String getMarkerFactoryClassStr();
}
