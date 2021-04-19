

package ws.billy.bedwars.api.upgrades;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;

public interface MenuContent
{
    ItemStack getDisplayItem(final Player p0, final ITeam p1);
    
    void onClick(final Player p0, final ClickType p1, final ITeam p2);
    
    String getName();
}
