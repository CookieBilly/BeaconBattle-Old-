

package ws.billy.bedwars.arena.spectator;

import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import ws.billy.bedwars.BeaconBattle;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TeleporterGUI
{
    public static final String NBT_SPECTATOR_TELEPORTER_GUI_HEAD = "spectatorTeleporterGUIhead_";
    private static HashMap<Player, Inventory> refresh;
    
    public static void refreshInv(final Player player, final Inventory inventory) {
        if (player.getOpenInventory() == null) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            player.closeInventory();
            return;
        }
        final List<Player> players = arenaByPlayer.getPlayers();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (i < players.size()) {
                inventory.setItem(i, createHead(players.get(i), player));
            }
            else {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }
    
    public static void openGUI(final Player key) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(key);
        if (arenaByPlayer == null) {
            return;
        }
        final int size = arenaByPlayer.getPlayers().size();
        int n;
        if (size <= 9) {
            n = 9;
        }
        else if (size <= 18) {
            n = 18;
        }
        else if (size > 19 && size <= 27) {
            n = 27;
        }
        else if (size > 27 && size <= 36) {
            n = 36;
        }
        else if (size > 36 && size <= 45) {
            n = 45;
        }
        else {
            n = 54;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)key, n, Language.getMsg(key, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_NAME));
        refreshInv(key, inventory);
        TeleporterGUI.refresh.put(key, inventory);
        key.openInventory(inventory);
    }
    
    public static HashMap<Player, Inventory> getRefresh() {
        return TeleporterGUI.refresh;
    }
    
    public static void refreshAllGUIs() {
        for (final Map.Entry<Player, Object> entry : new HashMap<Player, Object>(getRefresh()).entrySet()) {
            refreshInv(entry.getKey(), entry.getValue());
        }
    }
    
    private static ItemStack createHead(final Player player, final Player player2) {
        final ItemStack setSkullOwner = BeaconBattle.nms.setSkullOwner(BeaconBattle.nms.createItemStack(BeaconBattle.nms.materialPlayerHead().toString(), 1, (short)3), player);
        final ItemMeta itemMeta = setSkullOwner.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player2, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_NAME).replace("{prefix}", BeaconBattle.getChatSupport().getPrefix(player)).replace("{suffix}", BeaconBattle.getChatSupport().getSuffix(player)).replace("{player}", player.getDisplayName()));
        final ArrayList<String> lore = new ArrayList<String>();
        final String value = String.valueOf((int)player.getHealth() * 100 / player.getHealthScale());
        final Iterator<String> iterator = Language.getList(player2, Messages.ARENA_SPECTATOR_TELEPORTER_GUI_HEAD_LORE).iterator();
        while (iterator.hasNext()) {
            lore.add(iterator.next().replace("{health}", value).replace("{food}", String.valueOf(player.getFoodLevel())));
        }
        itemMeta.setLore((List)lore);
        setSkullOwner.setItemMeta(itemMeta);
        return BeaconBattle.nms.addCustomData(setSkullOwner, "spectatorTeleporterGUIhead_" + player.getName());
    }
    
    public static void closeGUI(final Player player) {
        if (getRefresh().containsKey(player)) {
            TeleporterGUI.refresh.remove(player);
            if (player.getOpenInventory() != null) {
                player.closeInventory();
            }
        }
    }
    
    static {
        TeleporterGUI.refresh = new HashMap<Player, Inventory>();
    }
}
