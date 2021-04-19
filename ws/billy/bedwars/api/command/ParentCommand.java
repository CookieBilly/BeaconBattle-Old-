

package ws.billy.bedwars.api.command;

import java.util.List;
import org.bukkit.entity.Player;

public interface ParentCommand
{
    boolean hasSubCommand(final String p0);
    
    void addSubCommand(final SubCommand p0);
    
    void sendSubCommands(final Player p0);
    
    List<SubCommand> getSubCommands();
    
    String getName();
}
