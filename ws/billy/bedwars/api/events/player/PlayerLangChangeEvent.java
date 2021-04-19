

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerLangChangeEvent extends Event
{
    private static final HandlerList HANDLERS;
    private boolean cancelled;
    private Player player;
    private String oldLang;
    private String newLang;
    
    public PlayerLangChangeEvent(final Player player, final String oldLang, final String newLang) {
        this.cancelled = false;
        this.player = player;
        this.oldLang = oldLang;
        this.newLang = newLang;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getOldLang() {
        return this.oldLang;
    }
    
    public String getNewLang() {
        return this.newLang;
    }
    
    public HandlerList getHandlers() {
        return PlayerLangChangeEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerLangChangeEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
