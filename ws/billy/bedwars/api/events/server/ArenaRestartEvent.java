

package ws.billy.bedwars.api.events.server;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ArenaRestartEvent extends Event
{
    private static final HandlerList HANDLERS;
    private String arena;
    private String worldName;
    
    public ArenaRestartEvent(final String arena, final String worldName) {
        this.arena = arena;
        this.worldName = worldName;
    }
    
    public String getArenaName() {
        return this.arena;
    }
    
    public HandlerList getHandlers() {
        return ArenaRestartEvent.HANDLERS;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public static HandlerList getHandlerList() {
        return ArenaRestartEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
