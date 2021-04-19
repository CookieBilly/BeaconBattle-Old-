

package ws.billy.bedwars.api.arena.team;

import org.bukkit.util.Vector;
import ws.billy.bedwars.api.upgrades.EnemyBaseEnterTrap;
import java.util.LinkedList;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import java.util.UUID;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.language.Language;

public interface ITeam
{
    TeamColor getColor();
    
    String getName();
    
    String getDisplayName(final Language p0);
    
    boolean isMember(final Player p0);
    
    IArena getArena();
    
    List<Player> getMembers();
    
    void defaultSword(final Player p0, final boolean p1);
    
    Location getBed();
    
    ConcurrentHashMap<String, Integer> getTeamUpgradeTiers();
    
    List<TeamEnchant> getBowsEnchantments();
    
    List<TeamEnchant> getSwordsEnchantments();
    
    List<TeamEnchant> getArmorsEnchantments();
    
    int getSize();
    
    void addPlayers(final Player... p0);
    
    void firstSpawn(final Player p0);
    
    void spawnNPCs();
    
    void reJoin(final Player p0);
    
    void sendDefaultInventory(final Player p0, final boolean p1);
    
    void respawnMember(final Player p0);
    
    void sendArmor(final Player p0);
    
    void addTeamEffect(final PotionEffectType p0, final int p1, final int p2);
    
    void addBaseEffect(final PotionEffectType p0, final int p1, final int p2);
    
    List<PotionEffect> getBaseEffects();
    
    void addBowEnchantment(final Enchantment p0, final int p1);
    
    void addSwordEnchantment(final Enchantment p0, final int p1);
    
    void addArmorEnchantment(final Enchantment p0, final int p1);
    
    boolean wasMember(final UUID p0);
    
    boolean isBedDestroyed();
    
    Location getSpawn();
    
    Location getShop();
    
    Location getTeamUpgrades();
    
    void setBedDestroyed(final boolean p0);
    
    @Deprecated
    IGenerator getIronGenerator();
    
    @Deprecated
    IGenerator getGoldGenerator();
    
    @Deprecated
    IGenerator getEmeraldGenerator();
    
    @Deprecated
    void setEmeraldGenerator(final IGenerator p0);
    
    List<IGenerator> getGenerators();
    
    int getDragons();
    
    void setDragons(final int p0);
    
    @Deprecated
    List<Player> getMembersCache();
    
    void destroyData();
    
    @Deprecated
    void destroyBedHolo(final Player p0);
    
    LinkedList<EnemyBaseEnterTrap> getActiveTraps();
    
    Vector getKillDropsLocation();
    
    void setKillDropsLocation(final Vector p0);
}
