

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.List;
import ws.billy.bedwars.listeners.BreakPlace;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class Build extends SubCommand
{
    public Build(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(9);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_BUILD);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + "         §8 - §ebuild permission", "§fEnable or disable build session \n§fso you can break or place blocks.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
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
        if (BreakPlace.isBuildSession(player)) {
            player.sendMessage("§6 \u25aa §7You can't place and break blocks anymore!");
            BreakPlace.removeBuildSession(player);
        }
        else {
            player.sendMessage("§6 \u25aa §7You can place and break blocks now.");
            BreakPlace.addBuildSession(player);
        }
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
