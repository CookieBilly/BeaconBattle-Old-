

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.ArrayList;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdStats extends SubCommand
{
    private static ConcurrentHashMap<UUID, Long> statsCoolDown;
    
    public CmdStats(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(16);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fOpens the stats GUI."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null && arenaByPlayer.getStatus() != GameState.starting && arenaByPlayer.getStatus() != GameState.waiting && !arenaByPlayer.isSpectator(player)) {
            return false;
        }
        if (CmdStats.statsCoolDown.containsKey(player.getUniqueId())) {
            if (System.currentTimeMillis() - 3000L < CmdStats.statsCoolDown.get(player.getUniqueId())) {
                return true;
            }
            CmdStats.statsCoolDown.replace(player.getUniqueId(), System.currentTimeMillis());
        }
        else {
            CmdStats.statsCoolDown.put(player.getUniqueId(), System.currentTimeMillis());
        }
        Misc.openStatsGUI(player);
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<String>();
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
    
    public static ConcurrentHashMap<UUID, Long> getStatsCoolDown() {
        return CmdStats.statsCoolDown;
    }
    
    static {
        CmdStats.statsCoolDown = new ConcurrentHashMap<UUID, Long>();
    }
}
