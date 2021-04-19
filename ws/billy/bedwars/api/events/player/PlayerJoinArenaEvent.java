

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerJoinArenaEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private boolean spectator;
    private boolean cancelled;
    private IArena arena;
    
    public PlayerJoinArenaEvent(final IArena arena, final Player player, final boolean spectator) {
        this.cancelled = false;
        this.arena = arena;
        this.player = player;
        this.spectator = spectator;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public boolean isSpectator() {
        return this.spectator;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerJoinArenaEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerJoinArenaEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
