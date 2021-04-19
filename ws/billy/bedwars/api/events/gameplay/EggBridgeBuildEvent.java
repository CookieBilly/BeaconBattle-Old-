

package ws.billy.bedwars.api.events.gameplay;

import org.bukkit.block.Block;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.team.TeamColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class EggBridgeBuildEvent extends Event
{
    private static final HandlerList HANDLERS;
    private TeamColor teamColor;
    private IArena arena;
    private Block block;
    
    public EggBridgeBuildEvent(final TeamColor teamColor, final IArena arena, final Block block) {
        this.teamColor = teamColor;
        this.arena = arena;
        this.block = block;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public TeamColor getTeamColor() {
        return this.teamColor;
    }
    
    public HandlerList getHandlers() {
        return EggBridgeBuildEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return EggBridgeBuildEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
