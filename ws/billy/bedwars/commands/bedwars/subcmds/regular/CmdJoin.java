

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.Iterator;
import ws.billy.bedwars.api.arena.IArena;

import java.util.ArrayList;
import java.util.List;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdJoin extends SubCommand
{
    public CmdJoin(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(19);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " join §e<random/ arena/ groupName>", "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fJoin an arena by name or by group.\n§f/bw join random - join random arena."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (array.length < 1) {
            commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_USAGE));
            return true;
        }
        if (array[0].equalsIgnoreCase("random")) {
            if (!Arena.joinRandomArena(player)) {
                commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
                Sounds.playSound("join-denied", player);
            }
            else {
                Sounds.playSound("join-allowed", player);
            }
            return true;
        }
        if (MainCommand.isArenaGroup(array[0]) || array[0].contains("+")) {
            if (!Arena.joinRandomFromGroup(player, array[0])) {
                commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
                Sounds.playSound("join-denied", player);
            }
            else {
                Sounds.playSound("join-allowed", player);
            }
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            if (Arena.getArenaByName(array[0]).addPlayer(player, false)) {
                Sounds.playSound("join-allowed", player);
            }
            else {
                Sounds.playSound("join-denied", player);
            }
            return true;
        }
        if (Arena.getArenaByIdentifier(array[0]) != null) {
            if (Arena.getArenaByIdentifier(array[0]).addPlayer(player, false)) {
                Sounds.playSound("join-allowed", player);
            }
            else {
                Sounds.playSound("join-denied", player);
            }
            return true;
        }
        commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_GROUP_OR_ARENA_NOT_FOUND).replace("{name}", array[0]));
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>(ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups"));
        final Iterator<IArena> iterator = Arena.getArenas().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getArenaName());
        }
        return list;
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
