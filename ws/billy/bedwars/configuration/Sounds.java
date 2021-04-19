

package ws.billy.bedwars.configuration;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.api.arena.NextEvent;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class Sounds
{
    private static ConfigManager sounds;
    
    public Sounds() {
        saveDefaultSounds();
    }
    
    public static void saveDefaultSounds() {
        final YamlConfiguration yml = Sounds.sounds.getYml();
        yml.addDefault("game-end", (Object)BeaconBattle.getForCurrentVersion("AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER", "ITEM_TRIDENT_THUNDER"));
        yml.addDefault("rejoin-denied", (Object)BeaconBattle.getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO"));
        yml.addDefault("rejoin-allowed", (Object)BeaconBattle.getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP"));
        yml.addDefault("spectate-denied", (Object)BeaconBattle.getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO"));
        yml.addDefault("spectate-allowed", (Object)BeaconBattle.getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP"));
        yml.addDefault("join-denied", (Object)BeaconBattle.getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO"));
        yml.addDefault("join-allowed", (Object)BeaconBattle.getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP"));
        yml.addDefault("spectator-gui-click", (Object)BeaconBattle.getForCurrentVersion("SLIME_WALK", "ENTITY_SLIME_JUMP", "ENTITY_SLIME_JUMP"));
        yml.addDefault("game-countdown-others", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-s5", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-s4", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-s3", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-s2", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-s1", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("game-countdown-start", (Object)BeaconBattle.getForCurrentVersion("SLIME_ATTACK", "BLOCK_SLIME_FALL", "BLOCK_SLIME_BLOCK_FALL"));
        yml.addDefault("bed-destroy", (Object)BeaconBattle.getForCurrentVersion("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL", "ENTITY_ENDER_DRAGON_GROWL"));
        yml.addDefault("shop-insufficient-money", (Object)BeaconBattle.getForCurrentVersion("VILLAGER_NO", "ENTITY_VILLAGER_NO", "ENTITY_VILLAGER_NO"));
        yml.addDefault("shop-bought", (Object)BeaconBattle.getForCurrentVersion("VILLAGER_YES", "ENTITY_VILLAGER_YES", "ENTITY_VILLAGER_YES"));
        yml.addDefault(NextEvent.BEDS_DESTROY.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL", "ENTITY_ENDER_DRAGON_GROWL"));
        yml.addDefault(NextEvent.DIAMOND_GENERATOR_TIER_II.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
        yml.addDefault(NextEvent.DIAMOND_GENERATOR_TIER_III.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("LEVEL_UP", "ENTITY_PLAYER_LEVELUP", "ENTITY_PLAYER_LEVELUP"));
        yml.addDefault(NextEvent.EMERALD_GENERATOR_TIER_II.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("GHAST_MOAN", "ENTITY_GHAST_WARN", "ENTITY_GHAST_WARN"));
        yml.addDefault(NextEvent.EMERALD_GENERATOR_TIER_III.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("GHAST_MOAN", "ENTITY_GHAST_WARN", "ENTITY_GHAST_WARN"));
        yml.addDefault(NextEvent.ENDER_DRAGON.getSoundPath(), (Object)BeaconBattle.getForCurrentVersion("ENDERDRAGON_WINGS", "ENTITY_ENDERDRAGON_FLAP", "ENTITY_ENDER_DRAGON_FLAP"));
        yml.addDefault("player-re-spawn", (Object)BeaconBattle.getForCurrentVersion("SLIME_ATTACK", "BLOCK_SLIME_FALL", "BLOCK_SLIME_BLOCK_FALL"));
        yml.addDefault("arena-selector-open", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("stats-gui-open", (Object)BeaconBattle.getForCurrentVersion("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG", "ENTITY_CHICKEN_EGG"));
        yml.addDefault("trap-sound", (Object)BeaconBattle.getForCurrentVersion("ENDERMAN_TELEPORT", "ENDERMAN_TELEPORT", "ENTITY_ENDERMAN_TELEPORT"));
        yml.options().copyDefaults(true);
        yml.set("bought", (Object)null);
        yml.set("insufficient-money", (Object)null);
        yml.set("player-kill", (Object)null);
        yml.set("countdown", (Object)null);
        Sounds.sounds.save();
    }
    
    public static Sound getSound(final String s) {
        try {
            return Sound.valueOf(Sounds.sounds.getString(s));
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static boolean playSound(final String s, final List<Player> list) {
        if (getSound(s) != null) {
            final Sound sound;
            list.forEach(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
            return true;
        }
        return false;
    }
    
    public static boolean playSound(final Sound sound, final List<Player> list) {
        if (sound == null) {
            return false;
        }
        list.forEach(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
        return true;
    }
    
    public static void playSound(final String s, final Player player) {
        final Sound sound = getSound(s);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    public static void playSound(final Sound sound, final Player player) {
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    public static ConfigManager getSounds() {
        return Sounds.sounds;
    }
    
    static {
        Sounds.sounds = new ConfigManager((Plugin)BeaconBattle.plugin, "sounds", BeaconBattle.plugin.getDataFolder().getPath());
    }
}
