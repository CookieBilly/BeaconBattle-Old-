

package ws.billy.bedwars.api.tasks;

import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.IArena;

public interface RestartingTask
{
    IArena getArena();
    
    BukkitTask getBukkitTask();
    
    int getTask();
    
    int getRestarting();
    
    void cancel();
}
