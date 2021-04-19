

package ws.billy.bedwars.listeners;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdStats;
import ws.billy.bedwars.arena.LastHit;
import ws.billy.bedwars.arena.BeaconBattleTeam;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Location;
import ws.billy.bedwars.sidebar.BeaconBattleScoreboard;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.support.preloadedparty.PreLoadedParty;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.language.PreLoadedLanguage;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.ReJoin;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.lobbysocket.LoadedUser;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.Listener;

public class JoinLeaveTeleport implements Listener
{
    @EventHandler
    public void onLogin(final PlayerLoginEvent playerLoginEvent) {
        final Player player = playerLoginEvent.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        if (BeaconBattle.autoscale) {
            if (LoadedUser.isPreLoaded(uniqueId)) {
                BeaconBattle.debug("PlayerLoginEvent is pre loaded");
                final LoadedUser preLoaded = LoadedUser.getPreLoaded(uniqueId);
                final Language language = (preLoaded.getLanguage() == null) ? Language.getDefaultLanguage() : preLoaded.getLanguage();
                if (playerLoginEvent.getPlayer().hasPermission(Permissions.PERMISSION_REJOIN)) {
                    final ReJoin player2 = ReJoin.getPlayer(player);
                    if (player2 != null) {
                        if (player2.canReJoin()) {
                            return;
                        }
                        playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, language.m(Messages.REJOIN_DENIED));
                        return;
                    }
                }
                final IArena arenaByIdentifier = Arena.getArenaByIdentifier(preLoaded.getArenaIdentifier());
                if (arenaByIdentifier == null || preLoaded.getRequestTime() > System.currentTimeMillis() + 5000L || arenaByIdentifier.getStatus() == GameState.restarting) {
                    BeaconBattle.debug("PlayerLoginEvent is pre loaded but time out");
                    playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, language.m(Messages.ARENA_STATUS_RESTARTING_NAME));
                    preLoaded.destroy();
                }
                return;
            }
            else if (!playerLoginEvent.getPlayer().hasPermission("bw.setup")) {
                playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You must be op to join directly. Use the arena selector otherwise.");
            }
        }
        if (!BeaconBattle.autoscale) {
            final String s;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
                BeaconBattle.getRemoteDatabase().getLanguage(uniqueId);
                if (Language.isLanguageExist(s) && !BeaconBattle.config.getYml().getStringList("disabled-languages").contains(s)) {
                    new PreLoadedLanguage(playerLoginEvent.getPlayer().getUniqueId(), s);
                }
                return;
            });
        }
        if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
            if (Arena.getArenas().isEmpty()) {
                if (Arena.getEnableQueue().isEmpty()) {
                    return;
                }
                playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Language.getMsg(playerLoginEvent.getPlayer(), Messages.ARENA_STATUS_RESTARTING_NAME));
            }
            final IArena arena = Arena.getArenas().get(0);
            if (arena.getStatus() == GameState.waiting || (arena.getStatus() == GameState.starting && arena.getStartingTask().getCountdown() > 2)) {
                if (arena.getPlayers().size() >= arena.getMaxPlayers() && !Arena.isVip(playerLoginEvent.getPlayer())) {
                    playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_FULL, Language.getMsg(playerLoginEvent.getPlayer(), Messages.COMMAND_JOIN_DENIED_IS_FULL));
                }
                else if (arena.getPlayers().size() >= arena.getMaxPlayers() && Arena.isVip(playerLoginEvent.getPlayer())) {
                    boolean b = false;
                    for (final Player player3 : arena.getPlayers()) {
                        if (!Arena.isVip(player3)) {
                            b = true;
                            arena.removePlayer(player3, true);
                            player3.kickPlayer(Language.getMsg(player3, Messages.ARENA_JOIN_VIP_KICK));
                            break;
                        }
                    }
                    if (!b) {
                        playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_FULL, Language.getMsg(playerLoginEvent.getPlayer(), Messages.COMMAND_JOIN_DENIED_IS_FULL_OF_VIPS));
                    }
                }
            }
            else if (arena.getStatus() == GameState.playing) {
                if (!arena.isAllowSpectate()) {
                    if (playerLoginEvent.getPlayer().hasPermission(Permissions.PERMISSION_REJOIN)) {
                        final ReJoin player4 = ReJoin.getPlayer(player);
                        if (player4 != null && player4.canReJoin()) {
                            return;
                        }
                    }
                    playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, Language.getMsg(playerLoginEvent.getPlayer(), Messages.REJOIN_DENIED));
                }
            }
            else {
                playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Language.getMsg(playerLoginEvent.getPlayer(), Messages.ARENA_STATUS_RESTARTING_NAME));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        if (BeaconBattle.autoscale) {
            playerJoinEvent.setJoinMessage((String)null);
            if (LoadedUser.isPreLoaded(player.getUniqueId())) {
                final LoadedUser preLoaded = LoadedUser.getPreLoaded(player.getUniqueId());
                final IArena arenaByIdentifier = Arena.getArenaByIdentifier(preLoaded.getArenaIdentifier());
                if (ReJoin.exists(player)) {
                    if (!ReJoin.getPlayer(player).canReJoin()) {
                        player.kickPlayer(Language.getMsg(player, Messages.REJOIN_DENIED));
                        return;
                    }
                    player.sendMessage(Language.getMsg(player, Messages.REJOIN_ALLOWED).replace("{arena}", ReJoin.getPlayer(player).getArena().getDisplayName()));
                    ReJoin.getPlayer(player).reJoin(player);
                    return;
                }
                else {
                    if (arenaByIdentifier == null || preLoaded.getRequestTime() > System.currentTimeMillis() + 5000L || arenaByIdentifier.getStatus() == GameState.restarting) {
                        player.kickPlayer(((preLoaded.getLanguage() == null) ? Language.getDefaultLanguage() : preLoaded.getLanguage()).m(Messages.ARENA_STATUS_RESTARTING_NAME));
                        return;
                    }
                    if (preLoaded.getLanguage() != null) {
                        Language.setPlayerLanguage(playerJoinEvent.getPlayer(), preLoaded.getLanguage().getIso(), true);
                    }
                    if (arenaByIdentifier.getStatus() == GameState.starting || arenaByIdentifier.getStatus() == GameState.waiting) {
                        Sounds.playSound("join-allowed", player);
                        if (preLoaded.getPartyOwnerOrSpectateTarget() != null) {
                            final Player player2 = Bukkit.getPlayer(preLoaded.getPartyOwnerOrSpectateTarget());
                            if (player2 != null && player2.isOnline()) {
                                if (player2.equals(playerJoinEvent.getPlayer())) {
                                    BeaconBattle.getParty().createParty(playerJoinEvent.getPlayer(), new Player[0]);
                                    final PreLoadedParty partyByOwner = PreLoadedParty.getPartyByOwner(playerJoinEvent.getPlayer().getName());
                                    if (partyByOwner != null) {
                                        partyByOwner.teamUp();
                                    }
                                }
                                else {
                                    BeaconBattle.getParty().addMember(player2, playerJoinEvent.getPlayer());
                                }
                            }
                            else {
                                PreLoadedParty partyByOwner2 = PreLoadedParty.getPartyByOwner(preLoaded.getPartyOwnerOrSpectateTarget());
                                if (partyByOwner2 == null) {
                                    partyByOwner2 = new PreLoadedParty(preLoaded.getPartyOwnerOrSpectateTarget());
                                }
                                partyByOwner2.addMember(playerJoinEvent.getPlayer());
                            }
                        }
                        arenaByIdentifier.addPlayer(player, true);
                    }
                    else {
                        arenaByIdentifier.addSpectator(player, false, (preLoaded.getPartyOwnerOrSpectateTarget() == null) ? null : ((Bukkit.getPlayer(preLoaded.getPartyOwnerOrSpectateTarget()) == null) ? null : Bukkit.getPlayer(preLoaded.getPartyOwnerOrSpectateTarget()).getLocation()));
                        Sounds.playSound("spectate-allowed", player);
                    }
                    preLoaded.destroy();
                    return;
                }
            }
            else {
                if (!player.hasPermission("bw.setup")) {
                    player.kickPlayer(Language.getMsg(player, Messages.ARENA_STATUS_RESTARTING_NAME));
                    return;
                }
                Bukkit.dispatchCommand((CommandSender)player, "/bw");
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
        }
        final PreLoadedLanguage byUUID = PreLoadedLanguage.getByUUID(playerJoinEvent.getPlayer().getUniqueId());
        if (byUUID != null) {
            Language.setPlayerLanguage(playerJoinEvent.getPlayer(), byUUID.getIso(), true);
            PreLoadedLanguage.clear(playerJoinEvent.getPlayer().getUniqueId());
        }
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            final Iterator<Player> iterator;
            Player player3;
            final Player player4;
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                Bukkit.getOnlinePlayers().iterator();
                while (iterator.hasNext()) {
                    player3 = iterator.next();
                    if (Arena.isInArena(playerJoinEvent.getPlayer())) {
                        break;
                    }
                    else if (Arena.getArenaByPlayer(player3) != null) {
                        player4.hidePlayer(player3);
                        player3.hidePlayer(player4);
                    }
                    else {
                        continue;
                    }
                }
                return;
            }, 14L);
        }
        if (player.isOp() && Arena.getArenas().isEmpty()) {
            player.performCommand(BeaconBattle.mainCmd);
        }
        if (BeaconBattle.getServerType() != ServerType.SHARED) {
            playerJoinEvent.setJoinMessage((String)null);
            player.getInventory().setArmorContents((ItemStack[])null);
        }
        if (BeaconBattle.getServerType() == ServerType.SHARED || !ReJoin.exists(player)) {
            if (BeaconBattle.getServerType() == ServerType.SHARED && playerJoinEvent.getPlayer().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
                BeaconBattleScoreboard.giveScoreboard(playerJoinEvent.getPlayer(), null, true);
            }
            if (BeaconBattle.getServerType() == ServerType.BUNGEE && !BeaconBattle.autoscale) {
                if (!Arena.getArenas().isEmpty()) {
                    final IArena arena = Arena.getArenas().get(0);
                    if (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting) {
                        if (arena.addPlayer(player, false)) {
                            Sounds.playSound("join-allowed", player);
                        }
                        else {
                            arena.addSpectator(player, false, null);
                            Sounds.playSound("join-denied", player);
                        }
                    }
                    else {
                        arena.addSpectator(player, false, null);
                        Sounds.playSound("spectate-allowed", player);
                    }
                }
            }
            else if (BeaconBattle.getServerType() == ServerType.MULTIARENA) {
                if (BeaconBattle.config.getConfigLoc("lobbyLoc") != null) {
                    final Location configLoc = BeaconBattle.config.getConfigLoc("lobbyLoc");
                    if (configLoc.getWorld() != null) {
                        player.teleport(configLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
                BeaconBattleScoreboard.giveScoreboard(player, null, true);
                Arena.sendLobbyCommandItems(player);
                player.setHealthScale(20.0);
                player.setFoodLevel(20);
                player.setExp(0.0f);
            }
            return;
        }
        if (!ReJoin.getPlayer(player).canReJoin()) {
            return;
        }
        player.sendMessage(Language.getMsg(player, Messages.REJOIN_ALLOWED).replace("{arena}", ReJoin.getPlayer(player).getArena().getDisplayName()));
        ReJoin.getPlayer(player).reJoin(player);
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer != null) {
            if (arenaByPlayer.isPlayer(player)) {
                arenaByPlayer.removePlayer(player, true);
            }
            else if (arenaByPlayer.isSpectator(player)) {
                arenaByPlayer.removeSpectator(player, true);
            }
        }
        if (Language.getLangByPlayer().containsKey(player)) {
            final Object o;
            String iso = null;
            final UUID uuid;
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
                player.getUniqueId();
                Language.getLangByPlayer().get(o).getIso();
                if (Language.isLanguageExist(iso)) {
                    if (BeaconBattle.config.getYml().getStringList("disabled-languages").contains(iso)) {
                        iso = Language.getDefaultLanguage().getIso();
                    }
                    BeaconBattle.getRemoteDatabase().setLanguage(uuid, iso);
                }
                Language.getLangByPlayer().remove(o);
                return;
            });
        }
        if (BeaconBattle.getServerType() != ServerType.SHARED) {
            playerQuitEvent.setQuitMessage((String)null);
        }
        if (BeaconBattle.getParty().isInternal() && BeaconBattle.getParty().hasParty(player)) {
            BeaconBattle.getParty().removeFromParty(player);
        }
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session != null) {
            session.cancel();
        }
        final BeaconBattleScoreboard sBoard = BeaconBattleScoreboard.getSBoard(playerQuitEvent.getPlayer().getUniqueId());
        if (sBoard != null) {
            sBoard.remove();
        }
        BeaconBattleTeam.antiFallDamageAtRespawn.remove(playerQuitEvent.getPlayer().getUniqueId());
        final LastHit lastHit = LastHit.getLastHit(player);
        if (lastHit != null) {
            lastHit.remove();
        }
        CmdStats.getStatsCoolDown().remove(playerQuitEvent.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        if (playerTeleportEvent == null) {
            return;
        }
        if (playerTeleportEvent.isCancelled()) {
            return;
        }
        if (playerTeleportEvent.getTo() == null) {
            return;
        }
        if (playerTeleportEvent.getTo().getWorld() == null) {
            return;
        }
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerTeleportEvent.getPlayer());
        if (arenaByPlayer != null) {
            final IArena arenaByIdentifier = Arena.getArenaByIdentifier(playerTeleportEvent.getTo().getWorld().getName());
            if (arenaByIdentifier != null && !arenaByIdentifier.equals(arenaByPlayer)) {
                if (arenaByPlayer.isSpectator(playerTeleportEvent.getPlayer())) {
                    arenaByPlayer.removeSpectator(playerTeleportEvent.getPlayer(), false);
                }
                if (arenaByPlayer.isPlayer(playerTeleportEvent.getPlayer())) {
                    arenaByPlayer.removePlayer(playerTeleportEvent.getPlayer(), false);
                }
                playerTeleportEvent.getPlayer().sendMessage("PlayerTeleportEvent something went wrong. You have joined an arena world while playing on a different map.");
            }
        }
    }
    
    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent playerChangedWorldEvent) {
        if (BeaconBattle.getServerType() == ServerType.SHARED && BeaconBattle.config.getBoolean("scoreboard-settings.sidebar.enable-lobby-sidebar")) {
            if (playerChangedWorldEvent.getPlayer().getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
                BeaconBattleScoreboard.giveScoreboard(playerChangedWorldEvent.getPlayer(), null, true);
            }
            else {
                final BeaconBattleScoreboard sBoard = BeaconBattleScoreboard.getSBoard(playerChangedWorldEvent.getPlayer().getUniqueId());
                if (sBoard != null) {
                    sBoard.remove();
                }
            }
        }
        if (Arena.isInArena(playerChangedWorldEvent.getPlayer())) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(playerChangedWorldEvent.getPlayer());
            if (arenaByPlayer.isPlayer(playerChangedWorldEvent.getPlayer())) {
                if (arenaByPlayer.getStatus() == GameState.waiting || arenaByPlayer.getStatus() == GameState.starting) {
                    return;
                }
                if (!playerChangedWorldEvent.getPlayer().getWorld().getName().equalsIgnoreCase(arenaByPlayer.getWorld().getName())) {
                    arenaByPlayer.removePlayer(playerChangedWorldEvent.getPlayer(), BeaconBattle.getServerType() == ServerType.BUNGEE);
                    BeaconBattle.debug(playerChangedWorldEvent.getPlayer().getName() + " was removed from " + arenaByPlayer.getDisplayName() + " because he was teleported outside the arena.");
                }
            }
        }
    }
}
