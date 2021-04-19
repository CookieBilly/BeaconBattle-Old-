

package ws.billy.bedwars.api.events.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ShopOpenEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private boolean cancelled;
    
    public ShopOpenEvent(final Player player) {
        this.cancelled = false;
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public HandlerList getHandlers() {
        return ShopOpenEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
