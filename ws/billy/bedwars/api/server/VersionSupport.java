

package ws.billy.bedwars.api.server;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.Nullable;
import org.bukkit.block.BlockState;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;
import ws.billy.bedwars.api.arena.team.TeamColor;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import ws.billy.bedwars.api.exceptions.InvalidEffectException;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.entity.Despawnable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;

public abstract class VersionSupport
{
    private static String name2;
    private Effect eggBridge;
    private static ConcurrentHashMap<UUID, Despawnable> despawnables;
    private Plugin plugin;
    
    public VersionSupport(final Plugin plugin, final String name2) {
        VersionSupport.name2 = name2;
        this.plugin = plugin;
    }
    
    protected void loadDefaultEffects() {
        try {
            this.setEggBridgeEffect("MOBSPAWNER_FLAMES");
        }
        catch (InvalidEffectException ex) {
            ex.printStackTrace();
        }
    }
    
    public abstract void registerCommand(final String p0, final Command p1);
    
    public abstract void sendTitle(final Player p0, final String p1, final String p2, final int p3, final int p4, final int p5);
    
    public abstract void playAction(final Player p0, final String p1);
    
    public abstract boolean isBukkitCommandRegistered(final String p0);
    
    public abstract ItemStack getItemInHand(final Player p0);
    
    public abstract void hideEntity(final Entity p0, final Player p1);
    
    public abstract boolean isArmor(final ItemStack p0);
    
    public abstract boolean isTool(final ItemStack p0);
    
    public abstract boolean isSword(final ItemStack p0);
    
    public abstract boolean isAxe(final ItemStack p0);
    
    public abstract boolean isBow(final ItemStack p0);
    
    public abstract boolean isProjectile(final ItemStack p0);
    
    public abstract void registerEntities();
    
    public abstract void spawnShop(final Location p0, final String p1, final List<Player> p2, final IArena p3);
    
    public abstract double getDamage(final ItemStack p0);
    
    public abstract void spawnSilverfish(final Location p0, final ITeam p1, final double p2, final double p3, final int p4, final double p5);
    
    public abstract void spawnIronGolem(final Location p0, final ITeam p1, final double p2, final double p3, final int p4);
    
    public abstract void hidePlayer(final Player p0, final List<Player> p1);
    
    public abstract void hidePlayer(final Player p0, final Player p1);
    
    public abstract void showPlayer(final Player p0, final Player p1);
    
    public abstract void showPlayer(final Player p0, final List<Player> p1);
    
    public boolean isDespawnable(final Entity entity) {
        return VersionSupport.despawnables.get(entity.getUniqueId()) != null;
    }
    
    public abstract void minusAmount(final Player p0, final ItemStack p1, final int p2);
    
    public abstract void setSource(final TNTPrimed p0, final Player p1);
    
    public abstract void voidKill(final Player p0);
    
    public abstract void hideArmor(final Player p0, final Player p1);
    
    public abstract void showArmor(final Player p0, final Player p1);
    
    public abstract void spawnDragon(final Location p0, final ITeam p1);
    
    public abstract void colorBed(final ITeam p0);
    
    public abstract void registerTntWhitelist();
    
    public Effect eggBridge() {
        return this.eggBridge;
    }
    
    public void setEggBridgeEffect(final String s) {
        try {
            this.eggBridge = Effect.valueOf(s);
        }
        catch (Exception ex) {
            throw new InvalidEffectException(s);
        }
    }
    
    public abstract void setBlockTeamColor(final Block p0, final TeamColor p1);
    
    public abstract void setCollide(final Player p0, final IArena p1, final boolean p2);
    
    public abstract ItemStack addCustomData(final ItemStack p0, final String p1);
    
    public abstract boolean isCustomBeaconBattleItem(final ItemStack p0);
    
    public abstract String getCustomData(final ItemStack p0);
    
    public abstract ItemStack setSkullOwner(final ItemStack p0, final Player p1);
    
    public abstract ItemStack colourItem(final ItemStack p0, final ITeam p1);
    
    public abstract ItemStack createItemStack(final String p0, final int p1, final short p2);
    
    public abstract void teamCollideRule(final Team p0);
    
    public boolean isPlayerHead(final String s, final int n) {
        return s.equalsIgnoreCase("PLAYER_HEAD");
    }
    
    public abstract Material materialFireball();
    
    public abstract Material materialPlayerHead();
    
    public abstract Material materialSnowball();
    
    public abstract Material materialGoldenHelmet();
    
    public abstract Material materialGoldenChestPlate();
    
    public abstract Material materialGoldenLeggings();
    
    public abstract Material materialCake();
    
    public abstract Material materialCraftingTable();
    
    public abstract Material materialEnchantingTable();
    
    public boolean isBed(final Material material) {
        return material.toString().contains("_BED");
    }
    
    public boolean itemStackDataCompare(final ItemStack itemStack, final short n) {
        return true;
    }
    
    public void setJoinSignBackgroundBlockData(final BlockState blockState, final byte b) {
    }
    
    public abstract void setJoinSignBackground(final BlockState p0, final Material p1);
    
    public abstract Material woolMaterial();
    
    public abstract String getShopUpgradeIdentifier(final ItemStack p0);
    
    public abstract ItemStack setShopUpgradeIdentifier(final ItemStack p0, final String p1);
    
    public abstract ItemStack getPlayerHead(final Player p0, @Nullable final ItemStack p1);
    
    public abstract void invisibilityFix(final Player p0, final IArena p1);
    
    public abstract String getInventoryName(final InventoryEvent p0);
    
    public abstract void setUnbreakable(final ItemMeta p0);
    
    public ConcurrentHashMap<UUID, Despawnable> getDespawnablesList() {
        return VersionSupport.despawnables;
    }
    
    public static String getName() {
        return VersionSupport.name2;
    }
    
    public abstract int getVersion();
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public abstract void registerVersionListeners();
    
    public abstract String getMainLevel();
    
    static {
        VersionSupport.despawnables = new ConcurrentHashMap<UUID, Despawnable>();
    }
}
