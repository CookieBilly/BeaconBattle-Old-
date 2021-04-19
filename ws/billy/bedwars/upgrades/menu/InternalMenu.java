

package ws.billy.bedwars.upgrades.menu;

import org.bukkit.inventory.Inventory;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.upgrades.UpgradesManager;
import java.util.Map;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.upgrades.MenuContent;
import java.util.HashMap;
import ws.billy.bedwars.api.upgrades.UpgradesIndex;

public class InternalMenu implements UpgradesIndex
{
    private String name;
    private HashMap<Integer, MenuContent> menuContentBySlot;
    
    public InternalMenu(final String s) {
        this.menuContentBySlot = new HashMap<Integer, MenuContent>();
        this.name = s.toLowerCase();
        Language.saveIfNotExists(Messages.UPGRADES_MENU_GUI_NAME_PATH + s.toLowerCase(), "&8Upgrades & Traps");
    }
    
    @Override
    public void open(final Player player) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (!arenaByPlayer.isPlayer(player)) {
            return;
        }
        final ITeam team = arenaByPlayer.getTeam(player);
        if (team == null) {
            return;
        }
        if (!BeaconBattle.getAPI().getArenaUtil().isPlaying(player)) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 45, Language.getMsg(player, Messages.UPGRADES_MENU_GUI_NAME_PATH + this.name));
        for (final Map.Entry<Integer, MenuContent> entry : this.menuContentBySlot.entrySet()) {
            inventory.setItem((int)entry.getKey(), entry.getValue().getDisplayItem(player, team));
        }
        player.openInventory(inventory);
        UpgradesManager.setWatchingUpgrades(player.getUniqueId());
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean addContent(final MenuContent value, final int n) {
        if (this.menuContentBySlot.get(n) != null) {
            return false;
        }
        this.menuContentBySlot.put(n, value);
        return true;
    }
}
