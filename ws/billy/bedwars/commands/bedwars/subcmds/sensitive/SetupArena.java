

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import java.util.List;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetupArena extends SubCommand
{
    public SetupArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(2);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " setupArena §6<worldName>", "§fCreate or edit an arena.\n'_' and '-' will not be displayed in the arena's name.", "/" + MainCommand.getInstance().getName() + " setupArena ", ClickEvent.Action.SUGGEST_COMMAND));
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
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
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Usage: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mapName>");
            return true;
        }
        if (!array[0].equals(array[0].toLowerCase())) {
            player.sendMessage("§c\u25aa §c" + array[0] + ChatColor.GRAY + " mustn't contain capital letters! Rename your folder to: " + ChatColor.GREEN + array[0].toLowerCase());
            return true;
        }
        if (array[0].contains("+")) {
            player.sendMessage("§c\u25aa §7" + array[0] + " mustn't contain this symbol: " + ChatColor.RED + "+");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null && !ws.billy.bedwars.BeaconBattle.autoscale) {
            player.sendMessage("§c\u25aa §7Please disable it first!");
            return true;
        }
        if (SetupSession.isInSetupSession(player.getUniqueId())) {
            player.sendMessage("§c \u25aa §7You're already in a setup session!");
            return true;
        }
        new SetupSession(player, array[0]);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().getWorldsList();
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
