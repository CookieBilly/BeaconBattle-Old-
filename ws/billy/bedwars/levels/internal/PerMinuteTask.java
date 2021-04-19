

package ws.billy.bedwars.levels.internal;

import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.events.player.PlayerXpGainEvent;
import org.bukkit.entity.Player;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import ws.billy.bedwars.configuration.LevelsConfig;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.scheduler.BukkitTask;

public class PerMinuteTask
{
    private int xp;
    private BukkitTask task;
    
    public PerMinuteTask(final Arena arena) {
        this.xp = LevelsConfig.levels.getInt("xp-rewards.per-minute");
        final Iterator<Player> iterator;
        Player player;
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, () -> {
            arena.getPlayers().iterator();
            while (iterator.hasNext()) {
                player = iterator.next();
                PlayerLevel.getLevelByPlayer(player.getUniqueId()).addXp(this.xp, PlayerXpGainEvent.XpSource.PER_MINUTE);
                player.sendMessage(Language.getMsg(player, Messages.XP_REWARD_PER_MINUTE).replace("{xp}", String.valueOf(this.xp)));
            }
        }, 1200L, 1200L);
    }
    
    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }
    }
}
