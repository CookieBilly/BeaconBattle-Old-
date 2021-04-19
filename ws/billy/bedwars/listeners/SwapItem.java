

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.support.version.common.VersionCommon;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.Listener;

public class SwapItem implements Listener
{
    @EventHandler
    public void itemSwap(final PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
        if (playerSwapHandItemsEvent.isCancelled()) {
            return;
        }
        if (VersionCommon.api.getArenaUtil().isPlaying(playerSwapHandItemsEvent.getPlayer())) {
            if (VersionCommon.api.getArenaUtil().getArenaByPlayer(playerSwapHandItemsEvent.getPlayer()).getStatus() != GameState.playing) {
                playerSwapHandItemsEvent.setCancelled(true);
            }
        }
        else if (VersionCommon.api.getArenaUtil().isSpectating(playerSwapHandItemsEvent.getPlayer())) {
            playerSwapHandItemsEvent.setCancelled(true);
        }
    }
}
