

package ws.billy.bedwars.shop.defaultrestore;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.Listener;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.support.version.common.VersionCommon;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Item;

public class ShopItemRestoreListener
{
    public static boolean managePickup(final Item item, final LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player)) {
            return false;
        }
        if (VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)livingEntity) == null) {
            return false;
        }
        if (VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)livingEntity).getStatus() != GameState.playing) {
            return false;
        }
        if (!VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)livingEntity).isPlayer((Player)livingEntity)) {
            return false;
        }
        if (VersionCommon.api.getVersionSupport().isSword(item.getItemStack())) {
            for (final ItemStack itemStack : ((Player)livingEntity).getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (itemStack.getType() == Material.AIR) {
                    continue;
                }
                if (!VersionCommon.api.getVersionSupport().isCustomBeaconBattleItem(itemStack)) {
                    continue;
                }
                if (!VersionCommon.api.getVersionSupport().getCustomData(itemStack).equalsIgnoreCase("DEFAULT_ITEM")) {
                    continue;
                }
                if (VersionCommon.api.getVersionSupport().isSword(item.getItemStack()) && VersionCommon.api.getVersionSupport().getDamage(item.getItemStack()) >= VersionCommon.api.getVersionSupport().getDamage(itemStack)) {
                    ((Player)livingEntity).getInventory().remove(itemStack);
                    ((Player)livingEntity).updateInventory();
                    return false;
                }
            }
            item.remove();
            return true;
        }
        return false;
    }
    
    private static boolean manageDrop(final Entity entity, final Item item) {
        if (!(entity instanceof Player)) {
            return false;
        }
        if (VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)entity) == null) {
            return false;
        }
        final IArena arenaByPlayer = VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)entity);
        if (arenaByPlayer.getStatus() != GameState.playing) {
            return false;
        }
        if (!arenaByPlayer.isPlayer((Player)entity)) {
            return false;
        }
        if (VersionCommon.api.getVersionSupport().isCustomBeaconBattleItem(item.getItemStack()) && VersionCommon.api.getVersionSupport().getCustomData(item.getItemStack()).equalsIgnoreCase("DEFAULT_ITEM") && VersionCommon.api.getVersionSupport().isSword(item.getItemStack())) {
            boolean b = false;
            for (final ItemStack itemStack : ((Player)entity).getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (VersionCommon.api.getVersionSupport().isSword(itemStack) && VersionCommon.api.getVersionSupport().getDamage(itemStack) >= VersionCommon.api.getVersionSupport().getDamage(item.getItemStack())) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                return true;
            }
        }
        else {
            boolean b2 = false;
            for (final ItemStack itemStack2 : ((Player)entity).getInventory()) {
                if (itemStack2 == null) {
                    continue;
                }
                if (VersionCommon.api.getVersionSupport().isSword(itemStack2)) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                arenaByPlayer.getTeam((Player)entity).defaultSword((Player)entity, true);
            }
        }
        return false;
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
            if (ShopItemRestoreListener.managePickup(playerPickupItemEvent.getItem(), (LivingEntity)playerPickupItemEvent.getPlayer())) {
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
        public void onDrop(final EntityPickupItemEvent entityPickupItemEvent) {
            if (ShopItemRestoreListener.managePickup(entityPickupItemEvent.getItem(), entityPickupItemEvent.getEntity())) {
                entityPickupItemEvent.setCancelled(true);
            }
        }
    }
    
    public static class DefaultRestoreInvClose implements Listener
    {
        @EventHandler
        public void onInventoryClose(final InventoryCloseEvent inventoryCloseEvent) {
            if (inventoryCloseEvent.getInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            if (VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)inventoryCloseEvent.getPlayer()) == null) {
                return;
            }
            final IArena arenaByPlayer = VersionCommon.api.getArenaUtil().getArenaByPlayer((Player)inventoryCloseEvent.getPlayer());
            if (arenaByPlayer.getStatus() != GameState.playing) {
                return;
            }
            if (!arenaByPlayer.isPlayer((Player)inventoryCloseEvent.getPlayer())) {
                return;
            }
            boolean b = false;
            for (final ItemStack itemStack : inventoryCloseEvent.getPlayer().getInventory()) {
                if (itemStack == null) {
                    continue;
                }
                if (itemStack.getType() == Material.AIR) {
                    continue;
                }
                if (!VersionCommon.api.getVersionSupport().isSword(itemStack)) {
                    continue;
                }
                b = true;
            }
            if (!b) {
                arenaByPlayer.getTeam((Player)inventoryCloseEvent.getPlayer()).defaultSword((Player)inventoryCloseEvent.getPlayer(), true);
            }
        }
    }
}
