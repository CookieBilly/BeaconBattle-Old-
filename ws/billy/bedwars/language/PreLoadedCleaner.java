

package ws.billy.bedwars.language;

import java.util.Map;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.LinkedList;

public class PreLoadedCleaner implements Runnable
{
    private static boolean started;
    private LinkedList<UUID> toRemove;
    
    private PreLoadedCleaner() {
        this.toRemove = new LinkedList<UUID>();
        PreLoadedCleaner.started = true;
    }
    
    public static void init() {
        if (!PreLoadedCleaner.started) {
            Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)BeaconBattle.plugin, (Runnable)new PreLoadedCleaner(), 20L, 60L);
        }
    }
    
    @Override
    public void run() {
        if (!PreLoadedLanguage.getPreLoadedLanguage().isEmpty()) {
            final long currentTimeMillis = System.currentTimeMillis();
            for (final Map.Entry<UUID, PreLoadedLanguage> entry : PreLoadedLanguage.getPreLoadedLanguage().entrySet()) {
                if (entry.getValue().getTimeout() <= currentTimeMillis) {
                    this.toRemove.add(entry.getKey());
                }
            }
            this.toRemove.forEach(PreLoadedLanguage::clear);
            this.toRemove.clear();
        }
    }
    
    static {
        PreLoadedCleaner.started = false;
    }
}
