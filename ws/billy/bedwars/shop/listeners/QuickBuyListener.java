

package ws.billy.bedwars.shop.listeners;

import org.bukkit.event.player.PlayerQuitEvent;
import ws.billy.bedwars.api.events.player.PlayerReJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import org.bukkit.event.Listener;

public class QuickBuyListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerJoinArenaEvent playerJoinArenaEvent) {
        if (playerJoinArenaEvent == null) {
            return;
        }
        if (playerJoinArenaEvent.isSpectator()) {
            return;
        }
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(playerJoinArenaEvent.getPlayer().getUniqueId());
        if (quickBuyCache != null) {
            quickBuyCache.destroy();
        }
        new PlayerQuickBuyCache(playerJoinArenaEvent.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaJoin(final PlayerReJoinEvent playerReJoinEvent) {
        if (playerReJoinEvent == null) {
            return;
        }
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(playerReJoinEvent.getPlayer().getUniqueId());
        if (quickBuyCache != null) {
            quickBuyCache.destroy();
        }
        new PlayerQuickBuyCache(playerReJoinEvent.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(final PlayerQuitEvent playerQuitEvent) {
        if (playerQuitEvent == null) {
            return;
        }
        final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(playerQuitEvent.getPlayer().getUniqueId());
        if (quickBuyCache == null) {
            return;
        }
        quickBuyCache.destroy();
    }
}
