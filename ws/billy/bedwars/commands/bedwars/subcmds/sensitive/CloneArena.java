

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import java.io.File;

import ws.billy.bedwars.commands.bedwars.MainCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CloneArena extends SubCommand
{
    public CloneArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(7);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_CLONE);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<worldName> <newName>", "§fClone an existing arena.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.SUGGEST_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (!MainCommand.isLobbySet(player)) {
            return true;
        }
        if (array.length != 2) {
            player.sendMessage("§c\u25aa §7Usage: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mapName> <newArena>");
            return true;
        }
        if (!ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist!");
            return true;
        }
        final File file = new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas/" + array[0] + ".yml");
        final File file2 = new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas/" + array[1] + ".yml");
        if (!file.exists()) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist!");
            return true;
        }
        if (ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().isWorld(array[1]) && file2.exists()) {
            player.sendMessage("§c\u25aa §7" + array[1] + " already exist!");
            return true;
        }
        if (array[1].contains("+")) {
            player.sendMessage("§c\u25aa §7" + array[1] + " mustn't contain this symbol: " + ChatColor.RED + "+");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Please disable " + array[0] + " first!");
            return true;
        }
        ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().cloneArena(array[0], array[1]);
        if (file.exists()) {
            try {
                FileUtils.copyFile(file, file2, true);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                player.sendMessage("§c\u25aa §7An error occurred while copying the map's config. Check the console.");
            }
        }
        player.sendMessage("§6 \u25aa §7Done :D.");
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>();
        final File file = new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas");
        if (file.exists()) {
            for (final File file2 : Objects.requireNonNull(file.listFiles())) {
                if (file2.isFile() && file2.getName().contains(".yml")) {
                    list.add(file2.getName().replace(".yml", ""));
                }
            }
        }
        return list;
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
