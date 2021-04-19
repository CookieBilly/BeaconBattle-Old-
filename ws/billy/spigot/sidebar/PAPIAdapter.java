

package ws.billy.spigot.sidebar;

import org.jetbrains.annotations.NotNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPIAdapter implements PAPISupport
{
    @Override
    public String replacePlaceholders(final Player player, final String s) {
        return PlaceholderAPI.setPlaceholders(player, s);
    }
    
    @Override
    public boolean hasPlaceholders(@NotNull final String s) {
        final String[] split = s.split(" ");
        for (int length = split.length, i = 0; i < length; ++i) {
            if (split[i].matches(PlaceholderAPI.getPlaceholderPattern().pattern())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
}
