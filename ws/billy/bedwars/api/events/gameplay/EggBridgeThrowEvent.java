

package ws.billy.bedwars.api.events.gameplay;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class EggBridgeThrowEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private IArena arena;
    private boolean cancelled;
    
    public EggBridgeThrowEvent(final Player player, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.arena = arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return EggBridgeThrowEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return EggBridgeThrowEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
