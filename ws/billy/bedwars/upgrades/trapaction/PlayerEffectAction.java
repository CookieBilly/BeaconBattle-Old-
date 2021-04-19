

package ws.billy.bedwars.upgrades.trapaction;

import java.util.Iterator;
import org.bukkit.potion.PotionEffect;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.api.upgrades.TrapAction;

public class PlayerEffectAction implements TrapAction
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
    public String getName() {
        return "player-effect";
    }
    
    @Override
    public void onTrigger(@NotNull final Player player, final ITeam team, final ITeam team2) {
        if (this.type == ApplyType.TEAM) {
            final Iterator<Player> iterator = team2.getMembers().iterator();
            while (iterator.hasNext()) {
                iterator.next().addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier));
            }
        }
        else if (this.type == ApplyType.BASE) {
            for (final Player player2 : team2.getMembers()) {
                if (player2.getLocation().distance(team2.getBed()) <= team2.getArena().getIslandRadius()) {
                    player2.addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier));
                }
            }
        }
        else if (this.type == ApplyType.ENEMY) {
            player.addPotionEffect(new PotionEffect(this.potionEffectType, this.duration, this.amplifier));
        }
    }
    
    public enum ApplyType
    {
        TEAM, 
        BASE, 
        ENEMY;
    }
}
