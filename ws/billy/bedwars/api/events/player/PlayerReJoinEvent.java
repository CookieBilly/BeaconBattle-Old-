

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerReJoinEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private IArena arena;
    private boolean cancelled;
    
    public PlayerReJoinEvent(final Player player, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.arena = arena;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerReJoinEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerReJoinEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
