

package ws.billy.bedwars.configuration;

import java.util.Iterator;
import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.api.server.ServerType;
import java.io.IOException;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import ws.billy.bedwars.arena.Misc;
import org.bukkit.Material;
import java.util.Collections;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class MainConfig extends ConfigManager
{
    public MainConfig(final Plugin plugin, final String s) {
        super(plugin, s, BeaconBattle.plugin.getDataFolder().getPath());
        final YamlConfiguration yml = this.getYml();
        yml.addDefault("serverType", (Object)"MULTIARENA");
        yml.addDefault("language", (Object)"en");
        yml.addDefault("lobbyServer", (Object)"hub");
        yml.addDefault("globalChat", (Object)false);
        yml.addDefault("formatChat", (Object)true);
        yml.addDefault("debug", (Object)false);
        yml.addDefault("enable-party-cmd", (Object)true);
        yml.addDefault("scoreboard-settings.sidebar.enable-lobby-sidebar", (Object)true);
        yml.addDefault("scoreboard-settings.sidebar.enable-game-sidebar", (Object)true);
        yml.addDefault("scoreboard-settings.sidebar.title-refresh-interval", (Object)4);
        yml.addDefault("scoreboard-settings.sidebar.placeholders-refresh-interval", (Object)20);
        yml.addDefault("scoreboard-settings.player-list.format-lobby-list", (Object)false);
        yml.addDefault("scoreboard-settings.player-list.format-waiting-list", (Object)false);
        yml.addDefault("scoreboard-settings.player-list.format-starting-list", (Object)false);
        yml.addDefault("scoreboard-settings.player-list.format-playing-list", (Object)true);
        yml.addDefault("scoreboard-settings.player-list.format-restarting-list", (Object)true);
        yml.addDefault("scoreboard-settings.player-list.names-refresh-interval", (Object)200);
        yml.addDefault("scoreboard-settings.health.display-in-tab", (Object)true);
        yml.addDefault("scoreboard-settings.health.animation-refresh-interval", (Object)80);
        yml.addDefault("allow-parties", (Object)true);
        yml.addDefault("rejoin-time", (Object)300);
        yml.addDefault("bungee-settings.games-before-restart", (Object)30);
        yml.addDefault("bungee-settings.restart-cmd", (Object)"restart");
        yml.addDefault("bungee-settings.auto-scale-clone-limit", (Object)5);
        yml.addDefault("bungee-settings.lobby-sockets", (Object)Collections.singletonList("0.0.0.0:2019"));
        yml.addDefault("countdowns.game-start-regular", (Object)40);
        yml.addDefault("countdowns.game-start-half-arena", (Object)25);
        yml.addDefault("countdowns.game-start-shortened", (Object)10);
        yml.addDefault("countdowns.game-restart", (Object)15);
        yml.addDefault("countdowns.next-event-beds-destroy", (Object)360);
        yml.addDefault("countdowns.next-event-dragon-spawn", (Object)600);
        yml.addDefault("countdowns.next-event-game-end", (Object)120);
        yml.addDefault("shout-cmd-cooldown", (Object)30);
        yml.addDefault("server-ip", (Object)"yourServer.Com");
        yml.addDefault("bungee-settings.server-id", (Object)"bw1");
        yml.addDefault("database.enable", (Object)false);
        yml.addDefault("database.host", (Object)"localhost");
        yml.addDefault("database.port", (Object)3306);
        yml.addDefault("database.database", (Object)"BeaconBattle1058");
        yml.addDefault("database.user", (Object)"root");
        yml.addDefault("database.pass", (Object)"cheese");
        yml.addDefault("database.ssl", (Object)false);
        yml.addDefault("performance-settings.rotate-generators", (Object)true);
        yml.addDefault("performance-settings.disable-armor-packets", (Object)false);
        yml.addDefault("performance-settings.disable-respawn-packets", (Object)false);
        yml.addDefault("inventories.disable-crafting-table", (Object)true);
        yml.addDefault("inventories.disable-enchanting-table", (Object)true);
        yml.addDefault("inventories.disable-furnace", (Object)true);
        yml.addDefault("inventories.disable-brewing-stand", (Object)true);
        yml.addDefault("inventories.disable-anvil", (Object)true);
        this.saveLobbyCommandItem("stats", "bw stats", false, BeaconBattle.getForCurrentVersion("SKULL_ITEM", "SKULL_ITEM", "PLAYER_HEAD"), 3, 0);
        this.saveLobbyCommandItem("arena-selector", "bw gui", true, "CHEST", 5, 4);
        this.saveLobbyCommandItem("leave", "bw leave", false, BeaconBattle.getForCurrentVersion("BED", "BED", "RED_BED"), 0, 8);
        this.savePreGameCommandItem("stats", "bw stats", false, BeaconBattle.getForCurrentVersion("SKULL_ITEM", "SKULL_ITEM", "PLAYER_HEAD"), 3, 0);
        this.savePreGameCommandItem("leave", "bw leave", false, BeaconBattle.getForCurrentVersion("BED", "BED", "RED_BED"), 0, 8);
        this.saveSpectatorCommandItem("teleporter", "bw teleporter", false, BeaconBattle.getForCurrentVersion("SKULL_ITEM", "SKULL_ITEM", "PLAYER_HEAD"), 3, 0);
        this.saveSpectatorCommandItem("leave", "bw leave", false, BeaconBattle.getForCurrentVersion("BED", "BED", "RED_BED"), 0, 8);
        yml.addDefault("arena-gui.settings.inv-size", (Object)27);
        yml.addDefault("arena-gui.settings.show-playing", (Object)true);
        yml.addDefault("arena-gui.settings.use-slots", (Object)"10,11,12,13,14,15,16");
        yml.addDefault("arena-gui.%path%.material".replace("%path%", "waiting"), (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "CONCRETE", "LIME_CONCRETE"));
        yml.addDefault("arena-gui.%path%.data".replace("%path%", "waiting"), (Object)5);
        yml.addDefault("arena-gui.%path%.enchanted".replace("%path%", "waiting"), (Object)false);
        yml.addDefault("arena-gui.%path%.material".replace("%path%", "starting"), (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "CONCRETE", "YELLOW_CONCRETE"));
        yml.addDefault("arena-gui.%path%.data".replace("%path%", "starting"), (Object)4);
        yml.addDefault("arena-gui.%path%.enchanted".replace("%path%", "starting"), (Object)true);
        yml.addDefault("arena-gui.%path%.material".replace("%path%", "playing"), (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "CONCRETE", "RED_CONCRETE"));
        yml.addDefault("arena-gui.%path%.data".replace("%path%", "playing"), (Object)14);
        yml.addDefault("arena-gui.%path%.enchanted".replace("%path%", "playing"), (Object)false);
        yml.addDefault("arena-gui.%path%.material".replace("%path%", "skipped-slot"), (Object)BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE"));
        yml.addDefault("arena-gui.%path%.data".replace("%path%", "skipped-slot"), (Object)15);
        yml.addDefault("arena-gui.%path%.enchanted".replace("%path%", "skipped-slot"), (Object)false);
        yml.addDefault("stats-gui.inv-size", (Object)27);
        if (this.isFirstTime()) {
            Misc.addDefaultStatsItem(yml, 10, Material.DIAMOND, 0, "wins");
            Misc.addDefaultStatsItem(yml, 11, Material.REDSTONE, 0, "losses");
            Misc.addDefaultStatsItem(yml, 12, Material.IRON_SWORD, 0, "kills");
            Misc.addDefaultStatsItem(yml, 13, Material.valueOf(BeaconBattle.getForCurrentVersion("SKULL_ITEM", "SKULL_ITEM", "SKELETON_SKULL")), 0, "deaths");
            Misc.addDefaultStatsItem(yml, 14, Material.DIAMOND_SWORD, 0, "final-kills");
            Misc.addDefaultStatsItem(yml, 15, Material.valueOf(BeaconBattle.getForCurrentVersion("SKULL_ITEM", "SKULL_ITEM", "SKELETON_SKULL")), 1, "final-deaths");
            Misc.addDefaultStatsItem(yml, 16, Material.valueOf(BeaconBattle.getForCurrentVersion("BED", "BED", "RED_BED")), 0, "beds-destroyed");
            Misc.addDefaultStatsItem(yml, 21, Material.valueOf(BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE")), 0, "first-play");
            Misc.addDefaultStatsItem(yml, 22, Material.CHEST, 0, "games-played");
            Misc.addDefaultStatsItem(yml, 23, Material.valueOf(BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE")), 0, "last-play");
        }
        yml.addDefault("start-items-per-group.Default", (Object)Collections.singletonList(BeaconBattle.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
        yml.addDefault("allowed-commands", (Object)Arrays.asList("shout", "bw", "leave"));
        yml.options().copyDefaults(true);
        this.save();
        if (yml.get("bungee-settings.lobby-servers") != null) {
            yml.set("bungee-settings.lobby-sockets", (Object)new ArrayList(yml.getStringList("bungee-settings.lobby-servers")));
            yml.set("bungee-settings.lobby-servers", (Object)null);
        }
        if (yml.get("arenaGui.settings.showPlaying") != null) {
            this.set("arena-gui.settings.show-playing", yml.getBoolean("arenaGui.settings.showPlaying"));
        }
        if (yml.get("arenaGui.settings.size") != null) {
            this.set("arena-gui.settings.inv-size", yml.getInt("arenaGui.settings.size"));
        }
        if (yml.get("arenaGui.settings.useSlots") != null) {
            this.set("arena-gui.settings.use-slots", yml.getString("arenaGui.settings.useSlots"));
        }
        if (this.getYml().get("arenaGui") != null) {
            for (final String str : this.getYml().getConfigurationSection("arenaGui").getKeys(false)) {
                if (str.equalsIgnoreCase("settings")) {
                    continue;
                }
                String replacement = str;
                if ("skippedSlot".equals(str)) {
                    replacement = "skipped-slot";
                }
                if (this.getYml().get("arenaGui." + str + ".itemStack") != null) {
                    this.set("arena-gui.%path%.material".replace("%path%", replacement), this.getYml().getString("arenaGui." + str + ".itemStack"));
                }
                if (this.getYml().get("arenaGui." + str + ".data") != null) {
                    this.set("arena-gui.%path%.data".replace("%path%", replacement), this.getYml().getInt("arenaGui." + str + ".data"));
                }
                if (this.getYml().get("arenaGui." + str + ".enchanted") == null) {
                    continue;
                }
                this.set("arena-gui.%path%.enchanted".replace("%path%", replacement), this.getYml().getBoolean("arenaGui." + str + ".enchanted"));
            }
        }
        this.set("arenaGui", null);
        if (this.getYml().get("npcLoc") != null) {
            this.set("join-npc-locations", this.getYml().getString("npcLoc"));
        }
        if (this.getYml().get("statsGUI.invSize") != null) {
            this.set("stats-gui.inv-size", this.getInt("statsGUI.invSize"));
        }
        if (this.getYml().get("disableCrafting") != null) {
            this.set("inventories.disable-crafting-table", this.getString("disableCrafting"));
        }
        if (this.getYml().get("statsGUI") != null) {
            for (String replacement2 : this.getYml().getConfigurationSection("statsGUI").getKeys(false)) {
                final String s3;
                final String s2 = s3 = replacement2;
                switch (s3) {
                    case "gamesPlayed": {
                        replacement2 = "games-played";
                        break;
                    }
                    case "lastPlay": {
                        replacement2 = "last-play";
                        break;
                    }
                    case "firstPlay": {
                        replacement2 = "first-play";
                        break;
                    }
                    case "bedsDestroyed": {
                        replacement2 = "beds-destroyed";
                        break;
                    }
                    case "finalDeaths": {
                        replacement2 = "final-deaths";
                        break;
                    }
                    case "finalKills": {
                        replacement2 = "final-kills";
                        break;
                    }
                }
                if (this.getYml().get("statsGUI." + s2 + ".itemStack") != null) {
                    this.set("stats-gui.%path%.material".replace("%path%", replacement2), this.getYml().getString("statsGUI." + s2 + ".itemStack"));
                }
                if (this.getYml().get("statsGUI." + s2 + ".data") != null) {
                    this.set("stats-gui.%path%.data".replace("%path%", replacement2), this.getYml().getInt("statsGUI." + s2 + ".data"));
                }
                if (this.getYml().get("statsGUI." + s2 + ".slot") != null) {
                    this.set("stats-gui.%path%.slot".replace("%path%", replacement2), this.getYml().getInt("statsGUI." + s2 + ".slot"));
                }
            }
        }
        if (yml.get("server-name") != null) {
            this.set("bungee-settings.server-id", yml.get("server-name"));
        }
        if (yml.get("lobby-scoreboard") != null) {
            this.set("scoreboard-settings.sidebar.enable-lobby-sidebar", yml.getBoolean("lobby-scoreboard"));
            this.set("lobby-scoreboard", null);
        }
        if (yml.get("game-scoreboard") != null) {
            this.set("scoreboard-settings.sidebar.enable-game-sidebar", yml.getBoolean("game-scoreboard"));
            this.set("game-scoreboard", null);
        }
        this.set("server-name", null);
        this.set("statsGUI", null);
        this.set("startItems", null);
        this.set("generators", null);
        this.set("bedsDestroyCountdown", null);
        this.set("dragonSpawnCountdown", null);
        this.set("gameEndCountdown", null);
        this.set("npcLoc", null);
        this.set("blockedCmds", null);
        this.set("lobbyScoreboard", null);
        this.set("arenaGui.settings.startSlot", null);
        this.set("arenaGui.settings.endSlot", null);
        this.set("items", null);
        this.set("start-items-per-arena", null);
        this.set("safeMode", null);
        this.set("disableCrafting", null);
        String replace = "en";
        final File[] listFiles = new File(plugin.getDataFolder(), "/Languages").listFiles();
        if (listFiles != null) {
            for (final File file : listFiles) {
                if (file.isFile() && file.getName().contains("messages_") && file.getName().contains(".yml")) {
                    final String replace2 = file.getName().replace("messages_", "").replace(".yml", "");
                    if (replace2.equalsIgnoreCase(yml.getString("language"))) {
                        replace = file.getName().replace("messages_", "").replace(".yml", "");
                    }
                    if (Language.getLang(replace2) == null) {
                        new Language((Plugin)BeaconBattle.plugin, replace2);
                    }
                }
            }
        }
        final Language lang = Language.getLang(replace);
        if (lang == null) {
            throw new IllegalStateException("Could not found default language: " + replace);
        }
        Language.setDefaultLanguage(lang);
        final Iterator iterator3 = yml.getStringList("disabled-languages").iterator();
        while (iterator3.hasNext()) {
            final Language lang2 = Language.getLang(iterator3.next());
            if (lang2 != null && lang2 != lang) {
                Language.getLanguages().remove(lang2);
            }
        }
        BeaconBattle.setDebug(yml.getBoolean("debug"));
        new ConfigManager(plugin, "bukkit", Bukkit.getWorldContainer().getPath()).set("ticks-per.autosave", -1);
        Bukkit.spigot().getConfig().set("commands.send-namespaced", (Object)false);
        try {
            Bukkit.spigot().getConfig().save("spigot.yml");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            BeaconBattle.setServerType(ServerType.valueOf(yml.getString("serverType").toUpperCase()));
        }
        catch (Exception ex2) {
            if (yml.getString("serverType").equalsIgnoreCase("BUNGEE_LEGACY")) {
                BeaconBattle.setServerType(ServerType.BUNGEE);
                BeaconBattle.setAutoscale(false);
            }
            else {
                this.set("serverType", "MULTIARENA");
            }
        }
        BeaconBattle.setLobbyWorld(this.getLobbyWorldName());
    }
    
    public String getLobbyWorldName() {
        if (this.getYml().get("lobbyLoc") == null) {
            return "";
        }
        final String[] split = this.getYml().getString("lobbyLoc").replace("[", "").replace("]", "").split(",");
        return split[split.length - 1];
    }
    
    public void saveLobbyCommandItem(final String replacement, final String s, final boolean b, final String s2, final int i, final int j) {
        if (this.isFirstTime()) {
            this.getYml().addDefault("lobby-items.%path%.command".replace("%path%", replacement), (Object)s);
            this.getYml().addDefault("lobby-items.%path%.material".replace("%path%", replacement), (Object)s2);
            this.getYml().addDefault("lobby-items.%path%.data".replace("%path%", replacement), (Object)i);
            this.getYml().addDefault("lobby-items.%path%.enchanted".replace("%path%", replacement), (Object)b);
            this.getYml().addDefault("lobby-items.%path%.slot".replace("%path%", replacement), (Object)j);
            this.getYml().options().copyDefaults(true);
            this.save();
        }
    }
    
    public void savePreGameCommandItem(final String replacement, final String s, final boolean b, final String s2, final int i, final int j) {
        if (this.isFirstTime()) {
            this.getYml().addDefault("pre-game-items.%path%.command".replace("%path%", replacement), (Object)s);
            this.getYml().addDefault("pre-game-items.%path%.material".replace("%path%", replacement), (Object)s2);
            this.getYml().addDefault("pre-game-items.%path%.data".replace("%path%", replacement), (Object)i);
            this.getYml().addDefault("pre-game-items.%path%.enchanted".replace("%path%", replacement), (Object)b);
            this.getYml().addDefault("pre-game-items.%path%.slot".replace("%path%", replacement), (Object)j);
            this.getYml().options().copyDefaults(true);
            this.save();
        }
    }
    
    public void saveSpectatorCommandItem(final String replacement, final String s, final boolean b, final String s2, final int i, final int j) {
        if (this.isFirstTime()) {
            this.getYml().addDefault("spectator-items.%path%.command".replace("%path%", replacement), (Object)s);
            this.getYml().addDefault("spectator-items.%path%.material".replace("%path%", replacement), (Object)s2);
            this.getYml().addDefault("spectator-items.%path%.data".replace("%path%", replacement), (Object)i);
            this.getYml().addDefault("spectator-items.%path%.enchanted".replace("%path%", replacement), (Object)b);
            this.getYml().addDefault("spectator-items.%path%.slot".replace("%path%", replacement), (Object)j);
            this.getYml().options().copyDefaults(true);
            this.save();
        }
    }
}
