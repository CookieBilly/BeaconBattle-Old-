

package ws.billy.bedwars.api.tasks;

import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.IArena;

public interface PlayingTask
{
    IArena getArena();
    
    BukkitTask getBukkitTask();
    
    int getTask();
    
    int getBedsDestroyCountdown();
    
    int getDragonSpawnCountdown();
    
    int getGameEndCountdown();
    
    void cancel();
}
