

package ws.billy.bedwars.api.language;

import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerLangChangeEvent;
import java.util.Arrays;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Collections;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.HashMap;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class Language extends ConfigManager
{
    private String iso;
    private String prefix;
    private static HashMap<Player, Language> langByPlayer;
    private static List<Language> languages;
    private static Language defaultLanguage;
    
    public Language(final Plugin plugin, final String s) {
        super(plugin, "messages_" + s, plugin.getDataFolder().getPath() + "/Languages");
        this.prefix = "";
        this.iso = s;
        Language.languages.add(this);
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public static List<String> getScoreboard(final Player player, final String s, final String s2) {
        final Language playerLanguage = getPlayerLanguage(player);
        if (playerLanguage.exists(s)) {
            return playerLanguage.l(s);
        }
        if (s.split("\\.").length == 3) {
            final String[] split = s.split("\\.");
            final String s3 = split[1];
            if (playerLanguage.exists(split[0] + "." + (String.valueOf(s3.charAt(0)).toUpperCase() + s3.substring(1).toLowerCase()) + "." + split[2])) {
                return playerLanguage.l(s);
            }
            if (playerLanguage.exists(split[0] + "." + split[1].toUpperCase() + "." + split[2])) {
                return playerLanguage.l(split[0] + "." + split[1].toUpperCase() + "." + split[2]);
            }
        }
        return playerLanguage.l(s2);
    }
    
    public String getLangName() {
        return this.getYml().getString("name");
    }
    
    public static String getMsg(final Player key, final String s) {
        return Language.langByPlayer.getOrDefault(key, getDefaultLanguage()).m(s);
    }
    
    public static Language getPlayerLanguage(final Player key) {
        return Language.langByPlayer.getOrDefault(key, getDefaultLanguage());
    }
    
    public boolean exists(final String s) {
        return this.getYml().get(s) != null;
    }
    
    public static List<String> getList(final Player key, final String s) {
        return Language.langByPlayer.getOrDefault(key, getDefaultLanguage()).l(s);
    }
    
    public static void saveIfNotExists(final String s, final Object o) {
        for (final Language language : Language.languages) {
            if (language.getYml().get(s) == null) {
                language.set(s, o);
            }
        }
    }
    
    public String m(final String s) {
        return ChatColor.translateAlternateColorCodes('&', this.getYml().getString(s).replace("{prefix}", this.prefix));
    }
    
    public List<String> l(final String s) {
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<String> iterator = this.getYml().getStringList(s).iterator();
        while (iterator.hasNext()) {
            list.add(ChatColor.translateAlternateColorCodes('&', (String)iterator.next()));
        }
        return list;
    }
    
    public static HashMap<Player, Language> getLangByPlayer() {
        return Language.langByPlayer;
    }
    
    public static boolean isLanguageExist(final String anotherString) {
        final Iterator<Language> iterator = Language.languages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().iso.equalsIgnoreCase(anotherString)) {
                return true;
            }
        }
        return false;
    }
    
    public static Language getLang(final String anotherString) {
        for (final Language language : Language.languages) {
            if (language.iso.equalsIgnoreCase(anotherString)) {
                return language;
            }
        }
        return getDefaultLanguage();
    }
    
    public String getIso() {
        return this.iso;
    }
    
    public static List<Language> getLanguages() {
        return Language.languages;
    }
    
    public static void setupCustomStatsMessages() {
        final BeaconBattle BeaconBattle = (BeaconBattle)Bukkit.getServer().getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
        for (final Language language : getLanguages()) {
            if (language == null) {
                continue;
            }
            if (language.getYml() == null) {
                continue;
            }
            if (BeaconBattle.getConfigs().getMainConfig().getYml().get("ConfigPath.GENERAL_CONFIGURATION_STATS_PATH") == null) {
                return;
            }
            for (final String str : BeaconBattle.getConfigs().getMainConfig().getYml().getConfigurationSection("stats-gui").getKeys(false)) {
                if ("stats-gui.inv-size".contains(str)) {
                    continue;
                }
                if (language.getYml().getDefaults() == null || !language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + str + "-name")) {
                    language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + str + "-name", (Object)"Name not set");
                }
                if (language.getYml().getDefaults() != null && language.getYml().getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + str + "-lore")) {
                    continue;
                }
                language.getYml().addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + str + "-lore", (Object)Collections.singletonList("lore not set"));
            }
            language.save();
        }
    }
    
    public void addDefaultStatsMsg(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String... array) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name")) {
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name", (Object)s2);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore")) {
            yamlConfiguration.addDefault(Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore", (Object)array);
        }
    }
    
    public static void addDefaultMessagesCommandItems(final Language language) {
        if (language == null) {
            return;
        }
        final YamlConfiguration yml = language.getYml();
        if (yml == null) {
            return;
        }
        final BeaconBattle BeaconBattle = (BeaconBattle)Bukkit.getServer().getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
        if (BeaconBattle.getConfigs().getMainConfig().getYml().get("lobby-items") != null) {
            for (final String s : BeaconBattle.getConfigs().getMainConfig().getYml().getConfigurationSection("lobby-items").getKeys(false)) {
                if (s.isEmpty()) {
                    continue;
                }
                final String replace = "lobby-items-%path%-name".replace("%path%", s);
                final String replace2 = "lobby-items-%path%-lore".replace("%path%", s);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace)) {
                    yml.addDefault(replace, (Object)("&cName not set at: &f" + replace));
                }
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace)) {
                    continue;
                }
                yml.addDefault(replace2, (Object)Arrays.asList("&cLore not set at:", " &f" + replace2));
            }
        }
        if (BeaconBattle.getConfigs().getMainConfig().getYml().get("spectator-items") != null) {
            for (final String s2 : BeaconBattle.getConfigs().getMainConfig().getYml().getConfigurationSection("spectator-items").getKeys(false)) {
                if (s2.isEmpty()) {
                    continue;
                }
                final String replace3 = "spectator-items-%path%-name".replace("%path%", s2);
                final String replace4 = "spectator-items-%path%-lore".replace("%path%", s2);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace3)) {
                    yml.addDefault(replace3, (Object)("&cName not set at: &f" + replace3));
                }
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace3)) {
                    continue;
                }
                yml.addDefault(replace4, (Object)Arrays.asList("&cLore not set at:", " &f" + replace4));
            }
        }
        if (BeaconBattle.getConfigs().getMainConfig().getYml().get("pre-game-items") != null) {
            for (final String s3 : BeaconBattle.getConfigs().getMainConfig().getYml().getConfigurationSection("pre-game-items").getKeys(false)) {
                if (s3.isEmpty()) {
                    continue;
                }
                final String replace5 = "pre-game-items-%path%-name".replace("%path%", s3);
                final String replace6 = "pre-game-items-%path%-lore".replace("%path%", s3);
                if (yml.getDefaults() == null || !yml.getDefaults().contains(replace5)) {
                    yml.addDefault(replace5, (Object)("&cName not set at: &f" + replace5));
                }
                if (yml.getDefaults() != null && yml.getDefaults().contains(replace5)) {
                    continue;
                }
                yml.addDefault(replace6, (Object)Arrays.asList("&cLore not set at:", " &f" + replace6));
            }
        }
        yml.options().copyDefaults(true);
        language.save();
    }
    
    public void setupUnSetCategories() {
        final BeaconBattle BeaconBattle = (BeaconBattle)Bukkit.getServer().getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
        for (final String s : BeaconBattle.getConfigs().getShopConfig().getYml().getConfigurationSection("").getKeys(false)) {
            if (s.equalsIgnoreCase("shop-settings")) {
                continue;
            }
            if (s.equalsIgnoreCase("shop-specials")) {
                continue;
            }
            if (s.equals("quick-buy-defaults")) {
                continue;
            }
            if (!this.exists("shop-items-messages.%category%.inventory-name".replace("%category%", s))) {
                this.set("shop-items-messages.%category%.inventory-name".replace("%category%", s), "&8Name not set");
            }
            if (!this.exists("shop-items-messages.%category%.category-item-name".replace("%category%", s))) {
                this.set("shop-items-messages.%category%.category-item-name".replace("%category%", s), "&8Name not set");
            }
            if (!this.exists("shop-items-messages.%category%.category-item-lore".replace("%category%", s))) {
                this.set("shop-items-messages.%category%.category-item-lore".replace("%category%", s), Collections.singletonList("&8Lore not set"));
            }
            if (BeaconBattle.getConfigs().getShopConfig().getYml().get(s + ".category-content") == null) {
                continue;
            }
            for (final String s2 : BeaconBattle.getConfigs().getShopConfig().getYml().getConfigurationSection(s + ".category-content").getKeys(false)) {
                if (!this.exists("shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s).replace("%content%", s2))) {
                    this.set("shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s).replace("%content%", s2), "&8Name not set");
                }
                if (!this.exists("shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s).replace("%content%", s2))) {
                    this.set("shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s).replace("%content%", s2), Collections.singletonList("&8Lore not set"));
                }
            }
        }
    }
    
    public static void addCategoryMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.inventory-name".replace("%category%", s))) {
            yamlConfiguration.addDefault("shop-items-messages.%category%.inventory-name".replace("%category%", s), (Object)s2);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-name".replace("%category%", s))) {
            yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-name".replace("%category%", s), (Object)s3);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains("shop-items-messages.%category%.category-item-lore".replace("%category%", s))) {
            yamlConfiguration.addDefault("shop-items-messages.%category%.category-item-lore".replace("%category%", s), (Object)list);
        }
    }
    
    public static void addContentMessages(final YamlConfiguration yamlConfiguration, final String s, final String s2, final String s3, final List<String> list) {
        final String replace = "shop-items-messages.%category%.content-item-%content%-name".replace("%category%", s2).replace("%content%", s);
        final String replace2 = "shop-items-messages.%category%.content-item-%content%-lore".replace("%category%", s2).replace("%content%", s);
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace)) {
            yamlConfiguration.addDefault(replace, (Object)s3);
        }
        if (yamlConfiguration.getDefaults() == null || !yamlConfiguration.getDefaults().contains(replace2)) {
            yamlConfiguration.addDefault(replace2, (Object)list);
        }
    }
    
    public static void setPlayerLanguage(final Player player, final String anotherString, final boolean b) {
        if (b && getDefaultLanguage().getIso().equalsIgnoreCase(anotherString)) {
            return;
        }
        final Language lang = getLang(anotherString);
        if (!b) {
            final PlayerLangChangeEvent playerLangChangeEvent = new PlayerLangChangeEvent(player, (getLangByPlayer().containsKey(player) ? getPlayerLanguage(player) : getLanguages().get(0)).getIso(), lang.getIso());
            Bukkit.getPluginManager().callEvent((Event)playerLangChangeEvent);
            if (playerLangChangeEvent.isCancelled()) {
                return;
            }
        }
        if (getLangByPlayer().containsKey(player)) {
            getLangByPlayer().replace(player, lang);
        }
        else {
            getLangByPlayer().put(player, lang);
        }
    }
    
    public static void setDefaultLanguage(final Language defaultLanguage) {
        Language.defaultLanguage = defaultLanguage;
    }
    
    public static Language getDefaultLanguage() {
        return Language.defaultLanguage;
    }
    
    static {
        Language.langByPlayer = new HashMap<Player, Language>();
        Language.languages = new ArrayList<Language>();
    }
}
