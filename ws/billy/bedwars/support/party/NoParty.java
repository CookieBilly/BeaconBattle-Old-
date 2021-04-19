

package ws.billy.bedwars.support.party;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.party.Party;

public class NoParty implements Party
{
    @Override
    public boolean hasParty(final Player player) {
        return false;
    }
    
    @Override
    public int partySize(final Player player) {
        return 0;
    }
    
    @Override
    public boolean isOwner(final Player player) {
        return false;
    }
    
    @Override
    public List<Player> getMembers(final Player player) {
        return new ArrayList<Player>();
    }
    
    @Override
    public void createParty(final Player player, final Player... array) {
    }
    
    @Override
    public void addMember(final Player player, final Player player2) {
    }
    
    @Override
    public void removeFromParty(final Player player) {
    }
    
    @Override
    public void disband(final Player player) {
    }
    
    @Override
    public boolean isMember(final Player player, final Player player2) {
        return false;
    }
    
    @Override
    public void removePlayer(final Player player, final Player player2) {
    }
    
    @Override
    public boolean isInternal() {
        return false;
    }
}
