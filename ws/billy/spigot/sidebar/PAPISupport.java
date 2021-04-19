

package ws.billy.spigot.sidebar;

import org.bukkit.entity.Player;

public interface PAPISupport
{
    String replacePlaceholders(final Player p0, final String p1);
    
    boolean hasPlaceholders(final String p0);
    
    boolean isSupported();
}
