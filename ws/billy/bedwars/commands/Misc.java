

package ws.billy.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import java.util.List;
import org.bukkit.Material;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Location;

public class Misc
{
    public static void createArmorStand(final String customName, @NotNull final Location location, final String s) {
        final ArmorStand armorStand = (ArmorStand)location.getWorld().spawnEntity(location.getBlock().getLocation().add(0.5, 2.0, 0.5), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(customName);
        armorStand.setMetadata("bw1058-setup", (MetadataValue)new FixedMetadataValue((Plugin)BeaconBattle.plugin, (Object)"hologram"));
        if (s != null) {
            armorStand.setMetadata("bw1058-loc", (MetadataValue)new FixedMetadataValue((Plugin)BeaconBattle.plugin, (Object)s));
        }
    }
    
    public static void removeArmorStand(final String s, @NotNull final Location location, final String anotherString) {
        for (final Entity entity : location.getWorld().getNearbyEntities(location, 1.0, 3.0, 1.0)) {
            if (entity.hasMetadata("bw1058-setup")) {
                if (entity.hasMetadata("bw1058-loc")) {
                    if (!((MetadataValue)entity.getMetadata("bw1058-loc").get(0)).asString().equalsIgnoreCase(anotherString)) {
                        continue;
                    }
                    if (s != null && !s.isEmpty() && ChatColor.stripColor(entity.getCustomName()).contains(s)) {
                        entity.remove();
                        return;
                    }
                    entity.remove();
                }
                else {
                    entity.remove();
                }
            }
            else {
                if (entity.getType() != EntityType.ARMOR_STAND || ((ArmorStand)entity).isVisible() || s == null || !entity.getCustomName().contains(s)) {
                    continue;
                }
                entity.remove();
            }
        }
    }
    


    
    private static void detectGenerators(Location location, final SetupSession setupSession) {
        location = location.getBlock().getLocation();
        setupSession.addSkipAutoCreateGen(location);
        final Material type = location.getBlock().getType();
        final Material type2 = location.clone().add(0.0, 1.0, -1.0).getBlock().getType();
        final Material type3 = location.clone().add(0.0, 1.0, 1.0).getBlock().getType();
        final Material type4 = location.clone().add(-1.0, 1.0, 0.0).getBlock().getType();
        final Material type5 = location.clone().add(1.0, 1.0, 0.0).getBlock().getType();
        final Material type6 = location.clone().add(1.0, 1.0, 1.0).getBlock().getType();
        final Material type7 = location.clone().add(1.0, 1.0, -1.0).getBlock().getType();
        final Material type8 = location.clone().add(-1.0, 1.0, 1.0).getBlock().getType();
        final Material type9 = location.clone().add(-1.0, 1.0, -1.0).getBlock().getType();
        final String string = "generator." + ((type == Material.DIAMOND_BLOCK) ? "Diamond" : "Emerald");
        if (type2 == Material.AIR || type3 == Material.AIR || type4 == Material.AIR || type5 == Material.AIR || type6 == Material.AIR || type7 == Material.AIR || type8 == Material.AIR || type9 == Material.AIR) {
            return;
        }
        final List<Location> arenaLocations = setupSession.getConfig().getArenaLocations(string);
        for (int i = -150; i < 150; ++i) {
            for (int j = -150; j < 150; ++j) {
                final Block block = location.clone().add((double)i, 0.0, (double)j).getBlock();
                if (block.getX() != location.getBlockX() || block.getY() != location.getBlockY() || block.getZ() != location.getBlockZ()) {
                    final Location add = block.getLocation().clone().add(0.0, 1.0, 0.0);
                    final Iterator<Location> iterator = arenaLocations.iterator();
                    while (iterator.hasNext()) {
                        if (setupSession.getConfig().compareArenaLoc(iterator.next(), block.getLocation().add(0.0, 1.0, 0.0))) {
                            continue;
                        }
                    }
                    if (block.getType() == type && type2 == add.clone().add(0.0, 0.0, -1.0).getBlock().getType() && type3 == add.clone().add(0.0, 0.0, 1.0).getBlock().getType() && type4 == add.clone().add(-1.0, 0.0, 0.0).getBlock().getType() && type5 == add.clone().add(1.0, 0.0, 0.0).getBlock().getType() && type7 == add.clone().add(1.0, 0.0, -1.0).getBlock().getType() && type6 == add.clone().add(1.0, 0.0, 1.0).getBlock().getType() && type8 == add.clone().add(-1.0, 0.0, 1.0).getBlock().getType() && type9 == add.clone().add(-1.0, 0.0, -1.0).getBlock().getType() && !setupSession.getSkipAutoCreateGen().contains(add)) {
                        setupSession.addSkipAutoCreateGen(add);
                        detectGenerators(block.getLocation(), setupSession);
                    }
                }
            }
        }
    }
}
