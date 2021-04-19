

package ws.billy.bedwars.api.arena.shop;

import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;

public interface IBuyItem
{
    boolean isLoaded();
    
    void give(final Player p0, final IArena p1);
    
    String getUpgradeIdentifier();
    
    ItemStack getItemStack();
    
    void setItemStack(final ItemStack p0);
    
    boolean isAutoEquip();
    
    void setAutoEquip(final boolean p0);
    
    boolean isPermanent();
    
    void setPermanent(final boolean p0);
}
