

package ws.billy.bedwars.libs.org.slf4j;

import ws.billy.bedwars.libs.org.slf4j.helpers.Util;
import ws.billy.bedwars.libs.org.slf4j.helpers.BasicMarkerFactory;
import ws.billy.bedwars.libs.org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory
{
    static IMarkerFactory MARKER_FACTORY;
    
    private MarkerFactory() {
    }
    
    private static IMarkerFactory bwCompatibleGetMarkerFactoryFromBinder() {
        try {
            return StaticMarkerBinder.getSingleton().getMarkerFactory();
        }
        catch (NoSuchMethodError noSuchMethodError) {
            return StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
    }
    
    public static Marker getMarker(final String s) {
        return MarkerFactory.MARKER_FACTORY.getMarker(s);
    }
    
    public static Marker getDetachedMarker(final String s) {
        return MarkerFactory.MARKER_FACTORY.getDetachedMarker(s);
    }
    
    public static IMarkerFactory getIMarkerFactory() {
        return MarkerFactory.MARKER_FACTORY;
    }
    
    static {
        try {
            MarkerFactory.MARKER_FACTORY = bwCompatibleGetMarkerFactoryFromBinder();
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            MarkerFactory.MARKER_FACTORY = new BasicMarkerFactory();
        }
        catch (Exception ex) {
            Util.report("Unexpected failure while binding MarkerFactory", ex);
        }
    }
}
