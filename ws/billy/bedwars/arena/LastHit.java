

package ws.billy.bedwars.arena;

import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import java.util.UUID;

public class LastHit
{
    private UUID victim;
    private Entity damager;
    private long time;
    private static ConcurrentHashMap<UUID, LastHit> lastHit;
    
    public LastHit(@NotNull final Player player, final Entity damager, final long time) {
        this.victim = player.getUniqueId();
        this.damager = damager;
        this.time = time;
        LastHit.lastHit.put(player.getUniqueId(), this);
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public void setDamager(final Entity damager) {
        this.damager = damager;
    }
    
    public Entity getDamager() {
        return this.damager;
    }
    
    public UUID getVictim() {
        return this.victim;
    }
    
    public void remove() {
        LastHit.lastHit.remove(this.victim);
    }
    
    public long getTime() {
        return this.time;
    }
    
    public static LastHit getLastHit(@NotNull final Player player) {
        return LastHit.lastHit.getOrDefault(player.getUniqueId(), null);
    }
    
    static {
        LastHit.lastHit = new ConcurrentHashMap<UUID, LastHit>();
    }
}
