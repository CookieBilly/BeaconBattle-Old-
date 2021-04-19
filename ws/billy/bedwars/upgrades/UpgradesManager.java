

package ws.billy.bedwars.upgrades;

import org.bukkit.ChatColor;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ws.billy.bedwars.upgrades.menu.MenuBaseTrap;
import ws.billy.bedwars.upgrades.menu.MenuTrapSlot;
import ws.billy.bedwars.upgrades.menu.MenuSeparator;
import ws.billy.bedwars.upgrades.menu.UpgradeTier;
import ws.billy.bedwars.upgrades.menu.MenuUpgrade;
import ws.billy.bedwars.upgrades.menu.MenuCategory;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.upgrades.menu.InternalMenu;
import java.util.Iterator;
import ws.billy.bedwars.upgrades.listeners.UpgradeOpenListener;
import ws.billy.bedwars.upgrades.listeners.InventoryListener;
import org.bukkit.event.Listener;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import java.io.File;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.configuration.UpgradesConfig;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.upgrades.UpgradesIndex;
import ws.billy.bedwars.api.upgrades.MenuContent;

import java.util.HashMap;
import java.util.UUID;
import java.util.LinkedList;

public class UpgradesManager
{
    private static LinkedList<UUID> upgradeViewers;
    private static HashMap<String, MenuContent> menuContentByName;
    private static HashMap<String, UpgradesIndex> menuByName;
    private static HashMap<IArena, UpgradesIndex> customMenuForArena;
    private static UpgradesConfig upgrades;
    
    private UpgradesManager() {
    }
    
    public static void init() {
        new File(BeaconBattle.plugin.getDataFolder(), "/upgrades.yml").delete();
        UpgradesManager.upgrades = new UpgradesConfig("upgrades2", BeaconBattle.plugin.getDataFolder().getPath());
        for (final String s2 : UpgradesManager.upgrades.getYml().getConfigurationSection("").getKeys(false)) {
            final String s = s2;
            if (s.startsWith("upgrade-")) {
                if (getMenuContent(s2) != null || loadUpgrade(s2)) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not load upgrade: " + s2);
            }
            else if (s.startsWith("separator-")) {
                if (getMenuContent(s2) != null || loadSeparator(s2)) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not load separator: " + s2);
            }
            else if (s.startsWith("category-")) {
                if (getMenuContent(s2) != null || loadCategory(s2)) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not load category: " + s2);
            }
            else if (s.startsWith("base-trap-")) {
                if (getMenuContent(s2) != null || loadBaseTrap(s2)) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not base trap: " + s2);
            }
            else {
                if (!s.endsWith("-upgrades-settings")) {
                    continue;
                }
                final String replace = s.replace("-upgrades-settings", "");
                if (replace.isEmpty() || loadMenu(replace)) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not load menu: " + replace);
            }
        }
        BeaconBattle.registerEvents((Listener)new InventoryListener(), (Listener)new UpgradeOpenListener());
    }
    
    public static boolean isWatchingUpgrades(final UUID o) {
        return UpgradesManager.upgradeViewers.contains(o);
    }
    
    public static void setWatchingUpgrades(final UUID uuid) {
        if (!UpgradesManager.upgradeViewers.contains(uuid)) {
            UpgradesManager.upgradeViewers.add(uuid);
        }
    }
    
    public static void removeWatchingUpgrades(final UUID o) {
        UpgradesManager.upgradeViewers.remove(o);
    }
    
