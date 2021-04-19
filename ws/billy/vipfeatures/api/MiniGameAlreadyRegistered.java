

package ws.billy.vipfeatures.api;

import org.bukkit.plugin.Plugin;

public class MiniGameAlreadyRegistered extends Throwable
{
    public MiniGameAlreadyRegistered(final Plugin plugin) {
        super("Cannot register mini-game adapter for: " + plugin.getName() + " because it was already one registered.");
    }
}
