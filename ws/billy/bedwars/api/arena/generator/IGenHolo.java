

package ws.billy.bedwars.api.arena.generator;

import org.bukkit.entity.Player;

public interface IGenHolo
{
    void setTimerName(final String p0);
    
    void setTierName(final String p0);
    
    String getIso();
    
    void updateForPlayer(final Player p0, final String p1);
    
    void updateForAll();
    
    void destroy();
}
