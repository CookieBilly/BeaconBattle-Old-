

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerLevelUpEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private int newXpTarget;
    private int newLevel;
    
    public PlayerLevelUpEvent(final Player player, final int newLevel, final int newXpTarget) {
        this.player = player;
        this.newLevel = newLevel;
        this.newXpTarget = newXpTarget;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public int getNewLevel() {
        return this.newLevel;
    }
    
    public int getNewXpTarget() {
        return this.newXpTarget;
    }
    
    public HandlerList getHandlers() {
        return PlayerLevelUpEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLevelUpEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
