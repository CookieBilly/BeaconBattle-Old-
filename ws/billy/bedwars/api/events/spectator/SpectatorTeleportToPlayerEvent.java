

package ws.billy.bedwars.api.events.spectator;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.IArena;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class SpectatorTeleportToPlayerEvent extends Event
{
    private static final HandlerList HANDLERS;
    private UUID spectator;
    private UUID target;
    private IArena arena;
    private boolean cancelled;
    
    public SpectatorTeleportToPlayerEvent(@NotNull final Player player, @NotNull final Player player2, final IArena arena) {
        this.cancelled = false;
        this.spectator = player.getUniqueId();
        this.target = player2.getUniqueId();
        this.arena = arena;
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
    
    public HandlerList getHandlers() {
        return SpectatorTeleportToPlayerEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return SpectatorTeleportToPlayerEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
