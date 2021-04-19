

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
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

public class SetSpawn extends SubCommand
{
    public SetSpawn(final ParentCommand parentCommand, final String s) {
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
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Usage: /" + ws.billy.bedwars.BeaconBattle.mainCmd + " setSpawn <team>");
            if (session.getConfig().getYml().get("Team") != null) {
                for (final String s : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                    if (session.getConfig().getYml().get("Team." + s + ".Spawn") == null) {
                        player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick(session.getPrefix() + "Set spawn for: " + session.getTeamColor(s) + s + " " + ChatColor.getLastColors(session.getPrefix()) + "(click to set)", ChatColor.WHITE + "Set spawn for " + session.getTeamColor(s) + s, "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " setSpawn " + s, ClickEvent.Action.RUN_COMMAND));
                    }
                }
            }
        }
        else if (session.getConfig().getYml().get("Team." + array[0]) == null) {
            player.sendMessage(session.getPrefix() + ChatColor.RED + "Could not find target team: " + ChatColor.RED + array[0]);
            if (session.getConfig().getYml().get("Team") != null) {
                player.sendMessage(session.getPrefix() + "Teams list: ");
                for (final String str : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                    player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(ChatColor.GOLD + " " + '\u25aa' + " " + session.getTeamColor(str) + str + " " + ChatColor.getLastColors(session.getPrefix()) + "(click to set)", ChatColor.WHITE + "Set spawn for " + session.getTeamColor(str) + str, "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " setSpawn " + str, ClickEvent.Action.RUN_COMMAND));
                }
            }
        }
        else {
            if (session.getConfig().getYml().get("Team." + array[0] + ".Spawn") != null) {
                ws.billy.bedwars.commands.Misc.removeArmorStand("spawn", session.getConfig().getArenaLoc("Team." + array[0] + ".Spawn"), session.getConfig().getString("Team." + array[0] + ".Spawn"));
            }
            session.getConfig().saveArenaLoc("Team." + array[0] + ".Spawn", player.getLocation());
            final String string = session.getTeamColor(array[0]) + array[0];
            player.sendMessage(ChatColor.GOLD + " " + '\u25aa' + " Spawn set for: " + string);
            ws.billy.bedwars.commands.Misc.createArmorStand(string + " " + ChatColor.GOLD + "SPAWN SET", player.getLocation(), session.getConfig().stringLocationArenaFormat(player.getLocation()));
            final int int1 = session.getConfig().getInt("island-radius");
            final Location location = player.getLocation();
            for (int i = -int1; i < int1; ++i) {
                for (int j = -int1; j < int1; ++j) {
                    for (int k = -int1; k < int1; ++k) {
                        final Block block = location.clone().add((double)i, (double)j, (double)k).getBlock();
                        if (ws.billy.bedwars.BeaconBattle.nms.isBed(block.getType())) {
                            player.teleport(block.getLocation());
                            Bukkit.dispatchCommand((CommandSender)player, this.getParent().getName() + " setBed " + array[0]);
                            return true;
                        }
                    }
                }
            }
            if (session.getConfig().getYml().get("Team") != null) {
                final StringBuilder sb = new StringBuilder();
                for (final String s2 : Objects.requireNonNull(session.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                    if (session.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) {
                        sb.append(session.getTeamColor(s2)).append(s2).append(" ");
                    }
                }
                if (sb.toString().length() > 0) {
                    player.sendMessage(session.getPrefix() + "Remaining: " + sb.toString());
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
