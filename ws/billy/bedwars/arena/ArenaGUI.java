

package ws.billy.bedwars.arena;

import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.Inventory;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import java.util.List;
import java.util.ArrayList;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class ArenaGUI
{
    private static YamlConfiguration yml;
    private static HashMap<UUID, Long> antiCalledTwice;
    
    public static void refreshInv(final Player player, final IArena arena, final int n) {
        if (player == null) {
            return;
        }
        if (player.getOpenInventory() == null) {
            return;
        }
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof ArenaSelectorHolder)) {
            return;
        }
        final ArenaSelectorHolder arenaSelectorHolder = (ArenaSelectorHolder)player.getOpenInventory().getTopInventory().getHolder();
        ArrayList<IArena> list;
        if (arenaSelectorHolder.getGroup().equalsIgnoreCase("default")) {
            list = new ArrayList<IArena>(Arena.getArenas());
        }
        else {
            list = new ArrayList<IArena>();
            for (final IArena arena2 : Arena.getArenas()) {
                if (arena2.getGroup().equalsIgnoreCase(arenaSelectorHolder.getGroup())) {
                    list.add(arena2);
                }
            }
        }
        final List<IArena> sorted = Arena.getSorted(list);
        int n2 = 0;
        for (final Integer n3 : getUsedSlots()) {
            player.getOpenInventory().getTopInventory().setItem((int)n3, new ItemStack(Material.AIR));
            if (n2 >= sorted.size()) {
                continue;
            }
            String replacement = null;
            switch (sorted.get(n2).getStatus()) {
                case waiting: {
                    replacement = "waiting";
                    break;
                }
                case playing: {
                    replacement = "playing";
                    break;
                }
                case starting: {
                    replacement = "starting";
                    break;
                }
                default: {
                    continue;
                }
            }
            final ItemStack itemStack = BeaconBattle.nms.createItemStack(ArenaGUI.yml.getString("arena-gui.%path%.material".replace("%path%", replacement)), 1, (short)ArenaGUI.yml.getInt("arena-gui.%path%.data".replace("%path%", replacement)));
            if (ArenaGUI.yml.getBoolean("arena-gui.%path%.enchanted".replace("%path%", replacement))) {
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.addEnchant(Enchantment.LURE, 1, true);
                itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
                itemStack.setItemMeta(itemMeta);
            }
            final ItemMeta itemMeta2 = itemStack.getItemMeta();
            itemMeta2.setDisplayName(Language.getMsg(player, Messages.ARENA_GUI_ARENA_CONTENT_NAME).replace("{name}", sorted.get(n2).getDisplayName()));
            final ArrayList<String> lore = new ArrayList<String>();
            for (final String s : Language.getList(player, Messages.ARENA_GUI_ARENA_CONTENT_LORE)) {
                if (!s.contains("{group}") || !sorted.get(n2).getGroup().equalsIgnoreCase("default")) {
                    lore.add(s.replace("{on}", String.valueOf((arena != null) ? ((arena == sorted.get(n2)) ? n : sorted.get(n2).getPlayers().size()) : sorted.get(n2).getPlayers().size())).replace("{max}", String.valueOf(sorted.get(n2).getMaxPlayers())).replace("{status}", sorted.get(n2).getDisplayStatus(Language.getPlayerLanguage(player))).replace("{group}", sorted.get(n2).getGroup()));
                }
            }
            itemMeta2.setLore((List)lore);
            itemStack.setItemMeta(itemMeta2);
            player.getOpenInventory().getTopInventory().setItem((int)n3, BeaconBattle.nms.addCustomData(itemStack, "arena=" + sorted.get(n2).getArenaName()));
            ++n2;
        }
        player.updateInventory();
    }
    
    public static void openGui(final Player player, final String s) {
        if (preventCalledTwice(player)) {
            return;
        }
        updateCalledTwice(player);
        int int1 = BeaconBattle.config.getYml().getInt("arena-gui.settings.inv-size");
        if (int1 % 9 != 0) {
            int1 = 27;
        }
        if (int1 > 54) {
            int1 = 54;
        }
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)new ArenaSelectorHolder(s), int1, Language.getMsg(player, Messages.ARENA_GUI_INV_NAME));
        final ItemStack addCustomData = BeaconBattle.nms.addCustomData(BeaconBattle.nms.createItemStack(BeaconBattle.config.getString("arena-gui.%path%.material".replace("%path%", "skipped-slot")), 1, (byte)BeaconBattle.config.getInt("arena-gui.%path%.data".replace("%path%", "skipped-slot"))), "RUNCOMMAND_bw join random");
        final ItemMeta itemMeta = addCustomData.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', BeaconBattle.config.getString("server-ip")));
        itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
        addCustomData.setItemMeta(itemMeta);
        final List<Integer> usedSlots = getUsedSlots();
        for (int i = 0; i < inventory.getSize(); ++i) {
            if (!usedSlots.contains(i)) {
                inventory.setItem(i, addCustomData);
            }
        }
        player.openInventory(inventory);
        refreshInv(player, null, 0);
        Sounds.playSound("arena-selector-open", player);
    }
    
    @NotNull
    private static List<Integer> getUsedSlots() {
        final ArrayList<Integer> list = new ArrayList<Integer>();
        for (final String s : BeaconBattle.config.getString("arena-gui.settings.use-slots").split(",")) {
            try {
                list.add(Integer.parseInt(s));
            }
            catch (Exception ex) {}
        }
        return list;
    }
    
    private static boolean preventCalledTwice(@NotNull final Player player) {
        return ArenaGUI.antiCalledTwice.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }
    
    private static void updateCalledTwice(@NotNull final Player player) {
        if (ArenaGUI.antiCalledTwice.containsKey(player.getUniqueId())) {
            ArenaGUI.antiCalledTwice.replace(player.getUniqueId(), System.currentTimeMillis() + 2000L);
        }
        else {
            ArenaGUI.antiCalledTwice.put(player.getUniqueId(), System.currentTimeMillis() + 2000L);
        }
    }
    
    static {
        ArenaGUI.yml = BeaconBattle.config.getYml();
        ArenaGUI.antiCalledTwice = new HashMap<UUID, Long>();
    }
    
    public static class ArenaSelectorHolder implements InventoryHolder
    {
        private String group;
        
        public ArenaSelectorHolder(final String group) {
            this.group = group;
        }
        
        public String getGroup() {
            return this.group;
        }
        
        public Inventory getInventory() {
            return null;
        }
    }
}
