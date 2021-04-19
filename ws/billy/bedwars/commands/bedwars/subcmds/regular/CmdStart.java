

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdStart extends SubCommand
{
    public CmdStart(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(15);
        this.showInList(true);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + " §8 - §eforce start an arena", "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fForcestart an arena.\n§fPermission: §c" + Permissions.PERMISSION_FORCESTART));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
            return true;
        }
        if (!arenaByPlayer.isPlayer(player)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
            return true;
        }
        if (!player.hasPermission(Permissions.PERMISSION_ALL) && !player.hasPermission(Permissions.PERMISSION_FORCESTART)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NO_PERM));
            return true;
        }
        if (arenaByPlayer.getStatus() == GameState.playing) {
            return true;
        }
        if (arenaByPlayer.getStatus() == GameState.restarting) {
            return true;
        }
        if (arenaByPlayer.getStartingTask() == null) {
            if (array.length != 1 || !array[0].equalsIgnoreCase("debug") || !commandSender.isOp()) {
                return true;
            }
            arenaByPlayer.changeStatus(GameState.starting);
            ws.billy.bedwars.BeaconBattle.debug = true;
        }
        if (arenaByPlayer.getStartingTask().getCountdown() < 5) {
            return true;
        }
        arenaByPlayer.getStartingTask().setCountdown(5);
        player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_SUCCESS));
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
        return arenaByPlayer != null && (arenaByPlayer.getStatus() == GameState.waiting || arenaByPlayer.getStatus() == GameState.starting) && arenaByPlayer.isPlayer(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && commandSender.hasPermission(Permissions.PERMISSION_FORCESTART);
    }
}
