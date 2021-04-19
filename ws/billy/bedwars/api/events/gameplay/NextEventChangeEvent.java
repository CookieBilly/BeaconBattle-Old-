

package ws.billy.bedwars.api.events.gameplay;

import ws.billy.bedwars.api.arena.NextEvent;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class NextEventChangeEvent extends Event
{
    private static final HandlerList HANDLERS;
    private IArena arena;
    private NextEvent newEvent;
    private NextEvent oldEvent;
    
    public NextEventChangeEvent(final IArena arena, final NextEvent newEvent, final NextEvent oldEvent) {
        this.arena = arena;
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public NextEvent getNewEvent() {
        return this.newEvent;
    }
    
    public NextEvent getOldEvent() {
        return this.oldEvent;
    }
    
    public HandlerList getHandlers() {
        return NextEventChangeEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return NextEventChangeEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
