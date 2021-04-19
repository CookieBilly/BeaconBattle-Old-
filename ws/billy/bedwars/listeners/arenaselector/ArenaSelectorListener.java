

package ws.billy.bedwars.listeners.arenaselector;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.event.inventory.ClickType;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Material;
import ws.billy.bedwars.arena.ArenaGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public class ArenaSelectorListener implements Listener
{
    public static final String ARENA_SELECTOR_GUI_IDENTIFIER = "arena=";
    
    @EventHandler
    public void onArenaSelectorClick(final InventoryClickEvent inventoryClickEvent) {
        final Player player = (Player)inventoryClickEvent.getWhoClicked();
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof ArenaGUI.ArenaSelectorHolder)) {
            return;
        }
        inventoryClickEvent.setCancelled(true);
        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();
        if (currentItem == null) {
            return;
        }
        if (currentItem.getType() == Material.AIR) {
            return;
        }
        if (!BeaconBattle.nms.isCustomBeaconBattleItem(currentItem)) {
            return;
        }
        final String customData = BeaconBattle.nms.getCustomData(currentItem);
        if (customData.startsWith("RUNCOMMAND")) {
            Bukkit.dispatchCommand((CommandSender)player, customData.split("_")[1]);
        }
        if (!customData.contains("arena=")) {
            return;
        }
        final IArena arenaByName = Arena.getArenaByName(customData.split("=")[1]);
        if (arenaByName == null) {
            return;
        }
        if (inventoryClickEvent.getClick() == ClickType.LEFT) {
            if ((arenaByName.getStatus() == GameState.waiting || arenaByName.getStatus() == GameState.starting) && arenaByName.addPlayer(player, false)) {
                Sounds.playSound("join-allowed", player);
            }
            else {
                Sounds.playSound("join-denied", player);
                player.sendMessage(Language.getMsg(player, Messages.ARENA_JOIN_DENIED_SELECTOR));
            }
        }
        else if (inventoryClickEvent.getClick() == ClickType.RIGHT) {
            if (arenaByName.getStatus() == GameState.playing && arenaByName.addSpectator(player, false, null)) {
                Sounds.playSound("spectate-allowed", player);
            }
            else {
                player.sendMessage(Language.getMsg(player, Messages.ARENA_SPECTATE_DENIED_SELECTOR));
                Sounds.playSound("spectate-denied", player);
            }
        }
        player.closeInventory();
    }
}
