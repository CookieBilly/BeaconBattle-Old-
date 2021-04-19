

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import java.io.File;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import org.bukkit.entity.Player;
import java.util.HashMap;
import ws.billy.bedwars.api.command.SubCommand;

public class DelArena extends SubCommand
{
    private static HashMap<Player, Long> delArenaConfirm;
    
    public DelArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(4);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_DEL_ARENA);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + " §6<worldName>", "§fDelete a map and its configuration.", "/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), ClickEvent.Action.SUGGEST_COMMAND));
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
        if (array.length != 1) {
            player.sendMessage("§c\u25aa §7Usage: §o/" + MainCommand.getInstance().getName() + " delArena <mapName>");
            return true;
        }
        if (!ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist as a world folder!");
            return true;
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7Please disable it first!");
            return true;
        }
        final File file = new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas/" + array[0] + ".yml");
        if (!file.exists()) {
            player.sendMessage("§c\u25aa §7This arena doesn't exist!");
            return true;
        }
        if (DelArena.delArenaConfirm.containsKey(player)) {
            if (System.currentTimeMillis() - 2000L <= DelArena.delArenaConfirm.get(player)) {
                ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().deleteWorld(array[0]);
                FileUtils.deleteQuietly(file);
                player.sendMessage("§c\u25aa §7" + array[0] + " was deleted!");
                return true;
            }
            player.sendMessage("§6 \u25aa §7Type again to confirm.");
            DelArena.delArenaConfirm.replace(player, System.currentTimeMillis());
        }
        else {
            player.sendMessage("§6 \u25aa §7Type again to confirm.");
            DelArena.delArenaConfirm.put(player, System.currentTimeMillis());
        }
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
    
    static {
        DelArena.delArenaConfirm = new HashMap<Player, Long>();
    }
}
