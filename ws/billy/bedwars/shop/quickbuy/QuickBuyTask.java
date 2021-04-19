

package ws.billy.bedwars.shop.quickbuy;

import java.util.Iterator;
import ws.billy.bedwars.shop.main.CategoryContent;
import ws.billy.bedwars.shop.main.ShopCategory;
import ws.billy.bedwars.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;

public class QuickBuyTask extends BukkitRunnable
{
    private UUID uuid;
    
    public QuickBuyTask(final UUID uuid) {
        this.uuid = uuid;
        this.runTaskLaterAsynchronously((Plugin)BeaconBattle.plugin, 140L);
    }
    
    public void run() {
        if (Bukkit.getPlayer(this.uuid) == null) {
            this.cancel();
            return;
        }
        if (Bukkit.getPlayer(this.uuid).isOnline()) {
            final PlayerQuickBuyCache quickBuyCache = PlayerQuickBuyCache.getQuickBuyCache(this.uuid);
            if (quickBuyCache == null) {
                this.cancel();
                return;
            }
            if (!BeaconBattle.getRemoteDatabase().hasQuickBuy(this.uuid)) {
                if (BeaconBattle.shop.getYml().get("quick-buy-defaults") != null) {
                    for (final String s : BeaconBattle.shop.getYml().getConfigurationSection("quick-buy-defaults").getKeys(false)) {
                        if (BeaconBattle.shop.getYml().get("quick-buy-defaults." + s + ".path") != null) {
                            if (BeaconBattle.shop.getYml().get("quick-buy-defaults." + s + ".slot") == null) {
                                continue;
                            }
                            try {
                                Integer.valueOf(BeaconBattle.shop.getYml().getString("quick-buy-defaults." + s + ".slot"));
                            }
                            catch (Exception ex) {
                                BeaconBattle.debug(BeaconBattle.shop.getYml().getString("quick-buy-defaults." + s + ".slot") + " must be an integer!");
                                continue;
                            }
                            final Iterator<ShopCategory> iterator2 = ShopManager.getShop().getCategoryList().iterator();
                            while (iterator2.hasNext()) {
                                for (final CategoryContent categoryContent : iterator2.next().getCategoryContentList()) {
                                    if (categoryContent.getIdentifier().equals(BeaconBattle.shop.getYml().getString("quick-buy-defaults." + s + ".path"))) {
                                        quickBuyCache.setElement(Integer.parseInt(BeaconBattle.shop.getYml().getString("quick-buy-defaults." + s + ".slot")), categoryContent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                for (final int n : PlayerQuickBuyCache.quickSlots) {
                    final String quickBuySlots = BeaconBattle.getRemoteDatabase().getQuickBuySlots(this.uuid, n);
                    if (!quickBuySlots.isEmpty()) {
                        if (!quickBuySlots.equals(" ")) {
                            final QuickBuyElement quickBuyElement = new QuickBuyElement(quickBuySlots, n);
                            if (quickBuyElement.isLoaded()) {
                                quickBuyCache.addQuickElement(quickBuyElement);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public synchronized void cancel() {
        super.cancel();
    }
}
