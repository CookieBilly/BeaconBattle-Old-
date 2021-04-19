

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.ArrayList;
import java.util.List;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.api.server.SetupType;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Bukkit;
import ws.billy.bedwars.configuration.Sounds;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetBed extends SubCommand
{
    public SetBed(final ParentCommand parentCommand, final String s) {
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
        if (array.length == 0) {
            final String nearestTeam = session.getNearestTeam();
            if (nearestTeam.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find any nearby team.");
                player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick(session.getPrefix() + "Make sure you set the team's spawn first!", ChatColor.WHITE + "Set a team bed.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.getPrefix() + "Or if you set the spawn and it wasn't found automatically try using: /bw " + this.getSubCommandName() + " <team>", "Add a team bed.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Could not find any nearby team.", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
                session.displayAvailableTeams();
            }
            else {
                Bukkit.dispatchCommand(commandSender, this.getParent().getName() + " " + this.getSubCommandName() + " " + nearestTeam);
            }
        }
        else {
            if (!ws.billy.bedwars.BeaconBattle.nms.isBed(player.getLocation().clone().add(0.0, -0.5, 0.0).getBlock().getType()) && !ws.billy.bedwars.BeaconBattle.nms.isBed(player.getLocation().clone().add(0.0, 0.5, 0.0).getBlock().getType()) && !ws.billy.bedwars.BeaconBattle.nms.isBed(player.getLocation().clone().getBlock().getType())) {
                player.sendMessage(session.getPrefix() + ChatColor.RED + "You must stay on a bed while using this command!");
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "You must stay on a bed.", 5, 40, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            if (session.getConfig().getYml().get("Team." + array[0]) == null) {
                player.sendMessage(session.getPrefix() + ChatColor.RED + "This team doesn't exist!");
                if (session.getConfig().getYml().get("Team") != null) {
                    player.sendMessage(session.getPrefix() + "Available teams: ");
                    for (final String str : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(str) + str, ChatColor.WHITE + "Set bed for " + session.getTeamColor(str) + str, "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " setBed " + str, ClickEvent.Action.RUN_COMMAND));
                    }
                }
            }
            else {
                final String string = session.getTeamColor(array[0]) + array[0];
                if (session.getConfig().getYml().get("Team." + array[0] + ".Bed") != null) {
                    ws.billy.bedwars.commands.Misc.removeArmorStand("bed", session.getConfig().getArenaLoc("Team." + array[0] + ".Bed"), null);
                }
                ws.billy.bedwars.commands.Misc.createArmorStand(string + " " + ChatColor.GOLD + "BED SET", player.getLocation().add(0.5, 0.0, 0.5), null);
                session.getConfig().saveArenaLoc("Team." + array[0] + ".Bed", player.getLocation());
                player.sendMessage(session.getPrefix() + "Bed set for: " + string);
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GREEN + "Bed set for: " + string, 5, 40, 5);
                Sounds.playSound("shop-bought", player);
                if (session.getSetupType() == SetupType.ASSISTED) {
                    Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
                }
            }
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<String>();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}
