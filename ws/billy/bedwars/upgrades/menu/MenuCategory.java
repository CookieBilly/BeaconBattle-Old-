

package ws.billy.bedwars.upgrades.menu;

import org.bukkit.inventory.Inventory;
import java.util.Map;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import java.util.Collections;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.upgrades.MenuContent;

public class MenuCategory implements MenuContent
{
    private ItemStack displayItem;
    private String name;
    private HashMap<Integer, MenuContent> menuContentBySlot;
    
    public MenuCategory(final String str, final ItemStack itemStack) {
        this.menuContentBySlot = new HashMap<Integer, MenuContent>();
        this.name = str;
        this.displayItem = BeaconBattle.nms.addCustomData(itemStack, "MCONT_" + str);
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + str.replace("category-", ""), "&8" + str);
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + str.replace("category-", ""), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + str.replace("category-", ""), Collections.singletonList("&cLore not set"));
    }
    
    public boolean addContent(final MenuContent value, final int n) {
        if (this.menuContentBySlot.get(n) != null) {
            return false;
        }
        this.menuContentBySlot.put(n, value);
        return true;
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        final ItemStack itemStack = new ItemStack(this.displayItem);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_CATEGORY_ITEM_NAME_PATH + this.name.replace("category-", "")));
        final List<String> list = Language.getList(player, Messages.UPGRADES_CATEGORY_ITEM_LORE_PATH + this.name.replace("category-", ""));
        if (this.name.equalsIgnoreCase("traps")) {
            int n = UpgradesManager.getConfiguration().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-queue-limit");
            if (n == 0) {
                n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-queue-limit");
            }
            if (n == team.getActiveTraps().size()) {
                list.add("");
                list.add(Language.getMsg(player, Messages.UPGRADES_TRAP_QUEUE_LIMIT));
            }
        }
        itemMeta.setLore((List)list);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        if (this.name.equalsIgnoreCase("category-traps")) {
            int n = UpgradesManager.getConfiguration().getInt(team.getArena().getGroup().toLowerCase() + "-upgrades-settings.trap-queue-limit");
            if (n == 0) {
                n = UpgradesManager.getConfiguration().getInt("default-upgrades-settings.trap-queue-limit");
            }
            if (n <= team.getActiveTraps().size()) {
                player.sendMessage(Language.getMsg(player, Messages.UPGRADES_TRAP_QUEUE_LIMIT));
                return;
            }
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 45, Language.getMsg(player, Messages.UPGRADES_CATEGORY_GUI_NAME_PATH + this.name.replace("category-", "")));
        for (final Map.Entry<Integer, MenuContent> entry : this.menuContentBySlot.entrySet()) {
            inventory.setItem((int)entry.getKey(), entry.getValue().getDisplayItem(player, team));
        }
        player.openInventory(inventory);
        UpgradesManager.setWatchingUpgrades(player.getUniqueId());
    }
}
