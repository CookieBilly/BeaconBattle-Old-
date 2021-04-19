

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.Arrays;
import java.util.List;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetMaxInTeam extends SubCommand
{
    public SetMaxInTeam(final ParentCommand parentCommand, final String s) {
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
            commandSender.sendMessage("§c \u25aa §7You're not in a setup session!");
            return true;
        }
        if (array.length == 0) {
            player.sendMessage("§c\u25aa §7Usage: /" + BeaconBattle.mainCmd + " setMaxInTeam <int>");
        }
        else {
            try {
                Integer.parseInt(array[0]);
            }
            catch (Exception ex) {
                player.sendMessage("§c\u25aa §7Usage: /" + BeaconBattle.mainCmd + " setMaxInTeam <int>");
                return true;
            }
            session.getConfig().set("maxInTeam", Integer.valueOf(array[0]));
            player.sendMessage("§6 \u25aa §7Max in team set!");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("1", "2", "4");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
