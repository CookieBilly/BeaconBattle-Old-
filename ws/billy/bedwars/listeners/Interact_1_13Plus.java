

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import ws.billy.bedwars.support.version.common.VersionCommon;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class Interact_1_13Plus implements Listener
{
    @EventHandler
    public void onInventoryInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.isCancelled()) {
            return;
        }
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (clickedBlock.getWorld().getName().equals(VersionCommon.api.getLobbyWorld()) || VersionCommon.api.getArenaUtil().getArenaByPlayer(playerInteractEvent.getPlayer()) != null) {
            final String string = clickedBlock.getType().toString();
            switch (string) {
                case "CHIPPED_ANVIL":
                case "DAMAGED_ANVIL": {
                    if (VersionCommon.api.getConfigs().getMainConfig().getBoolean("inventories.disable-anvil")) {
                        playerInteractEvent.setCancelled(true);
                        break;
                    }
                    if (VersionCommon.api.getArenaUtil().isSpectating(playerInteractEvent.getPlayer())) {
                        playerInteractEvent.setCancelled(true);
                        break;
                    }
                    break;
                }
            }
        }
    }
}
