

package ws.billy.bedwars.shop;

import org.bukkit.plugin.PluginManager;
import ws.billy.bedwars.shop.listeners.SpecialsListener;
import ws.billy.bedwars.shop.listeners.PlayerDropListener;
import ws.billy.bedwars.shop.listeners.ShopOpenListener;
import ws.billy.bedwars.shop.listeners.QuickBuyListener;
import ws.billy.bedwars.shop.listeners.ShopCacheListener;
import org.bukkit.event.Listener;
import ws.billy.bedwars.shop.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.shop.main.ShopCategory;
import ws.billy.bedwars.shop.main.QuickBuyButton;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.shop.main.ShopIndex;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class ShopManager extends ConfigManager
{
    public static ShopIndex shop;
    
    public ShopManager() {
        super((Plugin)BeaconBattle.plugin, "shop", BeaconBattle.plugin.getDataFolder().getPath());
        this.saveDefaults();
        this.loadShop();
        this.registerListeners();
    }
    
    private void saveDefaults() {
        this.getYml();
        this.getYml().options().header("Shop with quick buy and tiers");
        this.getYml().addDefault("shop-settings.quick-buy-category.material", (Object)BeaconBattle.getForCurrentVersion("NETHER_STAR", "NETHER_STAR", "NETHER_STAR"));
        this.getYml().addDefault("shop-settings.quick-buy-category.amount", (Object)1);
        this.getYml().addDefault("shop-settings.quick-buy-category.data", (Object)0);
        this.getYml().addDefault("shop-settings.quick-buy-category.enchanted", (Object)false);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "RED_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.amount", (Object)1);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.data", (Object)4);
        this.getYml().addDefault("shop-settings.quick-buy-empty-item.enchanted", (Object)false);
        this.getYml().addDefault("shop-settings.regular-separator-item.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.regular-separator-item.amount", (Object)1);
        this.getYml().addDefault("shop-settings.regular-separator-item.data", (Object)7);
        this.getYml().addDefault("shop-settings.regular-separator-item.enchanted", (Object)false);
        this.getYml().addDefault("shop-settings.selected-separator-item.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GREEN_STAINED_GLASS_PANE"));
        this.getYml().addDefault("shop-settings.selected-separator-item.amount", (Object)1);
        this.getYml().addDefault("shop-settings.selected-separator-item.data", (Object)13);
        this.getYml().addDefault("shop-settings.selected-separator-item.enchanted", (Object)false);
        this.getYml().addDefault("shop-specials.silverfish.enable", (Object)true);
        this.getYml().addDefault("shop-specials.silverfish.material", (Object)BeaconBattle.getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"));
        this.getYml().addDefault("shop-specials.silverfish.data", (Object)0);
        this.getYml().addDefault("shop-specials.silverfish.health", (Object)8.0);
        this.getYml().addDefault("shop-specials.silverfish.damage", (Object)4.0);
        this.getYml().addDefault("shop-specials.silverfish.speed", (Object)0.25);
        this.getYml().addDefault("shop-specials.silverfish.despawn", (Object)15);
        this.getYml().addDefault("shop-specials.iron-golem.enable", (Object)true);
        this.getYml().addDefault("shop-specials.iron-golem.material", (Object)BeaconBattle.getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"));
        this.getYml().addDefault("shop-specials.iron-golem.data", (Object)0);
        this.getYml().addDefault("shop-specials.iron-golem.health", (Object)100.0);
        this.getYml().addDefault("shop-specials.iron-golem.despawn", (Object)240);
        this.getYml().addDefault("shop-specials.iron-golem.speed", (Object)0.25);
        if (this.isFirstTime()) {
            this.getYml().addDefault("quick-buy-defaults.element1.path", (Object)"blocks-category.category-content.wool");
            this.getYml().addDefault("quick-buy-defaults.element1.slot", (Object)19);
            this.getYml().addDefault("quick-buy-defaults.element2.path", (Object)"melee-category.category-content.stone-sword");
            this.getYml().addDefault("quick-buy-defaults.element2.slot", (Object)20);
            this.getYml().addDefault("quick-buy-defaults.element3.path", (Object)"armor-category.category-content.chainmail");
            this.getYml().addDefault("quick-buy-defaults.element3.slot", (Object)21);
            this.getYml().addDefault("quick-buy-defaults.element4.path", (Object)"ranged-category.category-content.bow1");
            this.getYml().addDefault("quick-buy-defaults.element4.slot", (Object)23);
            this.getYml().addDefault("quick-buy-defaults.element5.path", (Object)"potions-category.category-content.speed-potion");
            this.getYml().addDefault("quick-buy-defaults.element5.slot", (Object)24);
            this.getYml().addDefault("quick-buy-defaults.element6.path", (Object)"utility-category.category-content.tnt");
            this.getYml().addDefault("quick-buy-defaults.element6.slot", (Object)25);
            this.getYml().addDefault("quick-buy-defaults.element7.path", (Object)"blocks-category.category-content.wood");
            this.getYml().addDefault("quick-buy-defaults.element7.slot", (Object)28);
            this.getYml().addDefault("quick-buy-defaults.element8.path", (Object)"melee-category.category-content.iron-sword");
            this.getYml().addDefault("quick-buy-defaults.element8.slot", (Object)29);
            this.getYml().addDefault("quick-buy-defaults.element9.path", (Object)"armor-category.category-content.iron-armor");
            this.getYml().addDefault("quick-buy-defaults.element9.slot", (Object)30);
            this.getYml().addDefault("quick-buy-defaults.element10.path", (Object)"tools-category.category-content.shears");
            this.getYml().addDefault("quick-buy-defaults.element10.slot", (Object)31);
            this.getYml().addDefault("quick-buy-defaults.element11.path", (Object)"ranged-category.category-content.arrow");
            this.getYml().addDefault("quick-buy-defaults.element11.slot", (Object)32);
            this.getYml().addDefault("quick-buy-defaults.element12.path", (Object)"potions-category.category-content.jump-potion");
            this.getYml().addDefault("quick-buy-defaults.element12.slot", (Object)33);
            this.getYml().addDefault("quick-buy-defaults.element13.path", (Object)"utility-category.category-content.water-bucket");
            this.getYml().addDefault("quick-buy-defaults.element13.slot", (Object)34);
        }
        if (this.isFirstTime()) {
            this.addDefaultShopCategory("blocks-category", 1, BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 1, false);
            this.adCategoryContentTier("blocks-category", "wool", 19, "tier1", BeaconBattle.getForCurrentVersion("WOOL", "WOOL", "WHITE_WOOL"), 0, 16, false, 4, "iron", false, false);
            this.addBuyItem("blocks-category", "wool", "tier1", "wool", BeaconBattle.getForCurrentVersion("WOOL", "WOOL", "WHITE_WOOL"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "clay", 20, "tier1", BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 16, false, 12, "iron", false, false);
            this.addBuyItem("blocks-category", "clay", "tier1", "clay", BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "ORANGE_TERRACOTTA"), 1, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "glass", 21, "tier1", BeaconBattle.getForCurrentVersion("GLASS", "GLASS", "GLASS"), 0, 4, false, 12, "iron", false, false);
            this.addBuyItem("blocks-category", "glass", "tier1", "glass", BeaconBattle.getForCurrentVersion("GLASS", "GLASS", "GLASS"), 0, 4, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "stone", 22, "tier1", BeaconBattle.getForCurrentVersion("ENDER_STONE", "ENDER_STONE", "END_STONE"), 0, 16, false, 24, "iron", false, false);
            this.addBuyItem("blocks-category", "stone", "tier1", "stone", BeaconBattle.getForCurrentVersion("ENDER_STONE", "ENDER_STONE", "END_STONE"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "ladder", 23, "tier1", BeaconBattle.getForCurrentVersion("LADDER", "LADDER", "LADDER"), 0, 16, false, 4, "iron", false, false);
            this.addBuyItem("blocks-category", "ladder", "tier1", "ladder", BeaconBattle.getForCurrentVersion("LADDER", "LADDER", "LADDER"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "wood", 24, "tier1", BeaconBattle.getForCurrentVersion("WOOD", "WOOD", "OAK_WOOD"), 0, 16, false, 4, "gold", false, false);
            this.addBuyItem("blocks-category", "wood", "tier1", "wood", BeaconBattle.getForCurrentVersion("WOOD", "WOOD", "OAK_WOOD"), 0, 16, "", "", "", false);
            this.adCategoryContentTier("blocks-category", "obsidian", 25, "tier1", BeaconBattle.getForCurrentVersion("OBSIDIAN", "OBSIDIAN", "OBSIDIAN"), 0, 4, false, 4, "emerald", false, false);
            this.addBuyItem("blocks-category", "obsidian", "tier1", "obsidian", BeaconBattle.getForCurrentVersion("OBSIDIAN", "OBSIDIAN", "OBSIDIAN"), 0, 4, "", "", "", false);
            this.addDefaultShopCategory("melee-category", 2, BeaconBattle.getForCurrentVersion("GOLD_SWORD", "GOLD_SWORD", "GOLDEN_SWORD"), 0, 1, false);
            this.adCategoryContentTier("melee-category", "stone-sword", 19, "tier1", BeaconBattle.getForCurrentVersion("STONE_SWORD", "STONE_SWORD", "STONE_SWORD"), 0, 1, false, 10, "iron", false, false);
            this.addBuyItem("melee-category", "stone-sword", "tier1", "sword", BeaconBattle.getForCurrentVersion("STONE_SWORD", "STONE_SWORD", "STONE_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "iron-sword", 20, "tier1", BeaconBattle.getForCurrentVersion("IRON_SWORD", "IRON_SWORD", "IRON_SWORD"), 0, 1, false, 7, "gold", false, false);
            this.addBuyItem("melee-category", "iron-sword", "tier1", "sword", BeaconBattle.getForCurrentVersion("IRON_SWORD", "IRON_SWORD", "IRON_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "diamond-sword", 21, "tier1", BeaconBattle.getForCurrentVersion("DIAMOND_SWORD", "DIAMOND_SWORD", "DIAMOND_SWORD"), 0, 1, false, 4, "emerald", false, false);
            this.addBuyItem("melee-category", "diamond-sword", "tier1", "sword", BeaconBattle.getForCurrentVersion("DIAMOND_SWORD", "DIAMOND_SWORD", "DIAMOND_SWORD"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("melee-category", "stick", 22, "tier1", BeaconBattle.getForCurrentVersion("STICK", "STICK", "STICK"), 0, 1, true, 10, "gold", false, false);
            this.addBuyItem("melee-category", "stick", "tier1", "stick", BeaconBattle.getForCurrentVersion("STICK", "STICK", "STICK"), 0, 1, "KNOCKBACK 1", "", "", false);
            this.addDefaultShopCategory("armor-category", 3, BeaconBattle.getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, false);
            this.adCategoryContentTier("armor-category", "chainmail", 19, "tier1", BeaconBattle.getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, false, 40, "iron", true, false);
            this.addBuyItem("armor-category", "chainmail", "tier1", "boots", BeaconBattle.getForCurrentVersion("CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS", "CHAINMAIL_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "chainmail", "tier1", "leggings", BeaconBattle.getForCurrentVersion("CHAINMAIL_LEGGINGS", "CHAINMAIL_LEGGINGS", "CHAINMAIL_LEGGINGS"), 0, 1, "", "", "", true);
            this.adCategoryContentTier("armor-category", "iron-armor", 20, "tier1", BeaconBattle.getForCurrentVersion("IRON_BOOTS", "IRON_BOOTS", "IRON_BOOTS"), 0, 1, false, 12, "gold", true, false);
            this.addBuyItem("armor-category", "iron-armor", "tier1", "boots", BeaconBattle.getForCurrentVersion("IRON_BOOTS", "IRON_BOOTS", "IRON_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "iron-armor", "tier1", "leggings", BeaconBattle.getForCurrentVersion("IRON_LEGGINGS", "IRON_LEGGINGS", "IRON_LEGGINGS"), 0, 1, "", "", "", true);
            this.adCategoryContentTier("armor-category", "diamond-armor", 21, "tier1", BeaconBattle.getForCurrentVersion("DIAMOND_BOOTS", "DIAMOND_BOOTS", "DIAMOND_BOOTS"), 0, 1, false, 6, "emerald", true, false);
            this.addBuyItem("armor-category", "diamond-armor", "tier1", "boots", BeaconBattle.getForCurrentVersion("DIAMOND_BOOTS", "DIAMOND_BOOTS", "DIAMOND_BOOTS"), 0, 1, "", "", "", true);
            this.addBuyItem("armor-category", "diamond-armor", "tier1", "leggings", BeaconBattle.getForCurrentVersion("DIAMOND_LEGGINGS", "DIAMOND_LEGGINGS", "DIAMOND_LEGGINGS"), 0, 1, "", "", "", true);
            this.addDefaultShopCategory("tools-category", 4, BeaconBattle.getForCurrentVersion("STONE_PICKAXE", "STONE_PICKAXE", "STONE_PICKAXE"), 0, 1, false);
            this.adCategoryContentTier("tools-category", "shears", 19, "tier1", BeaconBattle.getForCurrentVersion("SHEARS", "SHEARS", "SHEARS"), 0, 1, false, 20, "iron", true, false);
            this.addBuyItem("tools-category", "shears", "tier1", "shears", BeaconBattle.getForCurrentVersion("SHEARS", "SHEARS", "SHEARS"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier1", BeaconBattle.getForCurrentVersion("WOOD_PICKAXE", "WOOD_PICKAXE", "WOODEN_PICKAXE"), 0, 1, false, 10, "iron", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier1", "wooden-pickaxe", BeaconBattle.getForCurrentVersion("WOOD_PICKAXE", "WOOD_PICKAXE", "WOODEN_PICKAXE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier2", BeaconBattle.getForCurrentVersion("IRON_PICKAXE", "IRON_PICKAXE", "IRON_PICKAXE"), 0, 1, true, 10, "iron", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier2", "iron-pickaxe", BeaconBattle.getForCurrentVersion("IRON_PICKAXE", "IRON_PICKAXE", "IRON_PICKAXE"), 0, 1, "DIG_SPEED 2", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier3", BeaconBattle.getForCurrentVersion("GOLD_PICKAXE", "GOLD_PICKAXE", "GOLDEN_PICKAXE"), 0, 1, true, 3, "gold", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier3", "gold-pickaxe", BeaconBattle.getForCurrentVersion("GOLD_PICKAXE", "GOLD_PICKAXE", "GOLDEN_PICKAXE"), 0, 1, "DIG_SPEED 3,DAMAGE_ALL 2", "", "", false);
            this.adCategoryContentTier("tools-category", "pickaxe", 20, "tier4", BeaconBattle.getForCurrentVersion("DIAMOND_PICKAXE", "DIAMOND_PICKAXE", "DIAMOND_PICKAXE"), 0, 1, true, 6, "gold", true, true);
            this.addBuyItem("tools-category", "pickaxe", "tier4", "diamond-pickaxe", BeaconBattle.getForCurrentVersion("DIAMOND_PICKAXE", "DIAMOND_PICKAXE", "DIAMOND_PICKAXE"), 0, 1, "DIG_SPEED 3", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier1", BeaconBattle.getForCurrentVersion("WOOD_AXE", "WOOD_AXE", "WOODEN_AXE"), 0, 1, false, 10, "iron", true, true);
            this.addBuyItem("tools-category", "axe", "tier1", "wooden-axe", BeaconBattle.getForCurrentVersion("WOOD_AXE", "WOOD_AXE", "WOODEN_AXE"), 0, 1, "DIG_SPEED 1", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier2", BeaconBattle.getForCurrentVersion("IRON_AXE", "IRON_AXE", "IRON_AXE"), 0, 1, true, 10, "iron", true, true);
            this.addBuyItem("tools-category", "axe", "tier2", "iron-axe", BeaconBattle.getForCurrentVersion("IRON_AXE", "IRON_AXE", "IRON_AXE"), 0, 1, "DIG_SPEED 1", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier3", BeaconBattle.getForCurrentVersion("GOLD_AXE", "GOLD_AXE", "GOLDEN_AXE"), 0, 1, true, 3, "gold", true, true);
            this.addBuyItem("tools-category", "axe", "tier3", "gold-axe", BeaconBattle.getForCurrentVersion("GOLD_AXE", "GOLD_AXE", "GOLDEN_AXE"), 0, 1, "DIG_SPEED 2", "", "", false);
            this.adCategoryContentTier("tools-category", "axe", 21, "tier4", BeaconBattle.getForCurrentVersion("DIAMOND_AXE", "DIAMOND_AXE", "DIAMOND_AXE"), 0, 1, true, 6, "gold", true, true);
            this.addBuyItem("tools-category", "axe", "tier4", "diamond-axe", BeaconBattle.getForCurrentVersion("DIAMOND_AXE", "DIAMOND_AXE", "DIAMOND_AXE"), 0, 1, "DIG_SPEED 3", "", "", false);
            this.addDefaultShopCategory("ranged-category", 5, BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, false);
            this.adCategoryContentTier("ranged-category", "arrow", 19, "tier1", BeaconBattle.getForCurrentVersion("ARROW", "ARROW", "ARROW"), 0, 8, false, 2, "gold", false, false);
            this.addBuyItem("ranged-category", "arrow", "tier1", "arrows", BeaconBattle.getForCurrentVersion("ARROW", "ARROW", "ARROW"), 0, 8, "", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow1", 20, "tier1", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, false, 12, "gold", false, false);
            this.addBuyItem("ranged-category", "bow1", "tier1", "bow", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow2", 21, "tier1", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, true, 24, "gold", false, false);
            this.addBuyItem("ranged-category", "bow2", "tier1", "bow", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "ARROW_DAMAGE 1", "", "", false);
            this.adCategoryContentTier("ranged-category", "bow3", 22, "tier1", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, true, 6, "emerald", false, false);
            this.addBuyItem("ranged-category", "bow3", "tier1", "bow", BeaconBattle.getForCurrentVersion("BOW", "BOW", "BOW"), 0, 1, "ARROW_DAMAGE 1,ARROW_KNOCKBACK 1", "", "", false);
            this.addDefaultShopCategory("potions-category", 6, BeaconBattle.getForCurrentVersion("BREWING_STAND_ITEM", "BREWING_STAND_ITEM", "BREWING_STAND"), 0, 1, false);
            this.adCategoryContentTier("potions-category", "jump-potion", 20, "tier1", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 1, "emerald", false, false);
            this.addBuyItem("potions-category", "jump-potion", "tier1", "jump", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "JUMP 45 5", "", false);
            this.adCategoryContentTier("potions-category", "speed-potion", 19, "tier1", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 1, "emerald", false, false);
            this.addBuyItem("potions-category", "speed-potion", "tier1", "speed", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "SPEED 45 2", "", false);
            this.adCategoryContentTier("potions-category", "invisibility", 21, "tier1", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, false, 2, "emerald", false, false);
            this.addBuyItem("potions-category", "invisibility", "tier1", "invisibility", BeaconBattle.getForCurrentVersion("POTION", "POTION", "POTION"), 0, 1, "", "INVISIBILITY 30 1", "", false);
            this.addDefaultShopCategory("utility-category", 7, BeaconBattle.getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, false);
            this.adCategoryContentTier("utility-category", "golden-apple", 19, "tier1", BeaconBattle.getForCurrentVersion("GOLDEN_APPLE", "GOLDEN_APPLE", "GOLDEN_APPLE"), 0, 1, false, 3, "gold", false, false);
            this.addBuyItem("utility-category", "golden-apple", "tier1", "apple", BeaconBattle.getForCurrentVersion("GOLDEN_APPLE", "GOLDEN_APPLE", "GOLDEN_APPLE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "bedbug", 20, "tier1", BeaconBattle.getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"), 0, 1, false, 40, "iron", false, false);
            this.addBuyItem("utility-category", "bedbug", "tier1", "bedbug", BeaconBattle.getForCurrentVersion("SNOW_BALL", "SNOW_BALL", "SNOWBALL"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "dream-defender", 21, "tier1", BeaconBattle.getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"), 0, 1, false, 120, "iron", false, false);
            this.addBuyItem("utility-category", "dream-defender", "tier1", "defender", BeaconBattle.getForCurrentVersion("MONSTER_EGG", "MONSTER_EGG", "HORSE_SPAWN_EGG"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "fireball", 22, "tier1", BeaconBattle.getForCurrentVersion("FIREBALL", "FIREBALL", "FIRE_CHARGE"), 0, 1, false, 40, "iron", false, false);
            this.addBuyItem("utility-category", "fireball", "tier1", "fireball", BeaconBattle.getForCurrentVersion("FIREBALL", "FIREBALL", "FIRE_CHARGE"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "tnt", 23, "tier1", BeaconBattle.getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "tnt", "tier1", "tnt", BeaconBattle.getForCurrentVersion("TNT", "TNT", "TNT"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "ender-pearl", 24, "tier1", BeaconBattle.getForCurrentVersion("ENDER_PEARL", "ENDER_PEARL", "ENDER_PEARL"), 0, 1, false, 4, "emerald", false, false);
            this.addBuyItem("utility-category", "ender-pearl", "tier1", "ender-pearl", BeaconBattle.getForCurrentVersion("ENDER_PEARL", "ENDER_PEARL", "ENDER_PEARL"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "water-bucket", 25, "tier1", BeaconBattle.getForCurrentVersion("WATER_BUCKET", "WATER_BUCKET", "WATER_BUCKET"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "water-bucket", "tier1", "water-bucket", BeaconBattle.getForCurrentVersion("WATER_BUCKET", "WATER_BUCKET", "WATER_BUCKET"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "bridge-egg", 28, "tier1", BeaconBattle.getForCurrentVersion("EGG", "EGG", "EGG"), 0, 1, false, 3, "emerald", false, false);
            this.addBuyItem("utility-category", "bridge-egg", "tier1", "egg", BeaconBattle.getForCurrentVersion("EGG", "EGG", "EGG"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "magic-milk", 29, "tier1", BeaconBattle.getForCurrentVersion("MILK_BUCKET", "MILK_BUCKET", "MILK_BUCKET"), 0, 1, false, 4, "gold", false, false);
            this.addBuyItem("utility-category", "magic-milk", "tier1", "milk", BeaconBattle.getForCurrentVersion("MILK_BUCKET", "MILK_BUCKET", "MILK_BUCKET"), 0, 1, "", "", "", false);
            this.adCategoryContentTier("utility-category", "sponge", 30, "tier1", BeaconBattle.getForCurrentVersion("SPONGE", "SPONGE", "SPONGE"), 0, 1, false, 3, "gold", false, false);
            this.addBuyItem("utility-category", "sponge", "tier1", "sponge", BeaconBattle.getForCurrentVersion("SPONGE", "SPONGE", "SPONGE"), 0, 1, "", "", "", false);
        }
        if (this.getYml().get("armor-category.category-content.diamond-armor") != null) {
            this.getYml().addDefault("armor-category.category-content.diamond-armor.content-settings.weight", (Object)2);
        }
        if (this.getYml().get("armor-category.category-content.iron-armor") != null) {
            this.getYml().addDefault("armor-category.category-content.iron-armor.content-settings.weight", (Object)1);
        }
        try {
            final String string = this.getYml().getString("shop-specials.iron-golem.material");
            BeaconBattle.debug("shop-specials.iron-golem.material is set to: " + string);
            Material.valueOf(string);
        }
        catch (Exception ex) {
            BeaconBattle.plugin.getLogger().severe("Invalid material at shop-specials.iron-golem.material");
        }
        try {
            final String string2 = this.getYml().getString("shop-specials.silverfish.material");
            BeaconBattle.debug("shop-specials.silverfish.material is set to: " + string2);
            Material.valueOf(string2);
        }
        catch (Exception ex2) {
            BeaconBattle.plugin.getLogger().severe("Invalid material at shop-specials.silverfish.material");
        }
        this.getYml().options().copyDefaults(true);
        this.save();
    }
    
    private void loadShop() {
        ItemStack itemStack = BeaconBattle.nms.createItemStack(this.getYml().getString("shop-settings.quick-buy-category.material"), this.getYml().getInt("shop-settings.quick-buy-category.amount"), (short)this.getYml().getInt("shop-settings.quick-buy-category.data"));
        if (this.getYml().getBoolean("shop-settings.quick-buy-category.enchanted")) {
            itemStack = enchantItem(itemStack);
        }
        final QuickBuyButton quickBuyButton = new QuickBuyButton(0, itemStack, "shop-items-messages.quick-buy-item-name", "shop-items-messages.quick-buy-item-lore");
        ItemStack itemStack2 = BeaconBattle.nms.createItemStack(this.getYml().getString("shop-settings.regular-separator-item.material"), this.getYml().getInt("shop-settings.regular-separator-item.amount"), (short)this.getYml().getInt("shop-settings.regular-separator-item.data"));
        if (this.getYml().getBoolean("shop-settings.regular-separator-item.enchanted")) {
            itemStack2 = enchantItem(itemStack2);
        }
        ItemStack itemStack3 = BeaconBattle.nms.createItemStack(this.getYml().getString("shop-settings.selected-separator-item.material"), this.getYml().getInt("shop-settings.selected-separator-item.amount"), (short)this.getYml().getInt("shop-settings.selected-separator-item.data"));
        if (this.getYml().getBoolean("shop-settings.selected-separator-item.enchanted")) {
            itemStack3 = enchantItem(itemStack3);
        }
        ShopManager.shop = new ShopIndex("shop-items-messages.inventory-name", quickBuyButton, "shop-items-messages.separator-item-name", "shop-items-messages.separator-item-lore", itemStack3, itemStack2);
        for (final String s : this.getYml().getConfigurationSection("").getKeys(false)) {
            if (s.equalsIgnoreCase("shop-settings")) {
                continue;
            }
            if (s.equals("quick-buy-defaults")) {
                continue;
            }
            if (s.equalsIgnoreCase("shop-specials")) {
                continue;
            }
            final ShopCategory shopCategory = new ShopCategory(s, this.getYml());
            if (!shopCategory.isLoaded()) {
                continue;
            }
            ShopManager.shop.addShopCategory(shopCategory);
        }
    }
    
    public static ItemMeta hideItemStuff(final ItemMeta itemMeta) {
        itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON });
        return itemMeta;
    }
    
    public static ItemStack enchantItem(final ItemStack itemStack) {
        final ItemStack itemStack2 = new ItemStack(itemStack);
        final ItemMeta itemMeta = itemStack2.getItemMeta();
        itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        itemStack2.setItemMeta(hideItemStuff(itemMeta));
        return itemStack2;
    }
    
    private void addDefaultShopCategory(final String str, final int i, final String s, final int j, final int k, final boolean b) {
        this.getYml().addDefault(str + ".category-slot", (Object)i);
        this.getYml().addDefault(str + ".category-item.material", (Object)s);
        this.getYml().addDefault(str + ".category-item.data", (Object)j);
        this.getYml().addDefault(str + ".category-item.amount", (Object)k);
        this.getYml().addDefault(str + ".category-item.enchanted", (Object)b);
    }
    
    public void adCategoryContentTier(String str, final String str2, final int i, final String str3, final String s, final int j, final int k, final boolean b, final int l, final String s2, final boolean b2, final boolean b3) {
        str = str + ".category-content." + str2 + ".";
        this.getYml().addDefault(str + "content-settings.content-slot", (Object)i);
        this.getYml().addDefault(str + "content-settings.is-permanent", (Object)b2);
        this.getYml().addDefault(str + "content-settings.is-downgradable", (Object)b3);
        str = str + "content-tiers." + str3;
        this.getYml().addDefault(str + ".tier-item.material", (Object)s);
        this.getYml().addDefault(str + ".tier-item.data", (Object)j);
        this.getYml().addDefault(str + ".tier-item.amount", (Object)k);
        this.getYml().addDefault(str + ".tier-item.enchanted", (Object)b);
        this.getYml().addDefault(str + ".tier-settings.cost", (Object)l);
        this.getYml().addDefault(str + ".tier-settings.currency", (Object)s2);
    }
    
    public void addBuyItem(String string, final String str, final String str2, final String str3, final String s, final int i, final int j, final String s2, final String s3, final String s4, final boolean b) {
        string = string + ".category-content." + str + "." + "content-tiers" + "." + str2 + "." + "buy-items" + "." + str3 + ".";
        this.getYml().addDefault(string + "material", (Object)s);
        this.getYml().addDefault(string + "data", (Object)i);
        this.getYml().addDefault(string + "amount", (Object)j);
        if (!s2.isEmpty()) {
            this.getYml().addDefault(string + "enchants", (Object)s2);
        }
        if (!s3.isEmpty()) {
            this.getYml().addDefault(string + "potion", (Object)s3);
        }
        if (b) {
            this.getYml().addDefault(string + "auto-equip", (Object)true);
        }
        if (!s4.isEmpty()) {
            this.getYml().addDefault(string + "name", (Object)s4);
        }
    }
    
    public static ShopIndex getShop() {
        return ShopManager.shop;
    }
    
    private void registerListeners() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents((Listener)new InventoryListener(), (Plugin)BeaconBattle.plugin);
        pluginManager.registerEvents((Listener)new ShopCacheListener(), (Plugin)BeaconBattle.plugin);
        pluginManager.registerEvents((Listener)new QuickBuyListener(), (Plugin)BeaconBattle.plugin);
        pluginManager.registerEvents((Listener)new ShopOpenListener(), (Plugin)BeaconBattle.plugin);
        pluginManager.registerEvents((Listener)new PlayerDropListener(), (Plugin)BeaconBattle.plugin);
        pluginManager.registerEvents((Listener)new SpecialsListener(), (Plugin)BeaconBattle.plugin);
    }
}
