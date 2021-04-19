

package ws.billy.bedwars.upgrades.upgradeaction;

import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.enchantments.Enchantment;
import ws.billy.bedwars.api.upgrades.UpgradeAction;

public class EnchantItemAction implements UpgradeAction
{
    private Enchantment enchantment;
    private int amplifier;
    private ApplyType type;
    
    public EnchantItemAction(final Enchantment enchantment, final int amplifier, final ApplyType type) {
        this.enchantment = enchantment;
        this.amplifier = amplifier;
        this.type = type;
    }
    
    @Override
    public void onBuy(final ITeam team) {
        if (this.type == ApplyType.ARMOR) {
            team.addArmorEnchantment(this.enchantment, this.amplifier);
        }
        else if (this.type == ApplyType.SWORD) {
            team.addSwordEnchantment(this.enchantment, this.amplifier);
        }
        else if (this.type == ApplyType.BOW) {
            team.addBowEnchantment(this.enchantment, this.amplifier);
        }
    }
    
    public enum ApplyType
    {
        SWORD, 
        ARMOR, 
        BOW;
    }
}
