

package ws.billy.vipfeatures.api.event;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class BlockChangeEvent extends Event
{
    private static HandlerList handlerList;
    private Location location;
    private Material oldMaterial;
    private Material newMaterial;
    private boolean cancelled;
    
    public BlockChangeEvent(final Location location, final Material oldMaterial, final Material newMaterial) {
        this.cancelled = false;
        this.location = location;
        this.oldMaterial = oldMaterial;
        this.newMaterial = newMaterial;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public Material getOldMaterial() {
        return this.oldMaterial;
    }
    
    public Material getNewMaterial() {
        return this.newMaterial;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public HandlerList getHandlers() {
        return BlockChangeEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return BlockChangeEvent.handlerList;
    }
    
    static {
        BlockChangeEvent.handlerList = new HandlerList();
    }
}
