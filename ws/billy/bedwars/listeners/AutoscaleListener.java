

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.events.gameplay.GameStateChangeEvent;
import org.bukkit.event.Listener;

public class AutoscaleListener implements Listener
{
    @EventHandler
    public void onPlaying(final GameStateChangeEvent gameStateChangeEvent) {
        if (gameStateChangeEvent.getNewState() == GameState.playing && Arena.canAutoScale(gameStateChangeEvent.getArena().getArenaName())) {
            new Arena(gameStateChangeEvent.getArena().getArenaName(), null);
        }
    }
}
