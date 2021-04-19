

package ws.billy.bedwars.api.upgrades;

import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.inventory.ItemStack;

public interface EnemyBaseEnterTrap
{
    String getNameMsgPath();
    
    String getLoreMsgPath();
    
    ItemStack getItemStack();
    
    void trigger(final ITeam p0, final Player p1);
}
