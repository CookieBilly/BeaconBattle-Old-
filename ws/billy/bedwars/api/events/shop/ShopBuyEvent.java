

package ws.billy.bedwars.api.events.shop;

import ws.billy.bedwars.api.arena.shop.ICategoryContent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ShopBuyEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player buyer;
    private ICategoryContent categoryContent;
    
    public ShopBuyEvent(final Player buyer, final ICategoryContent categoryContent) {
        this.categoryContent = categoryContent;
        this.buyer = buyer;
    }
    
    public Player getBuyer() {
        return this.buyer;
    }
    
    public ICategoryContent getCategoryContent() {
        return this.categoryContent;
    }
    
    public HandlerList getHandlers() {
        return ShopBuyEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return ShopBuyEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
