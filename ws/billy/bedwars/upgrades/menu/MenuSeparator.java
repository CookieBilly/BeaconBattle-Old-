

package ws.billy.bedwars.upgrades.menu;

import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import ws.billy.bedwars.upgrades.UpgradesManager;
import java.util.Collections;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.api.upgrades.MenuContent;

public class MenuSeparator implements MenuContent
{
    private ItemStack displayItem;
    private String name;
    private List<String> playerCommands;
    private List<String> consoleCommands;
    
    public MenuSeparator(final String s, final ItemStack itemStack) {
        this.playerCommands = new ArrayList<String>();
        this.consoleCommands = new ArrayList<String>();
        if (s == null) {
            return;
        }
        this.displayItem = BeaconBattle.nms.addCustomData(itemStack, "MCONT_" + s);
        this.name = s;
        Language.saveIfNotExists(Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + s.replace("separator-", ""), "&cName not set");
        Language.saveIfNotExists(Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + s.replace("separator-", ""), Collections.singletonList("&cLore not set"));
        if (UpgradesManager.getConfiguration().getYml().getStringList(s + ".on-click.player") != null) {
            this.playerCommands.addAll(UpgradesManager.getConfiguration().getYml().getStringList(s + ".on-click.player"));
        }
        if (UpgradesManager.getConfiguration().getYml().getStringList(s + ".on-click.console") != null) {
            this.consoleCommands.addAll(UpgradesManager.getConfiguration().getYml().getStringList(s + ".on-click.console"));
        }
    }
    
    @Override
    public ItemStack getDisplayItem(final Player player, final ITeam team) {
        final ItemStack itemStack = new ItemStack(this.displayItem);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Language.getMsg(player, Messages.UPGRADES_SEPARATOR_ITEM_NAME_PATH + this.name.replace("separator-", "")));
        itemMeta.setLore((List)Language.getList(player, Messages.UPGRADES_SEPARATOR_ITEM_LORE_PATH + this.name.replace("separator-", "")));
        itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    @Override
    public void onClick(final Player player, final ClickType clickType, final ITeam team) {
        for (final String s : this.playerCommands) {
            if (s.trim().isEmpty()) {
                continue;
            }
            Bukkit.dispatchCommand((CommandSender)player, s.replace("{player}", player.getDisplayName()).replace("{team}", (team == null) ? "null" : team.getDisplayName(Language.getPlayerLanguage(player))));
        }
        for (final String s2 : this.consoleCommands) {
            if (s2.trim().isEmpty()) {
                continue;
            }
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s2.replace("{player}", player.getDisplayName()).replace("{team}", (team == null) ? "null" : team.getDisplayName(Language.getPlayerLanguage(player))));
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
