

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.bukkit.Location;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.Bukkit;
import ws.billy.bedwars.commands.Misc;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.server.SetupType;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class AddGenerator extends SubCommand
{
    public AddGenerator(final ParentCommand parentCommand, final String s) {
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
        if (array.length == 0 && session.getSetupType() == SetupType.ASSISTED) {
            final String nearestTeam = session.getNearestTeam();
            if (!nearestTeam.isEmpty()) {
                saveTeamGen(player.getLocation(), nearestTeam, session, "Iron");
                saveTeamGen(player.getLocation(), nearestTeam, session, "Gold");
                saveTeamGen(player.getLocation(), nearestTeam, session, "Emerald");
                Misc.createArmorStand(ChatColor.GOLD + "Generator set for team: " + session.getTeamColor(nearestTeam) + nearestTeam, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Generator set for team: " + session.getTeamColor(nearestTeam) + nearestTeam);
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GREEN + "Generator set for team: " + session.getTeamColor(nearestTeam) + nearestTeam, 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            if (player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.DIAMOND_BLOCK) {
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName() + " " + this.getSubCommandName() + " diamond");
                return true;
            }
            if (player.getLocation().add(0.0, -1.0, 0.0).getBlock().getType() == Material.EMERALD_BLOCK) {
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName() + " " + this.getSubCommandName() + " emerald");
                return true;
            }
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find any nearby team.");
            player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Make sure you set the team's spawn first!", ChatColor.WHITE + "Set a team spawn.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Or if you set the spawn and it wasn't found automatically try using: /bw addGenerator <team>", "Add a team generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "Other use: /bw addGenerator <emerald/ diamond>", "Add an emerald/ diamond generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
            ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Could not find any nearby team.", 5, 60, 5);
            Sounds.playSound("shop-insufficient-money", player);
            return true;
        }
        else {
            if (array.length == 1 && (array[0].equalsIgnoreCase("diamond") || array[0].equalsIgnoreCase("emerald"))) {
                final Iterator<Location> iterator = session.getConfig().getArenaLocations("generator." + array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase()).iterator();
                while (iterator.hasNext()) {
                    if (session.getConfig().compareArenaLoc(iterator.next(), player.getLocation())) {
                        player.sendMessage(session.getPrefix() + ChatColor.RED + "This generator was already set!");
                        ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "This generator was already set!", 5, 30, 5);
                        Sounds.playSound("shop-insufficient-money", player);
                        return true;
                    }
                }
                final String string = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
                ArrayList<String> list;
                if (session.getConfig().getYml().get("generator." + string) == null) {
                    list = new ArrayList<String>();
                }
                else {
                    list = (ArrayList<String>)session.getConfig().getYml().getStringList("generator." + string);
                }
                list.add(session.getConfig().stringLocationArenaFormat(player.getLocation()));
                session.getConfig().set("generator." + string, list);
                player.sendMessage(session.getPrefix() + string + " generator was added!");
                Misc.createArmorStand(ChatColor.GOLD + string + " SET", player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                if (session.getSetupType() == SetupType.ASSISTED) {
                    Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
                }
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GOLD + string + ChatColor.GREEN + " generator added!", 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            if (array.length >= 1 && (array[0].equalsIgnoreCase("iron") || array[0].equalsIgnoreCase("gold") || array[0].equalsIgnoreCase("upgrade")) && session.getSetupType() == SetupType.ADVANCED) {
                String nearestTeam2;
                if (array.length == 1) {
                    nearestTeam2 = session.getNearestTeam();
                }
                else {
                    nearestTeam2 = array[1];
                    if (session.getConfig().getYml().get("Team." + nearestTeam2 + ".Color") == null) {
                        player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find team: " + nearestTeam2);
                        player.sendMessage(session.getPrefix() + "Use: /bw createTeam if you want to create one.");
                        session.displayAvailableTeams();
                        ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.RED + "Could not find any nearby team.", 5, 60, 5);
                        Sounds.playSound("shop-insufficient-money", player);
                        return true;
                    }
                }
                if (nearestTeam2.isEmpty()) {
                    player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find any nearby team.");
                    player.sendMessage(session.getPrefix() + "Try using: /bw addGenerator <iron/ gold/ upgrade> <team>");
                    return true;
                }
                String string2 = array[0].substring(0, 1).toUpperCase() + array[0].substring(1).toLowerCase();
                if (string2.equalsIgnoreCase("upgrade")) {
                    string2 = "Emerald";
                }
                Misc.createArmorStand(ChatColor.GOLD + string2 + " generator added for team: " + session.getTeamColor(nearestTeam2) + nearestTeam2, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + string2 + " generator added for team: " + session.getTeamColor(nearestTeam2) + nearestTeam2);
                saveTeamGen(player.getLocation(), nearestTeam2, session, string2);
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GOLD + string2 + ChatColor.GREEN + " generator for " + session.getTeamColor(nearestTeam2) + nearestTeam2 + ChatColor.GREEN + " was added!", 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
            else {
                if (array.length != 1 || session.getSetupType() != SetupType.ASSISTED) {
                    if (session.getSetupType() == SetupType.ASSISTED) {
                        player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator (detect team automatically)", "Add a team generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                        player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <team>", "Add a team generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    }
                    if (session.getSetupType() == SetupType.ADVANCED) {
                        player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <iron/ gold/ upgrade>", "Add a team generator.\nThe team will be detected automatically.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                        player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <iron/ gold/ upgrade> <team>", "Add a team generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    }
                    player.spigot().sendMessage((BaseComponent) ws.billy.bedwars.arena.Misc.msgHoverClick(session.getPrefix() + "/bw addGenerator <emerald/ diamond>", "Add an emerald/ diamond generator.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
                    return true;
                }
                final String s = array[0];
                if (session.getConfig().getYml().get("Team." + s + ".Color") == null) {
                    player.sendMessage(session.getPrefix() + "Could not find team: " + ChatColor.RED + s);
                    player.sendMessage(session.getPrefix() + "Use: /bw createTeam if you want to create one.");
                    session.displayAvailableTeams();
                    ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", "Could not find team: " + ChatColor.RED + s, 5, 40, 5);
                    Sounds.playSound("shop-insufficient-money", player);
                    return true;
                }
                saveTeamGen(player.getLocation(), s, session, "Iron");
                saveTeamGen(player.getLocation(), s, session, "Gold");
                saveTeamGen(player.getLocation(), s, session, "Emerald");
                Misc.createArmorStand(ChatColor.GOLD + "Generator set for team: " + session.getTeamColor(s) + s, player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
                player.sendMessage(session.getPrefix() + "Generator set for team: " + session.getTeamColor(s) + s);
                Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName());
                ws.billy.bedwars.BeaconBattle.nms.sendTitle(player, " ", ChatColor.GREEN + "Generator set for team: " + session.getTeamColor(s) + s, 5, 60, 5);
                Sounds.playSound("shop-bought", player);
                return true;
            }
        }
    }
    
    @Override
    public List<String> getTabComplete() {
        return Arrays.asList("Diamond", "Emerald", "Iron", "Gold", "Upgrade");
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    private static void saveTeamGen(final Location location, final String str, @NotNull final SetupSession setupSession, final String str2) {
        final Object value = setupSession.getConfig().getYml().get("Team." + str + "." + str2);
        List<String> list;
        if (value == null) {
            list = new ArrayList<String>();
        }
        else if (value instanceof String) {
            list = new ArrayList<String>();
            list.add((String)value);
        }
        else {
            list = setupSession.getConfig().getList("Team." + str + "." + str2);
        }
        list.add(setupSession.getConfig().stringLocationArenaFormat(location));
        setupSession.getConfig().set("Team." + str + "." + str2, list);
    }
}
