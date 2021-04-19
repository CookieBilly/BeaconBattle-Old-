

package ws.billy.bedwars.support.vault;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class WithEconomy implements Economy
{
    private static net.milkbowl.vault.economy.Economy economy;
    
    @Override
    public boolean isEconomy() {
        return true;
    }
    
    @Override
    public double getMoney(final Player player) {
        return WithEconomy.economy.getBalance((OfflinePlayer)player);
    }
    
    @Override
    public void buyAction(final Player player, final double n) {
        WithEconomy.economy.bankWithdraw(player.getName(), n);
    }
    
    public static void setEconomy(final net.milkbowl.vault.economy.Economy economy) {
        WithEconomy.economy = economy;
    }
}
