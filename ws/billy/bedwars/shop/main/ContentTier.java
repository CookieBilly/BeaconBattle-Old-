

package ws.billy.bedwars.shop.main;

import java.util.Iterator;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.api.arena.shop.IBuyItem;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.shop.IContentTier;

public class ContentTier implements IContentTier
{
    private int value;
    private int price;
    private ItemStack itemStack;
    private Material currency;
    private List<IBuyItem> buyItemsList;
    private boolean loaded;
    
    public ContentTier(final String str, final String s, final String s2, final YamlConfiguration yamlConfiguration) {
        this.buyItemsList = new ArrayList<IBuyItem>();
        this.loaded = false;
        BeaconBattle.debug("Loading content tier" + str);
        if (yamlConfiguration.get(str + ".tier-item.material") == null) {
            BeaconBattle.plugin.getLogger().severe("tier-item material not set at " + str);
            return;
        }
        try {
            this.value = Integer.parseInt(s.replace("tier", ""));
        }
        catch (Exception ex) {
            BeaconBattle.plugin.getLogger().severe(str + " doesn't end with a number. It's not recognized as a tier!");
            return;
        }
        if (yamlConfiguration.get(str + ".tier-settings.cost") == null) {
            BeaconBattle.plugin.getLogger().severe("Cost not set for " + str);
            return;
        }
        this.price = yamlConfiguration.getInt(str + ".tier-settings.cost");
        if (yamlConfiguration.get(str + ".tier-settings.currency") == null) {
            BeaconBattle.plugin.getLogger().severe("Currency not set for " + str);
            return;
        }
        if (yamlConfiguration.getString(str + ".tier-settings.currency").toLowerCase().isEmpty()) {
            BeaconBattle.plugin.getLogger().severe("Invalid currency at " + str);
            return;
        }
        final String lowerCase = yamlConfiguration.getString(str + ".tier-settings.currency").toLowerCase();
        switch (lowerCase) {
            case "iron":
            case "gold":
            case "diamond":
            case "vault":
            case "emerald": {
                this.currency = CategoryContent.getCurrency(yamlConfiguration.getString(str + ".tier-settings.currency").toLowerCase());
                break;
            }
            default: {
                BeaconBattle.plugin.getLogger().severe("Invalid currency at " + str);
                this.currency = Material.IRON_INGOT;
                break;
            }
        }
        this.itemStack = BeaconBattle.nms.createItemStack(yamlConfiguration.getString(str + ".tier-item.material"), (yamlConfiguration.get(str + ".tier-item.amount") == null) ? 1 : yamlConfiguration.getInt(str + ".tier-item.amount"), (short)((yamlConfiguration.get(str + ".tier-item.data") == null) ? 0 : yamlConfiguration.getInt(str + ".tier-item.data")));
        if (yamlConfiguration.get(str + ".tier-item.enchanted") != null && yamlConfiguration.getBoolean(str + ".tier-item.enchanted")) {
            this.itemStack = ShopManager.enchantItem(this.itemStack);
        }
        this.itemStack.setItemMeta(ShopManager.hideItemStuff(this.itemStack.getItemMeta()));
        final Iterator<String> iterator = yamlConfiguration.getConfigurationSection(str + "." + "buy-items").getKeys(false).iterator();
        while (iterator.hasNext()) {
            final BuyItem buyItem = new BuyItem(str + "." + "buy-items" + "." + iterator.next(), yamlConfiguration, s2);
            if (buyItem.isLoaded()) {
                this.buyItemsList.add(buyItem);
            }
        }
        this.loaded = true;
    }
    
    @Override
    public int getPrice() {
        return this.price;
    }
    
    @Override
    public Material getCurrency() {
        return this.currency;
    }
    
    @Override
    public void setCurrency(final Material currency) {
        this.currency = currency;
    }
    
    @Override
    public void setPrice(final int price) {
        this.price = price;
    }
    
    @Override
    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    @Override
    public void setBuyItemsList(final List<IBuyItem> buyItemsList) {
        this.buyItemsList = buyItemsList;
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    @Override
    public List<IBuyItem> getBuyItemsList() {
        return this.buyItemsList;
    }
}
