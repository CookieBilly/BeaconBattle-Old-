

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import java.util.List;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetLobby extends SubCommand
{
    public SetLobby(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(1);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + (ws.billy.bedwars.BeaconBattle.config.getLobbyWorldName().isEmpty() ? " §c(not set)" : " §a(set)"), "§aSet the main lobby. §fThis is required but\n§fif you are going to use the server in §eBUNGEE §fmode\n§fthe lobby location will §enot §fbe used.\n§eType again to replace the old spawn location.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (SetupSession.isInSetupSession(player.getUniqueId())) {
            player.sendMessage("§6 \u25aa §4This command can't be used in arenas. It is meant for the main lobby!");
            return true;
        }
        ws.billy.bedwars.BeaconBattle.config.saveConfigLoc("lobbyLoc", player.getLocation());
        player.sendMessage("§6 \u25aa §7Lobby location set!");
        ws.billy.bedwars.BeaconBattle.config.reload();
        ws.billy.bedwars.BeaconBattle.setLobbyWorld(player.getLocation().getWorld().getName());
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
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && BeaconBattle.getLobbyWorld().isEmpty() && this.hasPermission(commandSender);
    }
}
