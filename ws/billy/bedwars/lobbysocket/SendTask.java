

package ws.billy.bedwars.lobbysocket;

import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import ws.billy.bedwars.api.arena.IArena;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;

public class SendTask
{
    public SendTask() {
        Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, () -> new BukkitRunnable() {
            final /* synthetic */ List val$arenas;
            
            public void run() {
                final Iterator<IArena> iterator = this.val$arenas.iterator();
                while (iterator.hasNext()) {
                    ArenaSocket.sendMessage(ArenaSocket.formatUpdateMessage(iterator.next()));
                }
            }
        }.runTaskAsynchronously((Plugin)BeaconBattle.plugin), 100L, 30L);
    }
}
