

package ws.billy.bedwars.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Collections;
import java.util.Arrays;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.configuration.ConfigManager;

public class UpgradesConfig extends ConfigManager
{
    public UpgradesConfig(final String s, final String s2) {
        super((Plugin)BeaconBattle.plugin, s, s2);
        final YamlConfiguration yml = this.getYml();
        yml.addDefault("default-upgrades-settings.menu-content", (Object)Arrays.asList("upgrade-swords,10", "upgrade-armor,11", "upgrade-miner,12", "upgrade-forge,13", "upgrade-heal-pool,14", "upgrade-dragon,15", "category-traps,16", "separator-glass,18,19,20,21,22,23,24,25,26", "trap-slot-first,30", "trap-slot-second,31", "trap-slot-third,32"));
        yml.addDefault("default-upgrades-settings.trap-start-price", (Object)1);
        yml.addDefault("default-upgrades-settings.trap-increment-price", (Object)1);
        yml.addDefault("default-upgrades-settings.trap-currency", (Object)"diamond");
        yml.addDefault("default-upgrades-settings.trap-queue-limit", (Object)3);
        if (this.isFirstTime()) {
            yml.addDefault("upgrade-swords.tier-1.cost", (Object)4);
            yml.addDefault("upgrade-swords.tier-1.currency", (Object)"diamond");
            this.addDefaultDisplayItem("upgrade-swords.tier-1", "IRON_SWORD", 0, 1, false);
            yml.addDefault("upgrade-swords.tier-1.receive", (Object)Arrays.asList("enchant-item: DAMAGE_ALL,1,sword"));
            yml.addDefault("upgrade-armor.tier-1.currency", (Object)"diamond");
            yml.addDefault("upgrade-armor.tier-1.cost", (Object)2);
            this.addDefaultDisplayItem("upgrade-armor.tier-1", "IRON_CHESTPLATE", 0, 1, false);
            yml.addDefault("upgrade-armor.tier-1.receive", (Object)Collections.singletonList("enchant-item: PROTECTION_ENVIRONMENTAL,1,armor"));
            yml.addDefault("upgrade-armor.tier-2.currency", (Object)"diamond");
            yml.addDefault("upgrade-armor.tier-2.cost", (Object)4);
            this.addDefaultDisplayItem("upgrade-armor.tier-2", "IRON_CHESTPLATE", 0, 2, false);
            yml.addDefault("upgrade-armor.tier-2.receive", (Object)Collections.singletonList("enchant-item: PROTECTION_ENVIRONMENTAL,2,armor"));
            yml.addDefault("upgrade-armor.tier-3.currency", (Object)"diamond");
            yml.addDefault("upgrade-armor.tier-3.cost", (Object)8);
            this.addDefaultDisplayItem("upgrade-armor.tier-3", "IRON_CHESTPLATE", 0, 3, false);
            yml.addDefault("upgrade-armor.tier-3.receive", (Object)Collections.singletonList("enchant-item: PROTECTION_ENVIRONMENTAL,3,armor"));
            yml.addDefault("upgrade-armor.tier-4.currency", (Object)"diamond");
            yml.addDefault("upgrade-armor.tier-4.cost", (Object)16);
            this.addDefaultDisplayItem("upgrade-armor.tier-4", "IRON_CHESTPLATE", 0, 4, false);
            yml.addDefault("upgrade-armor.tier-4.receive", (Object)Collections.singletonList("enchant-item: PROTECTION_ENVIRONMENTAL,4,armor"));
            yml.addDefault("upgrade-miner.tier-1.currency", (Object)"diamond");
            yml.addDefault("upgrade-miner.tier-1.cost", (Object)2);
            this.addDefaultDisplayItem("upgrade-miner.tier-1", "GOLD_PICKAXE", 0, 1, false);
            yml.addDefault("upgrade-miner.tier-1.receive", (Object)Collections.singletonList("player-effect: FAST_DIGGING,1,0,team"));
            yml.addDefault("upgrade-miner.tier-2.currency", (Object)"diamond");
            yml.addDefault("upgrade-miner.tier-2.cost", (Object)4);
            this.addDefaultDisplayItem("upgrade-miner.tier-2", "GOLD_PICKAXE", 0, 2, false);
            yml.addDefault("upgrade-miner.tier-2.receive", (Object)Collections.singletonList("player-effect: FAST_DIGGING,2,0,team"));
            yml.addDefault("upgrade-forge.tier-1.currency", (Object)"diamond");
            yml.addDefault("upgrade-forge.tier-1.cost", (Object)2);
            this.addDefaultDisplayItem("upgrade-forge.tier-1", "FURNACE", 0, 1, false);
            yml.addDefault("upgrade-forge.tier-1.receive", (Object)Arrays.asList("generator-edit: iron,2,2,41", "generator-edit: gold,3,1,14"));
            yml.addDefault("upgrade-forge.tier-2.currency", (Object)"diamond");
            yml.addDefault("upgrade-forge.tier-2.cost", (Object)4);
            this.addDefaultDisplayItem("upgrade-forge.tier-2", "FURNACE", 0, 2, false);
            yml.addDefault("upgrade-forge.tier-2.receive", (Object)Arrays.asList("generator-edit: iron,1,2,48", "generator-edit: gold,3,2,21"));
            yml.addDefault("upgrade-forge.tier-3.currency", (Object)"diamond");
            yml.addDefault("upgrade-forge.tier-3.cost", (Object)6);
            this.addDefaultDisplayItem("upgrade-forge.tier-3", "FURNACE", 0, 3, false);
            yml.addDefault("upgrade-forge.tier-3.receive", (Object)Arrays.asList("generator-edit: iron,1,2,64", "generator-edit: gold,3,2,29", "generator-edit: emerald,10,1,10"));
            yml.addDefault("upgrade-forge.tier-4.currency", (Object)"diamond");
            yml.addDefault("upgrade-forge.tier-4.cost", (Object)8);
            this.addDefaultDisplayItem("upgrade-forge.tier-4", "FURNACE", 0, 4, false);
            yml.addDefault("upgrade-forge.tier-4.receive", (Object)Arrays.asList("generator-edit: iron,1,4,120", "generator-edit: gold,2,4,80", "generator-edit: emerald,10,2,20"));
            yml.addDefault("upgrade-heal-pool.tier-1.currency", (Object)"diamond");
            yml.addDefault("upgrade-heal-pool.tier-1.cost", (Object)1);
            this.addDefaultDisplayItem("upgrade-heal-pool.tier-1", "BEACON", 0, 1, false);
            yml.addDefault("upgrade-heal-pool.tier-1.receive", (Object)Collections.singletonList("player-effect: REGENERATION,1,0,base"));
            yml.addDefault("upgrade-dragon.tier-1.currency", (Object)"diamond");
            yml.addDefault("upgrade-dragon.tier-1.cost", (Object)5);
            this.addDefaultDisplayItem("upgrade-dragon.tier-1", "DRAGON_EGG", 0, 1, false);
            yml.addDefault("upgrade-dragon.tier-1.receive", (Object)Collections.singletonList("dragon: 1"));
            this.addDefaultDisplayItem("category-traps", "LEATHER", 0, 1, false);
            yml.addDefault("category-traps.category-content", (Object)Arrays.asList("base-trap-1,10", "base-trap-2,11", "base-trap-3,12", "base-trap-4,13", "separator-back,31"));
            yml.addDefault("separator-glass.on-click", (Object)"");
            this.addDefaultDisplayItem("separator-glass", BeaconBattle.getForCurrentVersion("STAINED_GLASS_PANE", "STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"), 7, 1, false);
            yml.addDefault("trap-slot-first.trap", (Object)1);
            this.addDefaultDisplayItem("trap-slot-first", BeaconBattle.getForCurrentVersion("STAINED_GLASS", "STAINED_GLASS", "GRAY_STAINED_GLASS"), 8, 1, false);
            yml.addDefault("trap-slot-second.trap", (Object)2);
            this.addDefaultDisplayItem("trap-slot-second", BeaconBattle.getForCurrentVersion("STAINED_GLASS", "STAINED_GLASS", "GRAY_STAINED_GLASS"), 8, 2, false);
            yml.addDefault("trap-slot-third.trap", (Object)3);
            this.addDefaultDisplayItem("trap-slot-third", BeaconBattle.getForCurrentVersion("STAINED_GLASS", "STAINED_GLASS", "GRAY_STAINED_GLASS"), 8, 3, false);
            this.addDefaultDisplayItem("base-trap-1", "TRIPWIRE_HOOK", 0, 1, false);
            yml.addDefault("base-trap-1.receive", (Object)Arrays.asList("player-effect: BLINDNESS,1,5,enemy", "player-effect: SLOW,1,5,enemy"));
            this.addDefaultDisplayItem("base-trap-2", "FEATHER", 0, 1, false);
            yml.addDefault("base-trap-2.receive", (Object)Collections.singletonList("player-effect: SPEED,1,15,base"));
            this.addDefaultDisplayItem("base-trap-3", BeaconBattle.getForCurrentVersion("REDSTONE_TORCH_ON", "REDSTONE_TORCH", "REDSTONE_TORCH"), 0, 1, false);
            yml.addDefault("base-trap-3.custom-announce", (Object)true);
            yml.addDefault("base-trap-3.receive", (Object)Collections.singletonList("remove-effect: INVISIBILITY,enemy"));
            this.addDefaultDisplayItem("base-trap-4", "IRON_PICKAXE", 0, 1, false);
            yml.addDefault("base-trap-4.receive", (Object)Collections.singletonList("player-effect: SLOW_DIGGING,1,15,enemy"));
            yml.addDefault("separator-back.on-click.player", (Object)Arrays.asList("bw upgradesmenu"));
            yml.addDefault("separator-back.on-click.console", (Object)Arrays.asList(""));
            this.addDefaultDisplayItem("separator-back", "ARROW", 0, 1, false);
        }
        yml.options().copyDefaults(true);
        this.save();
    }
    
    private void addDefaultDisplayItem(final String s, final String s2, final int i, final int j, final boolean b) {
        this.getYml().addDefault(s + ".display-item.material", (Object)s2);
        this.getYml().addDefault(s + ".display-item.data", (Object)i);
        this.getYml().addDefault(s + ".display-item.amount", (Object)j);
        this.getYml().addDefault(s + ".display-item.enchanted", (Object)b);
    }
}
