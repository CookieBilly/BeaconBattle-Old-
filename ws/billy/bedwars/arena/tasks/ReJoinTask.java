

package ws.billy.bedwars.arena.tasks;

import java.util.ArrayList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.Collection;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import java.util.List;

public class ReJoinTask implements Runnable
{
    private static final List<ReJoinTask> reJoinTasks;
    private final IArena arena;
    private final ITeam BeaconBattleTeam;
    private final BukkitTask task;
    
    public ReJoinTask(final IArena arena, final ITeam BeaconBattleTeam) {
        this.arena = arena;
        this.BeaconBattleTeam = BeaconBattleTeam;
        this.task = Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, (Runnable)this, (long)(BeaconBattle.config.getInt("rejoin-time") * 20));
    }
    
    @Override
    public void run() {
        if (this.arena == null) {
            this.destroy();
            return;
        }
        if (this.BeaconBattleTeam == null) {
            this.destroy();
            return;
        }
        if (this.BeaconBattleTeam.getMembers() == null) {
            this.destroy();
            return;
        }
        if (this.BeaconBattleTeam.getMembers().isEmpty()) {
            this.BeaconBattleTeam.setBedDestroyed(true);
            this.destroy();
        }
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public void destroy() {
        ReJoinTask.reJoinTasks.remove(this);
        this.task.cancel();
    }
    
    @NotNull
    @Contract(pure = true)
    public static Collection<ReJoinTask> getReJoinTasks() {
        return Collections.unmodifiableCollection((Collection<? extends ReJoinTask>)ReJoinTask.reJoinTasks);
    }
    
    public void cancel() {
        this.task.cancel();
    }
    
    static {
        reJoinTasks = new ArrayList<ReJoinTask>();
    }
}
