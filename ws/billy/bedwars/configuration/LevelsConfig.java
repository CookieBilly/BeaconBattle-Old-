

package ws.billy.bedwars.configuration;

import org.jetbrains.annotations.NotNull;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class LevelsConfig extends ConfigManager
{
    public static LevelsConfig levels;
    
    private LevelsConfig() {
        super((Plugin)BeaconBattle.plugin, "levels", BeaconBattle.plugin.getDataFolder().toString());
    }
    
    public static void init() {
        LevelsConfig.levels = new LevelsConfig();
        LevelsConfig.levels.getYml().options().copyDefaults(true);
        if (LevelsConfig.levels.isFirstTime()) {
            LevelsConfig.levels.getYml().addDefault("levels.1.name", (Object)"&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.1.rankup-cost", (Object)1000);
            LevelsConfig.levels.getYml().addDefault("levels.2.name", (Object)"&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.2.rankup-cost", (Object)2000);
            LevelsConfig.levels.getYml().addDefault("levels.3.name", (Object)"&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.3.rankup-cost", (Object)3000);
            LevelsConfig.levels.getYml().addDefault("levels.4.name", (Object)"&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.4.rankup-cost", (Object)3500);
            LevelsConfig.levels.getYml().addDefault("levels.5-10.name", (Object)"&e[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.5-10.rankup-cost", (Object)5000);
            LevelsConfig.levels.getYml().addDefault("levels.others.name", (Object)"&7[{number}\u2729] ");
            LevelsConfig.levels.getYml().addDefault("levels.others.rankup-cost", (Object)5000);
        }
        LevelsConfig.levels.getYml().addDefault("xp-rewards.per-minute", (Object)10);
        LevelsConfig.levels.getYml().addDefault("xp-rewards.per-teammate", (Object)5);
        LevelsConfig.levels.getYml().addDefault("xp-rewards.game-win", (Object)100);
        LevelsConfig.levels.getYml().addDefault("progress-bar.symbol", (Object)"\u25a0");
        LevelsConfig.levels.getYml().addDefault("progress-bar.unlocked-color", (Object)"&b");
        LevelsConfig.levels.getYml().addDefault("progress-bar.locked-color", (Object)"&7");
        LevelsConfig.levels.getYml().addDefault("progress-bar.format", (Object)"&8 [{progress}&8]");
        LevelsConfig.levels.save();
    }
    
    @NotNull
    public static String getLevelName(final int i) {
        final String string = LevelsConfig.levels.getYml().getString("levels." + i + ".name");
        if (string != null) {
            return string;
        }
        for (final String str : LevelsConfig.levels.getYml().getConfigurationSection("levels").getKeys(false)) {
            if (str.contains("-")) {
                final String[] split = str.split("-");
                if (split.length != 2) {
                    continue;
                }
                int int1;
                int int2;
                try {
                    int1 = Integer.parseInt(split[0]);
                    int2 = Integer.parseInt(split[1]);
                }
                catch (Exception ex) {
                    continue;
                }
                if (int1 <= i && i <= int2) {
                    return LevelsConfig.levels.getYml().getString("levels." + str + ".name");
                }
                continue;
            }
        }
        return LevelsConfig.levels.getYml().getString("levels.others.name");
    }
    
    public static int getNextCost(final int n) {
        if (LevelsConfig.levels.getYml().get("levels." + n + ".rankup-cost") != null) {
            return LevelsConfig.levels.getYml().getInt("levels." + n + ".rankup-cost");
        }
        for (final String str : LevelsConfig.levels.getYml().getConfigurationSection("levels").getKeys(false)) {
            if (str.contains("-")) {
                final String[] split = str.split("-");
                if (split.length != 2) {
                    continue;
                }
                int int1;
                int int2;
                try {
                    int1 = Integer.parseInt(split[0]);
                    int2 = Integer.parseInt(split[1]);
                }
                catch (Exception ex) {
                    continue;
                }
                if (int1 <= n && n <= int2) {
                    return LevelsConfig.levels.getYml().getInt("levels." + str + ".rankup-cost");
                }
                continue;
            }
        }
        return LevelsConfig.levels.getYml().getInt("levels.others.rankup-cost");
    }
}
