

package ws.billy.bedwars.arena;

import java.util.Map;
import org.bukkit.Bukkit;
import java.util.Iterator;

import org.bukkit.Material;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.UUID;

class PlayerGoods
{
    private UUID uuid;
    private int level;
    private int foodLevel;
    private double health;
    private double healthscale;
    private float exp;
    private HashMap<ItemStack, Integer> items;
    private List<PotionEffect> potions;
    private ItemStack[] armor;
    private HashMap<ItemStack, Integer> enderchest;
    private GameMode gamemode;
    private boolean allowFlight;
    private boolean flying;
    private String displayName;
    private String tabName;
    private static HashMap<UUID, PlayerGoods> playerGoods;
    
    PlayerGoods(final Player player, final boolean b) {
        this.items = new HashMap<ItemStack, Integer>();
        this.potions = new ArrayList<PotionEffect>();
        this.enderchest = new HashMap<ItemStack, Integer>();
        if (hasGoods(player)) {
            BeaconBattle.plugin.getLogger().severe(player.getName() + " is already having a PlayerGoods vault :|");
            return;
        }
        this.uuid = player.getUniqueId();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.health = player.getHealth();
        this.healthscale = player.getHealthScale();
        this.foodLevel = player.getFoodLevel();
        PlayerGoods.playerGoods.put(player.getUniqueId(), this);
        int i = 0;
        for (final ItemStack key : player.getInventory()) {
            if (key != null && key.getType() != Material.AIR) {
                this.items.put(key, i);
            }
            ++i;
        }
        for (final PotionEffect potionEffect : player.getActivePotionEffects()) {
            this.potions.add(potionEffect);
            if (b) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
        this.armor = player.getInventory().getArmorContents();
        int j = 0;
        for (final ItemStack key2 : player.getEnderChest()) {
            if (key2 != null && key2.getType() != Material.AIR) {
                this.enderchest.put(key2, j);
            }
            ++j;
        }
        this.gamemode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.flying = player.isFlying();
        this.tabName = player.getPlayerListName();
        this.displayName = player.getDisplayName();
        if (b) {
            player.setExp(0.0f);
            player.setLevel(0);
            player.setHealthScale(20.0);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents((ItemStack[])null);
            player.getEnderChest().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
    
    static boolean hasGoods(final Player player) {
        return PlayerGoods.playerGoods.containsKey(player.getUniqueId());
    }
    
    static PlayerGoods getPlayerGoods(final Player player) {
        return PlayerGoods.playerGoods.get(player.getUniqueId());
    }
    
    void restore() {
        final Player player = Bukkit.getPlayer(this.uuid);
        PlayerGoods.playerGoods.remove(player.getUniqueId());
        final Iterator iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            player.removePotionEffect(iterator.next().getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.setLevel(this.level);
        player.setExp(this.exp);
        player.setHealthScale(this.healthscale);
        try {
            player.setHealth(this.health);
        }
        catch (Exception ex) {
            BeaconBattle.plugin.getLogger().severe("Something went wrong when restoring player health: " + this.health + ". Giving default of: 20");
            player.setHealth(20.0);
        }
        player.setFoodLevel(this.foodLevel);
        if (!this.items.isEmpty()) {
            for (final Map.Entry<ItemStack, Integer> entry : this.items.entrySet()) {
                player.getInventory().setItem((int)entry.getValue(), (ItemStack)entry.getKey());
            }
            player.updateInventory();
            this.items.clear();
        }
        if (!this.potions.isEmpty()) {
            final Iterator<PotionEffect> iterator3 = this.potions.iterator();
            while (iterator3.hasNext()) {
                player.addPotionEffect((PotionEffect)iterator3.next());
            }
            this.potions.clear();
        }
        player.getEnderChest().clear();
        if (!this.enderchest.isEmpty()) {
            for (final Map.Entry<ItemStack, Integer> entry2 : this.enderchest.entrySet()) {
                player.getEnderChest().setItem((int)entry2.getValue(), (ItemStack)entry2.getKey());
            }
            this.enderchest.clear();
        }
        player.getInventory().setArmorContents(this.armor);
        player.setGameMode(this.gamemode);
        player.setAllowFlight(this.allowFlight);
        player.setFlying(this.flying);
        final Iterator<Player> iterator5 = Bukkit.getOnlinePlayers().iterator();
        while (iterator5.hasNext()) {
            BeaconBattle.nms.showPlayer(player, iterator5.next());
        }
        player.setDisplayName(this.displayName);
        player.setPlayerListName(this.tabName);
        this.uuid = null;
        this.items = null;
        this.potions = null;
        this.armor = null;
        this.enderchest = null;
    }
    
    static {
        PlayerGoods.playerGoods = new HashMap<UUID, PlayerGoods>();
    }
}
