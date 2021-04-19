

package ws.billy.bedwars.api.events.server;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class ArenaDisableEvent extends Event
{
    private static final HandlerList HANDLERS;
    private String arenaName;
    private String worldName;
    
    public ArenaDisableEvent(final String arenaName, final String worldName) {
        this.arenaName = arenaName;
        this.worldName = worldName;
    }
    
    public String getArenaName() {
        return this.arenaName;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public HandlerList getHandlers() {
        return ArenaDisableEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return ArenaDisableEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