    public static boolean loadMenu(final String str) {
        if (!UpgradesManager.upgrades.getYml().isSet(str + "-upgrades-settings.menu-content")) {
            return false;
        }
        if (UpgradesManager.menuByName.containsKey(str.toLowerCase())) {
            return false;
        }
        final InternalMenu value = new InternalMenu(str);
        final Iterator<String> iterator = UpgradesManager.upgrades.getYml().getStringList(str + "-upgrades-settings.menu-content").iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split(",");
            if (split.length <= 1) {
                continue;
            }
            MenuContent menuContent = getMenuContent(split[0]);
            if (split[0].startsWith("category-")) {
                if (menuContent == null && loadCategory(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("upgrade-")) {
                if (menuContent == null && loadUpgrade(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("trap-slot-")) {
                if (menuContent == null && loadTrapSlot(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("separator-")) {
                if (menuContent == null && loadSeparator(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("base-trap-") && menuContent == null && loadBaseTrap(split[0])) {
                menuContent = getMenuContent(split[0]);
            }
            if (menuContent == null) {
                continue;
            }
            for (int i = 1; i < split.length; ++i) {
                if (Misc.isNumber(split[i])) {
                    value.addContent(menuContent, Integer.parseInt(split[i]));
                }
            }
        }
        UpgradesManager.menuByName.put(str.toLowerCase(), value);
        BeaconBattle.debug("Registering upgrade menu: " + str);
        return true;
    }
    
    private static boolean loadCategory(final String s) {
        if (s == null) {
            return false;
        }
        if (!s.startsWith("category-")) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(s) == null) {
            return false;
        }
        if (getMenuContent(s) != null) {
            return false;
        }
        final MenuCategory value = new MenuCategory(s, createDisplayItem(s));
        final Iterator<String> iterator = UpgradesManager.upgrades.getYml().getStringList(s + ".category-content").iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split(",");
            if (split.length <= 1) {
                continue;
            }
            MenuContent menuContent = null;
            if (split[0].startsWith("category-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadCategory(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("upgrade-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadUpgrade(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("trap-slot-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadTrapSlot(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("separator-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadSeparator(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            else if (split[0].startsWith("base-trap-")) {
                menuContent = getMenuContent(split[0]);
                if (menuContent == null && loadBaseTrap(split[0])) {
                    menuContent = getMenuContent(split[0]);
                }
            }
            if (menuContent == null) {
                continue;
            }
            for (int i = 1; i < split.length; ++i) {
                if (Misc.isNumber(split[i])) {
                    value.addContent(menuContent, Integer.parseInt(split[i]));
                }
            }
        }
        UpgradesManager.menuContentByName.put(s.toLowerCase(), value);
        BeaconBattle.debug("Registering upgrade: " + s);
        return true;
    }
    
    private static boolean loadUpgrade(final String s) {
        if (s == null) {
            return false;
        }
        if (!s.startsWith("upgrade-")) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(s) == null) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(s + ".tier-1") == null) {
            return false;
        }
        if (getMenuContent(s) != null) {
            return false;
        }
        final MenuUpgrade value = new MenuUpgrade(s);
        for (final String s2 : UpgradesManager.upgrades.getYml().getConfigurationSection(s).getKeys(false)) {
            if (!s2.startsWith("tier-")) {
                continue;
            }
            if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".receive") == null) {
                BeaconBattle.debug("Could not load Upgrade " + s + " tier: " + s2 + ". Receive not set.");
            }
            else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".display-item") == null) {
                BeaconBattle.debug("Could not load Upgrade " + s + " tier: " + s2 + ". Display item not set.");
            }
            else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".cost") == null) {
                BeaconBattle.debug("Could not load Upgrade " + s + " tier: " + s2 + ". Cost not set.");
            }
            else if (UpgradesManager.upgrades.getYml().get(s + "." + s2 + ".currency") == null) {
                BeaconBattle.debug("Could not load Upgrade " + s + " tier: " + s2 + ". Currency not set.");
            }
            else {
                if (value.addTier(new UpgradeTier(s, s2, createDisplayItem(s + "." + s2), UpgradesManager.upgrades.getYml().getInt(s + "." + s2 + ".cost"), getCurrency(UpgradesManager.upgrades.getYml().getString(s + "." + s2 + ".currency"))))) {
                    continue;
                }
                Bukkit.getLogger().log(Level.WARNING, "Could not load tier: " + s2 + " at upgrade: " + s);
            }
        }
        BeaconBattle.debug("Registering upgrade: " + s);
        UpgradesManager.menuContentByName.put(s.toLowerCase(), value);
        return true;
    }
    
    private static boolean loadSeparator(final String str) {
        if (str == null) {
            return false;
        }
        if (!str.startsWith("separator-")) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(str) == null) {
            return false;
        }
        if (getMenuContent(str) != null) {
            return false;
        }
        UpgradesManager.menuContentByName.put(str.toLowerCase(), new MenuSeparator(str, createDisplayItem(str)));
        BeaconBattle.debug("Registering upgrade: " + str);
        return true;
    }
    
    private static boolean loadTrapSlot(final String str) {
        if (str == null) {
            return false;
        }
        if (!str.startsWith("trap-slot-")) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(str) == null) {
            return false;
        }
        if (getMenuContent(str) != null) {
            return false;
        }
        UpgradesManager.menuContentByName.put(str.toLowerCase(), new MenuTrapSlot(str, createDisplayItem(str)));
        BeaconBattle.debug("Registering upgrade: " + str);
        return true;
    }
    
