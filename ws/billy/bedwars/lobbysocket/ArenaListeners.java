

package ws.billy.bedwars.lobbysocket;

import ws.billy.bedwars.api.events.server.ArenaEnableEvent;
import ws.billy.bedwars.api.events.gameplay.GameStateChangeEvent;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import org.bukkit.event.Listener;

public class ArenaListeners implements Listener
{
    @EventHandler
    public void onPlayerJoinArena(final PlayerJoinArenaEvent playerJoinArenaEvent) {
        if (playerJoinArenaEvent == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> ArenaSocket.sendMessage(ArenaSocket.formatUpdateMessage(playerJoinArenaEvent.getArena())));
    }
    
    @EventHandler
    public void onPlayerLeaveArena(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        if (playerLeaveArenaEvent == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> ArenaSocket.sendMessage(ArenaSocket.formatUpdateMessage(playerLeaveArenaEvent.getArena())));
    }
    
    @EventHandler
    public void onArenaStatusChange(final GameStateChangeEvent gameStateChangeEvent) {
        if (gameStateChangeEvent == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> ArenaSocket.sendMessage(ArenaSocket.formatUpdateMessage(gameStateChangeEvent.getArena())));
    }
    
    @EventHandler
    public void onArenaLoad(final ArenaEnableEvent arenaEnableEvent) {
        if (arenaEnableEvent == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> ArenaSocket.sendMessage(ArenaSocket.formatUpdateMessage(arenaEnableEvent.getArena())));
    }
}
