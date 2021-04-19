

package ws.billy.bedwars.api.party;

import java.util.List;
import org.bukkit.entity.Player;

public interface Party
{
    boolean hasParty(final Player p0);
    
    int partySize(final Player p0);
    
    boolean isOwner(final Player p0);
    
    List<Player> getMembers(final Player p0);
    
    void createParty(final Player p0, final Player... p1);
    
    void addMember(final Player p0, final Player p1);
    
    void removeFromParty(final Player p0);
    
    void disband(final Player p0);
    
    boolean isMember(final Player p0, final Player p1);
    
    void removePlayer(final Player p0, final Player p1);
    
    boolean isInternal();
}
