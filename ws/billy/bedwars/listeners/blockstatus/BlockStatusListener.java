

package ws.billy.bedwars.listeners.blockstatus;

import org.bukkit.Material;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import ws.billy.bedwars.api.events.gameplay.GameStateChangeEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.events.server.ArenaEnableEvent;
import org.bukkit.event.Listener;

public class BlockStatusListener implements Listener
{
    @EventHandler
    public void onArenaEnable(final ArenaEnableEvent arenaEnableEvent) {
        if (arenaEnableEvent == null) {
            return;
        }
        updateBlock((Arena)arenaEnableEvent.getArena());
    }
    
    @EventHandler
    public void onStatusChange(final GameStateChangeEvent gameStateChangeEvent) {
        if (gameStateChangeEvent == null) {
            return;
        }
        updateBlock((Arena)gameStateChangeEvent.getArena());
    }
    
    public static void updateBlock(final Arena arena) {
        if (arena == null) {
            return;
        }
        for (final Block block : arena.getSigns()) {
            if (!(block.getState() instanceof Sign)) {
                continue;
            }
            String s = "";
            String s2 = "";
            switch (arena.getStatus()) {
                case waiting: {
                    s = "status-block.waiting.material";
                    s2 = "status-block.waiting.data";
                    break;
                }
                case playing: {
                    s = "status-block.playing.material";
                    s2 = "status-block.starting.data";
                    break;
                }
                case starting: {
                    s = "status-block.playing.material";
                    s2 = "status-block.playing.data";
                    break;
                }
                case restarting: {
                    s = "status-block.restarting.material";
                    s2 = "status-block.restarting.data";
                    break;
                }
            }
            BeaconBattle.nms.setJoinSignBackground(block.getState(), Material.valueOf(BeaconBattle.signs.getString(s)));
            BeaconBattle.nms.setJoinSignBackgroundBlockData(block.getState(), (byte)BeaconBattle.signs.getInt(s2));
        }
    }
}
