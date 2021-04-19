

package ws.billy.bedwars.shop.main;

import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.team.TeamEnchant;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.ChatColor;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.shop.IBuyItem;

public class BuyItem implements IBuyItem
{
    private ItemStack itemStack;
    private boolean autoEquip;
    private boolean permanent;
    private boolean loaded;
    private String upgradeIdentifier;
    
    public BuyItem(final String str, final YamlConfiguration yamlConfiguration, final String str2) {
        this.autoEquip = false;
        this.permanent = false;
        this.loaded = false;
        BeaconBattle.debug("Loading BuyItems: " + str);
        this.upgradeIdentifier = str2;
        if (yamlConfiguration.get(str + ".material") == null) {
            BeaconBattle.plugin.getLogger().severe("BuyItem: Material not set at " + str);
            return;
        }
        this.itemStack = BeaconBattle.nms.createItemStack(yamlConfiguration.getString(str + ".material"), (yamlConfiguration.get(str + ".amount") == null) ? 1 : yamlConfiguration.getInt(str + ".amount"), (short)((yamlConfiguration.get(str + ".data") == null) ? 1 : yamlConfiguration.getInt(str + ".data")));
        if (yamlConfiguration.get(str + ".name") != null) {
            final ItemMeta itemMeta = this.itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString(str + ".name")));
            this.itemStack.setItemMeta(itemMeta);
        }
        if (yamlConfiguration.get(str + ".enchants") != null) {
            final ItemMeta itemMeta2 = this.itemStack.getItemMeta();
            final String[] split = yamlConfiguration.getString(str + ".enchants").split(",");
            for (int length = split.length, i = 0; i < length; ++i) {
                final String[] split2 = split[i].split(" ");
                try {
                    Enchantment.getByName(split2[0]);
                }
                catch (Exception ex) {
                    BeaconBattle.plugin.getLogger().severe("BuyItem: Invalid enchants " + split2[0] + " at: " + str + ".enchants");
                    continue;
                }
                int int1 = 1;
                if (split2.length >= 2) {
                    try {
                        int1 = Integer.parseInt(split2[1]);
                    }
                    catch (Exception ex2) {
                        BeaconBattle.plugin.getLogger().severe("BuyItem: Invalid int " + split2[1] + " at: " + str + ".enchants");
                        continue;
                    }
                }
                itemMeta2.addEnchant(Enchantment.getByName(split2[0]), int1, true);
            }
            this.itemStack.setItemMeta(itemMeta2);
        }
        if (yamlConfiguration.get(str + ".potion") != null && this.itemStack.getType() == Material.POTION) {
            final PotionMeta itemMeta3 = (PotionMeta)this.itemStack.getItemMeta();
            final String[] split3 = yamlConfiguration.getString(str + ".potion").split(",");
            for (int length2 = split3.length, j = 0; j < length2; ++j) {
                final String[] split4 = split3[j].split(" ");
                try {
                    PotionEffectType.getByName(split4[0]);
                }
                catch (Exception ex3) {
                    BeaconBattle.plugin.getLogger().severe("BuyItem: Invalid potion effect " + split4[0] + " at: " + str + ".potion");
                    continue;
                }
                int int2 = 50;
                int int3 = 1;
                if (split4.length >= 3) {
                    try {
                        int2 = Integer.parseInt(split4[1]);
                    }
                    catch (Exception ex4) {
                        BeaconBattle.plugin.getLogger().severe("BuyItem: Invalid int (duration) " + split4[1] + " at: " + str + ".potion");
                        continue;
                    }
                    try {
                        int3 = Integer.parseInt(split4[2]);
                    }
                    catch (Exception ex5) {
                        BeaconBattle.plugin.getLogger().severe("BuyItem: Invalid int (amplifier) " + split4[2] + " at: " + str + ".potion");
                        continue;
                    }
                }
                itemMeta3.addCustomEffect(new PotionEffect(PotionEffectType.getByName(split4[0]), int2 * 20, int3), false);
            }
            this.itemStack.setItemMeta((ItemMeta)itemMeta3);
        }
        if (yamlConfiguration.get(str + ".auto-equip") != null) {
            this.autoEquip = yamlConfiguration.getBoolean(str + ".auto-equip");
        }
        if (yamlConfiguration.get(str2 + "." + "content-settings.is-permanent") != null) {
            this.permanent = yamlConfiguration.getBoolean(str2 + "." + "content-settings.is-permanent");
        }
        this.loaded = true;
    }
    
    @Override
    public boolean isLoaded() {
        return this.loaded;
    }
    
    @Override
    public void give(final Player player, final IArena arena) {
        ItemStack itemStack = this.itemStack.clone();
        BeaconBattle.debug("Giving BuyItem: " + this.getUpgradeIdentifier() + " to: " + player.getName());
        if (!this.autoEquip || !BeaconBattle.nms.isArmor(this.itemStack)) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            ItemStack itemStack2 = BeaconBattle.nms.colourItem(itemStack, arena.getTeam(player));
            if (this.permanent) {
                BeaconBattle.nms.setUnbreakable(itemMeta);
            }
            if (itemStack2.getType() == Material.BOW) {
                if (this.permanent) {
                    BeaconBattle.nms.setUnbreakable(itemMeta);
                }
                for (final TeamEnchant teamEnchant : arena.getTeam(player).getBowsEnchantments()) {
                    itemMeta.addEnchant(teamEnchant.getEnchantment(), teamEnchant.getAmplifier(), true);
                }
            }
            else if (BeaconBattle.nms.isSword(itemStack2) || BeaconBattle.nms.isAxe(itemStack2)) {
                for (final TeamEnchant teamEnchant2 : arena.getTeam(player).getSwordsEnchantments()) {
                    itemMeta.addEnchant(teamEnchant2.getEnchantment(), teamEnchant2.getAmplifier(), true);
                }
            }
            itemStack2.setItemMeta(itemMeta);
            if (this.permanent) {
                itemStack2 = BeaconBattle.nms.setShopUpgradeIdentifier(itemStack2, this.upgradeIdentifier);
            }
            if (BeaconBattle.nms.isSword(itemStack2)) {
                for (final ItemStack itemStack3 : player.getInventory().getContents()) {
                    if (itemStack3 != null) {
                        if (itemStack3.getType() != Material.AIR) {
                            if (BeaconBattle.nms.isSword(itemStack3)) {
                                if (itemStack3 != itemStack2) {
                                    if (BeaconBattle.nms.isCustomBeaconBattleItem(itemStack3) && BeaconBattle.nms.getCustomData(itemStack3).equals("DEFAULT_ITEM") && BeaconBattle.nms.getDamage(itemStack3) <= BeaconBattle.nms.getDamage(itemStack2)) {
                                        player.getInventory().remove(itemStack3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            player.getInventory().addItem(new ItemStack[] { itemStack2 });
            player.updateInventory();
            return;
        }
        final Material type = itemStack.getType();
        final ItemMeta itemMeta2 = itemStack.getItemMeta();
        if (arena.getTeam(player) == null) {
            BeaconBattle.debug("Could not give BuyItem to " + player.getName() + " - TEAM IS NULL");
            return;
        }
        for (final TeamEnchant teamEnchant3 : arena.getTeam(player).getArmorsEnchantments()) {
            itemMeta2.addEnchant(teamEnchant3.getEnchantment(), teamEnchant3.getAmplifier(), true);
        }
        if (this.permanent) {
            BeaconBattle.nms.setUnbreakable(itemMeta2);
        }
        itemStack.setItemMeta(itemMeta2);
        if (type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.DIAMOND_HELMET || type == BeaconBattle.nms.materialGoldenHelmet() || type == Material.IRON_HELMET) {
            if (this.permanent) {
                itemStack = BeaconBattle.nms.setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            }
            player.getInventory().setHelmet(itemStack);
        }
        else if (type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == BeaconBattle.nms.materialGoldenChestPlate() || type == Material.DIAMOND_CHESTPLATE || type == Material.IRON_CHESTPLATE) {
            if (this.permanent) {
                itemStack = BeaconBattle.nms.setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            }
            player.getInventory().setChestplate(itemStack);
        }
        else if (type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.DIAMOND_LEGGINGS || type == BeaconBattle.nms.materialGoldenLeggings() || type == Material.IRON_LEGGINGS) {
            if (this.permanent) {
                itemStack = BeaconBattle.nms.setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            }
            player.getInventory().setLeggings(itemStack);
        }
        else {
            if (this.permanent) {
                itemStack = BeaconBattle.nms.setShopUpgradeIdentifier(itemStack, this.upgradeIdentifier);
            }
            player.getInventory().setBoots(itemStack);
        }
        player.updateInventory();
        if (!BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets")) {
            final Iterator<Player> iterator4;
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    arena.getPlayers().iterator();
                    while (iterator4.hasNext()) {
                        BeaconBattle.nms.hideArmor(player, iterator4.next());
                    }
                }
            }, 20L);
        }
    }
    
    @Override
    public String getUpgradeIdentifier() {
        return this.upgradeIdentifier;
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    @Override
    public void setItemStack(final ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    @Override
    public boolean isAutoEquip() {
        return this.autoEquip;
    }
    
    @Override
    public void setAutoEquip(final boolean autoEquip) {
        this.autoEquip = autoEquip;
    }
    
    @Override
    public boolean isPermanent() {
        return this.permanent;
    }
    
    @Override
    public void setPermanent(final boolean permanent) {
        this.permanent = permanent;
    }
}
