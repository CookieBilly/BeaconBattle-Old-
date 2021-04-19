

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerGeneratorCollectEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private ItemStack itemStack;
    private IArena arena;
    private boolean cancelled;
    
    public PlayerGeneratorCollectEvent(final Player player, final ItemStack itemStack, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.itemStack = itemStack;
        this.arena = arena;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerGeneratorCollectEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerGeneratorCollectEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
