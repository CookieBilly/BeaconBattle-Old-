

package ws.billy.bedwars.api.arena.shop;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public interface IContentTier
{
    int getPrice();
    
    Material getCurrency();
    
    void setCurrency(final Material p0);
    
    void setPrice(final int p0);
    
    void setItemStack(final ItemStack p0);
    
    void setBuyItemsList(final List<IBuyItem> p0);
    
    ItemStack getItemStack();
    
    int getValue();
    
    List<IBuyItem> getBuyItemsList();
}
