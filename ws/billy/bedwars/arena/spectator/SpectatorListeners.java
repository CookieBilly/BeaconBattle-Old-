

package ws.billy.bedwars.arena.spectator;

import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ws.billy.bedwars.api.events.player.PlayerKillEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ws.billy.bedwars.api.events.spectator.SpectatorFirstPersonEnterEvent;
import ws.billy.bedwars.api.events.spectator.SpectatorFirstPersonLeaveEvent;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.spectator.SpectatorTeleportToPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.Material;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class SpectatorListeners implements Listener
{
    @EventHandler
    public void onSpectatorItemInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        final ItemStack itemInHand = BeaconBattle.nms.getItemInHand(player);
        if (itemInHand == null) {
            return;
        }
        if (itemInHand.getType() == Material.AIR) {
            return;
        }
        if (!BeaconBattle.nms.isCustomBeaconBattleItem(itemInHand)) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (!arenaByPlayer.isSpectator(player)) {
            return;
        }
        playerInteractEvent.setCancelled(true);
    }
    
    @EventHandler
    public void onSpectatorBlockInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getClickedBlock() == null) {
            return;
        }
        if (!BeaconBattle.getAPI().getArenaUtil().isSpectating(playerInteractEvent.getPlayer())) {
            return;
        }
        if (playerInteractEvent.getClickedBlock().getType().toString().contains("DOOR")) {
            playerInteractEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onSpectatorInventoryClose(final InventoryCloseEvent inventoryCloseEvent) {
        TeleporterGUI.closeGUI((Player)inventoryCloseEvent.getPlayer());
    }
    
    @EventHandler
    public void onSpectatorClick(final InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getWhoClicked().getGameMode() == GameMode.SPECTATOR) {
            inventoryClickEvent.setCancelled(true);
            return;
        }
        if (inventoryClickEvent.getCurrentItem() == null) {
            return;
        }
        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();
        if (currentItem.getType() == Material.AIR) {
            return;
        }
        final Player player = (Player)inventoryClickEvent.getWhoClicked();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (!arenaByPlayer.isSpectator(player)) {
            return;
        }
        if (BeaconBattle.nms.isPlayerHead(currentItem.getType().toString(), 3) && BeaconBattle.nms.itemStackDataCompare(currentItem, (short)3) && BeaconBattle.nms.isCustomBeaconBattleItem(currentItem)) {
            inventoryClickEvent.setCancelled(true);
            final String customData = BeaconBattle.nms.getCustomData(currentItem);
            if (customData.contains("spectatorTeleporterGUIhead_")) {
                final Player player2 = Bukkit.getPlayer(customData.replace("spectatorTeleporterGUIhead_", ""));
                if (player2 == null) {
                    return;
                }
                if (player2.isDead()) {
                    return;
                }
                if (!player2.isOnline()) {
                    return;
                }
                final SpectatorTeleportToPlayerEvent spectatorTeleportToPlayerEvent = new SpectatorTeleportToPlayerEvent(player, player2, arenaByPlayer);
                Bukkit.getPluginManager().callEvent((Event)spectatorTeleportToPlayerEvent);
                if (!spectatorTeleportToPlayerEvent.isCancelled()) {
                    player.teleport((Entity)player2);
                }
                Sounds.playSound("spectator-gui-click", player);
                player.closeInventory();
            }
        }
    }
    
    @EventHandler
    public void onHealthChange(final EntityRegainHealthEvent entityRegainHealthEvent) {
        if (entityRegainHealthEvent.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        final Player player = (Player)entityRegainHealthEvent.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isPlayer(player)) {
            TeleporterGUI.refreshAllGUIs();
        }
    }
    
    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent foodLevelChangeEvent) {
        if (foodLevelChangeEvent.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        final Player player = (Player)foodLevelChangeEvent.getEntity();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isPlayer(player)) {
            TeleporterGUI.refreshAllGUIs();
        }
    }
    
    @EventHandler
    public void onPlayerLeave(final PlayerLeaveArenaEvent playerLeaveArenaEvent) {
        if (playerLeaveArenaEvent.getArena().isPlayer(playerLeaveArenaEvent.getPlayer())) {
            TeleporterGUI.refreshAllGUIs();
        }
    }
    
    @EventHandler
    public void onSpectatorInteractPlayer(final PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        if (playerInteractAtEntityEvent.getRightClicked().getType() != EntityType.PLAYER) {
            return;
        }
        final Player player = playerInteractAtEntityEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isPlayer(player)) {
            return;
        }
        playerInteractAtEntityEvent.setCancelled(true);
        final Player spectatorTarget = (Player)playerInteractAtEntityEvent.getRightClicked();
        if (arenaByPlayer.isPlayer(spectatorTarget)) {
            if (player.getSpectatorTarget() != null) {
                Bukkit.getPluginManager().callEvent((Event)new SpectatorFirstPersonLeaveEvent(player, arenaByPlayer, Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE)));
            }
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().setHeldItemSlot(5);
            player.setSpectatorTarget((Entity)spectatorTarget);
            final SpectatorFirstPersonEnterEvent spectatorFirstPersonEnterEvent = new SpectatorFirstPersonEnterEvent(player, spectatorTarget, arenaByPlayer, Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_TITLE).replace("{player}", spectatorTarget.getDisplayName()), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_ENTER_SUBTITLE));
            Bukkit.getPluginManager().callEvent((Event)spectatorFirstPersonEnterEvent);
            BeaconBattle.nms.sendTitle(player, spectatorFirstPersonEnterEvent.getTitle(), spectatorFirstPersonEnterEvent.getSubtitle(), 0, 30, 0);
        }
    }
    
    @EventHandler
    public void onSpectatorInteract(final PlayerInteractEntityEvent playerInteractEntityEvent) {
        final Player player = playerInteractEntityEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isPlayer(player)) {
            return;
        }
        playerInteractEntityEvent.setCancelled(true);
    }
    
    @EventHandler
    public void onSneak(final PlayerToggleSneakEvent playerToggleSneakEvent) {
        final Player player = playerToggleSneakEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isSpectator(player) && player.getSpectatorTarget() != null) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player, arenaByPlayer, Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent((Event)spectatorFirstPersonLeaveEvent);
            BeaconBattle.nms.sendTitle(player, spectatorFirstPersonLeaveEvent.getTitle(), spectatorFirstPersonLeaveEvent.getSubtitle(), 0, 30, 0);
        }
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerTeleportEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        if (arenaByPlayer.isSpectator(playerTeleportEvent.getPlayer()) && (playerTeleportEvent.getPlayer().getSpectatorTarget() != null || !playerTeleportEvent.getTo().getWorld().equals(playerTeleportEvent.getPlayer().getWorld())) && playerTeleportEvent.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            final Player player = playerTeleportEvent.getPlayer();
            playerTeleportEvent.setCancelled(true);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player, Arena.getArenaByPlayer(player), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent((Event)spectatorFirstPersonLeaveEvent);
            BeaconBattle.nms.sendTitle(player, spectatorFirstPersonLeaveEvent.getTitle(), spectatorFirstPersonLeaveEvent.getSubtitle(), 0, 30, 0);
        }
    }
    
    @EventHandler
    public void onTargetDeath(final PlayerKillEvent playerKillEvent) {
        for (final Player player : playerKillEvent.getArena().getSpectators()) {
            if (player.getSpectatorTarget() == null) {
                continue;
            }
            if (player.getSpectatorTarget() != playerKillEvent.getVictim()) {
                continue;
            }
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            final SpectatorFirstPersonLeaveEvent spectatorFirstPersonLeaveEvent = new SpectatorFirstPersonLeaveEvent(player, playerKillEvent.getArena(), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_TITLE), Language.getMsg(player, Messages.ARENA_SPECTATOR_FIRST_PERSON_LEAVE_SUBTITLE));
            Bukkit.getPluginManager().callEvent((Event)spectatorFirstPersonLeaveEvent);
            BeaconBattle.nms.sendTitle(player, spectatorFirstPersonLeaveEvent.getTitle(), spectatorFirstPersonLeaveEvent.getSubtitle(), 0, 30, 0);
        }
    }
    
    @EventHandler
    public void onDamageByEntity(final EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.isCancelled()) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(entityDamageByEntityEvent.getEntity().getWorld().getName());
        if (arenaByIdentifier == null) {
            return;
        }
        Player key = null;
        if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile)entityDamageByEntityEvent.getDamager()).getShooter();
            if (shooter instanceof Player) {
                key = (Player)shooter;
            }
        }
        else if (entityDamageByEntityEvent.getDamager() instanceof Player) {
            key = (Player)entityDamageByEntityEvent.getDamager();
            if (arenaByIdentifier.getRespawn().containsKey(key)) {
                entityDamageByEntityEvent.setCancelled(true);
                return;
            }
        }
        else if (entityDamageByEntityEvent.getDamager() instanceof TNTPrimed) {
            final TNTPrimed tntPrimed = (TNTPrimed)entityDamageByEntityEvent.getDamager();
            if (tntPrimed.getSource() instanceof Player) {
                key = (Player)tntPrimed.getSource();
            }
        }
        if (key == null) {
            return;
        }
        if (arenaByIdentifier.isSpectator(key)) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }
}
