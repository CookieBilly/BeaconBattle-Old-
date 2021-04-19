

package ws.billy.bedwars.shop.listeners;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.Listener;

public class PlayerDropListener implements Listener
{
    @EventHandler
    public void onDrop(final PlayerDropItemEvent playerDropItemEvent) {
        if (Arena.getArenaByPlayer(playerDropItemEvent.getPlayer()) == null) {
            return;
        }
        final String shopUpgradeIdentifier = BeaconBattle.nms.getShopUpgradeIdentifier(playerDropItemEvent.getItemDrop().getItemStack());
        if (shopUpgradeIdentifier == null) {
            return;
        }
        if (shopUpgradeIdentifier.isEmpty() || shopUpgradeIdentifier.equals(" ")) {
            return;
        }
        if (shopUpgradeIdentifier.equals("null")) {
            return;
        }
        playerDropItemEvent.setCancelled(true);
    }
    
    @EventHandler
    public void onClose(final InventoryCloseEvent inventoryCloseEvent) {
        if (!(inventoryCloseEvent instanceof Player)) {
            return;
        }
        if (Arena.getArenaByPlayer((Player)inventoryCloseEvent.getPlayer()) == null) {
            return;
        }
        for (final ItemStack itemStack : inventoryCloseEvent.getInventory()) {
            if (itemStack == null) {
                continue;
            }
            if (itemStack.getType() == Material.AIR) {
                continue;
            }
            final String shopUpgradeIdentifier = BeaconBattle.nms.getShopUpgradeIdentifier(itemStack);
            if (shopUpgradeIdentifier.isEmpty() || shopUpgradeIdentifier.equals(" ")) {
                return;
            }
        }
    }
}
