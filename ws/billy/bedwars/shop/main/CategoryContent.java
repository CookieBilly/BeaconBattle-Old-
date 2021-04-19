

package ws.billy.bedwars.shop.main;

import org.bukkit.Material;
import ws.billy.bedwars.shop.quickbuy.QuickBuyElement;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.shop.IBuyItem;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.shop.ShopBuyEvent;
import org.bukkit.ChatColor;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.Bukkit;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.entity.Player;
import java.util.Iterator;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.api.arena.shop.IContentTier;
import java.util.List;
import ws.billy.bedwars.api.arena.shop.ICategoryContent;

public class CategoryContent implements ICategoryContent
{
    private int slot;
    private boolean loaded;
    private List<IContentTier> contentTiers;
    private String contentName;
    private String itemNamePath;
    private String itemLorePath;
    private String identifier;
    private boolean permanent;
    private boolean downgradable;
    private byte weight;
    private ShopCategory father;
    
    public CategoryContent(final String identifier, final String contentName, final String s, final YamlConfiguration yamlConfiguration, final ShopCategory father) {
        this.loaded = false;
        this.contentTiers = new ArrayList<IContentTier>();
        this.permanent = false;
        this.downgradable = false;
        this.weight = 0;
        BeaconBattle.debug("Loading CategoryContent " + identifier);
        this.contentName = contentName;
        this.father = father;
        if (yamlConfiguration.get(identifier + "." + "content-settings.content-slot") == null) {
            BeaconBattle.plugin.getLogger().severe("Content slot not set at " + identifier);
            return;
        }
        if (yamlConfiguration.get(identifier + "." + "content-tiers") == null) {
            BeaconBattle.plugin.getLogger().severe("No tiers set for " + identifier);
            return;
        }
        if (yamlConfiguration.getConfigurationSection(identifier + "." + "content-tiers").getKeys(false).isEmpty()) {
            BeaconBattle.plugin.getLogger().severe("No tiers set for " + identifier);
            return;
        }
        if (yamlConfiguration.get(identifier + "." + "content-tiers" + ".tier1") == null) {
            BeaconBattle.plugin.getLogger().severe("tier1 not found for " + identifier);
            return;
        }
        if (yamlConfiguration.get(identifier + "." + "content-settings.is-permanent") != null) {
            this.permanent = yamlConfiguration.getBoolean(identifier + "." + "content-settings.is-permanent");
        }
        if (yamlConfiguration.get(identifier + "." + "content-settings.is-downgradable") != null) {
            this.downgradable = yamlConfiguration.getBoolean(identifier + "." + "content-settings.is-downgradable");
        }
        if (yamlConfiguration.get(identifier + "." + "content-settings.weight") != null) {
            this.weight = (byte)yamlConfiguration.getInt(identifier + "." + "content-settings.weight");
        }
        this.slot = yamlConfiguration.getInt(identifier + "." + "content-settings.content-slot");
        for (final String str : yamlConfiguration.getConfigurationSection(identifier + "." + "content-tiers").getKeys(false)) {
            this.contentTiers.add(new ContentTier(identifier + "." + "content-tiers" + "." + str, str, identifier, yamlConfiguration));
        }
        this.itemNamePath = "shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s).replace("%content%", this.contentName);
        for (final Language language : Language.getLanguages()) {
            if (!language.exists(this.itemNamePath)) {
                language.set(this.itemNamePath, "&cName not set");
            }
        }
        this.itemLorePath = "shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s).replace("%content%", this.contentName);
        for (final Language language2 : Language.getLanguages()) {
            if (!language2.exists(this.itemLorePath)) {
                language2.set(this.itemLorePath, "&cLore not set");
            }
        }
        this.identifier = identifier;
        this.loaded = true;
    }
    
