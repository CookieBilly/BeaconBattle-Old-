

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerBaseEnterEvent extends Event
{
    private static final HandlerList HANDLERS;
    private ITeam team;
    private Player p;
    
    public PlayerBaseEnterEvent(final Player p2, final ITeam team) {
        this.p = p2;
        this.team = team;
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public HandlerList getHandlers() {
        return PlayerBaseEnterEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerBaseEnterEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
