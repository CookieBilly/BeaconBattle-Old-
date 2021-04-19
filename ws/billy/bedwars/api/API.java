

package ws.billy.bedwars.api;

import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.server.VersionSupport;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.api.server.ISetupSession;
import ws.billy.bedwars.api.party.Party;
import ws.billy.bedwars.api.levels.Level;
import ws.billy.bedwars.stats.StatsAPI;
import ws.billy.bedwars.api.arena.shop.IContentTier;
import org.bukkit.ChatColor;
import ws.billy.bedwars.shop.main.CategoryContent;
import org.bukkit.Material;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.api.configuration.ConfigManager;
import java.util.LinkedList;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerAfkEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;
import ws.billy.bedwars.api.server.RestoreAdapter;

public class API implements BeaconBattle
{
    private static RestoreAdapter restoreAdapter;
    private final AFKUtil afkSystem;
    private final ArenaUtil arenaUtil;
    private final Configs configs;
    private final ShopUtil shopUtil;
    private final TeamUpgradesUtil teamUpgradesUtil;
    
    public API() {
        this.afkSystem = new AFKUtil() {
            private final HashMap<UUID, Integer> afkPlayers = new HashMap<UUID, Integer>();
            
            @Override
            public boolean isPlayerAFK(final Player player) {
                return this.afkPlayers.containsKey(player.getUniqueId());
            }
            
            @Override
            public void setPlayerAFK(final Player player, final boolean b) {
                if (b) {
                    if (!this.afkPlayers.containsKey(player.getUniqueId())) {
                        this.afkPlayers.put(player.getUniqueId(), Arena.afkCheck.get(player.getUniqueId()));
                        Bukkit.getPluginManager().callEvent((Event)new PlayerAfkEvent(player, PlayerAfkEvent.AFKType.START));
                    }
                }
                else {
                    if (this.afkPlayers.containsKey(player.getUniqueId())) {
                        this.afkPlayers.remove(player.getUniqueId());
                        Bukkit.getPluginManager().callEvent((Event)new PlayerAfkEvent(player, PlayerAfkEvent.AFKType.END));
                    }
                    Arena.afkCheck.remove(player.getUniqueId());
                }
            }
            
            @Override
            public int getPlayerTimeAFK(final Player player) {
                return this.afkPlayers.getOrDefault(player.getUniqueId(), 0);
            }
        };
        this.arenaUtil = new ArenaUtil() {
            @Override
            public boolean canAutoScale(final String s) {
                return Arena.canAutoScale(s);
            }
            
            @Override
            public void addToEnableQueue(final IArena arena) {
                Arena.addToEnableQueue(arena);
            }
            
            @Override
            public void removeFromEnableQueue(final IArena arena) {
                Arena.removeFromEnableQueue(arena);
            }
            
            @Override
            public boolean isPlaying(final Player player) {
                return Arena.isInArena(player);
            }
            
            @Override
            public boolean isSpectating(final Player player) {
                return Arena.isInArena(player) && Arena.getArenaByPlayer(player).isSpectator(player);
            }
            
            @Override
            public void loadArena(final String s, final Player player) {
                new Arena(s, player);
            }
            
            @Override
            public void setGamesBeforeRestart(final int gamesBeforeRestart) {
                Arena.setGamesBeforeRestart(gamesBeforeRestart);
            }
            
            @Override
            public int getGamesBeforeRestart() {
                return Arena.getGamesBeforeRestart();
            }
            
            @Override
            public IArena getArenaByPlayer(final Player player) {
                return Arena.getArenaByPlayer(player);
            }
            
            @Override
            public void setArenaByPlayer(final Player player, final IArena arena) {
                Arena.setArenaByPlayer(player, arena);
            }
            
            @Override
            public void removeArenaByPlayer(final Player player, final IArena arena) {
                Arena.removeArenaByPlayer(player, arena);
            }
            
            @Override
            public IArena getArenaByName(final String s) {
                return Arena.getArenaByName(s);
            }
            
            @Override
            public IArena getArenaByIdentifier(final String s) {
                return Arena.getArenaByIdentifier(s);
            }
            
            @Override
            public void setArenaByName(final IArena arenaByName) {
                Arena.setArenaByName(arenaByName);
            }
            
            @Override
            public void removeArenaByName(final String s) {
                Arena.removeArenaByName(s);
            }
            
            @Override
            public LinkedList<IArena> getArenas() {
                return Arena.getArenas();
            }
            
            @Override
            public boolean vipJoin(final Player player) {
                return Arena.isVip(player);
            }
            
            @Override
            public int getPlayers(final String s) {
                return Arena.getPlayers(s);
            }
            
            @Override
            public boolean joinRandomArena(final Player player) {
                return Arena.joinRandomArena(player);
            }
            
            @Override
            public boolean joinRandomFromGroup(final Player player, final String s) {
                return Arena.joinRandomFromGroup(player, s);
            }
            
            @Override
            public LinkedList<IArena> getEnableQueue() {
                return Arena.getEnableQueue();
            }
            
            @Override
            public void sendLobbyCommandItems(final Player player) {
                Arena.sendLobbyCommandItems(player);
            }
        };
        this.configs = new Configs() {
            @Override
            public ConfigManager getMainConfig() {
                return ws.billy.bedwars.BeaconBattle.config;
            }
            
            @Override
            public ConfigManager getSignsConfig() {
                return ws.billy.bedwars.BeaconBattle.signs;
            }
            
            @Override
            public ConfigManager getGeneratorsConfig() {
                return ws.billy.bedwars.BeaconBattle.generators;
            }
            
            @Override
            public ConfigManager getShopConfig() {
                return ws.billy.bedwars.BeaconBattle.shop;
            }
            
            @Override
            public ConfigManager getUpgradesConfig() {
                return UpgradesManager.getConfiguration();
            }
        };
        this.shopUtil = new ShopUtil() {
            @Override
            public int calculateMoney(final Player player, final Material material) {
                return CategoryContent.calculateMoney(player, material);
            }
            
            @Override
            public Material getCurrency(final String s) {
                return CategoryContent.getCurrency(s);
            }
            
            @Override
            public ChatColor getCurrencyColor(final Material material) {
                return CategoryContent.getCurrencyColor(material);
            }
            
            @Override
            public String getCurrencyMsgPath(final IContentTier contentTier) {
                return CategoryContent.getCurrencyMsgPath(contentTier);
            }
            
            @Override
            public String getRomanNumber(final int n) {
                return CategoryContent.getRomanNumber(n);
            }
            
            @Override
            public void takeMoney(final Player player, final Material material, final int n) {
                CategoryContent.takeMoney(player, material, n);
            }
        };
        this.teamUpgradesUtil = new TeamUpgradesUtil() {
            @Override
            public boolean isWatchingGUI(final Player player) {
                return UpgradesManager.isWatchingUpgrades(player.getUniqueId());
            }
            
            @Override
            public void setWatchingGUI(final Player player) {
                UpgradesManager.setWatchingUpgrades(player.getUniqueId());
            }
            
            @Override
            public void removeWatchingUpgrades(final UUID uuid) {
                UpgradesManager.removeWatchingUpgrades(uuid);
            }
        };
    }
    
