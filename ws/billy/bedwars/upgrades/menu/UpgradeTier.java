

package ws.billy.bedwars.upgrades.menu;

import java.util.Iterator;
import ws.billy.bedwars.upgrades.upgradeaction.DragonAction;
import ws.billy.bedwars.upgrades.upgradeaction.GeneratorEditAction;
import ws.billy.bedwars.upgrades.upgradeaction.PlayerEffectAction;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.upgrades.upgradeaction.EnchantItemAction;
import org.bukkit.enchantments.Enchantment;
import ws.billy.bedwars.upgrades.UpgradesManager;
import java.util.Collections;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.Material;
import ws.billy.bedwars.api.upgrades.UpgradeAction;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class UpgradeTier
{
    private ItemStack displayItem;
    private String name;
    private List<UpgradeAction> upgradeActions;
    private int cost;
    private Material currency;
    
    public UpgradeTier(final String str, final String str2, final ItemStack itemStack, final int cost, final Material currency) {
        this.upgradeActions = new ArrayList<UpgradeAction>();
        this.displayItem = BeaconBattle.nms.addCustomData(itemStack, "MCONT_" + str);
        this.name = str2;
        Language.saveIfNotExists(Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", str.replace("upgrade-", "")).replace("{tier}", str2), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("{name}", str.replace("upgrade-", "")).replace("{tier}", str2), Collections.singletonList("&cLore not set"));
        this.cost = cost;
        this.currency = currency;
        final Iterator<String> iterator = UpgradesManager.getConfiguration().getYml().getStringList(str + "." + str2 + ".receive").iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().trim().split(":");
            if (split.length < 2) {
                continue;
            }
            final String[] split2 = split[1].trim().toLowerCase().split(",");
            UpgradeAction upgradeAction = null;
            final String lowerCase = split[0].trim().toLowerCase();
            switch (lowerCase) {
                case "enchant-item": {
                    if (split2.length < 3) {
                        BeaconBattle.plugin.getLogger().warning("Invalid " + split[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    final Enchantment byName = Enchantment.getByName(split2[0].toUpperCase());
                    if (byName == null) {
                        BeaconBattle.plugin.getLogger().warning("Invalid enchantment " + split2[0].toUpperCase() + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    EnchantItemAction.ApplyType applyType = null;
                    final String lowerCase2 = split2[2].toLowerCase();
                    switch (lowerCase2) {
                        case "sword": {
                            applyType = EnchantItemAction.ApplyType.SWORD;
                            break;
                        }
                        case "armor": {
                            applyType = EnchantItemAction.ApplyType.ARMOR;
                            break;
                        }
                        case "bow": {
                            applyType = EnchantItemAction.ApplyType.BOW;
                            break;
                        }
                    }
                    if (applyType == null) {
                        BeaconBattle.plugin.getLogger().warning("Invalid apply type " + split2[2] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    int int1 = 1;
                    try {
                        int1 = Integer.parseInt(split2[1]);
                    }
                    catch (Exception ex) {}
                    upgradeAction = new EnchantItemAction(byName, int1, applyType);
                    break;
                }
                case "player-effect": {
                    if (split2.length < 4) {
                        BeaconBattle.plugin.getLogger().warning("Invalid " + split[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    final PotionEffectType byName2 = PotionEffectType.getByName(split2[0].toUpperCase());
                    if (byName2 == null) {
                        BeaconBattle.plugin.getLogger().warning("Invalid potion effect " + split2[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    PlayerEffectAction.ApplyType applyType2 = null;
                    final String lowerCase3 = split2[3].toLowerCase();
                    switch (lowerCase3) {
                        case "team": {
                            applyType2 = PlayerEffectAction.ApplyType.TEAM;
                            break;
                        }
                        case "base": {
                            applyType2 = PlayerEffectAction.ApplyType.BASE;
                            break;
                        }
                    }
                    if (applyType2 == null) {
                        BeaconBattle.plugin.getLogger().warning("Invalid apply type " + split2[3] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    int int2 = 1;
                    int int3 = 0;
                    try {
                        int2 = Integer.parseInt(split2[1]);
                        int3 = Integer.parseInt(split2[2]);
                    }
                    catch (Exception ex2) {}
                    upgradeAction = new PlayerEffectAction(byName2, int2, int3, applyType2);
                    break;
                }
                case "generator-edit": {
                    if (split2.length < 4) {
                        BeaconBattle.plugin.getLogger().warning("Invalid " + split[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    GeneratorEditAction.ApplyType applyType3 = null;
                    final String lowerCase4 = split2[0].toLowerCase();
                    switch (lowerCase4) {
                        case "gold":
                        case "g": {
                            applyType3 = GeneratorEditAction.ApplyType.GOLD;
                            break;
                        }
                        case "iron":
                        case "i": {
                            applyType3 = GeneratorEditAction.ApplyType.IRON;
                            break;
                        }
                        case "emerald":
                        case "e": {
                            applyType3 = GeneratorEditAction.ApplyType.EMERALD;
                            break;
                        }
                    }
                    if (applyType3 == null) {
                        BeaconBattle.plugin.getLogger().warning("Invalid generator type " + split2[0] + " at upgrades2: " + str + "." + str2);
                    }
                    int int4;
                    int int5;
                    int int6;
                    try {
                        int4 = Integer.parseInt(split2[1]);
                        int5 = Integer.parseInt(split2[2]);
                        int6 = Integer.parseInt(split2[3]);
                    }
                    catch (Exception ex3) {
                        BeaconBattle.plugin.getLogger().warning("Invalid generator configuration " + split2[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    upgradeAction = new GeneratorEditAction(applyType3, int5, int4, int6);
                    break;
                }
                case "dragon": {
                    if (split2.length < 1) {
                        BeaconBattle.plugin.getLogger().warning("Invalid " + split[0] + " at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    int int7;
                    try {
                        int7 = Integer.parseInt(split2[0]);
                    }
                    catch (Exception ex4) {
                        BeaconBattle.plugin.getLogger().warning("Invalid dragon amount at upgrades2: " + str + "." + str2);
                        continue;
                    }
                    upgradeAction = new DragonAction(int7);
                    break;
                }
            }
            if (upgradeAction == null) {
                continue;
            }
            this.upgradeActions.add(upgradeAction);
        }
    }
    
    public ItemStack getDisplayItem() {
        return this.displayItem;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public Material getCurrency() {
        return this.currency;
    }
    
    public List<UpgradeAction> getUpgradeActions() {
        return this.upgradeActions;
    }
}
