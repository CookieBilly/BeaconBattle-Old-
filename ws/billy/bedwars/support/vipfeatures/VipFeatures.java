

package ws.billy.bedwars.support.vipfeatures;

import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ws.billy.vipfeatures.api.MiniGame;

public class VipFeatures extends MiniGame
{
    public VipFeatures(final Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean isPlaying(final Player player) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        return arenaByPlayer != null && arenaByPlayer.getStatus() != GameState.waiting && arenaByPlayer.getStatus() != GameState.starting;
    }
    
    @Override
    public boolean hasBoosters() {
        return false;
    }
    
    @Override
    public String getDisplayName() {
        return "BeaconBattle1058";
    }
}
