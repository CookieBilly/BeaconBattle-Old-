

package ws.billy.bedwars.listeners;

import ws.billy.bedwars.api.arena.IArena;
import java.util.Iterator;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.server.SetupType;
import java.util.Objects;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.event.inventory.InventoryEvent;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.Listener;

public class Inventory implements Listener
{
    @EventHandler
    public void onClose(final InventoryCloseEvent inventoryCloseEvent) {
        final Player player = (Player)inventoryCloseEvent.getPlayer();
        if (BeaconBattle.nms.getInventoryName((InventoryEvent)inventoryCloseEvent).equalsIgnoreCase(SetupSession.getInvName())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            if (session != null && session.getSetupType() == null) {
                session.cancel();
            }
        }
    }
    
    @EventHandler
    public void onCommandItemClick(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getAction() == InventoryAction.HOTBAR_SWAP && inventoryClickEvent.getClick() == ClickType.NUMBER_KEY && inventoryClickEvent.getHotbarButton() > -1) {
            final ItemStack item = inventoryClickEvent.getWhoClicked().getInventory().getItem(inventoryClickEvent.getHotbarButton());
            if (item != null && isCommandItem(item)) {
                inventoryClickEvent.setCancelled(true);
                return;
            }
        }
        if (inventoryClickEvent.getCursor() != null && inventoryClickEvent.getCursor().getType() != Material.AIR) {
            if (inventoryClickEvent.getClickedInventory() == null) {
                if (isCommandItem(inventoryClickEvent.getCursor())) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (inventoryClickEvent.getClickedInventory().getType() != inventoryClickEvent.getWhoClicked().getInventory().getType()) {
                if (isCommandItem(inventoryClickEvent.getCursor())) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (isCommandItem(inventoryClickEvent.getCursor())) {
                inventoryClickEvent.setCancelled(true);
            }
        }
        if (inventoryClickEvent.getCurrentItem() != null && inventoryClickEvent.getCurrentItem().getType() != Material.AIR) {
            if (inventoryClickEvent.getClickedInventory() == null) {
                if (isCommandItem(inventoryClickEvent.getCurrentItem())) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (inventoryClickEvent.getClickedInventory().getType() != inventoryClickEvent.getWhoClicked().getInventory().getType()) {
                if (isCommandItem(inventoryClickEvent.getCurrentItem())) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.setCancelled(true);
                }
            }
            else if (isCommandItem(inventoryClickEvent.getCurrentItem())) {
                inventoryClickEvent.setCancelled(true);
            }
        }
        if (inventoryClickEvent.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && isCommandItem(inventoryClickEvent.getCurrentItem())) {
            inventoryClickEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.ARMOR && Arena.getArenaByPlayer((Player)inventoryClickEvent.getWhoClicked()) != null && !BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets") && inventoryClickEvent.getWhoClicked().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            inventoryClickEvent.getWhoClicked().closeInventory();
            final Iterator<Player> iterator = inventoryClickEvent.getWhoClicked().getWorld().getPlayers().iterator();
            while (iterator.hasNext()) {
                BeaconBattle.nms.hideArmor((Player)inventoryClickEvent.getWhoClicked(), iterator.next());
            }
        }
        if (inventoryClickEvent.getCurrentItem() == null) {
            return;
        }
        if (inventoryClickEvent.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        final Player player = (Player)inventoryClickEvent.getWhoClicked();
        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (BeaconBattle.nms.getInventoryName((InventoryEvent)inventoryClickEvent).equals(Language.getMsg(player, Messages.PLAYER_STATS_GUI_INV_NAME).replace("{player}", player.getDisplayName()))) {
                inventoryClickEvent.setCancelled(true);
                return;
            }
            if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.ARMOR) {
                inventoryClickEvent.setCancelled(true);
                return;
            }
        }
        if (!currentItem.hasItemMeta()) {
            return;
        }
        if (!currentItem.getItemMeta().hasDisplayName()) {
            return;
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && inventoryClickEvent.getWhoClicked().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            inventoryClickEvent.setCancelled(true);
        }
        if (SetupSession.isInSetupSession(player.getUniqueId()) && BeaconBattle.nms.getInventoryName((InventoryEvent)inventoryClickEvent).equalsIgnoreCase(SetupSession.getInvName())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            if (inventoryClickEvent.getSlot() == SetupSession.getAdvancedSlot()) {
                Objects.requireNonNull(session).setSetupType(SetupType.ADVANCED);
            }
            else if (inventoryClickEvent.getSlot() == SetupSession.getAssistedSlot()) {
                Objects.requireNonNull(session).setSetupType(SetupType.ASSISTED);
            }
            if (!Objects.requireNonNull(session).startSetup()) {
                session.getPlayer().sendMessage(ChatColor.RED + "Could not start setup session. Pleas check the console.");
            }
            player.closeInventory();
            return;
        }
        if (arenaByPlayer != null && arenaByPlayer.isSpectator(player)) {
            inventoryClickEvent.setCancelled(true);
        }
    }
    
    private static boolean isCommandItem(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getType() == Material.AIR) {
            return false;
        }
        if (BeaconBattle.nms.isCustomBeaconBattleItem(itemStack)) {
            final String[] split = BeaconBattle.nms.getCustomData(itemStack).split("_");
            if (split.length >= 2) {
                return split[0].equals("RUNCOMMAND");
            }
        }
        return false;
    }
}
