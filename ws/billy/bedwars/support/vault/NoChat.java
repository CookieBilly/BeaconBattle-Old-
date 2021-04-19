

package ws.billy.bedwars.support.vault;

import org.bukkit.entity.Player;

public class NoChat implements Chat
{
    @Override
    public String getPrefix(final Player player) {
        return "";
    }
    
    @Override
    public String getSuffix(final Player player) {
        return "";
    }
}
