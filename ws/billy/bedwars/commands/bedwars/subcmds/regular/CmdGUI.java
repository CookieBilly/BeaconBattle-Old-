

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.List;
import ws.billy.bedwars.arena.ArenaGUI;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdGUI extends SubCommand
{
    public CmdGUI(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(17);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fOpens the arena GUI."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (Arena.getArenaByPlayer(player) != null) {
            return false;
        }
        String s = "default";
        if (array.length == 1) {
            s = array[0];
        }
        ArenaGUI.openGui(player, s);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
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
