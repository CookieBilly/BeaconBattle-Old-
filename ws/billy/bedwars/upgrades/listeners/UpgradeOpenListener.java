

package ws.billy.bedwars.upgrades.listeners;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.Location;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.Listener;

public class UpgradeOpenListener implements Listener
{
    @EventHandler
    public void onUpgradesOpen(final PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerInteractAtEntityEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        final Location location = playerInteractAtEntityEvent.getRightClicked().getLocation();
        final Iterator<ITeam> iterator = arenaByPlayer.getTeams().iterator();
        while (iterator.hasNext()) {
            final Location teamUpgrades = iterator.next().getTeamUpgrades();
            if (location.getBlockX() == teamUpgrades.getBlockX() && location.getBlockY() == teamUpgrades.getBlockY() && location.getBlockZ() == teamUpgrades.getBlockZ()) {
                playerInteractAtEntityEvent.setCancelled(true);
                if (!arenaByPlayer.isPlayer(playerInteractAtEntityEvent.getPlayer())) {
                    continue;
                }
                UpgradesManager.getMenuForArena(arenaByPlayer).open(playerInteractAtEntityEvent.getPlayer());
            }
        }
    }
}
