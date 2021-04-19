

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.sidebar.BeaconBattleScoreboard;
import ws.billy.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import org.bukkit.event.Listener;

public class InvisibilityPotionListener implements Listener
{
    @EventHandler
    public void onPotion(final PlayerInvisibilityPotionEvent playerInvisibilityPotionEvent) {
        if (playerInvisibilityPotionEvent.getTeam() == null) {
            return;
        }
        if (playerInvisibilityPotionEvent.getType() == PlayerInvisibilityPotionEvent.Type.ADDED) {
            if (playerInvisibilityPotionEvent.getPlayer().getPassenger() == null) {
                for (final ws.billy.bedwars.sidebar.BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
                    if (BeaconBattleScoreboard.getArena() == null) {
                        continue;
                    }
                    if (!BeaconBattleScoreboard.getArena().equals(playerInvisibilityPotionEvent.getArena()) || playerInvisibilityPotionEvent.getTeam().isMember(BeaconBattleScoreboard.getPlayer())) {
                        continue;
                    }
                    BeaconBattleScoreboard.invisibilityPotion(playerInvisibilityPotionEvent.getTeam(), playerInvisibilityPotionEvent.getPlayer(), true);
                }
            }
        }
        else if (playerInvisibilityPotionEvent.getPlayer().getPassenger() == null) {
            for (final BeaconBattleScoreboard BeaconBattleScoreboard2 : BeaconBattleScoreboard.getScoreboards().values()) {
                if (BeaconBattleScoreboard2.getArena() == null) {
                    continue;
                }
                if (!BeaconBattleScoreboard2.getArena().equals(playerInvisibilityPotionEvent.getArena()) || playerInvisibilityPotionEvent.getTeam().isMember(BeaconBattleScoreboard2.getPlayer())) {
                    continue;
                }
                BeaconBattleScoreboard2.invisibilityPotion(playerInvisibilityPotionEvent.getTeam(), playerInvisibilityPotionEvent.getPlayer(), false);
            }
        }
    }
}
