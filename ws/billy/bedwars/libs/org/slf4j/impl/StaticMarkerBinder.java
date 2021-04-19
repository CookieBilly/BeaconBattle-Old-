

package ws.billy.bedwars.libs.org.slf4j.impl;

import ws.billy.bedwars.libs.org.slf4j.helpers.BasicMarkerFactory;
import ws.billy.bedwars.libs.org.slf4j.IMarkerFactory;
import ws.billy.bedwars.libs.org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder
{
    public static final StaticMarkerBinder SINGLETON;
    final IMarkerFactory markerFactory;
    
    private StaticMarkerBinder() {
        this.markerFactory = new BasicMarkerFactory();
    }
    
    public static StaticMarkerBinder getSingleton() {
        return StaticMarkerBinder.SINGLETON;
    }
    
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }
    
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
    
    static {
        SINGLETON = new StaticMarkerBinder();
    }
}
