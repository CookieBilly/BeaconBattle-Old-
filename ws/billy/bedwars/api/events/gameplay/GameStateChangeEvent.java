

package ws.billy.bedwars.api.events.gameplay;

import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class GameStateChangeEvent extends Event
{
    private static final HandlerList HANDLERS;
    private IArena arena;
    private GameState oldState;
    private GameState newState;
    
    public GameStateChangeEvent(final IArena arena, final GameState oldState, final GameState newState) {
        this.arena = arena;
        this.oldState = oldState;
        this.newState = newState;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public GameState getOldState() {
        return this.oldState;
    }
    
    public GameState getNewState() {
        return this.newState;
    }
    
    public HandlerList getHandlers() {
        return GameStateChangeEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return GameStateChangeEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
