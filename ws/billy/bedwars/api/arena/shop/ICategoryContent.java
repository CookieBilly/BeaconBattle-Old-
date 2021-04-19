

package ws.billy.bedwars.api.arena.shop;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public interface ICategoryContent
{
    int getSlot();
    
    ItemStack getItemStack(final Player p0);
    
    boolean hasQuick(final Player p0);
    
    boolean isPermanent();
    
    boolean isDowngradable();
    
    String getIdentifier();
    
    List<IContentTier> getContentTiers();
}
