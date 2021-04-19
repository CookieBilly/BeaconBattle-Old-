

package ws.billy.bedwars.api.arena.generator;

import org.bukkit.entity.ArmorStand;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.util.HashMap;

public interface IGenerator
{
    HashMap<String, IGenHolo> getLanguageHolograms();
    
    void disable();
    
    void upgrade();
    
    void spawn();
    
    void dropItem(final Location p0);
    
    void setOre(final ItemStack p0);
    
    IArena getArena();
    
    void rotate();
    
    void setDelay(final int p0);
    
    void setAmount(final int p0);
    
    Location getLocation();
    
    ItemStack getOre();
    
    void updateHolograms(final Player p0, final String p1);
    
    void enableRotation();
    
    void setSpawnLimit(final int p0);
    
    ITeam getBwt();
    
    ArmorStand getHologramHolder();
    
    GeneratorType getType();
    
    int getAmount();
    
    int getDelay();
    
    int getNextSpawn();
    
    int getSpawnLimit();
    
    void setNextSpawn(final int p0);
    
    void setStack(final boolean p0);
    
    boolean isStack();
    
    void setType(final GeneratorType p0);
    
    void destroyData();
}
