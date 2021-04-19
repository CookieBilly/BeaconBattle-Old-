

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.Listener;

public class Ping implements Listener
{
    @EventHandler
    public void onPing(final ServerListPingEvent serverListPingEvent) {
        if (!Arena.getArenas().isEmpty()) {
            final IArena arena = Arena.getArenas().get(0);
            if (arena != null) {
                serverListPingEvent.setMaxPlayers(arena.getMaxPlayers());
                serverListPingEvent.setMotd(arena.getDisplayStatus(Language.getDefaultLanguage()));
            }
        }
    }
}