    public void execute(final Player player, final ShopCache shopCache, final int n) {
        if (shopCache.getCategoryWeight(this.father) > this.weight) {
            return;
        }
        if (shopCache.getContentTier(this.getIdentifier()) > this.contentTiers.size()) {
            Bukkit.getLogger().severe("Wrong tier order at: " + this.getIdentifier());
            return;
        }
        IContentTier contentTier;
        if (shopCache.getContentTier(this.getIdentifier()) == this.contentTiers.size()) {
            if (this.isPermanent() && shopCache.hasCachedItem(this)) {
                player.sendMessage(Language.getMsg(player, Messages.SHOP_ALREADY_BOUGHT));
                Sounds.playSound("shop-insufficient-money", player);
                return;
            }
            contentTier = this.contentTiers.get(shopCache.getContentTier(this.getIdentifier()) - 1);
        }
        else if (!shopCache.hasCachedItem(this)) {
            contentTier = this.contentTiers.get(0);
        }
        else {
            contentTier = this.contentTiers.get(shopCache.getContentTier(this.getIdentifier()));
        }
        final int calculateMoney = calculateMoney(player, contentTier.getCurrency());
        if (calculateMoney < contentTier.getPrice()) {
            player.sendMessage(Language.getMsg(player, Messages.SHOP_INSUFFICIENT_MONEY).replace("{currency}", Language.getMsg(player, getCurrencyMsgPath(contentTier))).replace("{amount}", String.valueOf(contentTier.getPrice() - calculateMoney)));
            Sounds.playSound("shop-insufficient-money", player);
            return;
        }
        takeMoney(player, contentTier.getCurrency(), contentTier.getPrice());
        shopCache.upgradeCachedItem(this, n);
        this.giveItems(player, shopCache, Arena.getArenaByPlayer(player));
        Sounds.playSound("shop-bought", player);
        player.sendMessage(Language.getMsg(player, Messages.SHOP_NEW_PURCHASE).replace("{item}", ChatColor.stripColor(Language.getMsg(player, this.itemNamePath))).replace("{color}", "").replace("{tier}", ""));
        Bukkit.getPluginManager().callEvent((Event)new ShopBuyEvent(player, this));
        shopCache.setCategoryWeight(this.father, this.weight);
    }
    
    public void giveItems(final Player player, final ShopCache shopCache, final IArena arena) {
        final Iterator<IBuyItem> iterator = this.contentTiers.get(shopCache.getContentTier(this.getIdentifier()) - 1).getBuyItemsList().iterator();
        while (iterator.hasNext()) {
            iterator.next().give(player, arena);
        }
    }
    
    @Override
    public int getSlot() {
        return this.slot;
    }
    
    @Override
    public ItemStack getItemStack(final Player player) {
        final ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        return (shopCache == null) ? null : this.getItemStack(player, shopCache);
    }
    
    @Override
    public boolean hasQuick(final Player player) {
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(player.getUniqueId());
        return quickBuyCache != null && this.hasQuick(quickBuyCache);
    }
    
    public ItemStack getItemStack(final Player player, final ShopCache shopCache) {
        IContentTier contentTier;
        if (shopCache.getContentTier(this.identifier) == this.contentTiers.size()) {
            contentTier = this.contentTiers.get(this.contentTiers.size() - 1);
        }
        else if (shopCache.hasCachedItem(this)) {
            contentTier = this.contentTiers.get(shopCache.getContentTier(this.identifier));
        }
        else {
            contentTier = this.contentTiers.get(shopCache.getContentTier(this.identifier) - 1);
        }
        final ItemStack itemStack = contentTier.getItemStack();
        final ItemMeta clone = itemStack.getItemMeta().clone();
        final boolean b = calculateMoney(player, contentTier.getCurrency()) >= contentTier.getPrice();
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(player.getUniqueId());
        final boolean b2 = quickBuyCache != null && this.hasQuick(quickBuyCache);
        final String msg = Language.getMsg(player, b ? "shop-items-messages.can-buy-color" : "shop-items-messages.cant-buy-color");
        final String msg2 = Language.getMsg(player, getCurrencyMsgPath(contentTier));
        final ChatColor currencyColor = getCurrencyColor(contentTier.getCurrency());
        final String romanNumber = getRomanNumber(contentTier.getValue());
        String replacement;
        if (this.isPermanent() && shopCache.hasCachedItem(this) && shopCache.getCachedItem(this).getTier() == this.getContentTiers().size()) {
            replacement = Language.getMsg(player, "shop-lore-status-tier-maxed");
        }
        else if (!b) {
            replacement = Language.getMsg(player, "shop-lore-status-cant-afford").replace("{currency}", msg2);
        }
        else {
            replacement = Language.getMsg(player, "shop-lore-status-can-buy");
        }
        clone.setDisplayName(Language.getMsg(player, this.itemNamePath).replace("{color}", msg).replace("{tier}", romanNumber));
        final ArrayList<String> lore = new ArrayList<String>();
        for (String s : Language.getList(player, this.itemLorePath)) {
            if (s.contains("{quick_buy}")) {
                if (b2) {
                    if (!ShopIndex.getIndexViewers().contains(player.getUniqueId())) {
                        continue;
                    }
                    s = Language.getMsg(player, "shop-lore-quick-remove");
                }
                else {
                    s = Language.getMsg(player, "shop-lore-quick-add");
                }
            }
            lore.add(s.replace("{tier}", romanNumber).replace("{color}", msg).replace("{cost}", currencyColor + String.valueOf(contentTier.getPrice())).replace("{currency}", currencyColor + msg2).replace("{buy_status}", replacement));
        }
        clone.setLore((List)lore);
        itemStack.setItemMeta(clone);
        return itemStack;
    }
    
