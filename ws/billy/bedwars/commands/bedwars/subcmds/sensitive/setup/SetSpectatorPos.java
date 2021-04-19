

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.ArrayList;
import java.util.List;
import ws.billy.bedwars.BeaconBattle;
import net.md_5.bungee.api.ChatColor;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetSpectatorPos extends SubCommand
{
    public SetSpectatorPos(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) {
            return false;
        }
        if (array.length != 0) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Usage: /" + BeaconBattle.mainCmd + " " + this.getSubCommandName());
        }
        else {
            session.getConfig().saveArenaLoc("spectator-loc", player.getLocation());
            player.sendMessage(session.getPrefix() + "Spectator location set!");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<String>();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
