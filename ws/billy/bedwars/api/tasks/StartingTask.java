

package ws.billy.bedwars.api.tasks;

import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.IArena;

public interface StartingTask
{
    int getCountdown();
    
    void setCountdown(final int p0);
    
    IArena getArena();
    
    int getTask();
    
    BukkitTask getBukkitTask();
    
    void cancel();
}
