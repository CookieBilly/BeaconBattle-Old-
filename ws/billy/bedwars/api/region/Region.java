

package ws.billy.bedwars.api.region;

import org.bukkit.Location;

public interface Region
{
    boolean isInRegion(final Location p0);
    
    boolean isProtected();
}
