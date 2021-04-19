

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerReSpawnEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private IArena arena;
    private ITeam team;
    
    public PlayerReSpawnEvent(final Player player, final IArena arena, final ITeam team) {
        this.player = player;
        this.arena = arena;
        this.team = team;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public HandlerList getHandlers() {
        return PlayerReSpawnEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerReSpawnEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
