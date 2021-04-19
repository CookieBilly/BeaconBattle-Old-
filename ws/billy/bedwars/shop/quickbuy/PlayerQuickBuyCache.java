

package ws.billy.bedwars.shop.quickbuy;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.shop.main.CategoryContent;
import java.util.Iterator;
import org.bukkit.Bukkit;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.inventory.Inventory;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerQuickBuyCache
{
    private List<QuickBuyElement> elements;
    private String emptyItemNamePath;
    private String emptyItemLorePath;
    private ItemStack emptyItem;
    private UUID player;
    private QuickBuyTask task;
    public static int[] quickSlots;
    private static ConcurrentHashMap<UUID, PlayerQuickBuyCache> quickBuyCaches;
    
    public PlayerQuickBuyCache(final Player player) {
        this.elements = new ArrayList<QuickBuyElement>();
        if (player == null) {
            return;
        }
        this.player = player.getUniqueId();
        this.emptyItem = BeaconBattle.nms.createItemStack(BeaconBattle.shop.getYml().getString("shop-settings.quick-buy-empty-item.material"), BeaconBattle.shop.getYml().getInt("shop-settings.quick-buy-empty-item.amount"), (short)BeaconBattle.shop.getYml().getInt("shop-settings.quick-buy-empty-item.data"));
        if (BeaconBattle.shop.getYml().getBoolean("shop-settings.quick-buy-empty-item.enchanted")) {
            this.emptyItem = ShopManager.enchantItem(this.emptyItem);
        }
        this.emptyItemNamePath = "shop-items-messages.quick-buy-empty-item-name";
        this.emptyItemLorePath = "shop-items-messages.quick-buy-empty-item-lore";
        this.task = new QuickBuyTask(player.getUniqueId());
        PlayerQuickBuyCache.quickBuyCaches.put(this.player, this);
    }
    
    public void addInInventory(final Inventory inventory, final ShopCache shopCache) {
        final Player player = Bukkit.getPlayer(this.player);
        for (final QuickBuyElement quickBuyElement : this.elements) {
            inventory.setItem(quickBuyElement.getSlot(), quickBuyElement.getCategoryContent().getItemStack(player, shopCache));
        }
        if (this.elements.size() == 21) {
            return;
        }
        final ItemStack emptyItem = this.getEmptyItem(player);
        for (final int n : PlayerQuickBuyCache.quickSlots) {
            if (inventory.getItem(n) == null) {
                inventory.setItem(n, emptyItem);
            }
        }
    }
    
    public void destroy() {
        this.elements.clear();
        if (this.task != null) {
            this.task.cancel();
        }
        PlayerQuickBuyCache.quickBuyCaches.remove(this.player);
    }
    
    public void setElement(final int n, final CategoryContent categoryContent) {
        this.elements.removeIf(quickBuyElement -> quickBuyElement.getSlot() == n);
        if (categoryContent != null) {
            this.addQuickElement(new QuickBuyElement(categoryContent.getIdentifier(), n));
            categoryContent.getIdentifier();
        }
        final String s;
        BeaconBattle.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> BeaconBattle.getRemoteDatabase().setQuickBuySlot(this.player, s, n));
    }
    
    @NotNull
    private ItemStack getEmptyItem(final Player player) {
        final ItemStack clone = this.emptyItem.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, this.emptyItemNamePath));
        itemMeta.setLore((List)Language.getList(player, this.emptyItemLorePath));
        clone.setItemMeta(itemMeta);
        return clone;
    }
    
    public boolean hasCategoryContent(final CategoryContent categoryContent) {
        final Iterator<QuickBuyElement> iterator = this.getElements().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCategoryContent() == categoryContent) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    public static PlayerQuickBuyCache getQuickBuyCache(final UUID key) {
        return PlayerQuickBuyCache.quickBuyCaches.getOrDefault(key, null);
    }
    
    public List<QuickBuyElement> getElements() {
        return this.elements;
    }
    
    public void addQuickElement(final QuickBuyElement quickBuyElement) {
        this.elements.add(quickBuyElement);
    }
    
    static {
        PlayerQuickBuyCache.quickSlots = new int[] { 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };
        PlayerQuickBuyCache.quickBuyCaches = new ConcurrentHashMap<UUID, PlayerQuickBuyCache>();
    }
}
