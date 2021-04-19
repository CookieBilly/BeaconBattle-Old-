

package ws.billy.bedwars.commands.shout;

import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.command.defaults.BukkitCommand;

public class ShoutCommand extends BukkitCommand
{
    private static HashMap<UUID, Long> shoutCooldown;
    
    public ShoutCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) {
            return true;
        }
        final Player player = (Player)commandSender;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null || arenaByPlayer.isSpectator(player)) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            return true;
        }
        final StringBuilder sb = new StringBuilder();
        for (int length = array.length, i = 0; i < length; ++i) {
            sb.append(array[i]).append(" ");
        }
        player.chat("!" + sb.toString());
        return false;
    }
    
    public static void updateShout(final Player player) {
        if (player.hasPermission("bw.shout.bypass")) {
            return;
        }
        if (ShoutCommand.shoutCooldown.containsKey(player.getUniqueId())) {
            ShoutCommand.shoutCooldown.replace(player.getUniqueId(), System.currentTimeMillis() + BeaconBattle.config.getInt("shout-cmd-cooldown") * 1000);
        }
        else {
            ShoutCommand.shoutCooldown.put(player.getUniqueId(), System.currentTimeMillis() + BeaconBattle.config.getInt("shout-cmd-cooldown") * 1000);
        }
    }
    
    public static boolean isShoutCooldown(final Player player) {
        return !player.hasPermission("bw.shout.bypass") && ShoutCommand.shoutCooldown.containsKey(player.getUniqueId()) && ShoutCommand.shoutCooldown.get(player.getUniqueId()) > System.currentTimeMillis();
    }
    
    public static double getShoutCooldown(final Player player) {
        return (ShoutCommand.shoutCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000.0f;
    }
    
    public static boolean isShout(final Player player) {
        return ShoutCommand.shoutCooldown.containsKey(player.getUniqueId()) && ShoutCommand.shoutCooldown.get(player.getUniqueId()) + 1000L > System.currentTimeMillis();
    }
    
    static {
        ShoutCommand.shoutCooldown = new HashMap<UUID, Long>();
    }
}
