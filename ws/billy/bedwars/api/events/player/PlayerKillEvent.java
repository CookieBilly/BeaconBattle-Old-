

package ws.billy.bedwars.api.events.player;

import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerKillEvent extends Event
{
    private static final HandlerList HANDLERS;
    private IArena a;
    private Player victim;
    private Player killer;
    private PlayerKillCause cause;
    private String message;
    
    public PlayerKillEvent(final IArena a, final Player victim, final Player killer, final String message, final PlayerKillCause cause) {
        this.a = a;
        this.victim = victim;
        this.killer = killer;
        this.message = message;
        this.cause = cause;
    }
    
    public Player getKiller() {
        return this.killer;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public PlayerKillCause getCause() {
        return this.cause;
    }
    
    public IArena getArena() {
        return this.a;
    }
    
    public Player getVictim() {
        return this.victim;
    }
    
    public HandlerList getHandlers() {
        return PlayerKillEvent.HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerKillEvent.HANDLERS;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
    
    public enum PlayerKillCause
    {
        UNKNOWN(false), 
        UNKNOWN_FINAL_KILL(true), 
        EXPLOSION(false), 
        EXPLOSION_FINAL_KILL(true), 
        VOID(false), 
        VOID_FINAL_KILL(true), 
        PVP(false), 
        PVP_FINAL_KILL(true), 
        PLAYER_SHOOT(false), 
        PLAYER_SHOOT_FINAL_KILL(true), 
        SILVERFISH(false), 
        SILVERFISH_FINAL_KILL(true), 
        IRON_GOLEM(false), 
        IRON_GOLEM_FINAL_KILL(true);
        
        private boolean finalKill;
        
        private PlayerKillCause(final boolean finalKill) {
            this.finalKill = finalKill;
        }
        
        public boolean isFinalKill() {
            return this.finalKill;
        }
    }
}
