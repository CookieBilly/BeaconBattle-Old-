

package ws.billy.bedwars.api.server;

import ws.billy.bedwars.api.configuration.ConfigManager;
import org.bukkit.entity.Player;

public interface ISetupSession
{
    String getWorldName();
    
    Player getPlayer();
    
    SetupType getSetupType();
    
    ConfigManager getConfig();
    
    void teleportPlayer();
    
    void close();
}
