

package ws.billy.bedwars.upgrades.upgradeaction;

import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.upgrades.UpgradeAction;

public class DragonAction implements UpgradeAction
{
    private int amount;
    
    public DragonAction(final int amount) {
        this.amount = amount;
    }
    
    @Override
    public void onBuy(final ITeam team) {
        team.setDragons(this.amount);
    }
}
