

package ws.billy.bedwars.shop.main;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuickBuyButton
{
    private int slot;
    private ItemStack itemStack;
    private String namePath;
    private String lorePath;
    
    public QuickBuyButton(final int slot, final ItemStack itemStack, final String namePath, final String lorePath) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.namePath = namePath;
        this.lorePath = lorePath;
    }
    
    public ItemStack getItemStack(final Player player) {
        final ItemStack clone = this.itemStack.clone();
        final ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, this.namePath));
        itemMeta.setLore((List)Language.getList(player, this.lorePath));
        clone.setItemMeta(itemMeta);
        return clone;
    }
    
    public int getSlot() {
        return this.slot;
    }
}
