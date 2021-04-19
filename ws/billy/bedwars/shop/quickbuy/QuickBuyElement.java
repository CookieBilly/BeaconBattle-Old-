

package ws.billy.bedwars.shop.quickbuy;

import ws.billy.bedwars.shop.main.ShopCategory;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.shop.main.CategoryContent;

public class QuickBuyElement
{
    private int slot;
    private CategoryContent categoryContent;
    private boolean loaded;
    
    public QuickBuyElement(final String s, final int slot) {
        this.loaded = false;
        this.categoryContent = ShopCategory.getCategoryContent(s, ShopManager.getShop());
        if (this.categoryContent != null) {
            this.loaded = true;
        }
        this.slot = slot;
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public CategoryContent getCategoryContent() {
        return this.categoryContent;
    }
}
