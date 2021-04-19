

package ws.billy.bedwars.support.preloadedparty;

import ws.billy.bedwars.api.events.server.ArenaRestartEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.events.server.ArenaDisableEvent;
import org.bukkit.event.Listener;

public class PrePartyListener implements Listener
{
    @EventHandler
    public void onDisable(final ArenaDisableEvent arenaDisableEvent) {
        final PreLoadedParty partyByOwner = PreLoadedParty.getPartyByOwner(arenaDisableEvent.getWorldName());
        if (partyByOwner != null) {
            PreLoadedParty.getPreLoadedParties().remove(partyByOwner);
        }
    }
    
    @EventHandler
    public void onRestart(final ArenaRestartEvent arenaRestartEvent) {
        final PreLoadedParty partyByOwner = PreLoadedParty.getPartyByOwner(arenaRestartEvent.getWorldName());
        if (partyByOwner != null) {
            PreLoadedParty.getPreLoadedParties().remove(partyByOwner);
        }
    }
}
