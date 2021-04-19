

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import org.bukkit.block.Block;
import org.bukkit.World;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.Location;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.team.TeamColor;
import ws.billy.bedwars.api.server.SetupType;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import java.util.List;
import org.bukkit.entity.Player;
import java.util.HashMap;
import ws.billy.bedwars.api.command.SubCommand;

public class AutoCreateTeams extends SubCommand
{
    private static HashMap<Player, Long> timeOut;
    private static HashMap<Player, List<Byte>> teamsFoundOld;
    private static HashMap<Player, List<String>> teamsFound13;
    
    public AutoCreateTeams(final ParentCommand parentCommand, final String s) {
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
        if (session.getSetupType() == SetupType.ASSISTED) {
            if (is13Higher()) {
                if (AutoCreateTeams.timeOut.containsKey(player) && AutoCreateTeams.timeOut.get(player) >= System.currentTimeMillis() && AutoCreateTeams.teamsFound13.containsKey(player)) {
                    for (final String s : AutoCreateTeams.teamsFound13.get(player)) {
                        Bukkit.dispatchCommand(commandSender, ws.billy.bedwars.BeaconBattle.mainCmd + " createTeam " + TeamColor.enName(s) + " " + TeamColor.enName(s));
                    }
                    if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§6§lWAITING LOBBY REMOVAL:");
                        commandSender.sendMessage("§fIf you'd like the lobby to disappear when the game starts,");
                        commandSender.sendMessage("§fplease use the following commands like a world edit selection.");
                        player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 1", "§dSet pos 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
                        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 2", "§dSet pos 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§7This step is OPTIONAL. If you wan to skip it do §6/" + ws.billy.bedwars.BeaconBattle.mainCmd);
                    }
                    return true;
                }
                final ArrayList list = new ArrayList<Object>();
                final World world = player.getWorld();
                if (session.getConfig().getYml().get("Team") == null) {
                    player.sendMessage("§6 \u25aa §7Searching for teams. This may cause lag.");
                    for (int i = -200; i < 200; ++i) {
                        for (int j = 50; j < 130; ++j) {
                            for (int k = -200; k < 200; ++k) {
                                final Block block = new Location(world, (double)i, (double)j, (double)k).getBlock();
                                if (block.getType().toString().contains("_WOOL") && !list.contains(block.getType().toString())) {
                                    int n = 0;
                                    for (int l = -2; l < 2; ++l) {
                                        for (int n2 = -2; n2 < 2; ++n2) {
                                            for (int n3 = -2; n3 < 2; ++n3) {
                                                if (new Location(world, (double)i, (double)j, (double)k).getBlock().getType() == block.getType()) {
                                                    ++n;
                                                }
                                            }
                                        }
                                    }
                                    if (n >= 5 && !TeamColor.enName(block.getType().toString()).isEmpty() && session.getConfig().getYml().get("Team." + TeamColor.enName(block.getType().toString())) == null) {
                                        list.add(block.getType().toString());
                                    }
                                }
                            }
                        }
                    }
                }
                if (list.isEmpty()) {
                    player.sendMessage("§6 \u25aa §7No new teams were found.\n§6 \u25aa §7Manually create teams with: §6/" + ws.billy.bedwars.BeaconBattle.mainCmd + " createTeam");
                }
                else {
                    if (AutoCreateTeams.timeOut.containsKey(player)) {
                        player.sendMessage("§c \u25aa §7Time out. Type again to search for teams.");
                        AutoCreateTeams.timeOut.remove(player);
                        return true;
                    }
                    AutoCreateTeams.timeOut.put(player, System.currentTimeMillis() + 16000L);
                    if (AutoCreateTeams.teamsFound13.containsKey(player)) {
                        AutoCreateTeams.teamsFound13.replace(player, list);
                    }
                    else {
                        AutoCreateTeams.teamsFound13.put(player, list);
                    }
                    player.sendMessage("§6§lNEW TEAMS FOUND:");
                    final Iterator<String> iterator2 = (Iterator<String>)list.iterator();
                    while (iterator2.hasNext()) {
                        final String enName = TeamColor.enName(iterator2.next());
                        player.sendMessage("§f \u25aa " + TeamColor.getChatColor(enName) + enName);
                    }
                    player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7§lClick here to create found teams.", "§fClick to create found teams!", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
                }
            }
            else {
                if (AutoCreateTeams.timeOut.containsKey(player) && AutoCreateTeams.timeOut.get(player) >= System.currentTimeMillis() && AutoCreateTeams.teamsFoundOld.containsKey(player)) {
                    for (final Byte b : AutoCreateTeams.teamsFoundOld.get(player)) {
                        Bukkit.dispatchCommand(commandSender, ws.billy.bedwars.BeaconBattle.mainCmd + " createTeam " + TeamColor.enName(b) + " " + TeamColor.enName(b));
                    }
                    if (session.getConfig().getYml().get("waiting.Pos1") == null) {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§6§lWAITING LOBBY REMOVAL:");
                        commandSender.sendMessage("§fIf you'd like the lobby to disappear when the game starts,");
                        commandSender.sendMessage("§fplease use the following commands like a world edit selection.");
                        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 1", "§dSet pos 1", "/" + this.getParent().getName() + " waitingPos 1", ClickEvent.Action.RUN_COMMAND));
                        player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§c \u25aa §7/" + ws.billy.bedwars.BeaconBattle.mainCmd + " waitingPos 2", "§dSet pos 2", "/" + this.getParent().getName() + " waitingPos 2", ClickEvent.Action.RUN_COMMAND));
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§7This step is OPTIONAL. If you wan to skip it do §6/" + ws.billy.bedwars.BeaconBattle.mainCmd);
                    }
                    return true;
                }
                final ArrayList list2 = new ArrayList<Object>();
                final World world2 = player.getWorld();
                if (session.getConfig().getYml().get("Team") == null) {
                    player.sendMessage("§6 \u25aa §7Searching for teams. This may cause lag.");
                    for (int n4 = -200; n4 < 200; ++n4) {
                        for (int n5 = 50; n5 < 130; ++n5) {
                            for (int n6 = -200; n6 < 200; ++n6) {
                                final Block block2 = new Location(world2, (double)n4, (double)n5, (double)n6).getBlock();
                                if (block2.getType() == Material.WOOL && !list2.contains(block2.getData())) {
                                    int n7 = 0;
                                    for (int n8 = -2; n8 < 2; ++n8) {
                                        for (int n9 = -2; n9 < 2; ++n9) {
                                            for (int n10 = -2; n10 < 2; ++n10) {
                                                final Block block3 = new Location(world2, (double)n4, (double)n5, (double)n6).getBlock();
                                                if (block3.getType() == block2.getType() && block2.getData() == block3.getData()) {
                                                    ++n7;
                                                }
                                            }
                                        }
                                    }
                                    if (n7 >= 5 && !TeamColor.enName(block2.getData()).isEmpty() && session.getConfig().getYml().get("Team." + TeamColor.enName(block2.getData())) == null) {
                                        list2.add(block2.getData());
                                    }
                                }
                            }
                        }
                    }
                }
                if (list2.isEmpty()) {
                    player.sendMessage("§6 \u25aa §7No new teams were found.\n§6 \u25aa §7Manually create teams with: §6/" + ws.billy.bedwars.BeaconBattle.mainCmd + " createTeam");
                }
                else {
                    if (AutoCreateTeams.timeOut.containsKey(player)) {
                        player.sendMessage("§c \u25aa §7Time out. Type again to search for teams.");
                        AutoCreateTeams.timeOut.remove(player);
                        return true;
                    }
                    AutoCreateTeams.timeOut.put(player, System.currentTimeMillis() + 16000L);
                    if (AutoCreateTeams.teamsFoundOld.containsKey(player)) {
                        AutoCreateTeams.teamsFoundOld.replace(player, list2);
                    }
                    else {
                        AutoCreateTeams.teamsFoundOld.put(player, list2);
                    }
                    player.sendMessage("§6§lNEW TEAMS FOUND:");
                    final Iterator<Byte> iterator4 = (Iterator<Byte>)list2.iterator();
                    while (iterator4.hasNext()) {
                        final String enName2 = TeamColor.enName(iterator4.next());
                        player.sendMessage("§f \u25aa " + TeamColor.getChatColor(enName2) + enName2);
                    }
                    player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick("§6 \u25aa §7§lClick here to create found teams.", "§fClick to create found teams!", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    public static boolean is13Higher() {
        final String serverVersion = ws.billy.bedwars.BeaconBattle.getServerVersion();
        switch (serverVersion) {
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1": {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
    
    static {
        AutoCreateTeams.timeOut = new HashMap<Player, Long>();
        AutoCreateTeams.teamsFoundOld = new HashMap<Player, List<Byte>>();
        AutoCreateTeams.teamsFound13 = new HashMap<Player, List<String>>();
    }
}
