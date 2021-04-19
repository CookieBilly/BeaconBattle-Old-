

package ws.billy.bedwars.api.events.server;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ArenaEnableEvent extends Event
{
    private static final HandlerList HANDLERS;
    private IArena arena;
    
    public ArenaEnableEvent(final IArena arena) {
        this.arena = arena;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public HandlerList getHandlers() {
        return ArenaEnableEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return ArenaEnableEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
