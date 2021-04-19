

package ws.billy.bedwars.shop.main;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.entity.Player;
import java.util.Iterator;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.UUID;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ShopCategory
{
    private int slot;
    private ItemStack itemStack;
    private String itemNamePath;
    private String itemLorePath;
    private String invNamePath;
    private boolean loaded;
    private List<CategoryContent> categoryContentList;
    public static List<UUID> categoryViewers;
    
    public ShopCategory(final String str, final YamlConfiguration yamlConfiguration) {
        this.loaded = false;
        this.categoryContentList = new ArrayList<CategoryContent>();
        BeaconBattle.debug("Loading shop category: " + str);
        if (yamlConfiguration.get(str + ".category-item.material") == null) {
            BeaconBattle.plugin.getLogger().severe("Category material not set at: " + str);
            return;
        }
        if (yamlConfiguration.get(str + ".category-slot") == null) {
            BeaconBattle.plugin.getLogger().severe("Category slot not set at: " + str);
            return;
        }
        this.slot = yamlConfiguration.getInt(str + ".category-slot");
        if (this.slot < 1 || this.slot > 7) {
            BeaconBattle.plugin.getLogger().severe("Slot must be n > 1 and n < 8 at: " + str);
            return;
        }
        final Iterator<ShopCategory> iterator = ShopManager.shop.getCategoryList().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getSlot() == this.slot) {
                BeaconBattle.plugin.getLogger().severe("Slot is already in use at: " + str);
                return;
            }
        }
        this.itemStack = BeaconBattle.nms.createItemStack(yamlConfiguration.getString(str + ".category-item.material"), (yamlConfiguration.get(str + ".category-item.amount") == null) ? 1 : yamlConfiguration.getInt(str + ".category-item.amount"), (short)((yamlConfiguration.get(str + ".category-item.data") == null) ? 0 : yamlConfiguration.getInt(str + ".category-item.data")));
        if (yamlConfiguration.get(str + ".category-item.enchanted") != null && yamlConfiguration.getBoolean(str + ".category-item.enchanted")) {
            this.itemStack = ShopManager.enchantItem(this.itemStack);
        }
        this.itemStack.setItemMeta(ShopManager.hideItemStuff(this.itemStack.getItemMeta()));
        this.itemNamePath = "shop-items-messages.%category%.category-item-name".replace("%category%", str);
        this.itemLorePath = "shop-items-messages.%category%.category-item-lore".replace("%category%", str);
        this.invNamePath = "shop-items-messages.%category%.inventory-name".replace("%category%", str);
        this.loaded = true;
        for (final String s : yamlConfiguration.getConfigurationSection(str + "." + ".category-content").getKeys(false)) {
            final CategoryContent categoryContent = new CategoryContent(str + ".category-content" + "." + s, s, str, yamlConfiguration, this);
            if (categoryContent.isLoaded()) {
                this.categoryContentList.add(categoryContent);
                BeaconBattle.debug("Adding CategoryContent: " + s + " to Shop Category: " + str);
            }
        }
    }
    
    public void open(final Player player, final ShopIndex shopIndex, final ShopCache shopCache) {
        if (player.getOpenInventory().getTopInventory() == null) {
            return;
        }
        ShopIndex.indexViewers.remove(player.getUniqueId());
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, shopIndex.getInvSize(), Language.getMsg(player, this.invNamePath));
        inventory.setItem(shopIndex.getQuickBuyButton().getSlot(), shopIndex.getQuickBuyButton().getItemStack(player));
        for (final ShopCategory shopCategory : shopIndex.getCategoryList()) {
            inventory.setItem(shopCategory.getSlot(), shopCategory.getItemStack(player));
        }
        shopIndex.addSeparator(player, inventory);
        inventory.setItem(this.getSlot() + 9, shopIndex.getSelectedItem(player));
        shopCache.setSelectedCategory(this.getSlot());
        for (final CategoryContent categoryContent : this.getCategoryContentList()) {
            inventory.setItem(categoryContent.getSlot(), categoryContent.getItemStack(player, shopCache));
        }
        player.openInventory(inventory);
        if (!ShopCategory.categoryViewers.contains(player.getUniqueId())) {
            ShopCategory.categoryViewers.add(player.getUniqueId());
        }
    }
    
    public ItemStack getItemStack(final Player player) {
        final ItemStack clone = this.itemStack.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, this.itemNamePath));
        itemMeta.setLore((List)Language.getList(player, this.itemLorePath));
        clone.setItemMeta(itemMeta);
        return clone;
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public List<CategoryContent> getCategoryContentList() {
        return this.categoryContentList;
    }
    
    public static CategoryContent getCategoryContent(final String anObject, final ShopIndex shopIndex) {
        final Iterator<ShopCategory> iterator = shopIndex.getCategoryList().iterator();
        while (iterator.hasNext()) {
            for (final CategoryContent categoryContent : iterator.next().getCategoryContentList()) {
                if (categoryContent.getIdentifier().equals(anObject)) {
                    return categoryContent;
                }
            }
        }
        return null;
    }
    
    public static List<UUID> getCategoryViewers() {
        return new ArrayList<UUID>(ShopCategory.categoryViewers);
    }
    
    static {
        ShopCategory.categoryViewers = new ArrayList<UUID>();
    }
}
