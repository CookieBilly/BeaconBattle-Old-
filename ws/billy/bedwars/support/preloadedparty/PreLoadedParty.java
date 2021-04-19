

package ws.billy.bedwars.support.preloadedparty;

import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import java.util.List;

public class PreLoadedParty
{
    private String owner;
    private List<Player> members;
    private static ConcurrentHashMap<String, PreLoadedParty> preLoadedParties;
    
    public PreLoadedParty(final String s) {
        this.members = new ArrayList<Player>();
        final PreLoadedParty partyByOwner = getPartyByOwner(s);
        if (partyByOwner != null) {
            partyByOwner.clean();
        }
        this.owner = s;
        PreLoadedParty.preLoadedParties.put(s, this);
    }
    
    public static PreLoadedParty getPartyByOwner(final String key) {
        return PreLoadedParty.preLoadedParties.getOrDefault(key, null);
    }
    
    public void addMember(final Player player) {
        this.members.add(player);
    }
    
    public void teamUp() {
        if (this.owner == null) {
            return;
        }
        final Player player = Bukkit.getPlayer(this.owner);
        if (player == null) {
            return;
        }
        if (!player.isOnline()) {
            return;
        }
        for (final Player player2 : this.members) {
            if (!player2.getName().equalsIgnoreCase(this.owner)) {
                BeaconBattle.getParty().addMember(player, player2);
            }
        }
        PreLoadedParty.preLoadedParties.remove(this.owner);
    }
    
    public static ConcurrentHashMap<String, PreLoadedParty> getPreLoadedParties() {
        return PreLoadedParty.preLoadedParties;
    }
    
    public void clean() {
        PreLoadedParty.preLoadedParties.remove(this.owner);
    }
    
    static {
        PreLoadedParty.preLoadedParties = new ConcurrentHashMap<String, PreLoadedParty>();
    }
}
