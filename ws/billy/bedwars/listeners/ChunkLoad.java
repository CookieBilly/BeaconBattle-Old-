

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.ArmorStand;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.Listener;

public class ChunkLoad implements Listener
{
    @EventHandler
    public void onChunkLoadEvent(final ChunkLoadEvent chunkLoadEvent) {
        if (chunkLoadEvent == null) {
            return;
        }
        if (chunkLoadEvent.getChunk() == null) {
            return;
        }
        if (chunkLoadEvent.getChunk().getEntities() == null) {
            return;
        }
        final Entity[] array;
        int length;
        int i = 0;
        Entity entity;
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
            chunkLoadEvent.getChunk().getEntities();
            for (length = array.length; i < length; ++i) {
                entity = array[i];
                if (entity instanceof ArmorStand) {
                    if (entity.hasMetadata("bw1058-setup")) {
                        Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, entity::remove);
                    }
                    else if (!((ArmorStand)entity).isVisible() && ((ArmorStand)entity).isMarker() && entity.isCustomNameVisible() && (ChatColor.stripColor(entity.getCustomName()).contains(" SET") || ChatColor.stripColor(entity.getCustomName()).contains(" set"))) {
                        Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, entity::remove);
                    }
                }
            }
        });
    }
}
