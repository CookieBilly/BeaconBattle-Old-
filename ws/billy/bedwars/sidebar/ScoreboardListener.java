

package ws.billy.bedwars.sidebar;

import ws.billy.bedwars.api.events.player.PlayerKillEvent;
import ws.billy.bedwars.api.events.player.PlayerBedBreakEvent;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.events.player.PlayerReJoinEvent;
import ws.billy.bedwars.api.events.player.PlayerReSpawnEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public class ScoreboardListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent == null) {
            return;
        }
        if (entityDamageEvent.isCancelled()) {
            return;
        }
        if (!(entityDamageEvent.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)entityDamageEvent.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        final int n = (int)(player.getHealth() - entityDamageEvent.getDamage()) + 1;
        if (arenaByPlayer == null) {
            return;
        }
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (arenaByPlayer.equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.getHandle().refreshHealth(player, n);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegain(final EntityRegainHealthEvent entityRegainHealthEvent) {
        if (entityRegainHealthEvent == null) {
            return;
        }
        if (entityRegainHealthEvent.isCancelled()) {
            return;
        }
        if (!(entityRegainHealthEvent.getEntity() instanceof Player)) {
            return;
        }
        final Player player = (Player)entityRegainHealthEvent.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        int n = (int)(player.getHealth() + entityRegainHealthEvent.getAmount()) + 1;
        if (n > player.getMaxHealth()) {
            --n;
        }
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (arenaByPlayer.equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.getHandle().refreshHealth(player, n);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onReSpawn(final PlayerReSpawnEvent playerReSpawnEvent) {
        if (playerReSpawnEvent == null) {
            return;
        }
        final IArena arena = playerReSpawnEvent.getArena();
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (arena.equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.getHandle().refreshHealth(playerReSpawnEvent.getPlayer(), (int)playerReSpawnEvent.getPlayer().getHealth());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void reJoin(final PlayerReJoinEvent playerReJoinEvent) {
        if (playerReJoinEvent == null) {
            return;
        }
        if (playerReJoinEvent.isCancelled()) {
            return;
        }
        if (!BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-playing-list")) {
            return;
        }
        final IArena arena = playerReJoinEvent.getArena();
        final Player player = playerReJoinEvent.getPlayer();
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (arena.equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.addToTabList(player, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING);
            }
        }
    }
    
    @EventHandler
    public void onBedDestroy(final PlayerBedBreakEvent playerBedBreakEvent) {
        if (playerBedBreakEvent == null) {
            return;
        }
        BeaconBattleScoreboard.getScoreboards().values().forEach(BeaconBattleScoreboard -> {
            if (playerBedBreakEvent.getArena().equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.getHandle().refreshPlaceholders();
            }
        });
    }
    
    @EventHandler
    public void onFinalKill(final PlayerKillEvent playerKillEvent) {
        if (playerKillEvent == null) {
            return;
        }
        if (!playerKillEvent.getCause().isFinalKill()) {
            return;
        }
        BeaconBattleScoreboard.getScoreboards().values().forEach(BeaconBattleScoreboard -> {
            if (playerKillEvent.getArena().equals(BeaconBattleScoreboard.getArena())) {
                BeaconBattleScoreboard.getHandle().refreshPlaceholders();
            }
        });
    }
}
