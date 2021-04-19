

package ws.billy.bedwars.api.events.spectator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.IArena;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class SpectatorFirstPersonLeaveEvent extends Event
{
    private static final HandlerList HANDLERS;
    private UUID spectator;
    private IArena arena;
    private String title;
    private String subtitle;
    
    public SpectatorFirstPersonLeaveEvent(final Player player, final IArena arena, final String title, final String subtitle) {
        this.spectator = player.getUniqueId();
        this.arena = arena;
        this.title = title;
        this.subtitle = subtitle;
    }
    
    public Player getSpectator() {
        return Bukkit.getPlayer(this.spectator);
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public String getSubtitle() {
        return this.subtitle;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title, final String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }
    
    public HandlerList getHandlers() {
        return SpectatorFirstPersonLeaveEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return SpectatorFirstPersonLeaveEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
