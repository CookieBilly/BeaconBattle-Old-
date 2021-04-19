

package ws.billy.bedwars.listeners;

import java.util.Collections;
import java.util.Map;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.gameplay.EggBridgeThrowEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import ws.billy.bedwars.arena.tasks.EggBridgeTask;
import org.bukkit.entity.Egg;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class EggBridge implements Listener
{
    private static HashMap<Egg, EggBridgeTask> bridges;
    
    @EventHandler
    public void onLaunch(final ProjectileLaunchEvent projectileLaunchEvent) {
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && projectileLaunchEvent.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            projectileLaunchEvent.setCancelled(true);
            return;
        }
        if (projectileLaunchEvent.getEntity() instanceof Egg) {
            final Egg key = (Egg)projectileLaunchEvent.getEntity();
            if (key.getShooter() instanceof Player) {
                final Player player = (Player)key.getShooter();
                final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
                if (arenaByPlayer != null && arenaByPlayer.isPlayer(player)) {
                    Bukkit.getPluginManager().callEvent((Event)new EggBridgeThrowEvent(player, arenaByPlayer));
                    if (projectileLaunchEvent.isCancelled()) {
                        projectileLaunchEvent.setCancelled(true);
                        return;
                    }
                    EggBridge.bridges.put(key, new EggBridgeTask(player, key, arenaByPlayer.getTeam(player).getColor()));
                }
            }
        }
    }
    
    @EventHandler
    public void onHit(final ProjectileHitEvent projectileHitEvent) {
        if (projectileHitEvent.getEntity() instanceof Egg) {
            removeEgg((Egg)projectileHitEvent.getEntity());
        }
    }
    
    public static void removeEgg(final Egg egg) {
        if (EggBridge.bridges.containsKey(egg)) {
            if (EggBridge.bridges.get(egg) != null) {
                EggBridge.bridges.get(egg).cancel();
            }
            EggBridge.bridges.remove(egg);
        }
    }
    
    public static Map<Egg, EggBridgeTask> getBridges() {
        return Collections.unmodifiableMap((Map<? extends Egg, ? extends EggBridgeTask>)EggBridge.bridges);
    }
    
    static {
        EggBridge.bridges = new HashMap<Egg, EggBridgeTask>();
    }
}
