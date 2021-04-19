

package ws.billy.bedwars.upgrades.trapaction;

import ws.billy.bedwars.api.arena.team.ITeam;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.api.upgrades.TrapAction;

public class RemoveEffectAction implements TrapAction
{
    private PotionEffectType potionEffectType;
    
    public RemoveEffectAction(final PotionEffectType potionEffectType) {
        this.potionEffectType = potionEffectType;
    }
    
    @Override
    public String getName() {
        return "remove-effect";
    }
    
    @Override
    public void onTrigger(@NotNull final Player player, final ITeam team, final ITeam team2) {
        player.removePotionEffect(this.potionEffectType);
    }
}
