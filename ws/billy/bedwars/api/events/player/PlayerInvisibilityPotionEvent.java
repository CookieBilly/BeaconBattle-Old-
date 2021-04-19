

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerInvisibilityPotionEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Type type;
    private Player player;
    private IArena arena;
    private ITeam team;
    
    public PlayerInvisibilityPotionEvent(final Type type, final ITeam team, final Player player, final IArena arena) {
        this.type = type;
        this.player = player;
        this.arena = arena;
        this.team = team;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public HandlerList getHandlers() {
        return PlayerInvisibilityPotionEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerInvisibilityPotionEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
    
    public enum Type
    {
        ADDED, 
        REMOVED;
    }
}
