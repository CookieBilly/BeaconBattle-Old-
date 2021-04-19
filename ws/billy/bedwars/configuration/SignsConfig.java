

package ws.billy.bedwars.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.BeaconBattle;
import java.util.Arrays;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class SignsConfig extends ConfigManager
{
    public SignsConfig(final Plugin plugin, final String s, final String s2) {
        super(plugin, s, s2);
        final YamlConfiguration yml = this.getYml();
        yml.addDefault("format", (Object)Arrays.asList("&a[arena]", "", "&2[on]&9/&2[max]", "[status]"));
        yml.addDefault("status-block.waiting.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "GREEN_CONCRETE"));
        yml.addDefault("status-block.waiting.data", (Object)5);
        yml.addDefault("status-block.starting.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "YELLOW_CONCRETE"));
        yml.addDefault("status-block.starting.data", (Object)14);
        yml.addDefault("status-block.playing.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "RED_CONCRETE"));
        yml.addDefault("status-block.playing.data", (Object)4);
        yml.addDefault("status-block.restarting.material", (Object)BeaconBattle.getForCurrentVersion("STAINED_CLAY", "STAINED_CLAY", "RED_CONCRETE"));
        yml.addDefault("status-block.restarting.data", (Object)4);
        yml.options().copyDefaults(true);
        this.save();
        if (yml.getStringList("format").size() < 4) {
            this.set("format", yml.getStringList("format").subList(0, 3));
        }
    }
}
