

package ws.billy.bedwars.arena;

import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.WorldBorder;
import ws.billy.bedwars.stats.PlayerStats;
import ws.billy.bedwars.support.papi.SupportPAPI;

import java.time.Instant;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.jetbrains.annotations.NotNull;
import org.bukkit.inventory.Inventory;
import java.util.Iterator;
import ws.billy.bedwars.configuration.Sounds;
import java.util.ArrayList;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.configuration.file.YamlConfiguration;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.block.BlockFace;
import ws.billy.bedwars.api.exceptions.InvalidMaterialException;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import java.util.Random;
import org.bukkit.Color;
import com.google.common.io.ByteArrayDataOutput;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import com.google.common.io.ByteStreams;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.entity.Player;

public class Misc
{
    public static void moveToLobbyOrKick(final Player player) {
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            if (!player.getWorld().getName().equalsIgnoreCase(BeaconBattle.config.getLobbyWorldName())) {
                player.teleport(BeaconBattle.config.getConfigLoc("lobbyLoc"));
                final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
                if (arenaByPlayer != null) {
                    if (arenaByPlayer.isSpectator(player)) {
                        arenaByPlayer.removeSpectator(player, false);
                    }
                    else {
                        arenaByPlayer.removePlayer(player, false);
                    }
                }
            }
            else {
                forceKick(player);
            }
            return;
        }
        forceKick(player);
    }
    
    public static void forceKick(final Player player) {
        final ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(BeaconBattle.config.getYml().getString("lobbyServer"));
        player.sendPluginMessage((Plugin)BeaconBattle.plugin, "BungeeCord", dataOutput.toByteArray());
        if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                if (player.isOnline()) {
                    player.kickPlayer(Language.getMsg(player, Messages.ARENA_RESTART_PLAYER_KICK));
                }
            }, 120L);
        }
    }
    
    public static void launchFirework(final Player player) {
        final Color[] array = { Color.WHITE, Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.RED, Color.YELLOW, Color.BLACK, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE };
        final Random random = new Random();
        final Firework firework = (Firework)player.getWorld().spawn(player.getEyeLocation(), (Class)Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(1);
        fireworkMeta.addEffect(FireworkEffect.builder().withFade(array[random.nextInt(array.length - 1)]).withTrail().withColor(array[random.nextInt(array.length - 1)]).with(FireworkEffect.Type.BALL_LARGE).build());
        firework.setFireworkMeta(fireworkMeta);
        firework.setVelocity(player.getEyeLocation().getDirection());
    }
    
    public static void launchFirework(final Location location) {
        final Color[] array = { Color.WHITE, Color.AQUA, Color.BLUE, Color.FUCHSIA, Color.GRAY, Color.GREEN, Color.LIME, Color.RED, Color.YELLOW, Color.BLACK, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE, Color.PURPLE };
        final Random random = new Random();
        final Firework firework = (Firework)location.getWorld().spawn(location, (Class)Firework.class);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.setPower(1);
        fireworkMeta.addEffect(FireworkEffect.builder().withFade(array[random.nextInt(array.length - 1)]).withTrail().withColor(array[random.nextInt(array.length - 1)]).with(FireworkEffect.Type.BALL_LARGE).build());
        firework.setFireworkMeta(fireworkMeta);
    }
    
    public static String replaceFirst(final String s, final String s2, final String replacement) {
        return s.replaceFirst("(?s)" + s2 + "(?!.*?" + s2 + ")", replacement);
    }
    
    static ItemStack createItem(final Material material, final byte b, final boolean b2, final String displayName, final List<String> lore, final Player player, final String str, final String str2) {
        ItemStack itemStack = new ItemStack(material, 1, (short)b);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore((List)lore);
        if (b2) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        }
        itemStack.setItemMeta(itemMeta);
        if (!str2.isEmpty() && !str.isEmpty()) {
            itemStack = BeaconBattle.nms.addCustomData(itemStack, str + "_" + str2);
        }
        if (player != null && BeaconBattle.nms.isPlayerHead(material.toString(), b)) {
            itemStack = BeaconBattle.nms.getPlayerHead(player, itemStack);
        }
        return itemStack;
    }
    
    public static ItemStack createItemStack(final String s, final int n, final String displayName, final List<String> lore, final boolean b, final String s2) {
        Material value;
        try {
            value = Material.valueOf(s);
        }
        catch (Exception ex) {
            throw new InvalidMaterialException(s);
        }
        ItemStack addCustomData = new ItemStack(value, 1, (short)n);
        final ItemMeta itemMeta = addCustomData.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore((List)lore);
        if (b) {
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        }
        addCustomData.setItemMeta(itemMeta);
        if (!s2.isEmpty()) {
            addCustomData = BeaconBattle.nms.addCustomData(addCustomData, s2);
        }
        return addCustomData;
    }
    
    public static BlockFace getDirection(final Location location) {
        int n = (int)location.getYaw();
        if (n < 0) {
            n += 360;
        }
        if (0 <= n && n < 22) {
            return BlockFace.SOUTH;
        }
        if (22 <= n && n < 67) {
            return BlockFace.SOUTH;
        }
        if (67 <= n && n < 112) {
            return BlockFace.WEST;
        }
        if (112 <= n && n < 157) {
            return BlockFace.NORTH;
        }
        if (157 <= n && n < 202) {
            return BlockFace.NORTH;
        }
        if (202 <= n && n < 247) {
            return BlockFace.NORTH;
        }
        if (247 <= n && n < 292) {
            return BlockFace.EAST;
        }
        if (292 <= n && n < 337) {
            return BlockFace.SOUTH;
        }
        if (337 <= n && n < 360) {
            return BlockFace.SOUTH;
        }
        return BlockFace.SOUTH;
    }
    
    public static boolean isProjectile(final Material material) {
        return Material.EGG == material || BeaconBattle.nms.materialFireball() == material || BeaconBattle.nms.materialSnowball() == material || Material.ARROW == material;
    }
    
    public static TextComponent msgHoverClick(final String s, final String s2, final String s3, final ClickEvent.Action action) {
        final TextComponent textComponent = new TextComponent(s);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s2).create()));
        textComponent.setClickEvent(new ClickEvent(action, s3));
        return textComponent;
    }
    
    public static void addDefaultStatsItem(final YamlConfiguration yamlConfiguration, final int i, final Material material, final int j, final String replacement) {
        yamlConfiguration.addDefault("stats-gui.%path%.material".replace("%path%", replacement), (Object)material.toString());
        yamlConfiguration.addDefault("stats-gui.%path%.data".replace("%path%", replacement), (Object)j);
        yamlConfiguration.addDefault("stats-gui.%path%.slot".replace("%path%", replacement), (Object)i);
    }
    
    public static void openStatsGUI(final Player player) {
        final Iterator<String> iterator;
        String s;
        final ItemStack itemStack;
        final ItemMeta itemMeta;
        ArrayList<String> lore;
        final Iterator<String> iterator2;
        final Inventory inventory;
        Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, () -> {
            Bukkit.createInventory((InventoryHolder)null, BeaconBattle.config.getInt("stats-gui.inv-size"), replaceStatsPlaceholders(player, Language.getMsg(player, Messages.PLAYER_STATS_GUI_INV_NAME), true));
            BeaconBattle.config.getYml().getConfigurationSection("stats-gui").getKeys(false).iterator();
            while (iterator.hasNext()) {
                s = iterator.next();
                if ("stats-gui.inv-size".contains(s)) {
                    continue;
                }
                else {
                    BeaconBattle.nms.createItemStack(BeaconBattle.config.getYml().getString("stats-gui.%path%.material".replace("%path%", s)).toUpperCase(), 1, (short)BeaconBattle.config.getInt("stats-gui.%path%.data".replace("%path%", s)));
                    itemStack.getItemMeta();
                    itemMeta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
                    itemMeta.setDisplayName(replaceStatsPlaceholders(player, Language.getMsg(player, Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-name"), true));
                    lore = new ArrayList<String>();
                    Language.getList(player, Messages.PLAYER_STATS_GUI_PATH + "-" + s + "-lore").iterator();
                    while (iterator2.hasNext()) {
                        lore.add(replaceStatsPlaceholders(player, iterator2.next(), true));
                    }
                    itemMeta.setLore((List)lore);
                    itemStack.setItemMeta(itemMeta);
                    inventory.setItem(BeaconBattle.config.getInt("stats-gui.%path%.slot".replace("%path%", s)), itemStack);
                }
            }
            player.openInventory(inventory);
            Sounds.playSound("stats-gui-open", player);
        });
    }
    
    public static String replaceStatsPlaceholders(final Player player, @NotNull String s, final boolean b) {
        final PlayerStats value = BeaconBattle.getStatsManager().get(player.getUniqueId());
        if (s.contains("{kills}")) {
            s = s.replace("{kills}", String.valueOf(value.getKills()));
        }
        if (s.contains("{deaths}")) {
            s = s.replace("{deaths}", String.valueOf(value.getDeaths()));
        }
        if (s.contains("{losses}")) {
            s = s.replace("{losses}", String.valueOf(value.getLosses()));
        }
        if (s.contains("{wins}")) {
            s = s.replace("{wins}", String.valueOf(value.getWins()));
        }
        if (s.contains("{finalKills}")) {
            s = s.replace("{finalKills}", String.valueOf(value.getFinalKills()));
        }
        if (s.contains("{finalDeaths}")) {
            s = s.replace("{finalDeaths}", String.valueOf(value.getFinalDeaths()));
        }
        if (s.contains("{bedsDestroyed}")) {
            s = s.replace("{bedsDestroyed}", String.valueOf(value.getBedsDestroyed()));
        }
        if (s.contains("{gamesPlayed}")) {
            s = s.replace("{gamesPlayed}", String.valueOf(value.getGamesPlayed()));
        }
        if (s.contains("{firstPlay}")) {
            s = s.replace("{firstPlay}", new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((value.getFirstPlay() != null) ? Timestamp.from(value.getFirstPlay()) : Timestamp.from(Instant.now())));
        }
        if (s.contains("{lastPlay}")) {
            s = s.replace("{lastPlay}", new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((value.getLastPlay() != null) ? Timestamp.from(value.getLastPlay()) : Timestamp.from(Instant.now())));
        }
        if (s.contains("{player}")) {
            s = s.replace("{player}", player.getDisplayName());
        }
        if (s.contains("{prefix}")) {
            s = s.replace("{prefix}", BeaconBattle.getChatSupport().getPrefix(player));
        }
        return b ? SupportPAPI.getSupportPAPI().replace(player, s) : s;
    }
    
    public static boolean isNumber(final String s) {
        try {
            Double.parseDouble(s);
        }
        catch (Exception ex) {
            try {
                Integer.parseInt(s);
            }
            catch (Exception ex2) {
                try {
                    Long.parseLong(s);
                }
                catch (Exception ex3) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isOutsideOfBorder(final Location location) {
        final WorldBorder worldBorder = location.getWorld().getWorldBorder();
        final double n = worldBorder.getSize() / 2.0;
        return worldBorder.getCenter().distanceSquared(location) >= n * n;
    }
    
    public static boolean isBuildProtected(final Location location, final IArena arena) {
        for (final ITeam team : arena.getTeams()) {
            if (team.getSpawn().distance(location) <= arena.getConfig().getInt("spawn-protection")) {
                return true;
            }
            if (team.getShop().distance(location) <= arena.getConfig().getInt("shop-protection")) {
                return true;
            }
            if (team.getTeamUpgrades().distance(location) <= arena.getConfig().getInt("upgrades-protection")) {
                return true;
            }
            final Iterator<IGenerator> iterator2 = team.getGenerators().iterator();
            while (iterator2.hasNext()) {
                if (iterator2.next().getLocation().distance(location) <= 1.0) {
                    return true;
                }
            }
        }
        final Iterator<IGenerator> iterator3 = arena.getOreGenerators().iterator();
        while (iterator3.hasNext()) {
            if (iterator3.next().getLocation().distance(location) <= 1.0) {
                return true;
            }
        }
        return isOutsideOfBorder(location);
    }
    
    public static Location minLoc(final Location location, final Location location2) {
        if (location.getWorld() != location2.getWorld()) {
            throw new IllegalStateException("Locations are not in the same world!");
        }
        return new Location(location.getWorld(), Math.min(location.getX(), location2.getX()), Math.min(location.getY(), location2.getY()), Math.min(location.getZ(), location2.getZ()));
    }
    
    public static Location maxLoc(final Location location, final Location location2) {
        if (location.getWorld() != location2.getWorld()) {
            throw new IllegalStateException("Locations are not in the same world!");
        }
        return new Location(location.getWorld(), Math.max(location.getX(), location2.getX()), Math.max(location.getY(), location2.getY()), Math.max(location.getZ(), location2.getZ()));
    }
}
