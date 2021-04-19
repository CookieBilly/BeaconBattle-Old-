

package ws.billy.vipfeatures.api;

import org.bukkit.entity.Player;

public interface IVipFeatures
{
    void registerMiniGame(final MiniGame p0) throws MiniGameAlreadyRegistered;
    
    void givePlayerItemStack(final Player p0);
}
