

package ws.billy.vipfeatures.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class MiniGame
{
    private Plugin owner;
    
    public MiniGame(final Plugin owner) {
        this.owner = owner;
    }
    
    public abstract boolean isPlaying(final Player p0);
    
    public abstract boolean hasBoosters();
    
    public abstract String getDisplayName();
    
    public Plugin getOwner() {
        return this.owner;
    }
}
