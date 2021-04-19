

package ws.billy.bedwars.shop.main;

import ws.billy.bedwars.BeaconBattle;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.inventory.InventoryHolder;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.shop.ShopOpenEvent;
import ws.billy.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopIndex
{
    private int invSize;
    private String namePath;
    private String separatorNamePath;
    private String separatorLorePath;
    private List<ShopCategory> categoryList;
    private QuickBuyButton quickBuyButton;
    public ItemStack separatorSelected;
    public ItemStack separatorStandard;
    public static List<UUID> indexViewers;
    
    public ShopIndex(final String namePath, final QuickBuyButton quickBuyButton, final String separatorNamePath, final String separatorLorePath, final ItemStack separatorSelected, final ItemStack separatorStandard) {
        this.invSize = 54;
        this.categoryList = new ArrayList<ShopCategory>();
        this.namePath = namePath;
        this.separatorLorePath = separatorLorePath;
        this.separatorNamePath = separatorNamePath;
        this.quickBuyButton = quickBuyButton;
        this.separatorStandard = separatorStandard;
        this.separatorSelected = separatorSelected;
    }
    
    public void open(final Player player, final PlayerQuickBuyCache playerQuickBuyCache, final boolean b) {
        if (playerQuickBuyCache == null) {
            return;
        }
        if (b) {
            final ShopOpenEvent shopOpenEvent = new ShopOpenEvent(player);
            Bukkit.getPluginManager().callEvent((Event)shopOpenEvent);
            if (shopOpenEvent.isCancelled()) {
                return;
            }
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, this.invSize, Language.getMsg(player, this.getNamePath()));
        inventory.setItem(this.getQuickBuyButton().getSlot(), this.getQuickBuyButton().getItemStack(player));
        for (final ShopCategory shopCategory : this.getCategoryList()) {
            inventory.setItem(shopCategory.getSlot(), shopCategory.getItemStack(player));
        }
        this.addSeparator(player, inventory);
        inventory.setItem(this.getQuickBuyButton().getSlot() + 9, this.getSelectedItem(player));
        ShopCache.getShopCache(player.getUniqueId()).setSelectedCategory(this.getQuickBuyButton().getSlot());
        playerQuickBuyCache.addInInventory(inventory, ShopCache.getShopCache(player.getUniqueId()));
        player.openInventory(inventory);
        if (!ShopIndex.indexViewers.contains(player.getUniqueId())) {
            ShopIndex.indexViewers.add(player.getUniqueId());
        }
    }
    
    public void addSeparator(final Player player, final Inventory inventory) {
        final ItemStack clone = this.separatorStandard.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, this.separatorNamePath));
        itemMeta.setLore((List)Language.getList(player, this.separatorLorePath));
        clone.setItemMeta(itemMeta);
        for (int i = 9; i < 18; ++i) {
            inventory.setItem(i, clone);
        }
    }
    
    public ItemStack getSelectedItem(final Player player) {
        final ItemStack clone = this.separatorSelected.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, this.separatorNamePath));
        itemMeta.setLore((List)Language.getList(player, this.separatorLorePath));
        clone.setItemMeta(itemMeta);
        return clone;
    }
    
    public void addShopCategory(final ShopCategory obj) {
        this.categoryList.add(obj);
        BeaconBattle.debug("Adding shop category: " + obj + " at slot " + obj.getSlot());
    }
    
    public String getNamePath() {
        return this.namePath;
    }
    
    public int getInvSize() {
        return this.invSize;
    }
    
    public List<ShopCategory> getCategoryList() {
        return this.categoryList;
    }
    
    public QuickBuyButton getQuickBuyButton() {
        return this.quickBuyButton;
    }
    
    public static List<UUID> getIndexViewers() {
        return new ArrayList<UUID>(ShopIndex.indexViewers);
    }
    
    static {
        ShopIndex.indexViewers = new ArrayList<UUID>();
    }
}
