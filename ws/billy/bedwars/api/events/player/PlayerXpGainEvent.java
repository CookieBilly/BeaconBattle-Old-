

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerXpGainEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private int amount;
    private XpSource xpSource;
    
    public PlayerXpGainEvent(final Player player, final int amount, final XpSource xpSource) {
        this.player = player;
        this.amount = amount;
        this.xpSource = xpSource;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public XpSource getXpSource() {
        return this.xpSource;
    }
    
    public HandlerList getHandlers() {
        return PlayerXpGainEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerXpGainEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
    
    public enum XpSource
    {
        PER_MINUTE, 
        PER_TEAMMATE, 
        GAME_WIN, 
        OTHER;
    }
}
