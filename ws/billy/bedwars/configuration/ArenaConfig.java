

package ws.billy.bedwars.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class ArenaConfig extends ConfigManager
{
    public ArenaConfig(final Plugin plugin, final String s, final String s2) {
        super(plugin, s, s2);
        final YamlConfiguration yml = this.getYml();
        yml.addDefault("group", (Object)"Default");
        yml.addDefault("display-name", (Object)"");
        yml.addDefault("minPlayers", (Object)2);
        yml.addDefault("maxInTeam", (Object)1);
        yml.addDefault("allowSpectate", (Object)true);
        yml.addDefault("spawn-protection", (Object)5);
        yml.addDefault("shop-protection", (Object)1);
        yml.addDefault("upgrades-protection", (Object)1);
        yml.addDefault("island-radius", (Object)17);
        yml.addDefault("worldBorder", (Object)300);
        yml.addDefault("voidKill", (Object)false);
        yml.addDefault("max-build-y", (Object)180);
        yml.addDefault("disable-generator-for-empty-teams", (Object)false);
        yml.addDefault("disable-npcs-for-empty-teams", (Object)true);
        yml.addDefault("vanilla-death-drops", (Object)false);
        yml.addDefault("use-bed-hologram", (Object)true);
        yml.addDefault("allow-map-break", (Object)false);
        final ArrayList<String> list = new ArrayList<String>();
        list.add("doDaylightCycle:false");
        list.add("announceAdvancements:false");
        list.add("doInsomnia:false");
        list.add("doImmediateRespawn:true");
        list.add("doWeatherCycle:false");
        yml.addDefault("game-rules", (Object)list);
        yml.options().copyDefaults(true);
        this.save();
        if (yml.get("spawnProtection") != null) {
            this.set("spawn-protection", yml.getInt("spawnProtection"));
            this.set("spawnProtection", null);
        }
        if (yml.get("shopProtection") != null) {
            this.set("shop-protection", yml.getInt("shopProtection"));
            this.set("shopProtection", null);
        }
        if (yml.get("upgradesProtection") != null) {
            this.set("upgrades-protection", yml.getInt("upgradesProtection"));
            this.set("upgradesProtection", null);
        }
        if (yml.get("islandRadius") != null) {
            this.set("island-radius", yml.getInt("islandRadius"));
        }
    }
}
