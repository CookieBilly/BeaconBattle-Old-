

package ws.billy.bedwars.api;

import java.sql.Timestamp;
import java.util.LinkedList;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.configuration.ConfigManager;
import ws.billy.bedwars.api.arena.shop.IContentTier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.server.VersionSupport;
import ws.billy.bedwars.api.server.RestoreAdapter;
import ws.billy.bedwars.api.command.ParentCommand;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.api.server.ISetupSession;
import java.util.UUID;
import ws.billy.bedwars.api.party.Party;
import ws.billy.bedwars.api.levels.Level;

public interface BeaconBattle
{
    IStats getStatsUtil();
    
    AFKUtil getAFKUtil();
    
    ArenaUtil getArenaUtil();
    
    Configs getConfigs();
    
    ShopUtil getShopUtil();
    
    TeamUpgradesUtil getTeamUpgradesUtil();
    
    Level getLevelsUtil();
    
    Party getPartyUtil();
    
    ISetupSession getSetupSession(final UUID p0);
    
    boolean isInSetupSession(final UUID p0);
    
    ServerType getServerType();
    
    String getLangIso(final Player p0);
    
    ParentCommand getBeaconBattleCommand();
    
    RestoreAdapter getRestoreAdapter();
    
    void setRestoreAdapter(final RestoreAdapter p0) throws IllegalAccessError;
    
    VersionSupport getVersionSupport();
    
    Language getDefaultLang();
    
    String getLobbyWorld();
    
    String getForCurrentVersion(final String p0, final String p1, final String p2);
    
    void setLevelAdapter(final Level p0);
    
    public interface TeamUpgradesUtil
    {
        boolean isWatchingGUI(final Player p0);
        
        void setWatchingGUI(final Player p0);
        
        void removeWatchingUpgrades(final UUID p0);
    }
    
    public interface ShopUtil
    {
        int calculateMoney(final Player p0, final Material p1);
        
        Material getCurrency(final String p0);
        
        ChatColor getCurrencyColor(final Material p0);
        
        String getCurrencyMsgPath(final IContentTier p0);
        
        String getRomanNumber(final int p0);
        
        void takeMoney(final Player p0, final Material p1, final int p2);
    }
    
    public interface Configs
    {
        ConfigManager getMainConfig();
        
        ConfigManager getSignsConfig();
        
        ConfigManager getGeneratorsConfig();
        
        ConfigManager getShopConfig();
        
        ConfigManager getUpgradesConfig();
    }
    
    public interface ArenaUtil
    {
        boolean canAutoScale(final String p0);
        
        void addToEnableQueue(final IArena p0);
        
        void removeFromEnableQueue(final IArena p0);
        
        boolean isPlaying(final Player p0);
        
        boolean isSpectating(final Player p0);
        
        void loadArena(final String p0, final Player p1);
        
        void setGamesBeforeRestart(final int p0);
        
        int getGamesBeforeRestart();
        
        IArena getArenaByPlayer(final Player p0);
        
        void setArenaByPlayer(final Player p0, final IArena p1);
        
        void removeArenaByPlayer(final Player p0, final IArena p1);
        
        IArena getArenaByName(final String p0);
        
        IArena getArenaByIdentifier(final String p0);
        
        void setArenaByName(final IArena p0);
        
        void removeArenaByName(final String p0);
        
        LinkedList<IArena> getArenas();
        
        boolean vipJoin(final Player p0);
        
        int getPlayers(final String p0);
        
        boolean joinRandomArena(final Player p0);
        
        boolean joinRandomFromGroup(final Player p0, final String p1);
        
        LinkedList<IArena> getEnableQueue();
        
        void sendLobbyCommandItems(final Player p0);
    }
    
    public interface AFKUtil
    {
        boolean isPlayerAFK(final Player p0);
        
        void setPlayerAFK(final Player p0, final boolean p1);
        
        int getPlayerTimeAFK(final Player p0);
    }
    
    public interface IStats
    {
        Timestamp getPlayerFirstPlay(final UUID p0);
        
        Timestamp getPlayerLastPlay(final UUID p0);
        
        int getPlayerWins(final UUID p0);
        
        int getPlayerKills(final UUID p0);
        
        int getPlayerFinalKills(final UUID p0);
        
        int getPlayerLoses(final UUID p0);
        
        int getPlayerDeaths(final UUID p0);
        
        int getPlayerFinalDeaths(final UUID p0);
        
        int getPlayerBedsDestroyed(final UUID p0);
        
        int getPlayerGamesPlayed(final UUID p0);
    }
}
