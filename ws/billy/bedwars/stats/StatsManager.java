

package ws.billy.bedwars.stats;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Map;

public class StatsManager
{
    private Map<UUID, PlayerStats> stats;
    
    public StatsManager() {
        this.stats = new ConcurrentHashMap<UUID, PlayerStats>();
        this.registerListeners();
    }
    
    public void remove(final UUID uuid) {
        this.stats.remove(uuid);
    }
    
    public void put(final UUID uuid, final PlayerStats playerStats) {
        this.stats.put(uuid, playerStats);
    }
    
    public PlayerStats get(final UUID uuid) {
        final PlayerStats playerStats = this.stats.get(uuid);
        if (playerStats == null) {
            throw new IllegalStateException("Trying to get stats data of an unloaded player!");
        }
        return playerStats;
    }
    
    public PlayerStats getUnsafe(final UUID uuid) {
        return this.stats.get(uuid);
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents((Listener)new StatsListener(), (Plugin)BeaconBattle.plugin);
    }
}
