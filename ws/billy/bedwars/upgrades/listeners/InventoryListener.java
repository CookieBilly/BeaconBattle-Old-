

package ws.billy.bedwars.upgrades.listeners;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.upgrades.MenuContent;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.Material;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public class InventoryListener implements Listener
{
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.isCancelled()) {
            return;
        }
        if (!(inventoryClickEvent.getWhoClicked() instanceof Player)) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer((Player)inventoryClickEvent.getWhoClicked());
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isSpectator((Player)inventoryClickEvent.getWhoClicked())) {
            return;
        }
        if (!UpgradesManager.isWatchingUpgrades(inventoryClickEvent.getWhoClicked().getUniqueId())) {
            return;
        }
        inventoryClickEvent.setCancelled(true);
        if (inventoryClickEvent.getCurrentItem() == null) {
            return;
        }
        if (inventoryClickEvent.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        final MenuContent menuContent = UpgradesManager.getMenuContent(inventoryClickEvent.getCurrentItem());
        if (menuContent == null) {
            return;
        }
        menuContent.onClick((Player)inventoryClickEvent.getWhoClicked(), inventoryClickEvent.getClick(), arenaByPlayer.getTeam((Player)inventoryClickEvent.getWhoClicked()));
    }
    
    @EventHandler
    public void onUpgradesClose(final InventoryCloseEvent inventoryCloseEvent) {
        UpgradesManager.removeWatchingUpgrades(inventoryCloseEvent.getPlayer().getUniqueId());
    }
}
