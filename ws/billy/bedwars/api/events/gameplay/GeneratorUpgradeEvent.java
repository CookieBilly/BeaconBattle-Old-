

package ws.billy.bedwars.api.events.gameplay;

import ws.billy.bedwars.api.arena.generator.IGenerator;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class GeneratorUpgradeEvent extends Event
{
    private static final HandlerList HANDLERS;
    private IGenerator generator;
    
    public GeneratorUpgradeEvent(final IGenerator generator) {
        this.generator = generator;
    }
    
    public IGenerator getGenerator() {
        return this.generator;
    }
    
    public HandlerList getHandlers() {
        return GeneratorUpgradeEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return GeneratorUpgradeEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
