

package ws.billy.bedwars.api.events.spectator;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class SpectatorFirstPersonEnterEvent extends Event
{
    private static final HandlerList HANDLERS;
    private UUID spectator;
    private UUID target;
    private IArena arena;
    private boolean cancelled;
    private String title;
    private String subtitle;
    private static List<UUID> spectatingInFirstPerson;
    
    public SpectatorFirstPersonEnterEvent(@NotNull final Player player, @NotNull final Player player2, final IArena arena, final String title, final String subtitle) {
        this.cancelled = false;
        this.spectator = player.getUniqueId();
        this.target = player2.getUniqueId();
        this.arena = arena;
        this.title = title;
        this.subtitle = subtitle;
        if (!SpectatorFirstPersonEnterEvent.spectatingInFirstPerson.contains(player.getUniqueId())) {
            SpectatorFirstPersonEnterEvent.spectatingInFirstPerson.add(player.getUniqueId());
        }
    }
    
    public Player getSpectator() {
        return Bukkit.getPlayer(this.spectator);
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public Player getTarget() {
        return Bukkit.getPlayer(this.target);
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
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
        return SpectatorFirstPersonEnterEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return SpectatorFirstPersonEnterEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
        SpectatorFirstPersonEnterEvent.spectatingInFirstPerson = new ArrayList<UUID>();
    }
}
