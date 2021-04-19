

package ws.billy.bedwars.api.events.server;

import ws.billy.bedwars.api.server.ISetupSession;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class SetupSessionCloseEvent extends Event
{
    private static final HandlerList HANDLERS;
    private ISetupSession setupSession;
    
    public SetupSessionCloseEvent(final ISetupSession setupSession) {
        this.setupSession = setupSession;
    }
    
    public ISetupSession getSetupSession() {
        return this.setupSession;
    }
    
    public HandlerList getHandlers() {
        return SetupSessionCloseEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return SetupSessionCloseEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
