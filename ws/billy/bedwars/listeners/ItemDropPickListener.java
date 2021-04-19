

package ws.billy.bedwars.listeners;

import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.Listener;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.support.version.common.VersionCommon;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Item;

public class ItemDropPickListener
{
    private static boolean managePickup(final Item item, final LivingEntity key) {
        if (!(key instanceof Player)) {
            return false;
        }
        if (VersionCommon.api.getServerType() != ServerType.BUNGEE && key.getLocation().getWorld().getName().equalsIgnoreCase(VersionCommon.api.getLobbyWorld())) {
            return true;
        }
        final IArena arenaByPlayer = VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)key);
        if (arenaByPlayer == null) {
            return false;
        }
        if (!arenaByPlayer.isPlayer((Player)key)) {
            return true;
        }
        if (arenaByPlayer.getStatus() != GameState.playing) {
            return true;
        }
        if (arenaByPlayer.getRespawn().containsKey(key)) {
            return true;
        }
        if (item.getItemStack().getType() == Material.ARROW) {
            item.setItemStack(VersionCommon.api.getVersionSupport().createItemStack(item.getItemStack().getType().toString(), item.getItemStack().getAmount(), (short)0));
            return false;
        }
        if (item.getItemStack().getType().toString().equals("BED")) {
            item.remove();
            return true;
        }
        if (item.getItemStack().hasItemMeta() && item.getItemStack().getItemMeta().hasDisplayName() && item.getItemStack().getItemMeta().getDisplayName().contains("custom")) {
            final ItemMeta itemMeta = new ItemStack(item.getItemStack().getType()).getItemMeta();
            final PlayerGeneratorCollectEvent playerGeneratorCollectEvent = new PlayerGeneratorCollectEvent((Player)key, item.getItemStack(), arenaByPlayer);
            Bukkit.getPluginManager().callEvent((Event)playerGeneratorCollectEvent);
            if (playerGeneratorCollectEvent.isCancelled()) {
                return true;
            }
            item.getItemStack().setItemMeta(itemMeta);
        }
        return false;
    }
    
    private static boolean manageDrop(final Entity key, final Item item) {
        if (!(key instanceof Player)) {
            return false;
        }
        if (VersionCommon.api.getServerType() != ServerType.BUNGEE && key.getLocation().getWorld().getName().equalsIgnoreCase(VersionCommon.api.getLobbyWorld())) {
            return true;
        }
        final IArena arenaByPlayer = VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)key);
        return arenaByPlayer != null && (!arenaByPlayer.isPlayer((Player)key) || arenaByPlayer.getStatus() != GameState.playing || item.getItemStack().getType() == Material.COMPASS || arenaByPlayer.getRespawn().containsKey(key));
    }
    
    public static class PlayerDrop implements Listener
    {
        @EventHandler
        public void onDrop(final PlayerDropItemEvent playerDropItemEvent) {
            if (manageDrop((Entity)playerDropItemEvent.getPlayer(), playerDropItemEvent.getItemDrop())) {
                playerDropItemEvent.setCancelled(true);
            }
        }
    }
    
    public static class PlayerPickup implements Listener
    {
        @EventHandler
        public void onDrop(final PlayerPickupItemEvent playerPickupItemEvent) {
            if (managePickup(playerPickupItemEvent.getItem(), (LivingEntity)playerPickupItemEvent.getPlayer())) {
                playerPickupItemEvent.setCancelled(true);
            }
        }
    }
    
    public static class EntityDrop implements Listener
    {
        @EventHandler
        public void onDrop(final EntityDropItemEvent entityDropItemEvent) {
            if (manageDrop(entityDropItemEvent.getEntity(), entityDropItemEvent.getItemDrop())) {
                entityDropItemEvent.setCancelled(true);
            }
        }
    }
    
    public static class EntityPickup implements Listener
    {
        @EventHandler
        public void onPickup(final EntityPickupItemEvent entityPickupItemEvent) {
            if (managePickup(entityPickupItemEvent.getItem(), entityPickupItemEvent.getEntity())) {
                entityPickupItemEvent.setCancelled(true);
            }
        }
    }
    
    public static class ArrowCollect implements Listener
    {
        @EventHandler
        public void onArrowPick(final PlayerPickupArrowEvent playerPickupArrowEvent) {
            if (VersionCommon.api.getArenaUtil().isSpectating(playerPickupArrowEvent.getPlayer())) {
                playerPickupArrowEvent.setCancelled(true);
            }
        }
    }
    
    public static class GeneratorCollect implements Listener
    {
        @EventHandler
        public void onCollect(final PlayerGeneratorCollectEvent playerGeneratorCollectEvent) {
            if (VersionCommon.api.getAFKUtil().isPlayerAFK(playerGeneratorCollectEvent.getPlayer())) {
                playerGeneratorCollectEvent.setCancelled(true);
            }
        }
    }
}
