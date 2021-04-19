

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.Listener;

public class CmdProcess implements Listener
{
    @EventHandler
    public void onCmd(final PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        final Player player = playerCommandPreprocessEvent.getPlayer();
        if (playerCommandPreprocessEvent.getMessage().equals("/party sethome")) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_ALLOWED_IN_GAME));
            playerCommandPreprocessEvent.setCancelled(true);
        }
        if (playerCommandPreprocessEvent.getMessage().equals("/party home")) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_ALLOWED_IN_GAME));
            playerCommandPreprocessEvent.setCancelled(true);
        }
        if (player.hasPermission(Permissions.PERMISSION_COMMAND_BYPASS)) {
            return;
        }
        final String[] split = playerCommandPreprocessEvent.getMessage().replaceFirst("/", "").split(" ");
        if (split.length == 0) {
            return;
        }
        if (Arena.isInArena(player) && !BeaconBattle.config.getList("allowed-commands").contains(split[0])) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_NOT_ALLOWED_IN_GAME));
            playerCommandPreprocessEvent.setCancelled(true);
        }
    }
}
