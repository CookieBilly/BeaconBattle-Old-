

package ws.billy.bedwars.arena.despawnables;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.Listener;

public class TargetListener implements Listener
{
    @EventHandler
    public void onTarget(final EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
        if (!(entityTargetLivingEntityEvent.getTarget() instanceof Player)) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(entityTargetLivingEntityEvent.getEntity().getWorld().getName());
        final Player player = (Player)entityTargetLivingEntityEvent.getTarget();
        if (arenaByIdentifier == null) {
            return;
        }
        if (!arenaByIdentifier.isPlayer(player)) {
            entityTargetLivingEntityEvent.setCancelled(true);
            return;
        }
        if (arenaByIdentifier.getStatus() != GameState.playing) {
            entityTargetLivingEntityEvent.setCancelled(true);
            return;
        }
        if (BeaconBattle.nms.isDespawnable(entityTargetLivingEntityEvent.getEntity()) && arenaByIdentifier.getTeam(player) == BeaconBattle.nms.getDespawnablesList().get(entityTargetLivingEntityEvent.getEntity().getUniqueId()).getTeam()) {
            entityTargetLivingEntityEvent.setCancelled(true);
        }
    }
}
