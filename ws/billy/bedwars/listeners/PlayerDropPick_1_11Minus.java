

package ws.billy.bedwars.listeners;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import ws.billy.bedwars.api.BeaconBattle;

public class PlayerDropPick_1_11Minus
{
    private static BeaconBattle api;
    
    public PlayerDropPick_1_11Minus(final BeaconBattle api) {
        PlayerDropPick_1_11Minus.api = api;
    }
    
    @EventHandler
    public void onPickup(final PlayerPickupItemEvent playerPickupItemEvent) {
        if (PlayerDropPick_1_11Minus.api.getServerType() != ServerType.BUNGEE && playerPickupItemEvent.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(PlayerDropPick_1_11Minus.api.getLobbyWorld())) {
            playerPickupItemEvent.setCancelled(true);
            return;
        }
        final IArena arenaByPlayer = PlayerDropPick_1_11Minus.api.getArenaUtil().getArenaByPlayer(playerPickupItemEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        if (!arenaByPlayer.isPlayer(playerPickupItemEvent.getPlayer())) {
            playerPickupItemEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameState.playing) {
            playerPickupItemEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawn().containsKey(playerPickupItemEvent.getPlayer())) {
            playerPickupItemEvent.setCancelled(true);
            return;
        }
        if (playerPickupItemEvent.getItem().getItemStack().getType() == Material.ARROW) {
            playerPickupItemEvent.getItem().setItemStack(PlayerDropPick_1_11Minus.api.getVersionSupport().createItemStack(playerPickupItemEvent.getItem().getItemStack().getType().toString(), playerPickupItemEvent.getItem().getItemStack().getAmount(), (short)0));
            return;
        }
        if (playerPickupItemEvent.getItem().getItemStack().getType().toString().equals("BED")) {
            playerPickupItemEvent.setCancelled(true);
            playerPickupItemEvent.getItem().remove();
        }
        else if (playerPickupItemEvent.getItem().getItemStack().hasItemMeta() && playerPickupItemEvent.getItem().getItemStack().getItemMeta().hasDisplayName() && playerPickupItemEvent.getItem().getItemStack().getItemMeta().getDisplayName().contains("custom")) {
            final ItemMeta itemMeta = new ItemStack(playerPickupItemEvent.getItem().getItemStack().getType()).getItemMeta();
            final PlayerGeneratorCollectEvent playerGeneratorCollectEvent = new PlayerGeneratorCollectEvent(playerPickupItemEvent.getPlayer(), playerPickupItemEvent.getItem().getItemStack(), arenaByPlayer);
            Bukkit.getPluginManager().callEvent((Event)playerGeneratorCollectEvent);
            if (playerGeneratorCollectEvent.isCancelled()) {
                playerPickupItemEvent.setCancelled(true);
            }
            else {
                playerPickupItemEvent.getItem().getItemStack().setItemMeta(itemMeta);
            }
        }
    }
    
    @EventHandler
    public void onDrop(final PlayerDropItemEvent playerDropItemEvent) {
        if (PlayerDropPick_1_11Minus.api.getServerType() != ServerType.BUNGEE && playerDropItemEvent.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(PlayerDropPick_1_11Minus.api.getLobbyWorld())) {
            playerDropItemEvent.setCancelled(true);
            return;
        }
        final IArena arenaByPlayer = PlayerDropPick_1_11Minus.api.getArenaUtil().getArenaByPlayer(playerDropItemEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        if (!arenaByPlayer.isPlayer(playerDropItemEvent.getPlayer())) {
            playerDropItemEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameState.playing) {
            playerDropItemEvent.setCancelled(true);
        }
        else if (playerDropItemEvent.getItemDrop().getItemStack().getType() == Material.COMPASS) {
            playerDropItemEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawn().containsKey(playerDropItemEvent.getPlayer())) {
            playerDropItemEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCollect(final PlayerGeneratorCollectEvent playerGeneratorCollectEvent) {
        if (PlayerDropPick_1_11Minus.api.getAFKUtil().isPlayerAFK(playerGeneratorCollectEvent.getPlayer())) {
            playerGeneratorCollectEvent.setCancelled(true);
        }
    }
}
