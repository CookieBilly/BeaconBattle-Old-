

package ws.billy.bedwars.listeners;

import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.Location;
import ws.billy.bedwars.shop.listeners.InventoryListener;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Fireball;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.block.Sign;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.block.Block;
import org.bukkit.Material;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class Interact implements Listener
{
    @EventHandler
    public void onItemCommand(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent == null) {
            return;
        }
        final Player player = playerInteractEvent.getPlayer();
        if (player == null) {
            return;
        }
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            final ItemStack itemInHand = BeaconBattle.nms.getItemInHand(player);
            if (!BeaconBattle.nms.isCustomBeaconBattleItem(itemInHand)) {
                return;
            }
            final String[] split = BeaconBattle.nms.getCustomData(itemInHand).split("_");
            if (split.length >= 2 && split[0].equals("RUNCOMMAND")) {
                playerInteractEvent.setCancelled(true);
                Bukkit.dispatchCommand((CommandSender)player, split[1]);
            }
        }
    }
    
    @EventHandler
    public void onInventoryInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent == null) {
            return;
        }
        if (playerInteractEvent.isCancelled()) {
            return;
        }
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if ((BeaconBattle.getServerType() == ServerType.MULTIARENA && clickedBlock.getWorld().getName().equals(BeaconBattle.getLobbyWorld())) || Arena.getArenaByPlayer(playerInteractEvent.getPlayer()) != null) {
            if (clickedBlock.getType() == BeaconBattle.nms.materialCraftingTable() && BeaconBattle.config.getBoolean("inventories.disable-crafting-table")) {
                playerInteractEvent.setCancelled(true);
            }
            else if (clickedBlock.getType() == BeaconBattle.nms.materialEnchantingTable() && BeaconBattle.config.getBoolean("inventories.disable-enchanting-table")) {
                playerInteractEvent.setCancelled(true);
            }
            else if (clickedBlock.getType() == Material.FURNACE && BeaconBattle.config.getBoolean("inventories.disable-furnace")) {
                playerInteractEvent.setCancelled(true);
            }
            else if (clickedBlock.getType() == Material.BREWING_STAND && BeaconBattle.config.getBoolean("inventories.disable-brewing-stand")) {
                playerInteractEvent.setCancelled(true);
            }
            else if (clickedBlock.getType() == Material.ANVIL && BeaconBattle.config.getBoolean("inventories.disable-anvil")) {
                playerInteractEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent == null) {
            return;
        }
        final Player player = playerInteractEvent.getPlayer();
        if (player == null) {
            return;
        }
        Arena.afkCheck.remove(player.getUniqueId());
        if (BeaconBattle.getAPI().getAFKUtil().isPlayerAFK(playerInteractEvent.getPlayer())) {
            BeaconBattle.getAPI().getAFKUtil().setPlayerAFK(playerInteractEvent.getPlayer(), false);
        }
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block clickedBlock = playerInteractEvent.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }
            if (clickedBlock.getType() == Material.AIR) {
                return;
            }
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer != null) {
                if (arenaByPlayer.getRespawn().containsKey(playerInteractEvent.getPlayer())) {
                    playerInteractEvent.setCancelled(true);
                    return;
                }
                if (BeaconBattle.nms.isBed(clickedBlock.getType())) {
                    if (player.isSneaking()) {
                        final ItemStack itemInHand = BeaconBattle.nms.getItemInHand(player);
                        if (itemInHand == null) {
                            playerInteractEvent.setCancelled(true);
                        }
                        else if (itemInHand.getType() == Material.AIR) {
                            playerInteractEvent.setCancelled(true);
                        }
                    }
                    else {
                        playerInteractEvent.setCancelled(true);
                    }
                    return;
                }
                if (clickedBlock.getType() == Material.CHEST) {
                    if (arenaByPlayer.isSpectator(player) || arenaByPlayer.getRespawn().containsKey(player)) {
                        playerInteractEvent.setCancelled(true);
                        return;
                    }
                    ITeam team = null;
                    final int int1 = arenaByPlayer.getConfig().getInt("island-radius");
                    for (final ITeam team2 : arenaByPlayer.getTeams()) {
                        if (team2.getSpawn().distance(playerInteractEvent.getClickedBlock().getLocation()) <= int1) {
                            team = team2;
                        }
                    }
                    if (team != null && team.getMembers().isEmpty()) {
                        playerInteractEvent.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CHEST_CANT_OPEN_TEAM_ELIMINATED));
                    }
                }
                if (arenaByPlayer.isSpectator(player) || arenaByPlayer.getRespawn().containsKey(player)) {
                    switch (clickedBlock.getType()) {
                        case CHEST:
                        case ENDER_CHEST:
                        case ANVIL:
                        case WORKBENCH:
                        case HOPPER: {
                            playerInteractEvent.setCancelled(true);
                            break;
                        }
                    }
                }
            }
            if (clickedBlock.getState() instanceof Sign) {
                for (final IArena arena : Arena.getArenas()) {
                    if (arena.getSigns().contains(clickedBlock)) {
                        if (arena.addPlayer(player, false)) {
                            Sounds.playSound("join-allowed", player);
                        }
                        else {
                            Sounds.playSound("join-denied", player);
                        }
                        return;
                    }
                }
            }
        }
        final ItemStack item = playerInteractEvent.getItem();
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            if (item == null) {
                return;
            }
            final IArena arenaByPlayer2 = Arena.getArenaByPlayer(player);
            if (arenaByPlayer2 != null && arenaByPlayer2.isPlayer(player) && item.getType() == BeaconBattle.nms.materialFireball()) {
                playerInteractEvent.setCancelled(true);
                final Fireball fireball = (Fireball)player.launchProjectile((Class)Fireball.class);
                fireball.setMetadata("bw1058", (MetadataValue)new FixedMetadataValue((Plugin)BeaconBattle.plugin, (Object)"ceva"));
                fireball.setIsIncendiary(false);
                for (final ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack != null) {
                        if (itemStack.getType() != null) {
                            if (itemStack.getType() != Material.AIR) {
                                if (itemStack.getType() == BeaconBattle.nms.materialFireball()) {
                                    BeaconBattle.nms.minusAmount(player, playerInteractEvent.getItem(), 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void disableItemFrameRotation(final PlayerInteractEntityEvent playerInteractEntityEvent) {
        if (playerInteractEntityEvent == null) {
            return;
        }
        if (playerInteractEntityEvent.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            if (((ItemFrame)playerInteractEntityEvent.getRightClicked()).getItem().getType().equals((Object)Material.AIR)) {
                final ItemStack itemInHand = BeaconBattle.nms.getItemInHand(playerInteractEntityEvent.getPlayer());
                if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                    final ShopCache shopCache = ShopCache.getShopCache(playerInteractEntityEvent.getPlayer().getUniqueId());
                    if (shopCache != null && InventoryListener.isUpgradable(itemInHand, shopCache)) {
                        playerInteractEntityEvent.setCancelled(true);
                    }
                }
                return;
            }
            if (Arena.getArenaByIdentifier(playerInteractEntityEvent.getPlayer().getWorld().getName()) != null) {
                playerInteractEntityEvent.setCancelled(true);
            }
            if (BeaconBattle.getServerType() == ServerType.MULTIARENA && BeaconBattle.getLobbyWorld().equals(playerInteractEntityEvent.getPlayer().getWorld().getName())) {
                playerInteractEntityEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityInteract(final PlayerInteractEntityEvent playerInteractEntityEvent) {
        if (playerInteractEntityEvent == null) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerInteractEntityEvent.getPlayer());
        if (arenaByPlayer == null) {
            return;
        }
        final Location location = playerInteractEntityEvent.getRightClicked().getLocation();
        for (final ITeam team : arenaByPlayer.getTeams()) {
            final Location shop = team.getShop();
            final Location teamUpgrades = team.getTeamUpgrades();
            if (location.getBlockX() == shop.getBlockX() && location.getBlockY() == shop.getBlockY() && location.getBlockZ() == shop.getBlockZ()) {
                playerInteractEntityEvent.setCancelled(true);
            }
            else {
                if (location.getBlockX() != teamUpgrades.getBlockX() || location.getBlockY() != teamUpgrades.getBlockY() || location.getBlockZ() != teamUpgrades.getBlockZ()) {
                    continue;
                }
                playerInteractEntityEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent playerBedEnterEvent) {
        if (playerBedEnterEvent == null) {
            return;
        }
        if (Arena.getArenaByPlayer(playerBedEnterEvent.getPlayer()) != null) {
            playerBedEnterEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onArmorManipulate(final PlayerArmorStandManipulateEvent playerArmorStandManipulateEvent) {
        if (playerArmorStandManipulateEvent == null) {
            return;
        }
        if (playerArmorStandManipulateEvent.isCancelled()) {
            return;
        }
        if (Arena.getArenaByPlayer(playerArmorStandManipulateEvent.getPlayer()) != null) {
            playerArmorStandManipulateEvent.setCancelled(true);
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && playerArmorStandManipulateEvent.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            playerArmorStandManipulateEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCrafting(final PrepareItemCraftEvent prepareItemCraftEvent) {
        if (prepareItemCraftEvent == null) {
            return;
        }
        if (Arena.getArenaByPlayer((Player)prepareItemCraftEvent.getView().getPlayer()) != null && BeaconBattle.config.getBoolean("inventories.disable-crafting-table")) {
            prepareItemCraftEvent.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
