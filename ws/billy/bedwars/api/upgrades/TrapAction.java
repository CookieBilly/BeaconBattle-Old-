

package ws.billy.bedwars.api.upgrades;

import ws.billy.bedwars.api.arena.team.ITeam;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public interface TrapAction
{
    String getName();
    
    void onTrigger(@NotNull final Player p0, final ITeam p1, final ITeam p2);
}
