

package ws.billy.vipfeatures.api;

import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.command.Command;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;

public abstract class VersionSupport
{
    private String version;
    
    public VersionSupport(final String version) {
        this.version = version;
    }
    
    public abstract void spawnParticle(final World p0, final float p1, final float p2, final float p3, final String p4);
    
    public abstract void spawnParticle(final World p0, final float p1, final float p2, final float p3);
    
    public abstract ItemStack addInteractID(final ItemStack p0, final String p1);
    
    public abstract boolean isInteractRegistered(final ItemStack p0);
    
    public abstract String getInteractID(final ItemStack p0);
    
    public abstract ItemStack getItemInHand(final Player p0);
    
    public abstract void registerCommand(final Plugin p0, final String p1, final Command p2);
    
    public String getVersion() {
        return this.version;
    }
    
    public abstract String getInvName(final InventoryEvent p0);
}
