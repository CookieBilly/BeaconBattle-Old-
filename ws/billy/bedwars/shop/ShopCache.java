

package ws.billy.bedwars.shop;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import ws.billy.bedwars.shop.main.CategoryContent;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import java.util.Iterator;
import java.util.ArrayList;
import ws.billy.bedwars.shop.main.ShopCategory;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShopCache
{
    private UUID player;
    private List<CachedItem> cachedItems;
    private int selectedCategory;
    private HashMap<ShopCategory, Byte> categoryWeight;
    private static List<ShopCache> shopCaches;
    
    public ShopCache(final UUID player) {
        this.cachedItems = new ArrayList<CachedItem>();
        this.categoryWeight = new HashMap<ShopCategory, Byte>();
        this.player = player;
        this.selectedCategory = ShopManager.getShop().getQuickBuyButton().getSlot();
        ShopCache.shopCaches.add(this);
    }
    
    public UUID getPlayer() {
        return this.player;
    }
    
    public void setSelectedCategory(final int selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
    
    public int getSelectedCategory() {
        return this.selectedCategory;
    }
    
    public int getContentTier(final String s) {
        final CachedItem cachedItem = this.getCachedItem(s);
        return (cachedItem == null) ? 1 : cachedItem.getTier();
    }
    
    public static ShopCache getShopCache(final UUID obj) {
        for (final ShopCache shopCache : new ArrayList<ShopCache>(ShopCache.shopCaches)) {
            if (shopCache.player.equals(obj)) {
                return shopCache;
            }
        }
        return null;
    }
    
    public void destroy() {
        ShopCache.shopCaches.remove(this);
        this.cachedItems.clear();
        this.cachedItems = null;
        this.categoryWeight = null;
    }
    
    public void managePermanentsAndDowngradables(final Arena arena) {
        BeaconBattle.debug("Restore permanents on death for: " + this.player);
        final Iterator<CachedItem> iterator = this.cachedItems.iterator();
        while (iterator.hasNext()) {
            iterator.next().manageDeath(arena);
        }
    }
    
    public CachedItem getCachedItem(final String anObject) {
        for (final CachedItem cachedItem : this.cachedItems) {
            if (cachedItem.getCc().getIdentifier().equals(anObject)) {
                return cachedItem;
            }
        }
        return null;
    }
    
    public boolean hasCachedItem(final CategoryContent categoryContent) {
        final Iterator<CachedItem> iterator = this.cachedItems.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCc() == categoryContent) {
                return true;
            }
        }
        return false;
    }
    
    public CachedItem getCachedItem(final CategoryContent categoryContent) {
        for (final CachedItem cachedItem : this.cachedItems) {
            if (cachedItem.getCc() == categoryContent) {
                return cachedItem;
            }
        }
        return null;
    }
    
    public void upgradeCachedItem(final CategoryContent categoryContent, final int n) {
        final CachedItem cachedItem = this.getCachedItem(categoryContent.getIdentifier());
        if (cachedItem == null) {
            new CachedItem(categoryContent).updateItem(n, Bukkit.getPlayer(this.player));
        }
        else if (categoryContent.getContentTiers().size() > cachedItem.getTier()) {
            BeaconBattle.debug("Cached item upgrade for " + categoryContent.getIdentifier() + " player " + this.player);
            cachedItem.upgrade(n);
        }
    }
    
    public void setCategoryWeight(final ShopCategory key, final byte b) {
        if (this.categoryWeight.containsKey(key)) {
            this.categoryWeight.replace(key, b);
        }
        else {
            this.categoryWeight.put(key, b);
        }
    }
    
    public byte getCategoryWeight(final ShopCategory key) {
        return this.categoryWeight.getOrDefault(key, (Byte)0);
    }
    
    public List<CachedItem> getCachedPermanents() {
        final ArrayList<CachedItem> list = new ArrayList<CachedItem>();
        for (final CachedItem cachedItem : this.cachedItems) {
            if (cachedItem.getCc().isPermanent() && !cachedItem.getCc().isDowngradable()) {
                list.add(cachedItem);
            }
        }
        return list;
    }
    
    public List<CachedItem> getCachedItems() {
        return this.cachedItems;
    }
    
    static {
        ShopCache.shopCaches = new ArrayList<ShopCache>();
    }
    
    public class CachedItem
    {
        private CategoryContent cc;
        private int tier;
        
        public CachedItem(final CategoryContent cc) {
            this.tier = 1;
            this.cc = cc;
            ShopCache.this.cachedItems.add(this);
            BeaconBattle.debug("New Cached item " + cc.getIdentifier() + " for player " + ShopCache.this.player);
        }
        
        public int getTier() {
            return this.tier;
        }
        
        public CategoryContent getCc() {
            return this.cc;
        }
        
        public void manageDeath(final Arena arena) {
            if (!this.cc.isPermanent()) {
                return;
            }
            if (this.cc.isDowngradable() && this.tier > 1) {
                --this.tier;
            }
            BeaconBattle.debug("ShopCache Item Restore: " + this.cc.getIdentifier() + " for " + ShopCache.this.player);
            this.cc.giveItems(Bukkit.getPlayer(ShopCache.this.player), ShopCache.getShopCache(ShopCache.this.player), arena);
        }
        
        public void upgrade(final int n) {
            ++this.tier;
            final Player player = Bukkit.getPlayer(ShopCache.this.player);
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() != Material.AIR) {
                        if (BeaconBattle.nms.getShopUpgradeIdentifier(itemStack).equals(this.cc.getIdentifier())) {
                            player.getInventory().remove(itemStack);
                        }
                    }
                }
            }
            this.updateItem(n, player);
            player.updateInventory();
        }
        
        public void updateItem(final int n, final Player player) {
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null) {
                player.getOpenInventory().getTopInventory().setItem(n, this.cc.getItemStack(Bukkit.getPlayer(ShopCache.this.player), ShopCache.getShopCache(ShopCache.this.player)));
            }
        }
    }
}
