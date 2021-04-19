

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class WaitingPos extends SubCommand
{
    public WaitingPos(final ParentCommand parentCommand, final String s) {
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
            player.sendMessage("§c\u25aa §7Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " " + this.getSubCommandName() + " 1 or 2");
        }
        else if (array[0].equalsIgnoreCase("1") || array[0].equalsIgnoreCase("2")) {
            player.sendMessage("§6 \u25aa §7Pos " + array[0] + " set!");
            session.getConfig().saveArenaLoc("waiting.Pos" + array[0], player.getLocation());
            session.getConfig().reload();
            if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                player.sendMessage("§c \u25aa §7Set the remaining position:");
                player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 1", "§dSet pos 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
            }
            else if (session.getConfig().getYml().get("waiting.Pos2") == null) {
                player.sendMessage("§c \u25aa §7Set the remaining position:");
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 2", "§dSet pos 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
            }
        }
        else {
            player.sendMessage("§c\u25aa §7Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " " + this.getSubCommandName() + " 1 or 2");
        }
        if (session.getConfig().getYml().get("waiting.Pos1") != null && session.getConfig().getYml().get("waiting.Pos2") != null) {
            Bukkit.dispatchCommand((CommandSender)player, ws.billy.bedwars.BeaconBattle.mainCmd + " cmds");
            commandSender.sendMessage("§6 \u25aa §7Set teams spawn if you didn't!");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("1", "2");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
