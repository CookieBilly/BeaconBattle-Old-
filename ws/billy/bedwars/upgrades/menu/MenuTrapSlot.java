

package ws.billy.bedwars.upgrades.menu;

import org.bukkit.event.inventory.ClickType;
import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import ws.billy.bedwars.api.upgrades.EnemyBaseEnterTrap;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import ws.billy.bedwars.upgrades.UpgradesManager;
import java.util.Collections;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.upgrades.MenuContent;

public class MenuTrapSlot implements MenuContent
{
    private ItemStack displayItem;
    private String name;
    private int trap;
    
    public MenuTrapSlot(final String str, final ItemStack itemStack) {
        this.displayItem = BeaconBattle.nms.addCustomData(itemStack, "MCONT_" + str);
        this.name = str;
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + str.replace("trap-slot-", ""), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + str.replace("trap-slot-", ""), Collections.singletonList("&cLore1 not set"));
        Language.saveIfNotExists(Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + str.replace("trap-slot-", ""), Collections.singletonList("&cLore2 not set"));
        this.trap = UpgradesManager.getConfiguration().getInt(str + ".trap");
        if (this.trap < 0) {
            this.trap = 0;
        }
        if (this.trap != 0) {
            --this.trap;
        }
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        ItemStack itemStack = this.displayItem.clone();
        EnemyBaseEnterTrap enemyBaseEnterTrap = null;
        if (!team.getActiveTraps().isEmpty() && team.getActiveTraps().size() > this.trap) {
            enemyBaseEnterTrap = team.getActiveTraps().get(this.trap);
        }
        if (enemyBaseEnterTrap != null) {
            itemStack = enemyBaseEnterTrap.getItemStack().clone();
        }
        itemStack.setAmount(this.trap + 1);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_TRAP_SLOT_ITEM_NAME_PATH + this.name.replace("trap-slot-", "")).replace("{name}", Language.getMsg(player, (enemyBaseEnterTrap == null) ? Messages.MEANING_NO_TRAP : enemyBaseEnterTrap.getNameMsgPath())).replace("{color}", Language.getMsg(player, (enemyBaseEnterTrap == null) ? Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD : Messages.FORMAT_UPGRADE_COLOR_UNLOCKED)));
        final ArrayList<String> lore = new ArrayList<String>();
        if (enemyBaseEnterTrap == null) {
            int n = UpgradesManager.getConfiguration().getInt(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-start-price");
            if (n == 0) {
                n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-start-price");
            }
            String s = UpgradesManager.getConfiguration().getString(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-currency");
            if (s == null) {
                s = UpgradesManager.getConfiguration().getString("default-upgrades-settings.trap-currency");
            }
            final String currencyMsg = UpgradesManager.getCurrencyMsg(player, n, s);
            if (!team.getActiveTraps().isEmpty()) {
                int n2 = UpgradesManager.getConfiguration().getInt(team.getArena().getArenaName().toLowerCase() + "-upgrades-settings.trap-increment-price");
                if (n2 == 0) {
                    n2 = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-increment-price");
                }
                n += team.getActiveTraps().size() * n2;
            }
            final Iterator<String> iterator = Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + this.name.replace("trap-slot-", "")).iterator();
            while (iterator.hasNext()) {
                lore.add(iterator.next().replace("{cost}", String.valueOf(n)).replace("{currency}", currencyMsg));
            }
            lore.add("");
            final Iterator<String> iterator2 = Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE2_PATH + this.name.replace("trap-slot-", "")).iterator();
            while (iterator2.hasNext()) {
                lore.add(iterator2.next().replace("{cost}", String.valueOf(n)).replace("{currency}", currencyMsg));
            }
        }
        else {
            lore.addAll((Collection<?>)Language.getList(player, enemyBaseEnterTrap.getLoreMsgPath()));
            lore.addAll((Collection<?>)Language.getList(player, Messages.UPGRADES_TRAP_SLOT_ITEM_LORE1_PATH + this.name.replace("trap-slot-", "")));
        }
        itemMeta.setLore((List)lore);
        itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
