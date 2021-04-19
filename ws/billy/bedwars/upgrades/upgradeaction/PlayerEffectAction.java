

package ws.billy.bedwars.upgrades.upgradeaction;

import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.api.upgrades.UpgradeAction;

public class PlayerEffectAction implements UpgradeAction
{
    private PotionEffectType potionEffectType;
    private int amplifier;
    private int duration;
    private ApplyType type;
    
    public PlayerEffectAction(final PotionEffectType potionEffectType, final int amplifier, final int duration, final ApplyType type) {
        this.potionEffectType = potionEffectType;
        this.amplifier = amplifier;
        this.type = type;
        this.duration = duration;
        if (duration < 0) {
            this.duration *= -1;
        }
        if (duration == 0) {
            this.duration = Integer.MAX_VALUE;
        }
        else {
            this.duration *= 20;
        }
    }
    
    @Override
    public void onBuy(final ITeam team) {
        if (this.type == ApplyType.BASE) {
            team.addBaseEffect(this.potionEffectType, this.amplifier, this.duration);
        }
        else if (this.type == ApplyType.TEAM) {
            team.addTeamEffect(this.potionEffectType, this.amplifier, this.duration);
        }
    }
    
    public enum ApplyType
    {
        TEAM, 
        BASE;
    }
}
