

package ws.billy.bedwars.shop.quickbuy;

import org.bukkit.inventory.Inventory;
import java.util.Objects;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.shop.main.ShopCategory;
import org.bukkit.entity.Player;
import ws.billy.bedwars.shop.main.CategoryContent;

import java.util.UUID;
import java.util.HashMap;

public class QuickBuyAdd
{
    public static HashMap<UUID, CategoryContent> quickBuyAdds;
    
    public QuickBuyAdd(final Player player, final CategoryContent categoryContent) {
        ShopCategory.categoryViewers.remove(player.getUniqueId());
        this.open(player, categoryContent);
    }
    
    public void open(final Player player, final CategoryContent value) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, ShopManager.getShop().getInvSize());
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(player.getUniqueId());
        final ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        if (shopCache == null || quickBuyCache == null) {
            player.closeInventory();
        }
        inventory.setItem(4, value.getItemStack(player, Objects.requireNonNull(shopCache)));
        Objects.requireNonNull(quickBuyCache).addInInventory(inventory, shopCache);
        player.openInventory(inventory);
        QuickBuyAdd.quickBuyAdds.put(player.getUniqueId(), value);
    }
    
    public static HashMap<UUID, CategoryContent> getQuickBuyAdds() {
        return new HashMap<UUID, CategoryContent>(QuickBuyAdd.quickBuyAdds);
    }
    
    static {
        QuickBuyAdd.quickBuyAdds = new HashMap<UUID, CategoryContent>();
    }
}
