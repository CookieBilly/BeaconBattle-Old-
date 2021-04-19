

package ws.billy.bedwars.shop.listeners;

import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.shop.quickbuy.QuickBuyAdd;
import ws.billy.bedwars.shop.main.CategoryContent;
import org.bukkit.event.inventory.InventoryAction;
import ws.billy.bedwars.shop.quickbuy.QuickBuyElement;
import ws.billy.bedwars.shop.main.ShopCategory;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.shop.main.ShopIndex;
import ws.billy.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import ws.billy.bedwars.shop.ShopCache;
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
        final ShopCache shopCache = ShopCache.getShopCache(inventoryClickEvent.getWhoClicked().getUniqueId());
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(inventoryClickEvent.getWhoClicked().getUniqueId());
        if (quickBuyCache == null) {
            return;
        }
        if (shopCache == null) {
            return;
        }
        if (ShopIndex.getIndexViewers().contains(inventoryClickEvent.getWhoClicked().getUniqueId())) {
            inventoryClickEvent.setCancelled(true);
            for (final ShopCategory shopCategory : ShopManager.getShop().getCategoryList()) {
                if (inventoryClickEvent.getSlot() == shopCategory.getSlot()) {
                    shopCategory.open((Player)inventoryClickEvent.getWhoClicked(), ShopManager.getShop(), shopCache);
                    return;
                }
            }
            for (final QuickBuyElement quickBuyElement : quickBuyCache.getElements()) {
                if (quickBuyElement.getSlot() == inventoryClickEvent.getSlot()) {
                    if (inventoryClickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        quickBuyCache.setElement(quickBuyElement.getSlot(), null);
                        inventoryClickEvent.getWhoClicked().closeInventory();
                        return;
                    }
                    quickBuyElement.getCategoryContent().execute((Player)inventoryClickEvent.getWhoClicked(), shopCache, quickBuyElement.getSlot());
                }
            }
        }
        else if (ShopCategory.getCategoryViewers().contains(inventoryClickEvent.getWhoClicked().getUniqueId())) {
            inventoryClickEvent.setCancelled(true);
            for (final ShopCategory shopCategory2 : ShopManager.getShop().getCategoryList()) {
                if (ShopManager.getShop().getQuickBuyButton().getSlot() == inventoryClickEvent.getSlot()) {
                    ShopManager.getShop().open((Player)inventoryClickEvent.getWhoClicked(), quickBuyCache, false);
                    return;
                }
                if (inventoryClickEvent.getSlot() == shopCategory2.getSlot()) {
                    shopCategory2.open((Player)inventoryClickEvent.getWhoClicked(), ShopManager.getShop(), shopCache);
                    return;
                }
                if (shopCategory2.getSlot() != shopCache.getSelectedCategory()) {
                    continue;
                }
                for (final CategoryContent categoryContent : shopCategory2.getCategoryContentList()) {
                    if (categoryContent.getSlot() == inventoryClickEvent.getSlot()) {
                        if (inventoryClickEvent.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                            categoryContent.execute((Player)inventoryClickEvent.getWhoClicked(), shopCache, categoryContent.getSlot());
                            return;
                        }
                        if (quickBuyCache.hasCategoryContent(categoryContent)) {
                            return;
                        }
                        new QuickBuyAdd((Player)inventoryClickEvent.getWhoClicked(), categoryContent);
                    }
                }
            }
        }
        else if (QuickBuyAdd.getQuickBuyAdds().containsKey(inventoryClickEvent.getWhoClicked().getUniqueId())) {
            inventoryClickEvent.setCancelled(true);
            boolean b = false;
            final int[] quickSlots = PlayerQuickBuyCache.quickSlots;
            for (int length = quickSlots.length, i = 0; i < length; ++i) {
                if (quickSlots[i] == inventoryClickEvent.getSlot()) {
                    b = true;
                }
            }
            if (!b) {
                return;
            }
            final CategoryContent categoryContent2 = QuickBuyAdd.getQuickBuyAdds().get(inventoryClickEvent.getWhoClicked().getUniqueId());
            if (categoryContent2 != null) {
                quickBuyCache.setElement(inventoryClickEvent.getSlot(), categoryContent2);
            }
            inventoryClickEvent.getWhoClicked().closeInventory();
        }
    }
    
    @EventHandler
    public void onUpgradableMove(final InventoryClickEvent inventoryClickEvent) {
        final ShopCache shopCache = ShopCache.getShopCache(((Player)inventoryClickEvent.getWhoClicked()).getUniqueId());
        if (shopCache == null) {
            return;
        }
        if (inventoryClickEvent.getAction() == InventoryAction.HOTBAR_SWAP && inventoryClickEvent.getClick() == ClickType.NUMBER_KEY && inventoryClickEvent.getHotbarButton() > -1) {
            final ItemStack item = inventoryClickEvent.getWhoClicked().getInventory().getItem(inventoryClickEvent.getHotbarButton());
            if (item != null && inventoryClickEvent.getClickedInventory().getType() != inventoryClickEvent.getWhoClicked().getInventory().getType() && isUpgradable(item, shopCache)) {
                inventoryClickEvent.setCancelled(true);
            }
        }
        if (inventoryClickEvent.getCursor() != null && inventoryClickEvent.getCursor().getType() != Material.AIR) {
            if (inventoryClickEvent.getClickedInventory() == null) {
                if (isUpgradable(inventoryClickEvent.getCursor(), shopCache)) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (inventoryClickEvent.getClickedInventory().getType() != inventoryClickEvent.getWhoClicked().getInventory().getType() && isUpgradable(inventoryClickEvent.getCursor(), shopCache)) {
                inventoryClickEvent.getWhoClicked().closeInventory();
                inventoryClickEvent.setCancelled(true);
            }
        }
        if (inventoryClickEvent.getCurrentItem() != null && inventoryClickEvent.getCurrentItem().getType() != Material.AIR) {
            if (inventoryClickEvent.getClickedInventory() == null) {
                if (isUpgradable(inventoryClickEvent.getCursor(), shopCache)) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (inventoryClickEvent.getClickedInventory().getType() != inventoryClickEvent.getWhoClicked().getInventory().getType() && isUpgradable(inventoryClickEvent.getCurrentItem(), shopCache)) {
                inventoryClickEvent.getWhoClicked().closeInventory();
                inventoryClickEvent.setCancelled(true);
            }
        }
        if (inventoryClickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isUpgradable(inventoryClickEvent.getCurrentItem(), shopCache)) {
            inventoryClickEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onShopClose(final InventoryCloseEvent inventoryCloseEvent) {
        ShopIndex.indexViewers.remove(inventoryCloseEvent.getPlayer().getUniqueId());
        ShopCategory.categoryViewers.remove(inventoryCloseEvent.getPlayer().getUniqueId());
        QuickBuyAdd.quickBuyAdds.remove(inventoryCloseEvent.getPlayer().getUniqueId());
    }
    
    public static boolean isUpgradable(final ItemStack itemStack, final ShopCache shopCache) {
        if (itemStack == null) {
            return false;
        }
        if (shopCache == null) {
            return false;
        }
        final String shopUpgradeIdentifier = BeaconBattle.nms.getShopUpgradeIdentifier(itemStack);
        return shopUpgradeIdentifier != null && !shopUpgradeIdentifier.equals("null") && shopCache.getCachedItem(shopUpgradeIdentifier) != null && shopCache.getCachedItem(shopUpgradeIdentifier).getCc().getContentTiers().size() > 1;
    }
}
