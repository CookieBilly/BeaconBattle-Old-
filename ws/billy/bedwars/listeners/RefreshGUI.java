

package ws.billy.bedwars.listeners;

import ws.billy.bedwars.api.events.server.ArenaDisableEvent;
import ws.billy.bedwars.api.events.server.ArenaEnableEvent;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import ws.billy.bedwars.arena.ArenaGUI;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.gameplay.GameStateChangeEvent;
import org.bukkit.event.Listener;

public class RefreshGUI implements Listener
{
    @EventHandler
    public void onGameStateChange(final GameStateChangeEvent gameStateChangeEvent) {
        if (gameStateChangeEvent == null) {
            return;
        }
        final int size = gameStateChangeEvent.getArena().getPlayers().size();
        final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ArenaGUI.refreshInv(iterator.next(), gameStateChangeEvent.getArena(), size);
        }
    }
    
    @EventHandler
    public void onPlayerJoinArena(final PlayerJoinArenaEvent playerJoinArenaEvent) {
        if (playerJoinArenaEvent == null) {
            return;
        }
        int size = playerJoinArenaEvent.getArena().getPlayers().size();
        if (!playerJoinArenaEvent.isSpectator()) {
            ++size;
        }
        final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ArenaGUI.refreshInv(iterator.next(), playerJoinArenaEvent.getArena(), size);
        }
    }
    
    @EventHandler
    public void onPlayerLeaveArena(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        if (playerLeaveArenaEvent == null) {
            return;
        }
        int size = playerLeaveArenaEvent.getArena().getPlayers().size();
        if (!playerLeaveArenaEvent.isSpectator()) {
            --size;
        }
        final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ArenaGUI.refreshInv(iterator.next(), playerLeaveArenaEvent.getArena(), size);
        }
    }
    
    @EventHandler
    public void onArenaEnable(final ArenaEnableEvent arenaEnableEvent) {
        if (arenaEnableEvent == null) {
            return;
        }
        final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ArenaGUI.refreshInv(iterator.next(), arenaEnableEvent.getArena(), 0);
        }
    }
    
    @EventHandler
    public void onArenaDisable(final ArenaDisableEvent arenaDisableEvent) {
        final Iterator<Player> iterator = Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            ArenaGUI.refreshInv(iterator.next(), null, 0);
        }
    }
}
