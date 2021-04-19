

package ws.billy.bedwars.shop.listeners;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.Location;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.shop.quickbuy.PlayerQuickBuyCache;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.Listener;

public class ShopOpenListener implements Listener
{
    @EventHandler
    public void onShopOpen(final PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerInteractAtEntityEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        final Location location = playerInteractAtEntityEvent.getRightClicked().getLocation();
        final Iterator<ITeam> iterator = arenaByPlayer.getTeams().iterator();
        while (iterator.hasNext()) {
            final Location shop = iterator.next().getShop();
            if (location.getBlockX() == shop.getBlockX() && location.getBlockY() == shop.getBlockY() && location.getBlockZ() == shop.getBlockZ()) {
                playerInteractAtEntityEvent.setCancelled(true);
                if (!arenaByPlayer.isPlayer(playerInteractAtEntityEvent.getPlayer())) {
                    continue;
                }
                ShopManager.shop.open(playerInteractAtEntityEvent.getPlayer(), PlayerQuickBuyCache.getQuickBuyCache(playerInteractAtEntityEvent.getPlayer().getUniqueId()), true);
            }
        }
    }
}
