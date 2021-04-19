

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.Objects;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class EnableArena extends SubCommand
{
    public EnableArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<worldName>", "§fEnable an arena.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
        this.showInList(true);
        this.setPriority(5);
        this.setPermission(Permissions.PERMISSION_ARENA_ENABLE);
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
            player.sendMessage("§c\u25aa §7Usage: §o/" + this.getParent().getName() + " enableRotation <mapName>");
            return true;
        }
        if (!ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist!");
            return true;
        }
        final Iterator<IArena> iterator = Arena.getEnableQueue().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getArenaName().equalsIgnoreCase(array[0])) {
                player.sendMessage("§c\u25aa §7This arena is already in the enable queue!");
                return true;
            }
        }
        if (Arena.getArenaByName(array[0]) != null) {
            player.sendMessage("§c\u25aa §7This arena is already enabled!");
            return true;
        }
        player.sendMessage("§6 \u25aa §7Enabling arena...");
        new Arena(array[0], player);
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
