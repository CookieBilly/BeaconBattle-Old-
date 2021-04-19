

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.List;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.server.SetupType;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetWaitingSpawn extends SubCommand
{
    public SetWaitingSpawn(final ParentCommand parentCommand, final String s) {
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
        player.sendMessage("§6 \u25aa §7Waiting spawn set for §e" + session.getWorldName() + "§7!");
        session.getConfig().saveArenaLoc("waiting.Loc", player.getLocation());
        if (session.getSetupType() == SetupType.ASSISTED) {
            Bukkit.dispatchCommand(commandSender, BeaconBattle.mainCmd + " autocreateteams");
        }
        else {
            Bukkit.dispatchCommand(commandSender, BeaconBattle.mainCmd);
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
