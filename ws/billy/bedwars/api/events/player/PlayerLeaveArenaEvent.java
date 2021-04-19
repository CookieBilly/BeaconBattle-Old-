

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerLeaveArenaEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private boolean spectator;
    private IArena arena;
    
    public PlayerLeaveArenaEvent(final Player player, final IArena arena) {
        this.player = player;
        this.spectator = arena.isSpectator(player);
        this.arena = arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public boolean isSpectator() {
        return this.spectator;
    }
    
    public HandlerList getHandlers() {
        return PlayerLeaveArenaEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLeaveArenaEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
