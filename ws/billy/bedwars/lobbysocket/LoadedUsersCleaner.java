

package ws.billy.bedwars.lobbysocket;

import org.bukkit.OfflinePlayer;
import ws.billy.bedwars.support.preloadedparty.PreLoadedParty;
import org.bukkit.Bukkit;

import java.util.LinkedList;
import java.util.List;

public class LoadedUsersCleaner implements Runnable
{
    private List<LoadedUser> toRemove;
    
    public LoadedUsersCleaner() {
        this.toRemove = new LinkedList<LoadedUser>();
    }
    
    @Override
    public void run() {
        for (final LoadedUser loadedUser2 : LoadedUser.getLoaded().values()) {
            if (loadedUser2.getRequestTime() + 6000L > System.currentTimeMillis()) {
                this.toRemove.add(loadedUser2);
            }
        }
        if (!this.toRemove.isEmpty()) {
            final OfflinePlayer offlinePlayer;
            final PreLoadedParty preLoadedParty;
            this.toRemove.forEach(loadedUser -> {
                Bukkit.getOfflinePlayer(loadedUser.getUuid());
                if (offlinePlayer != null) {
                    PreLoadedParty.getPartyByOwner(offlinePlayer.getName());
                    if (preLoadedParty != null) {
                        preLoadedParty.clean();
                    }
                }
                loadedUser.destroy();
                return;
            });
            this.toRemove.clear();
        }
    }
}
