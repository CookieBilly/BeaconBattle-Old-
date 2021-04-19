

package ws.billy.bedwars.upgrades.trapaction;

import ws.billy.bedwars.BeaconBattle;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import ws.billy.bedwars.api.upgrades.TrapAction;

public class DisenchantAction implements TrapAction
{
    private Enchantment enchantment;
    private ApplyType type;
    
    public DisenchantAction(final Enchantment enchantment, final ApplyType type) {
        this.enchantment = enchantment;
        this.type = type;
    }
    
    @Override
    public String getName() {
        return "disenchant-item";
    }
    
    @Override
    public void onTrigger(@NotNull final Player player, final ITeam team, final ITeam team2) {
        if (this.type == ApplyType.SWORD) {
            for (final ItemStack itemStack : player.getInventory()) {
                if (BeaconBattle.nms.isSword(itemStack)) {
                    itemStack.removeEnchantment(this.enchantment);
                }
                player.updateInventory();
            }
        }
        else if (this.type == ApplyType.ARMOR) {
            for (final ItemStack itemStack2 : player.getInventory()) {
                if (BeaconBattle.nms.isArmor(itemStack2)) {
                    itemStack2.removeEnchantment(this.enchantment);
                }
                player.updateInventory();
            }
            for (final ItemStack itemStack3 : player.getInventory().getArmorContents()) {
                if (BeaconBattle.nms.isArmor(itemStack3)) {
                    itemStack3.removeEnchantment(this.enchantment);
                }
                player.updateInventory();
            }
        }
        else if (this.type == ApplyType.BOW) {
            for (final ItemStack itemStack4 : player.getInventory()) {
                if (BeaconBattle.nms.isBow(itemStack4)) {
                    itemStack4.removeEnchantment(this.enchantment);
                }
                player.updateInventory();
            }
        }
    }
    
    public enum ApplyType
    {
        SWORD, 
        ARMOR, 
        BOW;
    }
}
