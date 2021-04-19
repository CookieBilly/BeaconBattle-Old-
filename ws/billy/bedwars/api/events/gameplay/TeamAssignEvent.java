

package ws.billy.bedwars.api.events.gameplay;

import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TeamAssignEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private ITeam team;
    private IArena arena;
    private boolean cancelled;
    
    public TeamAssignEvent(final Player player, final ITeam team, final IArena arena) {
        this.cancelled = false;
        this.player = player;
        this.team = team;
        this.arena = arena;
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return TeamAssignEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return TeamAssignEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
