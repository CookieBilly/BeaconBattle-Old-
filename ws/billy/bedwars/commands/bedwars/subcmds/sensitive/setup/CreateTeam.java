

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.List;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.api.server.SetupType;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.arena.team.TeamColor;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CreateTeam extends SubCommand
{
    public CreateTeam(final ParentCommand parentCommand, final String s) {
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
        if (array.length < 2) {
            player.sendMessage("§c\u25aa §7Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " createTeam §o<name> §o<color>");
            final StringBuilder sb = new StringBuilder("§7");
            for (final TeamColor teamColor : TeamColor.values()) {
                sb.append(teamColor.chat()).append(teamColor.toString()).append(ChatColor.GRAY).append(", ");
            }
            player.sendMessage("§6 \u25aa §7Available colors: " + (Object)new StringBuilder(sb.toString().substring(0, sb.toString().length() - 2) + ChatColor.GRAY + "."));
        }
        else {
            boolean b = true;
            final TeamColor[] values2 = TeamColor.values();
            for (int length2 = values2.length, j = 0; j < length2; ++j) {
                if (values2[j].toString().equalsIgnoreCase(array[1])) {
                    b = false;
                }
            }
            if (b) {
                player.sendMessage("§c\u25aa §7Invalid color!");
                final StringBuilder sb2 = new StringBuilder("§7");
                for (final TeamColor teamColor2 : TeamColor.values()) {
                    sb2.append(teamColor2.chat()).append(teamColor2.toString()).append(ChatColor.GRAY).append(", ");
                }
                player.sendMessage("§6 \u25aa §7Available colors: " + (Object)new StringBuilder(sb2.toString().substring(0, sb2.toString().length() - 2) + ChatColor.GRAY + "."));
            }
            else {
                if (session.getConfig().getYml().get("Team." + array[0] + ".Color") != null) {
                    player.sendMessage("§c\u25aa §7" + array[0] + " team already exists!");
                    return true;
                }
                session.getConfig().set("Team." + array[0] + ".Color", array[1].toUpperCase());
                player.sendMessage("§6 \u25aa §7" + TeamColor.getChatColor(array[1]) + array[0] + " §7created!");
                if (session.getSetupType() == SetupType.ASSISTED) {
                    session.getConfig().reload();
                    final int size = session.getConfig().getYml().getConfigurationSection("Team").getKeys(false).size();
                    int l = 1;
                    if (size == 4) {
                        l = 2;
                    }
                    session.getConfig().set("maxInTeam", l);
                }
            }
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
