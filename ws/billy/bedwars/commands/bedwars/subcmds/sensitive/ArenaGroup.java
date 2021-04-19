

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import net.md_5.bungee.api.chat.BaseComponent;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.configuration.ArenaConfig;
import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class ArenaGroup extends SubCommand
{
    public ArenaGroup(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(8);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_ARENA_GROUP);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §8- §eclick for details", "§fManage arena groups.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (!MainCommand.isLobbySet(player)) {
            return true;
        }
        if (array.length < 2) {
            this.sendArenaGroupCmdList(player);
        }
        else if (array[0].equalsIgnoreCase("create")) {
            if (array[0].contains("+")) {
                player.sendMessage("§c\u25aa §7" + array[0] + " mustn't contain this symbol: " + ChatColor.RED + "+");
                return true;
            }
            List<String> stringList;
            if (ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups") == null) {
                stringList = new ArrayList<String>();
            }
            else {
                stringList = (List<String>) ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups");
            }
            if (stringList.contains(array[1])) {
                player.sendMessage("§c\u25aa §7This group already exists!");
                return true;
            }
            stringList.add(array[1]);
            ws.billy.bedwars.BeaconBattle.config.set("arenaGroups", stringList);
            player.sendMessage("§6 \u25aa §7Group created!");
        }
        else if (array[0].equalsIgnoreCase("remove")) {
            List stringList2;
            if (ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups") == null) {
                stringList2 = new ArrayList();
            }
            else {
                stringList2 = ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups");
            }
            if (!stringList2.contains(array[1])) {
                player.sendMessage("§c\u25aa §7This group doesn't exist!");
                return true;
            }
            stringList2.remove(array[1]);
            ws.billy.bedwars.BeaconBattle.config.set("arenaGroups", stringList2);
            player.sendMessage("§6 \u25aa §7Group deleted!");
        }
        else if (array[0].equalsIgnoreCase("list")) {
            List<String> stringList3;
            if (ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups") == null) {
                stringList3 = new ArrayList<String>();
            }
            else {
                stringList3 = (List<String>) ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups");
            }
            player.sendMessage("§7Available arena groups:");
            player.sendMessage("§6 \u25aa §fDefault");
            final Iterator<String> iterator = stringList3.iterator();
            while (iterator.hasNext()) {
                player.sendMessage("§6 \u25aa §f" + iterator.next());
            }
        }
        else if (array[0].equalsIgnoreCase("set")) {
            if (array.length < 3) {
                this.sendArenaGroupCmdList(player);
                return true;
            }
            if (ws.billy.bedwars.BeaconBattle.config.getYml().get("arenaGroups") != null) {
                if (ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups").contains(array[2])) {
                    if (!new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas/" + array[1] + ".yml").exists()) {
                        player.sendMessage("§c\u25aa §7Arena " + array[1] + " doesn't exist!");
                        return true;
                    }
                    new ArenaConfig((Plugin) ws.billy.bedwars.BeaconBattle.plugin, array[1], ws.billy.bedwars.BeaconBattle.plugin.getDataFolder().getPath() + "/Arenas").set("group", array[2]);
                    if (Arena.getArenaByName(array[1]) != null) {
                        Arena.getArenaByName(array[1]).setGroup(array[2]);
                    }
                    player.sendMessage("§6 \u25aa §7" + array[1] + " was added to the group: " + array[2]);
                }
                else {
                    player.sendMessage("§6 \u25aa §7There isn't any group called: " + array[2]);
                    Bukkit.dispatchCommand((CommandSender)player, "/bw list");
                }
            }
            else {
                player.sendMessage("§6 \u25aa §7There isn't any group called: " + array[2]);
                Bukkit.dispatchCommand((CommandSender)player, "/bw list");
            }
        }
        else {
            this.sendArenaGroupCmdList(player);
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("create", "remove", "list", "set");
    }
    
    private void sendArenaGroupCmdList(final Player player) {
        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " create §o<groupName>", "Create an arena group. More details on our wiki.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " create", ClickEvent.Action.SUGGEST_COMMAND));
        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " list", "View available groups.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " list", ClickEvent.Action.RUN_COMMAND));
        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " remove §o<groupName>", "Remove an arena group. More details on our wiki.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " remove", ClickEvent.Action.SUGGEST_COMMAND));
        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §r§7set §o<arenaName> <groupName>", "Set the arena group. More details on our wiki.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " set", ClickEvent.Action.SUGGEST_COMMAND));
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
