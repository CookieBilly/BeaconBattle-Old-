

package ws.billy.bedwars.listeners;

import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventPriority;
import ws.billy.bedwars.api.arena.shop.ShopHolo;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.entity.EntityType;
import ws.billy.bedwars.api.events.player.PlayerKillEvent;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.Iterator;
import org.bukkit.projectiles.ProjectileSource;
import ws.billy.bedwars.api.entity.Despawnable;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.arena.LastHit;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.BeaconBattleTeam;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.Listener;

public class DamageDeathMove implements Listener
{
    @EventHandler
    public void onDamage(final EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            final Player player = (Player)entityDamageEvent.getEntity();
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer != null) {
                if (arenaByPlayer.isSpectator(player)) {
                    entityDamageEvent.setCancelled(true);
                    return;
                }
                if (arenaByPlayer.isRespawning(player)) {
                    entityDamageEvent.setCancelled(true);
                    return;
                }
                if (arenaByPlayer.getStatus() != GameState.playing) {
                    entityDamageEvent.setCancelled(true);
                    return;
                }
                if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    entityDamageEvent.setDamage(1.0);
                    return;
                }
                if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL && BeaconBattleTeam.antiFallDamageAtRespawn.containsKey(player.getUniqueId())) {
                    if (BeaconBattleTeam.antiFallDamageAtRespawn.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        entityDamageEvent.setCancelled(true);
                    }
                    else {
                        BeaconBattleTeam.antiFallDamageAtRespawn.remove(player.getUniqueId());
                    }
                }
            }
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && entityDamageEvent.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            entityDamageEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamageByEntity(final EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getEntity() instanceof Player) {
            final Player key = (Player)entityDamageByEntityEvent.getEntity();
            final IArena arenaByPlayer = Arena.getArenaByPlayer(key);
            if (arenaByPlayer != null) {
                if (arenaByPlayer.getStatus() != GameState.playing) {
                    entityDamageByEntityEvent.setCancelled(true);
                    return;
                }
                if (arenaByPlayer.isSpectator(key) || arenaByPlayer.isRespawning(key)) {
                    entityDamageByEntityEvent.setCancelled(true);
                    return;
                }
                Object o = null;
                if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    o = entityDamageByEntityEvent.getDamager();
                }
                else if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
                    final ProjectileSource shooter = ((Projectile)entityDamageByEntityEvent.getDamager()).getShooter();
                    if (!(shooter instanceof Player)) {
                        return;
                    }
                    o = shooter;
                }
                else if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    o = entityDamageByEntityEvent.getDamager();
                    if (arenaByPlayer.getRespawn().containsKey(o)) {
                        entityDamageByEntityEvent.setCancelled(true);
                        return;
                    }
                }
                else if (entityDamageByEntityEvent.getDamager() instanceof TNTPrimed) {
                    final TNTPrimed tntPrimed = (TNTPrimed)entityDamageByEntityEvent.getDamager();
                    if (!(tntPrimed.getSource() instanceof Player)) {
                        return;
                    }
                    o = tntPrimed.getSource();
                }
                else if (entityDamageByEntityEvent.getDamager() instanceof Silverfish || entityDamageByEntityEvent.getDamager() instanceof IronGolem) {
                    o = null;
                    final LastHit lastHit = LastHit.getLastHit(key);
                    if (lastHit != null) {
                        lastHit.setDamager(entityDamageByEntityEvent.getDamager());
                        lastHit.setTime(System.currentTimeMillis());
                    }
                    else {
                        new LastHit(key, entityDamageByEntityEvent.getDamager(), System.currentTimeMillis());
                    }
                }
                if (o != null) {
                    if (arenaByPlayer.isSpectator((Player)o) || arenaByPlayer.isReSpawning(((Player)o).getUniqueId())) {
                        entityDamageByEntityEvent.setCancelled(true);
                        return;
                    }
                    if (arenaByPlayer.getTeam(key).equals(arenaByPlayer.getTeam((Player)o))) {
                        if (!(entityDamageByEntityEvent.getDamager() instanceof TNTPrimed)) {
                            entityDamageByEntityEvent.setCancelled(true);
                        }
                        return;
                    }
                    final LastHit lastHit2 = LastHit.getLastHit(key);
                    if (lastHit2 != null) {
                        lastHit2.setDamager((Entity)o);
                        lastHit2.setTime(System.currentTimeMillis());
                    }
                    else {
                        new LastHit(key, (Entity)o, System.currentTimeMillis());
                    }
                    if (arenaByPlayer.getShowTime().containsKey(key)) {
                        final Iterator iterator = arenaByPlayer.getWorld().getPlayers().iterator();
                        while (iterator.hasNext()) {
                            BeaconBattle.nms.showArmor(key, iterator.next());
                        }
                    }
                }
            }
        }
        else if (BeaconBattle.nms.isDespawnable(entityDamageByEntityEvent.getEntity())) {
            Player player;
            if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                player = (Player)entityDamageByEntityEvent.getDamager();
            }
            else if (entityDamageByEntityEvent.getDamager() instanceof Projectile) {
                player = (Player)((Projectile)entityDamageByEntityEvent.getDamager()).getShooter();
            }
            else {
                if (!(entityDamageByEntityEvent.getDamager() instanceof TNTPrimed)) {
                    return;
                }
                final TNTPrimed tntPrimed2 = (TNTPrimed)entityDamageByEntityEvent.getDamager();
                if (!(tntPrimed2.getSource() instanceof Player)) {
                    return;
                }
                player = (Player)tntPrimed2.getSource();
            }
            final IArena arenaByPlayer2 = Arena.getArenaByPlayer(player);
            if (arenaByPlayer2 != null) {
                if (arenaByPlayer2.isPlayer(player)) {
                    if (arenaByPlayer2.getTeam(player) == BeaconBattle.nms.getDespawnablesList().get(entityDamageByEntityEvent.getEntity().getUniqueId()).getTeam()) {
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                }
                else {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && entityDamageByEntityEvent.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent playerDeathEvent) {
        playerDeathEvent.setDeathMessage((String)null);
        final Player entity = playerDeathEvent.getEntity();
        Player killer = playerDeathEvent.getEntity().getKiller();
        ITeam team = null;
        final IArena arenaByPlayer = Arena.getArenaByPlayer(entity);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isSpectator(entity)) {
                entity.spigot().respawn();
                return;
            }
            if (arenaByPlayer.getStatus() != GameState.playing) {
                entity.spigot().respawn();
                return;
            }
            final EntityDamageEvent lastDamageCause = playerDeathEvent.getEntity().getLastDamageCause();
            final ItemStack[] contents = entity.getInventory().getContents();
            if (!arenaByPlayer.getConfig().getBoolean("vanilla-death-drops")) {
                playerDeathEvent.getDrops().clear();
            }
            final ITeam team2 = arenaByPlayer.getTeam(entity);
            if (arenaByPlayer.getStatus() != GameState.playing) {
                entity.spigot().respawn();
                return;
            }
            if (team2 == null) {
                entity.spigot().respawn();
                return;
            }
            String s = team2.isBedDestroyed() ? Messages.PLAYER_DIE_UNKNOWN_REASON_FINAL_KILL : Messages.PLAYER_DIE_UNKNOWN_REASON_REGULAR;
            PlayerKillEvent.PlayerKillCause playerKillCause = team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.UNKNOWN_FINAL_KILL : PlayerKillEvent.PlayerKillCause.UNKNOWN;
            if (lastDamageCause != null) {
                if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    final LastHit lastHit = LastHit.getLastHit(entity);
                    if (lastHit != null && lastHit.getTime() >= System.currentTimeMillis() - 15000L) {
                        if (lastHit.getDamager() instanceof Player) {
                            killer = (Player)lastHit.getDamager();
                        }
                        if (killer != null && killer.getUniqueId().equals(entity.getUniqueId())) {
                            killer = null;
                        }
                    }
                    if (killer == null) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR);
                    }
                    else if (killer != entity) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITH_SOURCE_REGULAR_KILL);
                    }
                    else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_FINAL_KILL : Messages.PLAYER_DIE_EXPLOSION_WITHOUT_SOURCE_REGULAR);
                    }
                    playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.EXPLOSION_FINAL_KILL : PlayerKillEvent.PlayerKillCause.EXPLOSION);
                }
                else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    final LastHit lastHit2 = LastHit.getLastHit(entity);
                    if (lastHit2 != null && lastHit2.getTime() >= System.currentTimeMillis() - 15000L) {
                        if (lastHit2.getDamager() instanceof Player) {
                            killer = (Player)lastHit2.getDamager();
                        }
                        if (killer != null && killer.getUniqueId().equals(entity.getUniqueId())) {
                            killer = null;
                        }
                    }
                    if (killer == null) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL : Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL);
                    }
                    else if (killer != entity) {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_KNOCKED_IN_VOID_FINAL_KILL : Messages.PLAYER_DIE_KNOCKED_IN_VOID_REGULAR_KILL);
                    }
                    else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_VOID_FALL_FINAL_KILL : Messages.PLAYER_DIE_VOID_FALL_REGULAR_KILL);
                    }
                    playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.VOID_FINAL_KILL : PlayerKillEvent.PlayerKillCause.VOID);
                }
                else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    if (killer == null) {
                        final LastHit lastHit3 = LastHit.getLastHit(entity);
                        if (lastHit3 != null && lastHit3.getTime() >= System.currentTimeMillis() - 15000L && BeaconBattle.nms.isDespawnable(lastHit3.getDamager())) {
                            final Despawnable despawnable = BeaconBattle.nms.getDespawnablesList().get(lastHit3.getDamager().getUniqueId());
                            team = despawnable.getTeam();
                            s = ((despawnable.getEntity().getType() == EntityType.IRON_GOLEM) ? (team2.isBedDestroyed() ? Messages.PLAYER_DIE_IRON_GOLEM_FINAL_KILL : Messages.PLAYER_DIE_IRON_GOLEM_REGULAR) : (team2.isBedDestroyed() ? Messages.PLAYER_DIE_DEBUG_FINAL_KILL : Messages.PLAYER_DIE_DEBUG_REGULAR));
                            playerKillCause = (team2.isBedDestroyed() ? despawnable.getDeathFinalCause() : despawnable.getDeathRegularCause());
                            killer = null;
                        }
                    }
                    else {
                        s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_PVP_FINAL_KILL : Messages.PLAYER_DIE_PVP_REGULAR_KILL);
                        playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.PVP_FINAL_KILL : PlayerKillEvent.PlayerKillCause.PVP);
                    }
                }
                else if (lastDamageCause.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && killer != null) {
                    s = (team2.isBedDestroyed() ? Messages.PLAYER_DIE_SHOOT_FINAL_KILL : Messages.PLAYER_DIE_SHOOT_REGULAR);
                    playerKillCause = (team2.isBedDestroyed() ? PlayerKillEvent.PlayerKillCause.PLAYER_SHOOT_FINAL_KILL : PlayerKillEvent.PlayerKillCause.PLAYER_SHOOT);
                }
            }
            if (killer != null) {
                team = arenaByPlayer.getTeam(killer);
            }
            for (final Player player : arenaByPlayer.getPlayers()) {
                player.sendMessage(Language.getMsg(player, s).replace("{PlayerColor}", team2.getColor().chat().toString()).replace("{PlayerName}", entity.getDisplayName()).replace("{KillerColor}", (team == null) ? "" : team.getColor().chat().toString()).replace("{KillerName}", (killer == null) ? "" : killer.getDisplayName()).replace("{KillerTeamName}", (team == null) ? "" : team.getDisplayName(Language.getPlayerLanguage(player))));
            }
            for (final Player player2 : arenaByPlayer.getSpectators()) {
                player2.sendMessage(Language.getMsg(player2, s).replace("{PlayerColor}", team2.getColor().chat().toString()).replace("{PlayerName}", entity.getDisplayName()).replace("{KillerColor}", (team == null) ? "" : team.getColor().chat().toString()).replace("{KillerName}", (killer == null) ? "" : killer.getDisplayName()).replace("{KillerTeamName}", (team == null) ? "" : team.getDisplayName(Language.getPlayerLanguage(player2))));
            }
            if (killer != null) {
                if (team2.isBedDestroyed()) {
                    if (!arenaByPlayer.getConfig().getBoolean("vanilla-death-drops")) {
                        for (final ItemStack itemStack : contents) {
                            if (itemStack != null) {
                                if (itemStack.getType() != Material.AIR) {
                                    if (!BeaconBattle.nms.isArmor(itemStack) && !BeaconBattle.nms.isBow(itemStack) && !BeaconBattle.nms.isSword(itemStack)) {
                                        if (!BeaconBattle.nms.isTool(itemStack)) {
                                            if (BeaconBattle.nms.getShopUpgradeIdentifier(itemStack).trim().isEmpty()) {
                                                if (arenaByPlayer.getTeam(killer) != null) {
                                                    final Vector killDropsLocation = team2.getKillDropsLocation();
                                                    killer.getWorld().dropItemNaturally(new Location(arenaByPlayer.getWorld(), killDropsLocation.getX(), killDropsLocation.getY(), killDropsLocation.getZ()), itemStack);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    arenaByPlayer.addPlayerKill(killer, true, entity);
                }
                else {
                    if (!arenaByPlayer.getConfig().getBoolean("vanilla-death-drops") && !arenaByPlayer.getRespawn().containsKey(killer)) {
                        for (final ItemStack itemStack2 : contents) {
                            if (itemStack2 != null) {
                                if (itemStack2.getType() != Material.AIR) {
                                    if (itemStack2.getType() == Material.DIAMOND || itemStack2.getType() == Material.EMERALD || itemStack2.getType() == Material.IRON_INGOT || itemStack2.getType() == Material.GOLD_INGOT) {
                                        killer.getInventory().addItem(new ItemStack[] { itemStack2 });
                                        String s2 = "";
                                        final int amount = itemStack2.getAmount();
                                        switch (itemStack2.getType()) {
                                            case DIAMOND: {
                                                s2 = Language.getMsg(killer, Messages.PLAYER_DIE_REWARD_DIAMOND).replace("{meaning}", (amount == 1) ? Language.getMsg(killer, Messages.MEANING_DIAMOND_SINGULAR) : Language.getMsg(killer, Messages.MEANING_DIAMOND_PLURAL));
                                                break;
                                            }
                                            case EMERALD: {
                                                s2 = Language.getMsg(killer, Messages.PLAYER_DIE_REWARD_EMERALD).replace("{meaning}", (amount == 1) ? Language.getMsg(killer, Messages.MEANING_EMERALD_SINGULAR) : Language.getMsg(killer, Messages.MEANING_EMERALD_PLURAL));
                                                break;
                                            }
                                            case IRON_INGOT: {
                                                s2 = Language.getMsg(killer, Messages.PLAYER_DIE_REWARD_IRON).replace("{meaning}", (amount == 1) ? Language.getMsg(killer, Messages.MEANING_IRON_SINGULAR) : Language.getMsg(killer, Messages.MEANING_IRON_PLURAL));
                                                break;
                                            }
                                            case GOLD_INGOT: {
                                                s2 = Language.getMsg(killer, Messages.PLAYER_DIE_REWARD_GOLD).replace("{meaning}", (amount == 1) ? Language.getMsg(killer, Messages.MEANING_GOLD_SINGULAR) : Language.getMsg(killer, Messages.MEANING_GOLD_PLURAL));
                                                break;
                                            }
                                        }
                                        killer.sendMessage(s2.replace("{amount}", String.valueOf(amount)));
                                    }
                                }
                            }
                        }
                    }
                    arenaByPlayer.addPlayerKill(killer, false, entity);
                }
            }
            Bukkit.getPluginManager().callEvent((Event)new PlayerKillEvent(arenaByPlayer, entity, killer, s, playerKillCause));
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> entity.spigot().respawn(), 3L);
            arenaByPlayer.addPlayerDeath(entity);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(final PlayerRespawnEvent playerRespawnEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerRespawnEvent.getPlayer());
        if (arenaByPlayer == null) {
            if (SetupSession.getSession(playerRespawnEvent.getPlayer().getUniqueId()) != null) {
                playerRespawnEvent.setRespawnLocation(playerRespawnEvent.getPlayer().getWorld().getSpawnLocation());
            }
        }
        else {
            if (arenaByPlayer.isSpectator(playerRespawnEvent.getPlayer())) {
                playerRespawnEvent.setRespawnLocation(arenaByPlayer.getConfig().getArenaLoc("waiting.Loc"));
                final String iso = Language.getPlayerLanguage(playerRespawnEvent.getPlayer()).getIso();
                final Iterator<IGenerator> iterator = arenaByPlayer.getOreGenerators().iterator();
                while (iterator.hasNext()) {
                    iterator.next().updateHolograms(playerRespawnEvent.getPlayer(), iso);
                }
                final Iterator<ITeam> iterator2 = arenaByPlayer.getTeams().iterator();
                while (iterator2.hasNext()) {
                    final Iterator<IGenerator> iterator3 = iterator2.next().getGenerators().iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().updateHolograms(playerRespawnEvent.getPlayer(), iso);
                    }
                }
                for (final ShopHolo shopHolo : ShopHolo.getShopHolo()) {
                    if (shopHolo.getA() == arenaByPlayer) {
                        shopHolo.updateForPlayer(playerRespawnEvent.getPlayer(), iso);
                    }
                }
                arenaByPlayer.sendSpectatorCommandItems(playerRespawnEvent.getPlayer());
                return;
            }
            playerRespawnEvent.setRespawnLocation(arenaByPlayer.getConfig().getArenaLoc("waiting.Loc"));
            final ITeam team = arenaByPlayer.getTeam(playerRespawnEvent.getPlayer());
            if (team == null) {
                BeaconBattle.plugin.getLogger().severe(playerRespawnEvent.getPlayer().getName() + " re-spawn error on " + arenaByPlayer.getArenaName() + "[" + arenaByPlayer.getWorldName() + "] because the team was NULL and he was not spectating!");
                BeaconBattle.plugin.getLogger().severe("This is caused by one of your plugins: remove or configure any re-spawn related plugins.");
                arenaByPlayer.removePlayer(playerRespawnEvent.getPlayer(), false);
                arenaByPlayer.removeSpectator(playerRespawnEvent.getPlayer(), false);
                return;
            }
            if (team.isBedDestroyed()) {
                arenaByPlayer.addSpectator(playerRespawnEvent.getPlayer(), true, null);
                team.getMembers().remove(playerRespawnEvent.getPlayer());
                playerRespawnEvent.getPlayer().sendMessage(Language.getMsg(playerRespawnEvent.getPlayer(), Messages.PLAYER_DIE_ELIMINATED_CHAT));
                if (team.getMembers().isEmpty()) {
                    for (final Player player3 : arenaByPlayer.getWorld().getPlayers()) {
                        player3.sendMessage(Language.getMsg(player3, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                    }
                    Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, arenaByPlayer::checkWinner, 40L);
                }
            }
            else {
                playerRespawnEvent.getPlayer().getInventory().clear();
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> arenaByPlayer.getPlayers().forEach(player -> BeaconBattle.nms.hidePlayer(playerRespawnEvent.getPlayer(), player)), 10L);
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> arenaByPlayer.getSpectators().forEach(player2 -> BeaconBattle.nms.hidePlayer(playerRespawnEvent.getPlayer(), player2)), 10L);
                BeaconBattle.nms.setCollide(playerRespawnEvent.getPlayer(), arenaByPlayer, false);
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                    playerRespawnEvent.getPlayer().setAllowFlight(true);
                    playerRespawnEvent.getPlayer().setFlying(true);
                    return;
                }, 10L);
                arenaByPlayer.getRespawn().put(playerRespawnEvent.getPlayer(), 5);
                if (!BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets")) {
                    final Iterator<Player> iterator6;
                    Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                        arenaByPlayer.getShowTime().keySet().iterator();
                        while (iterator6.hasNext()) {
                            BeaconBattle.nms.hideArmor(iterator6.next(), playerRespawnEvent.getPlayer());
                        }
                        return;
                    }, 10L);
                }
            }
            final Iterator<Player> iterator7 = arenaByPlayer.getSpectators().iterator();
            while (iterator7.hasNext()) {
                arenaByPlayer.updateSpectatorCollideRule(iterator7.next(), false);
            }
        }
    }
    
    @EventHandler
    public void onMove(final PlayerMoveEvent playerMoveEvent) {
        if (Arena.isInArena(playerMoveEvent.getPlayer())) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(playerMoveEvent.getPlayer());
            if (playerMoveEvent.getFrom().getChunk() != playerMoveEvent.getTo().getChunk()) {
                final String iso = Language.getPlayerLanguage(playerMoveEvent.getPlayer()).getIso();
                final Iterator<IGenerator> iterator = arenaByPlayer.getOreGenerators().iterator();
                while (iterator.hasNext()) {
                    iterator.next().updateHolograms(playerMoveEvent.getPlayer(), iso);
                }
                final Iterator<ITeam> iterator2 = arenaByPlayer.getTeams().iterator();
                while (iterator2.hasNext()) {
                    final Iterator<IGenerator> iterator3 = iterator2.next().getGenerators().iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().updateHolograms(playerMoveEvent.getPlayer(), iso);
                    }
                }
                for (final ShopHolo shopHolo : ShopHolo.getShopHolo()) {
                    if (shopHolo.getA() == arenaByPlayer) {
                        shopHolo.updateForPlayer(playerMoveEvent.getPlayer(), iso);
                    }
                }
                if (arenaByPlayer.getRespawn().containsKey(playerMoveEvent.getPlayer())) {
                    for (final Player player : arenaByPlayer.getPlayers()) {
                        if (player == playerMoveEvent.getPlayer()) {
                            continue;
                        }
                        BeaconBattle.nms.hidePlayer(playerMoveEvent.getPlayer(), player);
                    }
                }
                else {
                    for (final Player key : arenaByPlayer.getPlayers()) {
                        if (arenaByPlayer.getRespawn().containsKey(key)) {
                            BeaconBattle.nms.hidePlayer(playerMoveEvent.getPlayer(), key);
                        }
                    }
                }
            }
            if (arenaByPlayer.isSpectator(playerMoveEvent.getPlayer()) || arenaByPlayer.isRespawning(playerMoveEvent.getPlayer())) {
                if (playerMoveEvent.getTo().getY() < 0.0) {
                    playerMoveEvent.getPlayer().teleport(arenaByPlayer.getConfig().getArenaLoc("waiting.Loc"));
                }
            }
            else {
                if (playerMoveEvent.getPlayer().getLocation().getY() <= 0.0) {
                    if (arenaByPlayer.getStatus() == GameState.playing) {
                        if (arenaByPlayer.getConfig().getBoolean("voidKill")) {
                            BeaconBattle.nms.voidKill(playerMoveEvent.getPlayer());
                        }
                    }
                    else {
                        final ITeam team = arenaByPlayer.getTeam(playerMoveEvent.getPlayer());
                        if (team != null) {
                            playerMoveEvent.getPlayer().teleport(team.getSpawn());
                        }
                        else {
                            playerMoveEvent.getPlayer().teleport(arenaByPlayer.getConfig().getArenaLoc("waiting.Loc"));
                        }
                    }
                }
                if (arenaByPlayer.getStatus() == GameState.playing) {
                    for (final ITeam team2 : arenaByPlayer.getTeams()) {
                        if (playerMoveEvent.getPlayer().getLocation().distance(team2.getBed()) < 4.0) {
                            if (!team2.isMember(playerMoveEvent.getPlayer()) || !(team2 instanceof BeaconBattleTeam)) {
                                continue;
                            }
                            if (((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()) == null) {
                                continue;
                            }
                            if (((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()).isHidden()) {
                                continue;
                            }
                            ((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()).hide();
                        }
                        else {
                            if (!team2.isMember(playerMoveEvent.getPlayer()) || !(team2 instanceof BeaconBattleTeam)) {
                                continue;
                            }
                            if (((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()) == null) {
                                continue;
                            }
                            if (!((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()).isHidden()) {
                                continue;
                            }
                            ((BeaconBattleTeam)team2).getBedHolo(playerMoveEvent.getPlayer()).show();
                        }
                    }
                    if (playerMoveEvent.getFrom() != playerMoveEvent.getTo()) {
                        Arena.afkCheck.remove(playerMoveEvent.getPlayer().getUniqueId());
                        if (BeaconBattle.getAPI().getAFKUtil().isPlayerAFK(playerMoveEvent.getPlayer())) {
                            BeaconBattle.getAPI().getAFKUtil().setPlayerAFK(playerMoveEvent.getPlayer(), false);
                        }
                    }
                }
            }
        }
        else if (playerMoveEvent.getPlayer().getWorld().getName().equalsIgnoreCase(BeaconBattle.config.getLobbyWorldName()) && BeaconBattle.getServerType() == ServerType.MULTIARENA && playerMoveEvent.getTo().getY() < 0.0) {
            playerMoveEvent.getPlayer().teleport(BeaconBattle.config.getConfigLoc("lobbyLoc"));
        }
    }
    
    @EventHandler
    public void onProjHit(final ProjectileHitEvent projectileHitEvent) {
        final Projectile entity = projectileHitEvent.getEntity();
        if (projectileHitEvent.getEntity().getShooter() instanceof Player) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer((Player)projectileHitEvent.getEntity().getShooter());
            if (arenaByPlayer != null) {
                if (!arenaByPlayer.isPlayer((Player)projectileHitEvent.getEntity().getShooter())) {
                    return;
                }
                if (projectileHitEvent.getEntity() instanceof Fireball) {
                    final Location location = projectileHitEvent.getEntity().getLocation();
                    projectileHitEvent.getEntity().getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 3.0f, false, true);
                    return;
                }
                String s = "";
                if (entity instanceof Snowball) {
                    s = "silverfish";
                }
                if (!s.isEmpty()) {
                    spawnUtility(s, projectileHitEvent.getEntity().getLocation(), arenaByPlayer.getTeam((Player)projectileHitEvent.getEntity().getShooter()), (Player)projectileHitEvent.getEntity().getShooter());
                }
            }
        }
    }
    
    @EventHandler
    public void onItemFrameDamage(final EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getEntity().getType() == EntityType.ITEM_FRAME) {
            if (Arena.getArenaByIdentifier(entityDamageByEntityEvent.getEntity().getWorld().getName()) != null) {
                entityDamageByEntityEvent.setCancelled(true);
            }
            if (BeaconBattle.getServerType() == ServerType.MULTIARENA && BeaconBattle.getLobbyWorld().equals(entityDamageByEntityEvent.getEntity().getWorld().getName())) {
                entityDamageByEntityEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDeath(final EntityDeathEvent entityDeathEvent) {
        if (Arena.getArenaByIdentifier(entityDeathEvent.getEntity().getLocation().getWorld().getName()) != null && (entityDeathEvent.getEntityType() == EntityType.IRON_GOLEM || entityDeathEvent.getEntityType() == EntityType.SILVERFISH)) {
            entityDeathEvent.getDrops().clear();
            entityDeathEvent.setDroppedExp(0);
        }
    }
    
    @EventHandler
    public void onEat(final PlayerItemConsumeEvent playerItemConsumeEvent) {
        if (playerItemConsumeEvent.getItem().getType() == BeaconBattle.nms.materialCake() && Arena.getArenaByIdentifier(playerItemConsumeEvent.getPlayer().getWorld().getName()) != null) {
            playerItemConsumeEvent.setCancelled(true);
        }
    }
    
    private static void spawnUtility(final String s, final Location location, final ITeam team, final Player player) {
        if ("silverfish".equals(s.toLowerCase())) {
            BeaconBattle.nms.spawnSilverfish(location, team, BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.speed"), BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.health"), BeaconBattle.shop.getInt("shop-specials.silverfish.despawn"), BeaconBattle.shop.getYml().getDouble("shop-specials.silverfish.damage"));
        }
    }
}
