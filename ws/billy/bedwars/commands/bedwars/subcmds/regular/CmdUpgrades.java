

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import java.util.ArrayList;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdUpgrades extends SubCommand
{
    public CmdUpgrades(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.showInList(false);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer((Player)commandSender);
        if (arenaByPlayer == null) {
            return false;
        }
        if (!arenaByPlayer.isPlayer((Player)commandSender)) {
            return false;
        }
        if (arenaByPlayer.getTeam((Player)commandSender).getTeamUpgrades().distance(((Player)commandSender).getLocation()) < 4.0) {
            UpgradesManager.getMenuForArena(arenaByPlayer).open((Player)commandSender);
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> getTabComplete() {
        return new ArrayList<String>();
    }
}
