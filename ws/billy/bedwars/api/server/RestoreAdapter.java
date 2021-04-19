

package ws.billy.bedwars.api.server;

import java.util.List;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.plugin.Plugin;

public abstract class RestoreAdapter
{
    private Plugin plugin;
    
    public RestoreAdapter(final Plugin plugin) {
        this.plugin = plugin;
    }
    
    public Plugin getOwner() {
        return this.plugin;
    }
    
    public abstract void onEnable(final IArena p0);
    
    public abstract void onRestart(final IArena p0);
    
    public abstract void onDisable(final IArena p0);
    
    public abstract void onSetupSessionStart(final ISetupSession p0);
    
    public abstract void onSetupSessionClose(final ISetupSession p0);
    
    public abstract void onLobbyRemoval(final IArena p0);
    
    public abstract boolean isWorld(final String p0);
    
    public abstract void deleteWorld(final String p0);
    
    public abstract void cloneArena(final String p0, final String p1);
    
    public abstract List<String> getWorldsList();
    
    public abstract void convertWorlds();
}
