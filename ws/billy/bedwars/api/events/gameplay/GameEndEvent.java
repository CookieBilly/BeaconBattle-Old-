

package ws.billy.bedwars.api.events.gameplay;

import java.util.ArrayList;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.team.ITeam;
import java.util.UUID;
import java.util.List;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class GameEndEvent extends Event
{
    private static final HandlerList HANDLERS;
    private List<UUID> winners;
    private List<UUID> losers;
    private List<UUID> aliveWinners;
    private ITeam teamWinner;
    private IArena arena;
    
    public GameEndEvent(final IArena arena, final List<UUID> c, final List<UUID> c2, final ITeam teamWinner, final List<UUID> c3) {
        this.winners = new ArrayList<UUID>(c);
        this.arena = arena;
        this.losers = new ArrayList<UUID>(c2);
        this.teamWinner = teamWinner;
        this.aliveWinners = new ArrayList<UUID>(c3);
    }
    
    public List<UUID> getWinners() {
        return this.winners;
    }
    
    public ITeam getTeamWinner() {
        return this.teamWinner;
    }
    
    public List<UUID> getLosers() {
        return this.losers;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public List<UUID> getAliveWinners() {
        return this.aliveWinners;
    }
    
    public HandlerList getHandlers() {
        return GameEndEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return GameEndEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
