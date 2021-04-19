

package ws.billy.bedwars.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.AutoCreateTeams;
import ws.billy.bedwars.api.arena.NextEvent;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.io.File;
import org.bukkit.event.block.SignChangeEvent;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerBedBreakEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Iterator;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.TNTPrimed;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.region.Region;
import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.Material;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.event.Listener;

public class BreakPlace implements Listener
{
    private static List<Player> buildSession;
    
    @EventHandler
    public void onIceMelt(final BlockFadeEvent blockFadeEvent) {
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && blockFadeEvent.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            blockFadeEvent.setCancelled(true);
            return;
        }
        if (blockFadeEvent.getBlock().getType() == Material.ICE && Arena.getArenaByIdentifier(blockFadeEvent.getBlock().getWorld().getName()) != null) {
            blockFadeEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onCactus(final BlockPhysicsEvent blockPhysicsEvent) {
        if (blockPhysicsEvent.getBlock().getType() == Material.CACTUS && Arena.getArenaByIdentifier(blockPhysicsEvent.getBlock().getWorld().getName()) != null) {
            blockPhysicsEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent blockPlaceEvent) {
        if (blockPlaceEvent.isCancelled()) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(blockPlaceEvent.getBlock().getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getStatus() != GameState.playing) {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        final Player player = blockPlaceEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            if (BeaconBattle.getServerType() == ServerType.MULTIARENA && blockPlaceEvent.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld()) && !isBuildSession(player)) {
                blockPlaceEvent.setCancelled(true);
            }
            return;
        }
        if (arenaByPlayer.isSpectator(player)) {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getRespawn().containsKey(player)) {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        if (arenaByPlayer.getStatus() != GameState.playing) {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        if (blockPlaceEvent.getBlockPlaced().getLocation().getBlockY() >= arenaByPlayer.getConfig().getInt("max-build-y")) {
            blockPlaceEvent.setCancelled(true);
            return;
        }
        for (final Region region : arenaByPlayer.getRegionsList()) {
            if (region.isInRegion(blockPlaceEvent.getBlock().getLocation()) && region.isProtected()) {
                blockPlaceEvent.setCancelled(true);
                player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                return;
            }
        }
        arenaByPlayer.addPlacedBlock(blockPlaceEvent.getBlock());
        if (blockPlaceEvent.getBlock().getType() == Material.TNT) {
            blockPlaceEvent.getBlockPlaced().setType(Material.AIR);
            final TNTPrimed tntPrimed = (TNTPrimed)blockPlaceEvent.getBlock().getLocation().getWorld().spawn(blockPlaceEvent.getBlock().getLocation().add(0.5, 0.0, 0.5), (Class)TNTPrimed.class);
            tntPrimed.setFuseTicks(45);
            BeaconBattle.nms.setSource(tntPrimed, player);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent playerInteractEvent) {
        final Player player = playerInteractEvent.getPlayer();
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && player.getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld()) && playerInteractEvent.getClickedBlock() != null && playerInteractEvent.getClickedBlock().getRelative(BlockFace.UP).getType() == Material.FIRE && !isBuildSession(player)) {
            playerInteractEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.isCancelled()) {
            return;
        }
        final Player player = blockBreakEvent.getPlayer();
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && blockBreakEvent.getBlock().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld()) && !isBuildSession(player)) {
            blockBreakEvent.setCancelled(true);
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (!arenaByPlayer.isPlayer(player)) {
                blockBreakEvent.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getRespawn().containsKey(player)) {
                blockBreakEvent.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getStatus() != GameState.playing) {
                blockBreakEvent.setCancelled(true);
                return;
            }
            if (BeaconBattle.nms.isBed(blockBreakEvent.getBlock().getType())) {
                for (final ITeam team : arenaByPlayer.getTeams()) {
                    for (int i = blockBreakEvent.getBlock().getX() - 2; i < blockBreakEvent.getBlock().getX() + 2; ++i) {
                        for (int j = blockBreakEvent.getBlock().getY() - 2; j < blockBreakEvent.getBlock().getY() + 2; ++j) {
                            int k = blockBreakEvent.getBlock().getZ() - 2;
                            while (k < blockBreakEvent.getBlock().getZ() + 2) {
                                if (team.getBed().getBlockX() == i && team.getBed().getBlockY() == j && team.getBed().getBlockZ() == k && !team.isBedDestroyed()) {
                                    if (team.isMember(player)) {
                                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_BREAK_OWN_BED));
                                        blockBreakEvent.setCancelled(true);
                                        if (blockBreakEvent.getPlayer().getLocation().getBlock().getType().toString().contains("BED")) {
                                            blockBreakEvent.getPlayer().teleport(blockBreakEvent.getPlayer().getLocation().add(0.0, 0.5, 0.0));
                                        }
                                        return;
                                    }
                                    blockBreakEvent.setCancelled(false);
                                    team.setBedDestroyed(true);
                                    arenaByPlayer.addPlayerBedDestroyed(player);
                                    Bukkit.getPluginManager().callEvent((Event)new PlayerBedBreakEvent(blockBreakEvent.getPlayer(), arenaByPlayer.getTeam(player), team, arenaByPlayer));
                                    for (final Player player2 : arenaByPlayer.getWorld().getPlayers()) {
                                        if (team.isMember(player2)) {
                                            player2.sendMessage(Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT_TO_VICTIM).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player2))).replace("{PlayerColor}", arenaByPlayer.getTeam(player).getColor().chat().toString()).replace("{PlayerName}", player.getDisplayName()));
                                            BeaconBattle.nms.sendTitle(player2, Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_TITLE_ANNOUNCEMENT), Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_SUBTITLE_ANNOUNCEMENT), 0, 25, 0);
                                        }
                                        else {
                                            player2.sendMessage(Language.getMsg(player2, Messages.INTERACT_BED_DESTROY_CHAT_ANNOUNCEMENT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player2))).replace("{PlayerColor}", arenaByPlayer.getTeam(player).getColor().chat().toString()).replace("{PlayerName}", player.getDisplayName()));
                                        }
                                        Sounds.playSound("bed-destroy", player2);
                                    }
                                    return;
                                }
                                else {
                                    ++k;
                                }
                            }
                        }
                    }
                }
            }
            for (final Region region : arenaByPlayer.getRegionsList()) {
                if (region.isInRegion(blockBreakEvent.getBlock().getLocation()) && region.isProtected()) {
                    blockBreakEvent.setCancelled(true);
                    player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_BREAK_BLOCK));
                    return;
                }
            }
            if (!arenaByPlayer.getConfig().getBoolean("allow-map-break")) {
                if (!arenaByPlayer.isBlockPlaced(blockBreakEvent.getBlock())) {
                    player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_BREAK_BLOCK));
                    blockBreakEvent.setCancelled(true);
                    return;
                }
                arenaByPlayer.removePlacedBlock(blockBreakEvent.getBlock());
            }
        }
    }
    
    @EventHandler
    public void onSignChange(final SignChangeEvent signChangeEvent) {
        if (signChangeEvent == null) {
            return;
        }
        final Player player = signChangeEvent.getPlayer();
        if (signChangeEvent.getLine(0).equalsIgnoreCase("[" + BeaconBattle.mainCmd + "]")) {
            final File file = new File(BeaconBattle.plugin.getDataFolder(), "/Arenas");
            boolean b = false;
            if (file.exists()) {
                for (final File file2 : file.listFiles()) {
                    if (file2.isFile() && file2.getName().contains(".yml") && signChangeEvent.getLine(1).equals(file2.getName().replace(".yml", ""))) {
                        b = true;
                    }
                }
                ArrayList<String> list;
                if (BeaconBattle.signs.getYml().get("locations") == null) {
                    list = new ArrayList<String>();
                }
                else {
                    list = new ArrayList<String>(BeaconBattle.signs.getYml().getStringList("locations"));
                }
                if (b) {
                    list.add(signChangeEvent.getLine(1) + "," + BeaconBattle.signs.stringLocationConfigFormat(signChangeEvent.getBlock().getLocation()));
                    BeaconBattle.signs.set("locations", list);
                }
                final IArena arenaByName = Arena.getArenaByName(signChangeEvent.getLine(1));
                if (arenaByName != null) {
                    player.sendMessage("§a\u25aa §7Sign saved for arena: " + signChangeEvent.getLine(1));
                    arenaByName.addSign(signChangeEvent.getBlock().getLocation());
                    final Sign sign = (Sign)signChangeEvent.getBlock().getState();
                    int n = 0;
                    final Iterator<String> iterator = BeaconBattle.signs.getList("format").iterator();
                    while (iterator.hasNext()) {
                        signChangeEvent.setLine(n, iterator.next().replace("[on]", String.valueOf(arenaByName.getPlayers().size())).replace("[max]", String.valueOf(arenaByName.getMaxPlayers())).replace("[arena]", arenaByName.getDisplayName()).replace("[status]", arenaByName.getDisplayStatus(Language.getDefaultLanguage())));
                        ++n;
                    }
                    sign.update(true);
                }
            }
            else {
                player.sendMessage("§c\u25aa §7You didn't set any arena yet!");
            }
        }
    }
    
    @EventHandler
    public void onBucketFill(final PlayerBucketFillEvent playerBucketFillEvent) {
        if (playerBucketFillEvent.isCancelled()) {
            return;
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && playerBucketFillEvent.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld()) && !isBuildSession(playerBucketFillEvent.getPlayer())) {
            playerBucketFillEvent.setCancelled(true);
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerBucketFillEvent.getPlayer());
        if (arenaByPlayer != null && (arenaByPlayer.isSpectator(playerBucketFillEvent.getPlayer()) || arenaByPlayer.getStatus() != GameState.playing || arenaByPlayer.getRespawn().containsKey(playerBucketFillEvent.getPlayer()))) {
            playerBucketFillEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        if (playerBucketEmptyEvent.isCancelled()) {
            return;
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && playerBucketEmptyEvent.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld()) && !isBuildSession(playerBucketEmptyEvent.getPlayer())) {
            playerBucketEmptyEvent.setCancelled(true);
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(playerBucketEmptyEvent.getBlockClicked().getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getStatus() != GameState.playing) {
            playerBucketEmptyEvent.setCancelled(true);
            return;
        }
        final Player player = playerBucketEmptyEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isSpectator(player)) {
                playerBucketEmptyEvent.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getRespawn().containsKey(player)) {
                playerBucketEmptyEvent.setCancelled(true);
                return;
            }
            if (arenaByPlayer.getStatus() != GameState.playing) {
                playerBucketEmptyEvent.setCancelled(true);
                return;
            }
            if (playerBucketEmptyEvent.getBlockClicked().getLocation().getBlockY() >= arenaByPlayer.getConfig().getInt("max-build-y")) {
                playerBucketEmptyEvent.setCancelled(true);
                return;
            }
            try {
                for (final ITeam team : arenaByPlayer.getTeams()) {
                    if (team.getSpawn().distance(playerBucketEmptyEvent.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("spawn-protection")) {
                        playerBucketEmptyEvent.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    if (team.getShop().distance(playerBucketEmptyEvent.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("shop-protection")) {
                        playerBucketEmptyEvent.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    if (team.getTeamUpgrades().distance(playerBucketEmptyEvent.getBlockClicked().getLocation()) <= arenaByPlayer.getConfig().getInt("upgrades-protection")) {
                        playerBucketEmptyEvent.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                    final Iterator<IGenerator> iterator2 = team.getGenerators().iterator();
                    while (iterator2.hasNext()) {
                        if (iterator2.next().getLocation().distance(playerBucketEmptyEvent.getBlockClicked().getLocation()) <= 1.0) {
                            playerBucketEmptyEvent.setCancelled(true);
                            player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                            return;
                        }
                    }
                }
                final Iterator<IGenerator> iterator3 = arenaByPlayer.getOreGenerators().iterator();
                while (iterator3.hasNext()) {
                    if (iterator3.next().getLocation().distance(playerBucketEmptyEvent.getBlockClicked().getLocation()) <= 1.0) {
                        playerBucketEmptyEvent.setCancelled(true);
                        player.sendMessage(Language.getMsg(player, Messages.INTERACT_CANNOT_PLACE_BLOCK));
                        return;
                    }
                }
            }
            catch (Exception ex) {}
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> BeaconBattle.nms.minusAmount(playerBucketEmptyEvent.getPlayer(), playerBucketEmptyEvent.getItemStack(), 1), 3L);
        }
    }
    
    @EventHandler
    public void onBlow(final EntityExplodeEvent entityExplodeEvent) {
        if (entityExplodeEvent.isCancelled()) {
            return;
        }
        if (entityExplodeEvent.blockList().isEmpty()) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(entityExplodeEvent.blockList().get(0).getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getNextEvent() != NextEvent.GAME_END) {
            for (final Block block : new ArrayList<Block>(entityExplodeEvent.blockList())) {
                if (!arenaByIdentifier.isBlockPlaced(block)) {
                    entityExplodeEvent.blockList().remove(block);
                }
                else {
                    if (!AutoCreateTeams.is13Higher() || !block.getType().toString().contains("_GLASS")) {
                        continue;
                    }
                    entityExplodeEvent.blockList().remove(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent blockExplodeEvent) {
        if (blockExplodeEvent.isCancelled()) {
            return;
        }
        if (blockExplodeEvent.blockList().isEmpty()) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(blockExplodeEvent.blockList().get(0).getWorld().getName());
        if (arenaByIdentifier != null && arenaByIdentifier.getNextEvent() != NextEvent.GAME_END) {
            for (final Block block : new ArrayList<Block>(blockExplodeEvent.blockList())) {
                if (!arenaByIdentifier.isBlockPlaced(block)) {
                    blockExplodeEvent.blockList().remove(block);
                }
                else {
                    if (!AutoCreateTeams.is13Higher() || !block.getType().toString().contains("_GLASS")) {
                        continue;
                    }
                    blockExplodeEvent.blockList().remove(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onPaintingRemove(final HangingBreakByEntityEvent hangingBreakByEntityEvent) {
        if (Arena.getArenaByIdentifier(hangingBreakByEntityEvent.getEntity().getWorld().getName()) == null) {
            if (BeaconBattle.getServerType() == ServerType.SHARED) {
                return;
            }
            if (!BeaconBattle.getLobbyWorld().equals(hangingBreakByEntityEvent.getEntity().getWorld().getName())) {
                return;
            }
        }
        if (hangingBreakByEntityEvent.getEntity().getType() == EntityType.PAINTING || hangingBreakByEntityEvent.getEntity().getType() == EntityType.ITEM_FRAME) {
            hangingBreakByEntityEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockCanBuildEvent(final BlockCanBuildEvent blockCanBuildEvent) {
        if (blockCanBuildEvent.isBuildable()) {
            return;
        }
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(blockCanBuildEvent.getBlock().getWorld().getName());
        if (arenaByIdentifier != null) {
            boolean b = false;
            for (final ITeam team : arenaByIdentifier.getTeams()) {
                for (int i = blockCanBuildEvent.getBlock().getX() - 1; i < blockCanBuildEvent.getBlock().getX() + 1; ++i) {
                    for (int j = blockCanBuildEvent.getBlock().getZ() - 1; j < blockCanBuildEvent.getBlock().getZ() + 1; ++j) {
                        if (team.getBed().getBlockX() == i && team.getBed().getBlockY() == blockCanBuildEvent.getBlock().getY() && team.getBed().getBlockZ() == j) {
                            blockCanBuildEvent.setBuildable(false);
                            b = true;
                            break;
                        }
                    }
                }
                if (team.getBed().getBlockX() == blockCanBuildEvent.getBlock().getX() && team.getBed().getBlockY() + 1 == blockCanBuildEvent.getBlock().getY() && team.getBed().getBlockZ() == blockCanBuildEvent.getBlock().getZ() && !b) {
                    blockCanBuildEvent.setBuildable(true);
                    break;
                }
            }
            if (b) {
                return;
            }
            final Object[] array = blockCanBuildEvent.getBlock().getWorld().getNearbyEntities(blockCanBuildEvent.getBlock().getLocation(), 1.0, 1.0, 1.0).stream().filter(entity -> entity.getType() == EntityType.PLAYER).toArray();
            for (int length = array.length, k = 0; k < length; ++k) {
                if (arenaByIdentifier.isSpectator((Player)array[k])) {
                    if (blockCanBuildEvent.getBlock().getType() == Material.AIR) {
                        blockCanBuildEvent.setBuildable(true);
                    }
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void soilChangeEntity(final EntityChangeBlockEvent entityChangeBlockEvent) {
        if (entityChangeBlockEvent.getTo() == Material.DIRT && (entityChangeBlockEvent.getBlock().getType().toString().equals("FARMLAND") || entityChangeBlockEvent.getBlock().getType().toString().equals("SOIL")) && (Arena.getArenaByIdentifier(entityChangeBlockEvent.getBlock().getWorld().getName()) != null || entityChangeBlockEvent.getBlock().getWorld().getName().equals(BeaconBattle.getLobbyWorld()))) {
            entityChangeBlockEvent.setCancelled(true);
        }
    }
    
    public static boolean isBuildSession(final Player player) {
        return BreakPlace.buildSession.contains(player);
    }
    
    public static void addBuildSession(final Player player) {
        BreakPlace.buildSession.add(player);
    }
    
    public static void removeBuildSession(final Player player) {
        BreakPlace.buildSession.remove(player);
    }
    
    static {
        BreakPlace.buildSession = new ArrayList<Player>();
    }
}
