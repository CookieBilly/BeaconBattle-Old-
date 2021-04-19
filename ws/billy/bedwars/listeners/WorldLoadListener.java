

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import java.util.LinkedList;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.Listener;

public class WorldLoadListener implements Listener
{
    @EventHandler
    public void onLoad(final WorldLoadEvent worldLoadEvent) {
        for (final IArena arena : new LinkedList<IArena>(Arena.getEnableQueue())) {
            if (arena.getWorldName().equalsIgnoreCase(worldLoadEvent.getWorld().getName())) {
                arena.init(worldLoadEvent.getWorld());
            }
        }
    }
}
