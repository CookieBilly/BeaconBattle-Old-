

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.ArrayList;
import java.util.List;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.configuration.ArenaConfig;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.server.SetupType;
import ws.billy.bedwars.commands.Misc;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.ChatColor;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class SetKillDropsLoc extends SubCommand
{
    public SetKillDropsLoc(final ParentCommand parentCommand, final String s) {
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
        final ArenaConfig config = session.getConfig();
        if (array.length < 1) {
            String s = "";
            double n = 100.0;
            if (session.getConfig().getYml().getConfigurationSection("Team") == null) {
                player.sendMessage(session.getPrefix() + "Please create teams first!");
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Please create teams first!", 5, 40, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            for (final String s2 : session.getConfig().getYml().getConfigurationSection("Team").getKeys(false)) {
                if (session.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) {
                    continue;
                }
                final double distance = session.getConfig().getArenaLoc("Team." + s2 + ".Spawn").distance(player.getLocation());
                if (distance > session.getConfig().getInt("island-radius") || distance >= n) {
                    continue;
                }
                n = distance;
                s = s2;
            }
            if (!s.isEmpty()) {
                if (session.getConfig().getYml().get("Team." + s + "." + "kill-drops-loc") != null) {
                    Misc.removeArmorStand("Kill drops", session.getConfig().getArenaLoc("Team." + s + "." + "kill-drops-loc"), null);
                }
                config.set("Team." + s + "." + "kill-drops-loc", config.stringLocationArenaFormat(player.getLocation()));
                final String string = session.getTeamColor(s) + s;
                player.sendMessage(session.getPrefix() + "Kill drops set for team: " + string);
                Misc.createArmorStand(ChatColor.GOLD + "Kill drops " + string, player.getLocation(), null);
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GREEN + "Kill drops set for team: " + string, 5, 40, 5);
                Sounds.playSound("shop-bought", player);
                if (session.getSetupType() == SetupType.ASSISTED) {
                    Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
                }
                return true;
            }
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " setKillDrops <teamName>");
            return true;
        }
        else {
            String nearestTeam = session.getNearestTeam();
            if (nearestTeam.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find any nearby team.");
                player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Make sure you set the team's spawn first!", ChatColor.WHITE + "Set a team spawn.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Or if you set the spawn and it wasn't found automatically try using: /bw " + this.getSubCommandName() + " <team>", "Set kill drops location for a team.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Could not find any nearby team.", 5, 60, 5);
                Sounds.playSound("shop-insufficient-money", player);
                return true;
            }
            if (array.length == 1) {
                if (config.getYml().get("Team." + array[0]) == null) {
                    player.sendMessage(session.getPrefix() + ChatColor.RED + "This team doesn't exist!");
                    if (config.getYml().get("Team") != null) {
                        player.sendMessage(session.getPrefix() + "Available teams: ");
                        for (final String str : Objects.requireNonNull(config.getYml().getConfigurationSection("Team")).getKeys(false)) {
                            player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " Kill drops " + session.getTeamColor(str) + str + " " + ChatColor.getLastColors(session.getPrefix()) + "(click to set)", ChatColor.WHITE + "Set Kill drops for " + session.getTeamColor(str) + str, "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " setKillDrops " + str, ClickEvent.Action.RUN_COMMAND));
                        }
                    }
                    return true;
                }
                nearestTeam = array[0];
            }
            config.set("Team." + nearestTeam + "." + "kill-drops-loc", config.stringLocationArenaFormat(player.getLocation()));
            player.sendMessage(session.getPrefix() + "Kill drops set for: " + session.getTeamColor(nearestTeam) + nearestTeam);
            if (session.getSetupType() == SetupType.ASSISTED) {
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
            }
            return true;
        }
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
