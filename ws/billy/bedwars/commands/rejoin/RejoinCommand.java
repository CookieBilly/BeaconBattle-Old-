

package ws.billy.bedwars.commands.rejoin;

import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.arena.ReJoin;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.configuration.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class RejoinCommand extends BukkitCommand
{
    public RejoinCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("This command is for players!");
            return true;
        }
        final Player player = (Player)commandSender;
        if (!player.hasPermission(Permissions.PERMISSION_REJOIN)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return true;
        }
        final ReJoin player2 = ReJoin.getPlayer(player);
        if (player2 == null) {
            player.sendMessage(Language.getMsg(player, Messages.REJOIN_NO_ARENA));
            Sounds.playSound("rejoin-denied", player);
            return true;
        }
        if (!player2.canReJoin()) {
            player.sendMessage(Language.getMsg(player, Messages.REJOIN_DENIED));
            Sounds.playSound("rejoin-denied", player);
            return true;
        }
        player.sendMessage(Language.getMsg(player, Messages.REJOIN_ALLOWED).replace("{arena}", player2.getArena().getDisplayName()));
        Sounds.playSound("rejoin-allowed", player);
        player2.reJoin(player);
        return true;
    }
}
