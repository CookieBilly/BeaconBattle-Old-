

package ws.billy.bedwars.libs.org.slf4j.helpers;

import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.libs.org.slf4j.Marker;
import java.util.concurrent.ConcurrentMap;
import ws.billy.bedwars.libs.org.slf4j.IMarkerFactory;

public class BasicMarkerFactory implements IMarkerFactory
{
    private final ConcurrentMap<String, Marker> markerMap;
    
    public BasicMarkerFactory() {
        this.markerMap = new ConcurrentHashMap<String, Marker>();
    }
    
    public Marker getMarker(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Marker name cannot be null");
        }
        Marker marker = this.markerMap.get(s);
        if (marker == null) {
            marker = new BasicMarker(s);
            final Marker marker2 = this.markerMap.putIfAbsent(s, marker);
            if (marker2 != null) {
                marker = marker2;
            }
        }
        return marker;
    }
    
    public boolean exists(final String s) {
        return s != null && this.markerMap.containsKey(s);
    }
    
    public boolean detachMarker(final String s) {
        return s != null && this.markerMap.remove(s) != null;
    }
    
    public Marker getDetachedMarker(final String s) {
        return new BasicMarker(s);
    }
}
