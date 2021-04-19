

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdTpStaff extends SubCommand
{
    public CmdTpStaff(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPermission("bw.tp");
        this.showInList(false);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        final Player player = (Player)commandSender;
        if (array.length != 1) {
            commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_TP_USAGE));
            return true;
        }
        if (!this.hasPermission((CommandSender)player)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NO_PERM));
            return true;
        }
        final Player player2 = Bukkit.getPlayer(array[0]);
        if (player2 == null) {
            commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_TP_PLAYER_NOT_FOUND));
            return true;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player2);
        final IArena arenaByPlayer2 = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            commandSender.sendMessage(Language.getMsg(player, Messages.COMMAND_TP_NOT_IN_ARENA));
            return true;
        }
        if (arenaByPlayer.getStatus() == GameState.playing) {
            if (arenaByPlayer2 != null) {
                if (arenaByPlayer2.isPlayer(player)) {
                    arenaByPlayer2.removePlayer(player, false);
                }
                if (arenaByPlayer2.isSpectator(player)) {
                    if (arenaByPlayer2.getArenaName().equals(arenaByPlayer.getArenaName())) {
                        player.teleport((Entity)player2);
                        return true;
                    }
                    arenaByPlayer2.removeSpectator(player, false);
                }
            }
            arenaByPlayer.addSpectator(player, false, player2.getLocation());
        }
        else {
            commandSender.sendMessage(Language.getMsg((Player)commandSender, Messages.COMMAND_TP_NOT_STARTED));
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>();
        for (final IArena arena : Arena.getArenas()) {
            final Iterator<Player> iterator2 = arena.getPlayers().iterator();
            while (iterator2.hasNext()) {
                list.add(iterator2.next().getName());
            }
            final Iterator<Player> iterator3 = arena.getSpectators().iterator();
            while (iterator3.hasNext()) {
                list.add(iterator3.next().getName());
            }
        }
        return list;
    }
}
