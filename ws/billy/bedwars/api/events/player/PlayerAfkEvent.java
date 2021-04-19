

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerAfkEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private AFKType afkType;
    
    public PlayerAfkEvent(final Player player, final AFKType afkType) {
        this.afkType = afkType;
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public AFKType getAfkType() {
        return this.afkType;
    }
    
    public HandlerList getHandlers() {
        return PlayerAfkEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerAfkEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
    
    public enum AFKType
    {
        START, 
        END;
    }
}
