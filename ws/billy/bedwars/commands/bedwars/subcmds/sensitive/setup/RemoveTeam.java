

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.List;
import java.util.Iterator;
import org.bukkit.Location;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.configuration.Sounds;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.api.arena.team.TeamColor;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class RemoveTeam extends SubCommand
{
    public RemoveTeam(final ParentCommand parentCommand, final String s) {
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
        if (array.length < 1) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " removeTeam <teamName>");
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Available teams: ");
                for (final String str : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                    player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + TeamColor.getChatColor(str) + str, ChatColor.GRAY + "Remove " + TeamColor.getChatColor(str) + str + " " + ChatColor.GRAY + "(click to remove)", "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " removeTeam " + str, ClickEvent.Action.RUN_COMMAND));
                }
            }
        }
        else if (session.getConfig().getYml().get("Team." + array[0] + ".Color") == null) {
            player.sendMessage(session.getPrefix() + "This team doesn't exist: " + array[0]);
            ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Team not found: " + array[0], 5, 40, 5);
            Sounds.playSound("shop-insufficient-money", player);
        }
        else {
            if (session.getConfig().getYml().get("Team." + array[0] + ".Iron") != null) {
                final Iterator<Location> iterator2 = session.getConfig().getArenaLocations("Team." + array[0] + ".Iron").iterator();
                while (iterator2.hasNext()) {
                    ws.billy.bedwars.commands.Misc.removeArmorStand(null, iterator2.next(), null);
                }
            }
            if (session.getConfig().getYml().get("Team." + array[0] + ".Gold") != null) {
                final Iterator<Location> iterator3 = session.getConfig().getArenaLocations("Team." + array[0] + ".Gold").iterator();
                while (iterator3.hasNext()) {
                    ws.billy.bedwars.commands.Misc.removeArmorStand(null, iterator3.next(), null);
                }
            }
            if (session.getConfig().getYml().get("Team." + array[0] + ".Emerald") != null) {
                final Iterator<Location> iterator4 = session.getConfig().getArenaLocations("Team." + array[0] + ".Emerald").iterator();
                while (iterator4.hasNext()) {
                    ws.billy.bedwars.commands.Misc.removeArmorStand(null, iterator4.next(), null);
                }
            }
            if (session.getConfig().getYml().get("Team." + array[0] + ".Shop") != null) {
                ws.billy.bedwars.commands.Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + ".Shop"), null);
            }
            if (session.getConfig().getYml().get("Team." + array[0] + ".Upgrade") != null) {
                ws.billy.bedwars.commands.Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + ".Upgrade"), null);
            }
            if (session.getConfig().getYml().get("Team." + array[0] + "." + "kill-drops-loc") != null) {
                ws.billy.bedwars.commands.Misc.removeArmorStand(null, session.getConfig().getArenaLoc("Team." + array[0] + "." + "kill-drops-loc"), null);
            }
            player.sendMessage(session.getPrefix() + "Team removed: " + session.getTeamColor(array[0]) + array[0]);
            ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GREEN + "Team removed: " + session.getTeamColor(array[0]) + array[0], 5, 40, 5);
            Sounds.playSound("shop-bought", player);
            session.getConfig().set("Team." + array[0], null);
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
