

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.command.ConsoleCommandSender;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.events.player.PlayerXpGainEvent;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.configuration.LevelsConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class Level extends SubCommand
{
    public Level(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPermission(Permissions.PERMISSION_LEVEL);
        this.setPriority(10);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §8      - §eclick for details", "§fManage a player level.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (array.length == 0) {
            this.sendSubCommands(commandSender, ws.billy.bedwars.BeaconBattle.getAPI());
            return true;
        }
        if (array[0].equalsIgnoreCase("setlevel")) {
            if (array.length != 3) {
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "Usage: /bw level setLevel §o<player> <level>");
                return true;
            }
            final Player player = Bukkit.getPlayer(array[1]);
            if (player == null) {
                commandSender.sendMessage(ChatColor.RED + " \u25aa " + ChatColor.GRAY + "Player not found!");
                return true;
            }
            int int1;
            try {
                int1 = Integer.parseInt(array[2]);
            }
            catch (Exception ex) {
                commandSender.sendMessage(ChatColor.RED + "Level must be an integer!");
                return true;
            }
            ws.billy.bedwars.BeaconBattle.getAPI().getLevelsUtil().setLevel(player, int1);
            final int n = (LevelsConfig.levels.getYml().get("levels." + int1 + ".rankup-cost") == null) ? LevelsConfig.levels.getInt("levels.others.rankup-cost") : LevelsConfig.levels.getInt("levels." + int1 + ".rankup-cost");
            if (LevelsConfig.levels.getYml().get("levels." + int1 + ".name") == null) {
                LevelsConfig.levels.getYml().getString("levels.others.name");
            }
            else {
                LevelsConfig.levels.getYml().getString("levels." + int1 + ".name");
            }
            final Player player2;
            final String s;
            final int n2;
            ws.billy.bedwars.BeaconBattle.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) ws.billy.bedwars.BeaconBattle.plugin, () -> {
                ws.billy.bedwars.BeaconBattle.getRemoteDatabase().setLevelData(player2.getUniqueId(), int1, 0, s, n2);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + player2.getName() + " level was set to: " + array[2]);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "The player may need to rejoin to see it updated.");
                return;
            });
        }
        else if (array[0].equalsIgnoreCase("givexp")) {
            if (array.length != 3) {
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "Usage: /bw level giveXp §o<player> <amount>");
                return true;
            }
            final Player player3 = Bukkit.getPlayer(array[1]);
            if (player3 == null) {
                commandSender.sendMessage(ChatColor.RED + " \u25aa " + ChatColor.GRAY + "Player not found!");
                return true;
            }
            int int2;
            try {
                int2 = Integer.parseInt(array[2]);
            }
            catch (Exception ex2) {
                commandSender.sendMessage(ChatColor.RED + "Amount must be an integer!");
                return true;
            }
            ws.billy.bedwars.BeaconBattle.getAPI().getLevelsUtil().addXp(player3, int2, PlayerXpGainEvent.XpSource.OTHER);
            final Player player4;
            final Object[] array2;
            final int n3;
            ws.billy.bedwars.BeaconBattle.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) ws.billy.bedwars.BeaconBattle.plugin, () -> {
                ws.billy.bedwars.BeaconBattle.getRemoteDatabase().getLevelData(player4.getUniqueId());
                ws.billy.bedwars.BeaconBattle.getRemoteDatabase().setLevelData(player4.getUniqueId(), (int)array2[0], (int)array2[1] + n3, (String)array2[2], (int)array2[3]);
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + array[2] + " xp was given to: " + player4.getName());
                commandSender.sendMessage(ChatColor.GOLD + " \u25aa " + ChatColor.GRAY + "The player may need to rejoin to see it updated.");
                return;
            });
        }
        else {
            this.sendSubCommands(commandSender, ws.billy.bedwars.BeaconBattle.getAPI());
        }
        return true;
    }
    
    private void sendSubCommands(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        if (commandSender instanceof Player) {
            final Player player = (Player)commandSender;
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " setLevel §o<player> <level>", "Set a player level.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " setLevel", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " giveXp §o<player> <amount>", "Give Xp to a player.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " giveXp", ClickEvent.Action.SUGGEST_COMMAND));
        }
        else {
            commandSender.sendMessage(ChatColor.GOLD + "bw level setLevel <player> <level>");
            commandSender.sendMessage(ChatColor.GOLD + "bw level giveXp <player> <amount>");
        }
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