    private static boolean loadBaseTrap(final String str) {
        if (str == null) {
            return false;
        }
        if (!str.startsWith("base-trap-")) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(str) == null) {
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(str + ".receive") == null) {
            BeaconBattle.debug("Could not load BaseTrap. Receive not set.");
            return false;
        }
        if (UpgradesManager.upgrades.getYml().get(str + ".display-item") == null) {
            BeaconBattle.debug("Could not load BaseTrap. Display item not set.");
            return false;
        }
        final MenuBaseTrap value = new MenuBaseTrap(str, createDisplayItem(str), UpgradesManager.upgrades.getYml().getInt(str + ".cost"), getCurrency(UpgradesManager.upgrades.getYml().getString(str + ".currency")));
        BeaconBattle.debug("Registering upgrade: " + str);
        UpgradesManager.menuContentByName.put(str.toLowerCase(), value);
        return true;
    }
    
    public static int getMoney(final Player player, final Material material) {
        if (material == Material.AIR) {
            final double money = BeaconBattle.getEconomy().getMoney(player);
            return (money % 2.0 == 0.0) ? ((int)money) : ((int)(money - 1.0));
        }
        return BeaconBattle.getAPI().getShopUtil().calculateMoney(player, material);
    }
    
    public static Material getCurrency(final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return BeaconBattle.getAPI().getShopUtil().getCurrency(s);
    }
    
    public static MenuContent getMenuContent(final ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        final String customData = BeaconBattle.nms.getCustomData(itemStack);
        if (customData == null) {
            return null;
        }
        if (customData.equals("null")) {
            return null;
        }
        if (!customData.startsWith("MCONT_")) {
            return null;
        }
        final String replaceFirst = customData.replaceFirst("MCONT_", "");
        if (replaceFirst.isEmpty()) {
            return null;
        }
        return UpgradesManager.menuContentByName.getOrDefault(replaceFirst.toLowerCase(), null);
    }
    
    public static MenuContent getMenuContent(final String s) {
        return UpgradesManager.menuContentByName.getOrDefault(s.toLowerCase(), null);
    }
    
    public static void setCustomMenuForArena(final IArena arena, final UpgradesIndex upgradesIndex) {
        if (!UpgradesManager.customMenuForArena.containsKey(arena)) {
            UpgradesManager.customMenuForArena.put(arena, upgradesIndex);
            BeaconBattle.debug("Registering custom menu for arena: " + arena.getArenaName() + ". Using index: " + upgradesIndex.getName());
        }
        else {
            BeaconBattle.debug("Overriding custom menu for arena: " + arena.getArenaName() + ". Using index: " + upgradesIndex.getName() + " Old index: " + UpgradesManager.customMenuForArena.get(arena).getName());
            UpgradesManager.customMenuForArena.replace(arena, upgradesIndex);
        }
    }
    
    public static UpgradesIndex getMenuForArena(final IArena arena) {
        if (UpgradesManager.customMenuForArena.containsKey(arena)) {
            return UpgradesManager.customMenuForArena.get(arena);
        }
        return UpgradesManager.menuByName.getOrDefault(arena.getGroup().toLowerCase(), UpgradesManager.menuByName.get("default"));
    }
    
    private static ItemStack createDisplayItem(final String s) {
        Material material;
        try {
            material = Material.valueOf(UpgradesManager.upgrades.getYml().getString(s + ".display-item.material"));
        }
        catch (Exception ex) {
            material = Material.BEDROCK;
        }
        final ItemStack itemStack = new ItemStack(material, Integer.parseInt(UpgradesManager.upgrades.getYml().getString(s + ".display-item.amount")), (short)UpgradesManager.upgrades.getYml().getInt(s + ".display-item.data"));
        if (UpgradesManager.upgrades.getYml().getBoolean(s + ".display-item.enchanted")) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
    public static String getCurrencyMsg(final Player player, final UpgradeTier upgradeTier) {
        String s = "";
        switch (upgradeTier.getCurrency()) {
            case IRON_INGOT: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case GOLD_INGOT: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case EMERALD: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case DIAMOND: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            case AIR: {
                s = ((upgradeTier.getCost() == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s);
    }
    
    public static String getCurrencyMsg(final Player player, final int n, final String s) {
        if (s == null) {
            return Language.getMsg(player, (n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
        }
        final String lowerCase = s.toLowerCase();
        String s2 = null;
        switch (lowerCase) {
            case "iron": {
                s2 = ((n == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case "gold": {
                s2 = ((n == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case "emerald": {
                s2 = ((n == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case "diamond": {
                s2 = ((n == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            default: {
                s2 = ((n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s2);
    }
    
    public static String getCurrencyMsg(final Player player, final int n, final Material material) {
        String s = null;
        switch (material) {
            case IRON_INGOT: {
                s = ((n == 1) ? Messages.MEANING_IRON_SINGULAR : Messages.MEANING_IRON_PLURAL);
                break;
            }
            case GOLD_INGOT: {
                s = ((n == 1) ? Messages.MEANING_GOLD_SINGULAR : Messages.MEANING_GOLD_PLURAL);
                break;
            }
            case EMERALD: {
                s = ((n == 1) ? Messages.MEANING_EMERALD_SINGULAR : Messages.MEANING_EMERALD_PLURAL);
                break;
            }
            case DIAMOND: {
                s = ((n == 1) ? Messages.MEANING_DIAMOND_SINGULAR : Messages.MEANING_DIAMOND_PLURAL);
                break;
            }
            default: {
                s = ((n == 1) ? Messages.MEANING_VAULT_SINGULAR : Messages.MEANING_VAULT_PLURAL);
                break;
            }
        }
        return Language.getMsg(player, s);
    }
    
    public static ChatColor getCurrencyColor(final Material material) {
        switch (material) {
            case DIAMOND: {
                return ChatColor.AQUA;
            }
            case GOLD_INGOT: {
                return ChatColor.GOLD;
            }
            case IRON_INGOT: {
                return ChatColor.WHITE;
            }
            case EMERALD: {
                return ChatColor.GREEN;
            }
            default: {
                return ChatColor.DARK_GREEN;
            }
        }
    }
    
    public static UpgradesConfig getConfiguration() {
        return UpgradesManager.upgrades;
    }
    
    static {
        UpgradesManager.upgradeViewers = new LinkedList<UUID>();
        UpgradesManager.menuContentByName = new HashMap<String, MenuContent>();
        UpgradesManager.menuByName = new HashMap<String, UpgradesIndex>();
        UpgradesManager.customMenuForArena = new HashMap<IArena, UpgradesIndex>();
    }
}