    public boolean hasQuick(final PlayerQuickBuyCache playerQuickBuyCache) {
        final Iterator<QuickBuyElement> iterator = playerQuickBuyCache.getElements().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCategoryContent() == this) {
                return true;
            }
        }
        return false;
    }
    
    public static int calculateMoney(final Player player, final Material material) {
        if (material == Material.AIR) {
            return (int)BeaconBattle.getEconomy().getMoney(player);
        }
        int n = 0;
        for (final ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType() == material) {
                    n += itemStack.getAmount();
                }
            }
        }
        return n;
    }
    
    public static Material getCurrency(final String s) {
        Material material = null;
        switch (s) {
            default: {
                material = Material.IRON_INGOT;
                break;
            }
            case "gold": {
                material = Material.GOLD_INGOT;
                break;
            }
            case "diamond": {
                material = Material.DIAMOND;
                break;
            }
            case "emerald": {
                material = Material.EMERALD;
                break;
            }
            case "vault": {
                material = Material.AIR;
                break;
            }
        }
        return material;
    }
    
    public static ChatColor getCurrencyColor(final Material material) {
        ChatColor chatColor = ChatColor.DARK_GREEN;
        if (material.toString().toLowerCase().contains("diamond")) {
            chatColor = ChatColor.AQUA;
        }
        else if (material.toString().toLowerCase().contains("gold")) {
            chatColor = ChatColor.GOLD;
        }
        else if (material.toString().toLowerCase().contains("iron")) {
            chatColor = ChatColor.WHITE;
        }
        return chatColor;
    }
    
    public static String getCurrencyMsgPath(final IContentTier contentTier) {
        String s;
        if (contentTier.getCurrency().toString().toLowerCase().contains("iron")) {
            s = ((contentTier.getPrice() == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
        }
        else if (contentTier.getCurrency().toString().toLowerCase().contains("gold")) {
            s = ((contentTier.getPrice() == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
        }
        else if (contentTier.getCurrency().toString().toLowerCase().contains("emerald")) {
            s = ((contentTier.getPrice() == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
        }
        else if (contentTier.getCurrency().toString().toLowerCase().contains("diamond")) {
            s = ((contentTier.getPrice() == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
        }
        else {
            s = ((contentTier.getPrice() == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
        }
        return s;
    }
    
    public static String getRomanNumber(final int i) {
        String value = null;
        switch (i) {
            default: {
                value = String.valueOf(i);
                break;
            }
            case 1: {
                value = "I";
                break;
            }
            case 2: {
                value = "II";
                break;
            }
            case 3: {
                value = "III";
                break;
            }
            case 4: {
                value = "IV";
                break;
            }
            case 5: {
                value = "V";
                break;
            }
            case 6: {
                value = "VI";
                break;
            }
            case 7: {
                value = "VII";
                break;
            }
            case 8: {
                value = "VIII";
                break;
            }
            case 9: {
                value = "IX";
                break;
            }
            case 10: {
                value = "X";
                break;
            }
        }
        return value;
    }
    
    public static void takeMoney(final Player player, final Material material, final int n) {
        if (material != Material.AIR) {
            int n2 = n;
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == material) {
                        if (itemStack.getAmount() >= n2) {
                            BeaconBattle.nms.minusAmount(player, itemStack, n2);
                            player.updateInventory();
                            break;
                        }
                        n2 -= itemStack.getAmount();
                        BeaconBattle.nms.minusAmount(player, itemStack, itemStack.getAmount());
                        player.updateInventory();
                    }
                }
            }
            return;
        }
        if (!BeaconBattle.getEconomy().isEconomy()) {
            player.sendMessage("§4§lERROR: This requires Vault Support! Please install Vault plugin!");
            return;
        }
        BeaconBattle.getEconomy().buyAction(player, n);
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    @Override
    public boolean isPermanent() {
        return this.permanent;
    }
    
    @Override
    public boolean isDowngradable() {
        return this.downgradable;
    }
    
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public List<IContentTier> getContentTiers() {
        return this.contentTiers;
    }
}
