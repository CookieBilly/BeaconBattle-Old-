

package ws.billy.bedwars.api.events.player;

import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerBedBreakEvent extends Event
{
    private static final HandlerList HANDLERS;
    private Player player;
    private IArena arena;
    private ITeam playerTeam;
    private ITeam victimTeam;
    
    public PlayerBedBreakEvent(final Player player, final ITeam playerTeam, final ITeam victimTeam, final IArena arena) {
        this.player = player;
        this.playerTeam = playerTeam;
        this.victimTeam = victimTeam;
        this.arena = arena;
    }
    
    public ITeam getPlayerTeam() {
        return this.playerTeam;
    }
    
    public ITeam getVictimTeam() {
        return this.victimTeam;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public HandlerList getHandlers() {
        return PlayerBedBreakEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerBedBreakEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
