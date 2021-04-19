

package ws.billy.bedwars.shop.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class SpecialsListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpecialInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.isCancelled()) {
            return;
        }
        final Player player = playerInteractEvent.getPlayer();
        final ItemStack item = playerInteractEvent.getItem();
        if (item == null) {
            return;
        }
        if (item.getType() == Material.AIR) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.getRespawn().containsKey(playerInteractEvent.getPlayer())) {
            return;
        }
        if (!arenaByPlayer.isPlayer(player)) {
            return;
        }
        if (BeaconBattle.shop.getYml().getBoolean("shop-specials.silverfish.enable") && !Misc.isProjectile(Material.valueOf(BeaconBattle.shop.getYml().getString("shop-specials.silverfish.material"))) && item.getType() == Material.valueOf(BeaconBattle.shop.getYml().getString("shop-specials.silverfish.material")) && BeaconBattle.nms.itemStackDataCompare(item, (short)BeaconBattle.shop.getYml().getInt("shop-specials.silverfish.data"))) {
            playerInteractEvent.setCancelled(true);
            BeaconBattle.nms.spawnSilverfish(player.getLocation().add(0.0, 1.0, 0.0), arenaByPlayer.getTeam(player), BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.speed"), BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.health"), BeaconBattle.shop.getInt("shop-specials.silverfish.despawn"), BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.damage"));
            if (!BeaconBattle.nms.isProjectile(item)) {
                BeaconBattle.nms.minusAmount(player, item, 1);
                player.updateInventory();
            }
        }
        if (BeaconBattle.shop.getYml().getBoolean("shop-specials.iron-golem.enable") && !Misc.isProjectile(Material.valueOf(BeaconBattle.shop.getYml().getString("shop-specials.iron-golem.material"))) && item.getType() == Material.valueOf(BeaconBattle.shop.getYml().getString("shop-specials.iron-golem.material")) && BeaconBattle.nms.itemStackDataCompare(item, (short)BeaconBattle.shop.getYml().getInt("shop-specials.iron-golem.data"))) {
            playerInteractEvent.setCancelled(true);
            BeaconBattle.nms.spawnIronGolem(player.getLocation().add(0.0, 1.0, 0.0), arenaByPlayer.getTeam(player), BeaconBattle.shop.getYml().getDouble("shop-specials.iron-golem.speed"), BeaconBattle.shop.getYml().getDouble("shop-specials.iron-golem.health"), BeaconBattle.shop.getInt("shop-specials.iron-golem.despawn"));
            if (!BeaconBattle.nms.isProjectile(item)) {
                BeaconBattle.nms.minusAmount(player, item, 1);
                player.updateInventory();
            }
        }
    }
}
