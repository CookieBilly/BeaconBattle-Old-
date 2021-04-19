

package ws.billy.bedwars.shop.listeners;

import org.bukkit.event.player.PlayerQuitEvent;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.shop.ShopCache;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import org.bukkit.event.Listener;

public class ShopCacheListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerJoinArenaEvent playerJoinArenaEvent) {
        if (playerJoinArenaEvent.isSpectator()) {
            return;
        }
        final ShopCache shopCache = ShopCache.getShopCache(playerJoinArenaEvent.getPlayer().getUniqueId());
        if (shopCache != null) {
            shopCache.destroy();
        }
        new ShopCache(playerJoinArenaEvent.getPlayer().getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaLeave(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        final ShopCache shopCache = ShopCache.getShopCache(playerLeaveArenaEvent.getPlayer().getUniqueId());
        if (shopCache != null) {
            shopCache.destroy();
        }
    }
    
    @EventHandler
    public void onServerLeave(final PlayerQuitEvent playerQuitEvent) {
        final ShopCache shopCache = ShopCache.getShopCache(playerQuitEvent.getPlayer().getUniqueId());
        if (shopCache != null) {
            shopCache.destroy();
        }
    }
}
