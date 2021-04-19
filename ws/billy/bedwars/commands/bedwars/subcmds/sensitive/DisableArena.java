

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
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

public class DisableArena extends SubCommand
{
    public DisableArena(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(6);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + " §6<worldName>", "§fDisable an arena.\nThis will remove the players \n§ffrom the arena before disabling.", "/" + this.getParent().getName() + " " + this.getSubCommandName() + " ", ClickEvent.Action.SUGGEST_COMMAND));
        this.setPermission(Permissions.PERMISSION_ARENA_DISABLE);
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
            player.sendMessage("§c\u25aa §7Usage: §o/" + this.getParent().getName() + " " + this.getSubCommandName() + " <mapName>");
            return true;
        }
        if (!ws.billy.bedwars.BeaconBattle.getAPI().getRestoreAdapter().isWorld(array[0])) {
            player.sendMessage("§c\u25aa §7" + array[0] + " doesn't exist!");
            return true;
        }
        final IArena arenaByName = Arena.getArenaByName(array[0]);
        if (arenaByName == null) {
            player.sendMessage("§c\u25aa §7This arena is disabled yet!");
            return true;
        }
        player.sendMessage("§6 \u25aa §7Disabling arena...");
        arenaByName.disable();
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<IArena> iterator = Arena.getArenas().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getArenaName());
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
