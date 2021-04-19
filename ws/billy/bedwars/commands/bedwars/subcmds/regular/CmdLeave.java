

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import java.util.UUID;
import java.util.HashMap;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdLeave extends SubCommand
{
    private static HashMap<UUID, Long> delay;
    
    public CmdLeave(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(20);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " leave", "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fLeave an arena."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (cancel(player.getUniqueId())) {
            return true;
        }
        update(player.getUniqueId());
        if (ws.billy.bedwars.BeaconBattle.getServerType() == ServerType.BUNGEE) {
            Misc.forceKick(player);
            return true;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            Misc.forceKick(player);
        }
        else if (arenaByPlayer.isPlayer(player)) {
            arenaByPlayer.removePlayer(player, false);
        }
        else if (arenaByPlayer.isSpectator(player)) {
            arenaByPlayer.removeSpectator(player, false);
        }
        return true;
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
        return (BeaconBattle.getServerType() != ServerType.SHARED || Arena.isInArena(player)) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
    
    private static boolean cancel(final UUID key) {
        return CmdLeave.delay.getOrDefault(key, 0L) > System.currentTimeMillis();
    }
    
    private static void update(final UUID key) {
        if (CmdLeave.delay.containsKey(key)) {
            CmdLeave.delay.replace(key, System.currentTimeMillis() + 2500L);
            return;
        }
        CmdLeave.delay.put(key, System.currentTimeMillis() + 2500L);
    }
    
    static {
        CmdLeave.delay = new HashMap<UUID, Long>();
    }
}
