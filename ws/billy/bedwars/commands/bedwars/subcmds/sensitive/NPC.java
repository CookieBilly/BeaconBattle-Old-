

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.arena.Arena;
import org.jetbrains.annotations.Nullable;
import org.bukkit.block.Block;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.util.BlockIterator;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.Location;
import java.util.Iterator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import net.md_5.bungee.api.ChatColor;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import ws.billy.bedwars.support.citizens.JoinNPC;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import java.util.Arrays;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.api.command.ParentCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import java.util.List;
import ws.billy.bedwars.api.command.SubCommand;

public class NPC extends SubCommand
{
    private final List<BaseComponent> MAIN_USAGE;
    private final List<BaseComponent> ADD_USAGE;
    
    public NPC(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.MAIN_USAGE = Arrays.asList((BaseComponent) Misc.msgHoverClick("§f\n§c\u25aa §7Usage: §e/" + ws.billy.bedwars.BeaconBattle.mainCmd + " " + this.getSubCommandName() + " add", "§fUse this command to create a join NPC.\n§fClick to see the syntax.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " add", ClickEvent.Action.RUN_COMMAND), (BaseComponent)Misc.msgHoverClick("§c\u25aa §7Usage: §e/" + ws.billy.bedwars.BeaconBattle.mainCmd + " " + this.getSubCommandName() + " remove", "§fStay in front of a NPC in order to remove it.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " remove", ClickEvent.Action.SUGGEST_COMMAND));
        this.ADD_USAGE = Arrays.asList((BaseComponent)Misc.msgHoverClick("f\n§c\u25aa §7Usage: §e§o/" + this.getParent().getName() + " " + this.getSubCommandName() + " add <skin> <arenaGroup> <§7line1§9\\n§7line2§e>\n§7You can use §e{players} §7for the players count in this arena §7group.", "Click to use.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " add", ClickEvent.Action.SUGGEST_COMMAND));
        this.showInList(true);
        this.setPriority(12);
        this.setPermission(Permissions.PERMISSION_NPC);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + "         §8   - §ecreate a join NPC", "§fCreate a join NPC  \n§fClick for more details.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        if (!JoinNPC.isCitizensSupport()) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (array.length < 1) {
            final Iterator<BaseComponent> iterator = this.MAIN_USAGE.iterator();
            while (iterator.hasNext()) {
                player.spigot().sendMessage((BaseComponent)iterator.next());
            }
            return true;
        }
        if (array[0].equalsIgnoreCase("add")) {
            if (array.length < 4) {
                final Iterator<BaseComponent> iterator2 = this.ADD_USAGE.iterator();
                while (iterator2.hasNext()) {
                    player.spigot().sendMessage((BaseComponent)iterator2.next());
                }
                return true;
            }
            List<String> stringList;
            if (ws.billy.bedwars.BeaconBattle.config.getYml().get("join-npc-locations") != null) {
                stringList = (List<String>) ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("join-npc-locations");
            }
            else {
                stringList = new ArrayList<String>();
            }
            final String replace = Joiner.on(" ").join((Object[])array).replace(array[0] + " " + array[1] + " " + array[2] + " ", "");
            final net.citizensnpcs.api.npc.NPC spawnNPC = JoinNPC.spawnNPC(player.getLocation(), replace, array[2], array[1], null);
            assert spawnNPC != null;
            stringList.add(ws.billy.bedwars.BeaconBattle.config.stringLocationConfigFormat(player.getLocation()) + "," + array[1] + "," + replace + "," + array[2] + "," + spawnNPC.getId());
            player.sendMessage("§a§c\u25aa §bNPC: %name% §bwas set!".replace("%name%", replace.replace("&", "§").replace("\\\\n", " ")));
            player.sendMessage("§a§c\u25aa §bTarget groups: " + ChatColor.GOLD + array[2]);
            ws.billy.bedwars.BeaconBattle.config.set("join-npc-locations", stringList);
        }
        else if (array[0].equalsIgnoreCase("remove")) {
            final List nearbyEntities = player.getNearbyEntities(4.0, 4.0, 4.0);
            final String s = "§c\u25aa §bThere isn't any NPC nearby.";
            if (nearbyEntities.isEmpty()) {
                player.sendMessage(s);
                return true;
            }
            if (ws.billy.bedwars.BeaconBattle.config.getYml().get("join-npc-locations") == null) {
                player.sendMessage("§c\u25aa §bThere isn't any NPC set yet!");
                return true;
            }
            final net.citizensnpcs.api.npc.NPC target = getTarget(player);
            if (target == null) {
                player.sendMessage(s);
                return true;
            }
            final List stringList2 = ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("join-npc-locations");
            final Iterator<Integer> iterator3 = JoinNPC.npcs.keySet().iterator();
            while (iterator3.hasNext()) {
                if (iterator3.next() == target.getId()) {
                    for (final String s2 : ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("join-npc-locations")) {
                        if (s2.split(",")[4].equalsIgnoreCase(String.valueOf(target.getId()))) {
                            stringList2.remove(s2);
                        }
                    }
                }
            }
            JoinNPC.npcs.remove(target.getId());
            for (final Entity entity : target.getEntity().getNearbyEntities(0.0, 3.0, 0.0)) {
                if (entity.getType() == EntityType.ARMOR_STAND) {
                    entity.remove();
                }
            }
            ws.billy.bedwars.BeaconBattle.config.set("join-npc-locations", stringList2);
            target.destroy();
            player.sendMessage("§c\u25aa §bThe target NPC was removed!");
        }
        else {
            final Iterator<BaseComponent> iterator6 = this.MAIN_USAGE.iterator();
            while (iterator6.hasNext()) {
                player.spigot().sendMessage((BaseComponent)iterator6.next());
            }
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("remove", "add");
    }
    
    @NotNull
    public static ArmorStand createArmorStand(final Location location) {
        final ArmorStand armorStand = (ArmorStand)location.getWorld().spawn(location, (Class)ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(false);
        armorStand.setMarker(true);
        return armorStand;
    }
    
    @Nullable
    public static net.citizensnpcs.api.npc.NPC getTarget(final Player player) {
        final BlockIterator blockIterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), player.getEyeLocation().getDirection(), 0.0, 100);
        while (blockIterator.hasNext()) {
            final Block next = blockIterator.next();
            for (final Entity entity : player.getNearbyEntities(100.0, 100.0, 100.0)) {
                for (int n = 2, i = -n; i < n; ++i) {
                    for (int j = -n; j < n; ++j) {
                        for (int k = -n; k < n; ++k) {
                            if (entity.getLocation().getBlock().getRelative(i, k, j).equals(next) && entity.hasMetadata("NPC")) {
                                final net.citizensnpcs.api.npc.NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                                if (npc != null) {
                                    return npc;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
