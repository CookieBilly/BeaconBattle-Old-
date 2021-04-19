

package ws.billy.bedwars.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class GeneratorsConfig extends ConfigManager
{
    public GeneratorsConfig(final Plugin plugin, final String s, final String s2) {
        super(plugin, s, s2);
        if (this.isFirstTime()) {
            final YamlConfiguration yml = this.getYml();
            yml.addDefault("Default.iron.delay", (Object)2);
            yml.addDefault("Default.iron.amount", (Object)2);
            yml.addDefault("Default.gold.delay", (Object)6);
            yml.addDefault("Default.gold.amount", (Object)2);
            yml.addDefault("Default.iron.spawn-limit", (Object)32);
            yml.addDefault("Default.gold.spawn-limit", (Object)7);
            yml.addDefault("stack-items", (Object)false);
            yml.addDefault("Default.diamond.tierI.delay", (Object)30);
            yml.addDefault("Default.diamond.tierI.spawn-limit", (Object)4);
            yml.addDefault("Default.diamond.tierII.delay", (Object)20);
            yml.addDefault("Default.diamond.tierII.spawn-limit", (Object)6);
            yml.addDefault("Default.diamond.tierII.start", (Object)360);
            yml.addDefault("Default.diamond.tierIII.delay", (Object)15);
            yml.addDefault("Default.diamond.tierIII.spawn-limit", (Object)8);
            yml.addDefault("Default.diamond.tierIII.start", (Object)1080);
            yml.addDefault("Default.emerald.tierI.delay", (Object)70);
            yml.addDefault("Default.emerald.tierI.spawn-limit", (Object)4);
            yml.addDefault("Default.emerald.tierII.delay", (Object)50);
            yml.addDefault("Default.emerald.tierII.spawn-limit", (Object)6);
            yml.addDefault("Default.emerald.tierII.start", (Object)720);
            yml.addDefault("Default.emerald.tierIII.delay", (Object)30);
            yml.addDefault("Default.emerald.tierIII.spawn-limit", (Object)8);
            yml.addDefault("Default.emerald.tierIII.start", (Object)1440);
            yml.options().copyDefaults(true);
            this.save();
        }
    }
}
