

package ws.billy.bedwars.levels.internal;

import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.events.player.PlayerXpGainEvent;
import ws.billy.bedwars.configuration.LevelsConfig;
import ws.billy.bedwars.api.events.gameplay.GameEndEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class LevelListeners implements Listener
{
    public static LevelListeners instance;
    
    public LevelListeners() {
        LevelListeners.instance = this;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
        new PlayerLevel(playerJoinEvent.getPlayer().getUniqueId(), 1, 0);
        final UUID uuid;
        final Object[] array;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
            BeaconBattle.getRemoteDatabase().getLevelData(uuid);
            PlayerLevel.getLevelByPlayer(uuid).lazyLoad((int)array[0], (int)array[1]);
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent playerQuitEvent) {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> PlayerLevel.getLevelByPlayer(playerQuitEvent.getPlayer().getUniqueId()).destroy());
    }
    
    @EventHandler
    public void onGameEnd(final GameEndEvent gameEndEvent) {
        for (final UUID uuid : gameEndEvent.getWinners()) {
            if (PlayerLevel.getLevelByPlayer(uuid) != null) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                PlayerLevel.getLevelByPlayer(uuid).addXp(LevelsConfig.levels.getInt("xp-rewards.game-win"), PlayerXpGainEvent.XpSource.GAME_WIN);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_WIN).replace("{xp}", String.valueOf(LevelsConfig.levels.getInt("xp-rewards.game-win"))));
                final ITeam exTeam = gameEndEvent.getArena().getExTeam(player.getUniqueId());
                if (exTeam == null || exTeam.getMembersCache().size() <= 1) {
                    continue;
                }
                final int i = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * exTeam.getMembersCache().size();
                PlayerLevel.getLevelByPlayer(uuid).addXp(i, PlayerXpGainEvent.XpSource.PER_TEAMMATE);
                player.sendMessage(Language.getMsg(player, "xp-reward-per-teammate").replace("{xp}", String.valueOf(i)));
            }
        }
        for (final UUID uuid2 : gameEndEvent.getLosers()) {
            if (PlayerLevel.getLevelByPlayer(uuid2) != null) {
                final Player player2 = Bukkit.getPlayer(uuid2);
                if (player2 == null) {
                    continue;
                }
                final ITeam exTeam2 = gameEndEvent.getArena().getExTeam(player2.getUniqueId());
                if (exTeam2 == null || exTeam2.getMembersCache().size() <= 1) {
                    continue;
                }
                final int j = LevelsConfig.levels.getInt("xp-rewards.per-teammate") * exTeam2.getMembersCache().size();
                PlayerLevel.getLevelByPlayer(uuid2).addXp(j, PlayerXpGainEvent.XpSource.PER_TEAMMATE);
                player2.sendMessage(Language.getMsg(player2, Messages.XP_REWARD_PER_TEAMMATE).replace("{xp}", String.valueOf(j)));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaLeave(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        final PlayerLevel playerLevel;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
            PlayerLevel.getLevelByPlayer(playerLeaveArenaEvent.getPlayer().getUniqueId());
            if (playerLevel != null) {
                playerLevel.updateDatabase();
            }
        });
    }
}
