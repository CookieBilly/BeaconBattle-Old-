

package ws.billy.bedwars.stats;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import java.time.Instant;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;
import ws.billy.bedwars.api.events.gameplay.GameEndEvent;
import ws.billy.bedwars.api.events.player.PlayerKillEvent;
import ws.billy.bedwars.api.events.player.PlayerBedBreakEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.Listener;

public class StatsListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLoginEvent(final AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
        if (asyncPlayerPreLoginEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        final PlayerStats fetchStats = BeaconBattle.getRemoteDatabase().fetchStats(asyncPlayerPreLoginEvent.getUniqueId());
        fetchStats.setName(asyncPlayerPreLoginEvent.getName());
        BeaconBattle.getStatsManager().put(asyncPlayerPreLoginEvent.getUniqueId(), fetchStats);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLoginEvent(final PlayerLoginEvent playerLoginEvent) {
        if (playerLoginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            BeaconBattle.getStatsManager().remove(playerLoginEvent.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onBedBreak(final PlayerBedBreakEvent playerBedBreakEvent) {
        final PlayerStats value = BeaconBattle.getStatsManager().get(playerBedBreakEvent.getPlayer().getUniqueId());
        value.setBedsDestroyed(value.getBedsDestroyed() + 1);
    }
    
    @EventHandler
    public void onPlayerKill(final PlayerKillEvent playerKillEvent) {
        final PlayerStats value = BeaconBattle.getStatsManager().get(playerKillEvent.getVictim().getUniqueId());
        final PlayerStats playerStats = (playerKillEvent.getKiller() != null) ? BeaconBattle.getStatsManager().get(playerKillEvent.getKiller().getUniqueId()) : null;
        if (playerKillEvent.getCause().isFinalKill()) {
            value.setFinalDeaths(value.getFinalDeaths() + 1);
            value.setLosses(value.getLosses() + 1);
            value.setGamesPlayed(value.getGamesPlayed() + 1);
            if (playerStats != null) {
                playerStats.setFinalKills(playerStats.getFinalKills() + 1);
            }
        }
        else {
            value.setDeaths(value.getDeaths() + 1);
            if (playerStats != null) {
                playerStats.setKills(playerStats.getKills() + 1);
            }
        }
    }
    
    @EventHandler
    public void onGameEnd(final GameEndEvent gameEndEvent) {
        for (final UUID uuid : gameEndEvent.getWinners()) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (!player.isOnline()) {
                continue;
            }
            final PlayerStats value = BeaconBattle.getStatsManager().get(uuid);
            value.setWins(value.getWins() + 1);
            value.setGamesPlayed(value.getGamesPlayed() + 1);
        }
    }
    
    @EventHandler
    public void onArenaLeave(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        final Player player = playerLeaveArenaEvent.getPlayer();
        if (playerLeaveArenaEvent.getArena().getStatus() != GameState.starting && playerLeaveArenaEvent.getArena().getStatus() != GameState.waiting) {
            final PlayerStats value = BeaconBattle.getStatsManager().get(player.getUniqueId());
            final Instant now = Instant.now();
            value.setLastPlay(now);
            if (value.getFirstPlay() == null) {
                value.setFirstPlay(now);
            }
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> BeaconBattle.getRemoteDatabase().saveStats(value));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent playerQuitEvent) {
        BeaconBattle.getStatsManager().remove(playerQuitEvent.getPlayer().getUniqueId());
    }
}
