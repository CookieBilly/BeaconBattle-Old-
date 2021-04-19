

package ws.billy.bedwars.language;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.sidebar.BeaconBattleScoreboard;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.events.player.PlayerLangChangeEvent;
import org.bukkit.event.Listener;

public class LangListener implements Listener
{
    @EventHandler
    public void onLanguageChangeEvent(final PlayerLangChangeEvent playerLangChangeEvent) {
        if (playerLangChangeEvent == null) {
            return;
        }
        if (BeaconBattle.config.getLobbyWorldName().equalsIgnoreCase(playerLangChangeEvent.getPlayer().getWorld().getName())) {
            Arena.sendLobbyCommandItems(playerLangChangeEvent.getPlayer());
            final BeaconBattleScoreboard sBoard = BeaconBattleScoreboard.getSBoard(playerLangChangeEvent.getPlayer().getUniqueId());
            if (sBoard == null) {
                return;
            }
            final IArena arena = sBoard.getArena();
            sBoard.remove();
            if (playerLangChangeEvent.getPlayer().getScoreboard() != null) {
                BeaconBattleScoreboard.giveScoreboard(playerLangChangeEvent.getPlayer(), arena, false);
            }
        }
    }
}
