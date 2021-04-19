

package ws.billy.bedwars.support.vault;

import org.bukkit.entity.Player;

public class NoEconomy implements Economy
{
    @Override
    public boolean isEconomy() {
        return false;
    }
    
    @Override
    public double getMoney(final Player player) {
        return 0.0;
    }
    
    @Override
    public void buyAction(final Player player, final double n) {
        player.sendMessage("§cVault support missing!");
    }
}
