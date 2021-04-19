

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.Arrays;

import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.server.SetupType;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import java.util.List;
import ws.billy.bedwars.api.command.SubCommand;

public class SetType extends SubCommand
{
    private static List<String> available;
    
    public SetType(final ParentCommand parentCommand, final String s) {
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
            this.sendUsage(player);
        }
        else {
            if (!SetType.available.contains(array[0])) {
                this.sendUsage(player);
                return true;
            }
            final List stringList = ws.billy.bedwars.BeaconBattle.config.getYml().getStringList("arenaGroups");
            final String string = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
            if (!stringList.contains(string)) {
                stringList.add(string);
                ws.billy.bedwars.BeaconBattle.config.set("arenaGroups", stringList);
                int i = 1;
                if (string.equalsIgnoreCase("Doubles")) {
                    i = 2;
                }
                else if (string.equalsIgnoreCase("3v3v3v3")) {
                    i = 3;
                }
                else if (string.equalsIgnoreCase("4v4v4v4")) {
                    i = 4;
                }
                session.getConfig().set("maxInTeam", i);
            }
            session.getConfig().set("group", string);
            player.sendMessage("§6 \u25aa §7Arena group changed to: §d" + string);
            if (session.getSetupType() == SetupType.ASSISTED) {
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
            }
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return SetType.available;
    }
    
    private void sendUsage(final Player player) {
        player.sendMessage("§9 \u25aa §7Usage: " + this.getParent().getName() + " " + this.getSubCommandName() + " <type>");
        player.sendMessage("§9Available types: ");
        for (final String str : SetType.available) {
            player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick("§1 \u25aa §e" + str + " §7(click to set)", "§dClick to make the arena " + str, "/" + this.getParent().getName() + " " + this.getSubCommandName() + " " + str, ClickEvent.Action.RUN_COMMAND));
        }
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    static {
        SetType.available = Arrays.asList("Solo", "Doubles", "3v3v3v3", "4v4v4v4");
    }
}
