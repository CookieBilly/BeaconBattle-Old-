

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.spectator.TeleporterGUI;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdTeleporter extends SubCommand
{
    public CmdTeleporter(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.showInList(false);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return false;
        }
        if (!arenaByPlayer.isSpectator(player)) {
            return false;
        }
        TeleporterGUI.openGUI(player);
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
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        return arenaByPlayer != null && arenaByPlayer.isSpectator(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
