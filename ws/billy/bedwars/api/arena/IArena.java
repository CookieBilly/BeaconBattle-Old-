

package ws.billy.bedwars.api.arena;

import ws.billy.bedwars.api.region.Region;
import org.bukkit.util.Vector;
import java.util.LinkedList;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.api.tasks.RestartingTask;
import ws.billy.bedwars.api.tasks.PlayingTask;
import ws.billy.bedwars.api.tasks.StartingTask;
import org.bukkit.block.Block;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.Location;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.api.arena.team.ITeam;
import java.util.List;
import ws.billy.bedwars.api.configuration.ConfigManager;
import org.bukkit.World;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface IArena
{
    boolean isSpectator(final Player p0);
    
    boolean isSpectator(final UUID p0);
    
    boolean isReSpawning(final UUID p0);
    
    String getArenaName();
    
    void init(final World p0);
    
    ConfigManager getConfig();
    
    boolean isPlayer(final Player p0);
    
    List<Player> getSpectators();
    
    ITeam getTeam(final Player p0);
    
    ITeam getExTeam(final UUID p0);
    
    String getDisplayName();
    
    void setWorldName(final String p0);
    
    GameState getStatus();
    
    List<Player> getPlayers();
    
    int getMaxPlayers();
    
    String getGroup();
    
    int getMaxInTeam();
    
    ConcurrentHashMap<Player, Integer> getRespawn();
    
    void updateSpectatorCollideRule(final Player p0, final boolean p1);
    
    void updateNextEvent();
    
    boolean addPlayer(final Player p0, final boolean p1);
    
    boolean addSpectator(final Player p0, final boolean p1, final Location p2);
    
    void removePlayer(final Player p0, final boolean p1);
    
    void removeSpectator(final Player p0, final boolean p1);
    
    boolean reJoin(final Player p0);
    
    void disable();
    
    void restart();
    
    World getWorld();
    
    String getDisplayStatus(final Language p0);
    
    String getDisplayGroup(final Player p0);
    
    String getDisplayGroup(final Language p0);
    
    List<ITeam> getTeams();
    
    void addPlacedBlock(final Block p0);
    
    void removePlacedBlock(final Block p0);
    
    boolean isBlockPlaced(final Block p0);
    
    int getPlayerKills(final Player p0, final boolean p1);
    
    int getPlayerBedsDestroyed(final Player p0);
    
    List<Block> getSigns();
    
    int getIslandRadius();
    
    void setGroup(final String p0);
    
    void setStatus(final GameState p0);
    
    void changeStatus(final GameState p0);
    
    boolean isRespawning(final Player p0);
    
    void addSign(final Location p0);
    
    void refreshSigns();
    
    void addPlayerKill(final Player p0, final boolean p1, final Player p2);
    
    void addPlayerBedDestroyed(final Player p0);
    
    @Deprecated
    ITeam getPlayerTeam(final String p0);
    
    void checkWinner();
    
    void addPlayerDeath(final Player p0);
    
    void setNextEvent(final NextEvent p0);
    
    NextEvent getNextEvent();
    
    void sendPreGameCommandItems(final Player p0);
    
    void sendSpectatorCommandItems(final Player p0);
    
    ITeam getTeam(final String p0);
    
    StartingTask getStartingTask();
    
    PlayingTask getPlayingTask();
    
    RestartingTask getRestartingTask();
    
    List<IGenerator> getOreGenerators();
    
    List<String> getNextEvents();
    
    int getPlayerDeaths(final Player p0, final boolean p1);
    
    void sendDiamondsUpgradeMessages();
    
    void sendEmeraldsUpgradeMessages();
    
    LinkedList<Vector> getPlaced();
    
    void destroyData();
    
    int getUpgradeDiamondsCount();
    
    int getUpgradeEmeraldsCount();
    
    List<Region> getRegionsList();
    
    ConcurrentHashMap<Player, Integer> getShowTime();
    
    void setAllowSpectate(final boolean p0);
    
    boolean isAllowSpectate();
    
    String getWorldName();
}