    @Override
    public IStats getStatsUtil() {
        return StatsAPI.getInstance();
    }
    
    @Override
    public AFKUtil getAFKUtil() {
        return this.afkSystem;
    }
    
    @Override
    public ArenaUtil getArenaUtil() {
        return this.arenaUtil;
    }
    
    @Override
    public Configs getConfigs() {
        return this.configs;
    }
    
    @Override
    public ShopUtil getShopUtil() {
        return this.shopUtil;
    }
    
    @Override
    public TeamUpgradesUtil getTeamUpgradesUtil() {
        return this.teamUpgradesUtil;
    }
    
    @Override
    public Level getLevelsUtil() {
        return ws.billy.bedwars.BeaconBattle.getLevelSupport();
    }
    
    @Override
    public Party getPartyUtil() {
        return ws.billy.bedwars.BeaconBattle.getParty();
    }
    
    @Override
    public ISetupSession getSetupSession(final UUID uuid) {
        return SetupSession.getSession(uuid);
    }
    
    @Override
    public boolean isInSetupSession(final UUID uuid) {
        return SetupSession.isInSetupSession(uuid);
    }
    
    @Override
    public ServerType getServerType() {
        return ws.billy.bedwars.BeaconBattle.getServerType();
    }
    
    @Override
    public ParentCommand getBeaconBattleCommand() {
        return MainCommand.getInstance();
    }
    
    @Override
    public RestoreAdapter getRestoreAdapter() {
        return API.restoreAdapter;
    }
    
    @Override
    public void setRestoreAdapter(final RestoreAdapter restoreAdapter) {
        if (!Arena.getArenas().isEmpty()) {
            throw new IllegalAccessError("Arenas must be unloaded when changing the adapter");
        }
        API.restoreAdapter = restoreAdapter;
        if (restoreAdapter.getOwner() != null && restoreAdapter.getOwner() != ws.billy.bedwars.BeaconBattle.plugin) {
            ws.billy.bedwars.BeaconBattle.plugin.getLogger().log(java.util.logging.Level.WARNING, restoreAdapter.getOwner().getName() + " changed the restore system to its own adapter.");
        }
    }
    
    @Override
    public VersionSupport getVersionSupport() {
        return ws.billy.bedwars.BeaconBattle.nms;
    }
    
    @Override
    public Language getDefaultLang() {
        return Language.getDefaultLanguage();
    }
    
    @Override
    public String getLobbyWorld() {
        return ws.billy.bedwars.BeaconBattle.getLobbyWorld();
    }
    
    @Override
    public String getForCurrentVersion(final String s, final String s2, final String s3) {
        return ws.billy.bedwars.BeaconBattle.getForCurrentVersion(s, s2, s3);
    }
    
    @Override
    public void setLevelAdapter(final Level levelAdapter) {
        ws.billy.bedwars.BeaconBattle.setLevelAdapter(levelAdapter);
    }
    
    @Override
    public String getLangIso(final Player player) {
        return Language.getPlayerLanguage(player).getIso();
    }
}
