

package ws.billy.bedwars.api.events.upgrades;

import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.upgrades.TeamUpgrade;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class UpgradeBuyEvent extends Event
{
    private static final HandlerList HANDLERS;
    private TeamUpgrade teamUpgrade;
    private Player player;
    private ITeam team;
    
    public UpgradeBuyEvent(final TeamUpgrade teamUpgrade, final Player player, final ITeam team) {
        this.teamUpgrade = teamUpgrade;
        this.player = player;
        this.team = team;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public TeamUpgrade getTeamUpgrade() {
        return this.teamUpgrade;
    }
    
    public HandlerList getHandlers() {
        return UpgradeBuyEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return UpgradeBuyEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
