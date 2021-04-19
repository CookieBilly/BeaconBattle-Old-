

package ws.billy.bedwars.upgrades.menu;

import java.util.Collections;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.upgrades.UpgradeBuyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.upgrades.UpgradeAction;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.event.inventory.ClickType;
import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import java.util.ArrayList;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.upgrades.UpgradesManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import java.util.List;
import ws.billy.bedwars.api.upgrades.TeamUpgrade;
import ws.billy.bedwars.api.upgrades.MenuContent;

public class MenuUpgrade implements MenuContent, TeamUpgrade
{
    private String name;
    private List<UpgradeTier> tiers;
    
    public MenuUpgrade(final String name) {
        this.tiers = new LinkedList<UpgradeTier>();
        this.name = name;
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        if (this.tiers.isEmpty()) {
            return new ItemStack(Material.BEDROCK);
        }
        int intValue = -1;
        if (team.getTeamUpgradeTiers().containsKey(this.getName())) {
            intValue = team.getTeamUpgradeTiers().get(this.getName());
        }
        final boolean b = this.getTiers().size() == intValue + 1 && team.getTeamUpgradeTiers().containsKey(this.getName());
        if (!b) {
            ++intValue;
        }
        final UpgradeTier upgradeTier = this.getTiers().get(intValue);
        final boolean b2 = UpgradesManager.getMoney(player, upgradeTier.getCurrency()) >= upgradeTier.getCost();
        final ItemStack itemStack = new ItemStack(this.tiers.get(intValue).getDisplayItem());
        final ItemMeta itemMeta = itemStack.getItemMeta();
        String replacement;
        if (!b) {
            if (b2) {
                replacement = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CAN_AFFORD);
            }
            else {
                replacement = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_CANT_AFFORD);
            }
        }
        else {
            replacement = Language.getMsg(player, Messages.FORMAT_UPGRADE_COLOR_UNLOCKED);
        }
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName())).replace("{color}", replacement));
        final ArrayList<String> lore = new ArrayList<String>();
        final String currencyMsg = UpgradesManager.getCurrencyMsg(player, upgradeTier);
        final Iterator<String> iterator = Language.getList(player, Messages.UPGRADES_UPGRADE_TIER_ITEM_LORE.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName())).iterator();
        while (iterator.hasNext()) {
            lore.add(iterator.next().replace("{cost}", String.valueOf(upgradeTier.getCost())).replace("{currency}", currencyMsg).replace("{tierColor}", Language.getMsg(player, b ? Messages.FORMAT_UPGRADE_TIER_UNLOCKED : Messages.FORMAT_UPGRADE_TIER_LOCKED)).replace("{color}", replacement));
        }
        if (b) {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_UNLOCKED).replace("{color}", replacement));
        }
        else if (b2) {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_CLICK_TO_BUY).replace("{color}", replacement));
        }
        else {
            lore.add(Language.getMsg(player, Messages.UPGRADES_LORE_REPLACEMENT_INSUFFICIENT_MONEY).replace("{currency}", currencyMsg).replace("{color}", replacement));
        }
        itemMeta.setLore((List)lore);
        itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        int intValue = -1;
        if (team.getTeamUpgradeTiers().containsKey(this.getName())) {
            intValue = team.getTeamUpgradeTiers().get(this.getName());
        }
        if (this.getTiers().size() - 1 > intValue) {
            final UpgradeTier upgradeTier = this.getTiers().get(intValue + 1);
            final int money = UpgradesManager.getMoney(player, upgradeTier.getCurrency());
            if (money < upgradeTier.getCost()) {
                Sounds.playSound("shop-insufficient-money", player);
                player.sendMessage(Language.getMsg(player, Messages.SHOP_INSUFFICIENT_MONEY).replace("{currency}", UpgradesManager.getCurrencyMsg(player, upgradeTier)).replace("{amount}", String.valueOf(upgradeTier.getCost() - money)));
                player.closeInventory();
                return;
            }
            if (upgradeTier.getCurrency() == Material.AIR) {
                BeaconBattle.getEconomy().buyAction(player, upgradeTier.getCost());
            }
            else {
                BeaconBattle.getAPI().getShopUtil().takeMoney(player, upgradeTier.getCurrency(), upgradeTier.getCost());
            }
            if (team.getTeamUpgradeTiers().containsKey(this.getName())) {
                team.getTeamUpgradeTiers().replace(this.getName(), team.getTeamUpgradeTiers().get(this.getName()) + 1);
            }
            else {
                team.getTeamUpgradeTiers().put(this.getName(), 0);
            }
            Sounds.playSound("shop-bought", player);
            final Iterator<UpgradeAction> iterator = upgradeTier.getUpgradeActions().iterator();
            while (iterator.hasNext()) {
                iterator.next().onBuy(team);
            }
            player.closeInventory();
            for (final Player player2 : team.getMembers()) {
                player2.sendMessage(Language.getMsg(player2, Messages.UPGRADES_UPGRADE_BOUGHT_CHAT).replace("{player}", player.getDisplayName()).replace("{upgradeName}", ChatColor.stripColor(Language.getMsg(player2, Messages.UPGRADES_UPGRADE_TIER_ITEM_NAME.replace("{name}", this.getName().replace("upgrade-", "")).replace("{tier}", upgradeTier.getName())))).replace("{color}", ""));
            }
            Bukkit.getPluginManager().callEvent((Event)new UpgradeBuyEvent(this, player, team));
        }
    }
    
    public boolean addTier(final UpgradeTier upgradeTier) {
        final Iterator<UpgradeTier> iterator = this.tiers.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equalsIgnoreCase(upgradeTier.getName())) {
                return false;
            }
        }
        this.tiers.add(upgradeTier);
        return true;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public List<UpgradeTier> getTiers() {
        return Collections.unmodifiableList((List<? extends UpgradeTier>)this.tiers);
    }
}
