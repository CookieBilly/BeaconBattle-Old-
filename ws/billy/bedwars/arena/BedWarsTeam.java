

package ws.billy.bedwars.arena;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.events.player.PlayerReSpawnEvent;
import ws.billy.bedwars.shop.ShopCache;
import org.bukkit.potion.PotionEffectType;
import ws.billy.bedwars.api.arena.generator.GeneratorType;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerFirstSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerTeleportEvent;
import ws.billy.bedwars.api.region.Cuboid;
import ws.billy.bedwars.api.language.Language;
import java.util.ArrayList;
import ws.billy.bedwars.api.upgrades.EnemyBaseEnterTrap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.HashMap;
import ws.billy.bedwars.api.arena.team.TeamEnchant;
import org.bukkit.potion.PotionEffect;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import ws.billy.bedwars.api.arena.team.TeamColor;
import org.bukkit.entity.Player;
import java.util.List;
import ws.billy.bedwars.api.arena.team.ITeam;

public class BeaconBattleTeam implements ITeam
{
    private List<Player> members;
    private TeamColor color;
    private Location spawn;
    private Location bed;
    private Location shop;
    private Location teamUpgrades;
    private String name;
    private Arena arena;
    private boolean bedDestroyed;
    private Vector killDropsLoc;
    private List<IGenerator> generators;
    private ConcurrentHashMap<String, Integer> teamUpgradeList;
    private List<PotionEffect> teamEffects;
    private List<PotionEffect> base;
    private List<TeamEnchant> bowsEnchantments;
    private List<TeamEnchant> swordsEnchantemnts;
    private List<TeamEnchant> armorsEnchantemnts;
    private HashMap<UUID, BedHolo> beds;
    private LinkedList<EnemyBaseEnterTrap> enemyBaseEnterTraps;
    private int dragons;
    private List<Player> membersCache;
    public static HashMap<UUID, Long> antiFallDamageAtRespawn;
    
    public BeaconBattleTeam(final String name, final TeamColor color, final Location spawn, final Location bed, final Location shop, final Location teamUpgrades, final Arena arena) {
        this.members = new ArrayList<Player>();
        this.bedDestroyed = false;
        this.killDropsLoc = null;
        this.generators = new ArrayList<IGenerator>();
        this.teamUpgradeList = new ConcurrentHashMap<String, Integer>();
        this.teamEffects = new ArrayList<PotionEffect>();
        this.base = new ArrayList<PotionEffect>();
        this.bowsEnchantments = new ArrayList<TeamEnchant>();
        this.swordsEnchantemnts = new ArrayList<TeamEnchant>();
        this.armorsEnchantemnts = new ArrayList<TeamEnchant>();
        this.beds = new HashMap<UUID, BedHolo>();
        this.enemyBaseEnterTraps = new LinkedList<EnemyBaseEnterTrap>();
        this.dragons = 1;
        this.membersCache = new ArrayList<Player>();
        if (arena == null) {
            return;
        }
        this.name = name;
        this.color = color;
        this.spawn = spawn;
        this.bed = bed;
        this.arena = arena;
        this.shop = shop;
        this.teamUpgrades = teamUpgrades;
        Language.saveIfNotExists("team-name-{arena}-{team}".replace("{arena}", this.getArena().getArenaName()).replace("{team}", this.getName()), name);
        arena.getRegionsList().add(new Cuboid(spawn, arena.getConfig().getInt("spawn-protection"), true));
        final Location arenaLoc = this.getArena().getConfig().getArenaLoc("Team." + this.getName() + "." + "kill-drops-loc");
        if (arenaLoc != null) {
            this.setKillDropsLocation(arenaLoc);
        }
    }
    
    @Override
    public int getSize() {
        return this.members.size();
    }
    
    @Override
    public void addPlayers(final Player... array) {
        if (array == null) {
            return;
        }
        for (final Player player : array) {
            if (player != null) {
                if (!this.members.contains(player)) {
                    this.members.add(player);
                }
                if (!this.membersCache.contains(player)) {
                    this.membersCache.add(player);
                }
                new BedHolo(player, this.getArena());
            }
        }
    }
    
    @Override
    public void firstSpawn(final Player player) {
        if (player == null) {
            return;
        }
        player.teleport(this.spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setGameMode(GameMode.SURVIVAL);
        this.sendDefaultInventory(player, true);
        Bukkit.getPluginManager().callEvent((Event)new PlayerFirstSpawnEvent(player, this.getArena(), this));
    }
    
    @Override
    public void spawnNPCs() {
        if (this.getMembers().isEmpty() && this.getArena().getConfig().getBoolean("disable-npcs-for-empty-teams")) {
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            BeaconBattle.nms.colorBed(this);
            BeaconBattle.nms.spawnShop(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Upgrade"), (this.getArena().getMaxInTeam() > 1) ? Messages.NPC_NAME_TEAM_UPGRADES : Messages.NPC_NAME_SOLO_UPGRADES, this.getArena().getPlayers(), this.getArena());
            BeaconBattle.nms.spawnShop(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Shop"), (this.getArena().getMaxInTeam() > 1) ? Messages.NPC_NAME_TEAM_SHOP : Messages.NPC_NAME_SOLO_SHOP, this.getArena().getPlayers(), this.getArena());
            return;
        }, 20L);
        final Cuboid cuboid = new Cuboid(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Upgrade"), 1, true);
        cuboid.setMinY(cuboid.getMinY() - 1);
        cuboid.setMaxY(cuboid.getMaxY() + 4);
        this.getArena().getRegionsList().add(cuboid);
        final Cuboid cuboid2 = new Cuboid(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + ".Shop"), 1, true);
        cuboid2.setMinY(cuboid2.getMinY() - 1);
        cuboid2.setMaxY(cuboid2.getMaxY() + 4);
        this.getArena().getRegionsList().add(cuboid2);
    }
    
    @Override
    public void reJoin(@NotNull final Player key) {
        this.members.add(Bukkit.getPlayer(key.getUniqueId()));
        BeaconBattle.nms.setCollide(key, this.arena, false);
        key.setAllowFlight(true);
        key.setFlying(true);
        this.arena.getRespawn().put(key, 5);
    }
    
    @Override
    public void sendDefaultInventory(final Player player, final boolean b) {
        if (b) {
            player.getInventory().clear();
        }
        for (final String s : BeaconBattle.config.getYml().getStringList((BeaconBattle.config.getYml().get("start-items-per-group." + this.arena.getGroup()) == null) ? "start-items-per-group.Default" : ("start-items-per-group." + this.arena.getGroup()))) {
            final String[] split = s.split(",");
            if (split.length != 0) {
                try {
                    ItemStack itemStack;
                    if (split.length > 1) {
                        try {
                            Integer.parseInt(split[1]);
                        }
                        catch (Exception ex) {
                            BeaconBattle.plugin.getLogger().severe(split[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack = new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1]));
                    }
                    else {
                        itemStack = new ItemStack(Material.valueOf(split[0]));
                    }
                    if (split.length > 2) {
                        try {
                            Integer.parseInt(split[2]);
                        }
                        catch (Exception ex2) {
                            BeaconBattle.plugin.getLogger().severe(split[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack.setAmount(Integer.parseInt(split[2]));
                    }
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (split.length > 3) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', split[3]));
                    }
                    BeaconBattle.nms.setUnbreakable(itemMeta);
                    itemStack.setItemMeta(itemMeta);
                    final ItemStack addCustomData = BeaconBattle.nms.addCustomData(itemStack, "DEFAULT_ITEM");
                    if (BeaconBattle.nms.isSword(addCustomData)) {
                        boolean b2 = false;
                        for (final ItemStack itemStack2 : player.getInventory().getContents()) {
                            if (itemStack2 != null) {
                                if (itemStack2.getType() != Material.AIR) {
                                    if (BeaconBattle.nms.isSword(itemStack2)) {
                                        b2 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (b2) {
                            continue;
                        }
                        player.getInventory().addItem(new ItemStack[] { addCustomData });
                    }
                    else if (BeaconBattle.nms.isBow(addCustomData)) {
                        boolean b3 = false;
                        for (final ItemStack itemStack3 : player.getInventory().getContents()) {
                            if (itemStack3 != null) {
                                if (itemStack3.getType() != Material.AIR) {
                                    if (BeaconBattle.nms.isBow(itemStack3)) {
                                        b3 = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (b3) {
                            continue;
                        }
                        player.getInventory().addItem(new ItemStack[] { addCustomData });
                    }
                    else {
                        player.getInventory().addItem(new ItemStack[] { addCustomData });
                    }
                }
                catch (Exception ex3) {}
            }
        }
        this.sendArmor(player);
    }
    
    @Override
    public void defaultSword(final Player player, final boolean b) {
        if (!b) {
            return;
        }
        for (final String s : BeaconBattle.config.getYml().getStringList((BeaconBattle.config.getYml().get("start-items-per-group." + this.arena.getGroup()) == null) ? "start-items-per-group.Default" : ("start-items-per-group." + this.arena.getGroup()))) {
            final String[] split = s.split(",");
            if (split.length != 0) {
                try {
                    ItemStack itemStack;
                    if (split.length > 1) {
                        try {
                            Integer.parseInt(split[1]);
                        }
                        catch (Exception ex) {
                            BeaconBattle.plugin.getLogger().severe(split[1] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack = new ItemStack(Material.valueOf(split[0]), Integer.parseInt(split[1]));
                    }
                    else {
                        itemStack = new ItemStack(Material.valueOf(split[0]));
                    }
                    if (split.length > 2) {
                        try {
                            Integer.parseInt(split[2]);
                        }
                        catch (Exception ex2) {
                            BeaconBattle.plugin.getLogger().severe(split[2] + " is not an integer at: " + s + " (config)");
                            continue;
                        }
                        itemStack.setAmount(Integer.parseInt(split[2]));
                    }
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    if (split.length > 3) {
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', split[3]));
                    }
                    BeaconBattle.nms.setUnbreakable(itemMeta);
                    itemStack.setItemMeta(itemMeta);
                    final ItemStack addCustomData = BeaconBattle.nms.addCustomData(itemStack, "DEFAULT_ITEM");
                    if (BeaconBattle.nms.isSword(addCustomData)) {
                        player.getInventory().addItem(new ItemStack[] { addCustomData });
                        break;
                    }
                    continue;
                }
                catch (Exception ex3) {}
            }
        }
    }
    
    public void spawnGenerators() {
        for (final String str : new String[] { "Iron", "Gold" }) {
            final GeneratorType value = GeneratorType.valueOf(str.toUpperCase());
            List<Location> arenaLocations = new ArrayList<Location>();
            if (this.getArena().getConfig().getYml().get("Team." + this.getName() + "." + str) instanceof String) {
                arenaLocations.add(this.getArena().getConfig().getArenaLoc("Team." + this.getName() + "." + str));
            }
            else {
                arenaLocations = this.getArena().getConfig().getArenaLocations("Team." + this.getName() + "." + str);
            }
            final Iterator<Location> iterator = arenaLocations.iterator();
            while (iterator.hasNext()) {
                this.generators.add(new OreGenerator(iterator.next(), this.getArena(), value, this));
            }
        }
    }
    
    @Override
    public void respawnMember(@NotNull final Player key) {
        if (BeaconBattleTeam.antiFallDamageAtRespawn.containsKey(key.getUniqueId())) {
            BeaconBattleTeam.antiFallDamageAtRespawn.replace(key.getUniqueId(), System.currentTimeMillis() + 3500L);
        }
        else {
            BeaconBattleTeam.antiFallDamageAtRespawn.put(key.getUniqueId(), System.currentTimeMillis() + 3500L);
        }
        key.teleport(this.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        key.setVelocity(new Vector(0, 0, 0));
        this.getArena().getRespawn().remove(key);
        if (key.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            key.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
        BeaconBattle.nms.setCollide(key, this.arena, true);
        key.setAllowFlight(false);
        key.setFlying(false);
        key.setHealth(20.0);
        BeaconBattle.nms.sendTitle(key, Language.getMsg(key, Messages.PLAYER_DIE_RESPAWNED_TITLE), "", 0, 20, 0);
        final ShopCache shopCache = ShopCache.getShopCache(key.getUniqueId());
        if (shopCache != null) {
            shopCache.managePermanentsAndDowngradables(this.getArena());
        }
        this.sendDefaultInventory(key, false);
        key.setHealth(20.0);
        if (!this.getBaseEffects().isEmpty()) {
            final Iterator<PotionEffect> iterator = this.getBaseEffects().iterator();
            while (iterator.hasNext()) {
                key.addPotionEffect((PotionEffect)iterator.next());
            }
        }
        if (!this.getTeamEffects().isEmpty()) {
            final Iterator<PotionEffect> iterator2 = this.getTeamEffects().iterator();
            while (iterator2.hasNext()) {
                key.addPotionEffect((PotionEffect)iterator2.next());
            }
        }
        if (!this.getBowsEnchantments().isEmpty()) {
            for (final ItemStack itemStack : key.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == Material.BOW) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        for (final TeamEnchant teamEnchant : this.getBowsEnchantments()) {
                            itemMeta.addEnchant(teamEnchant.getEnchantment(), teamEnchant.getAmplifier(), true);
                        }
                        itemStack.setItemMeta(itemMeta);
                    }
                    key.updateInventory();
                }
            }
        }
        if (!this.getSwordsEnchantments().isEmpty()) {
            for (final ItemStack itemStack2 : key.getInventory().getContents()) {
                if (itemStack2 != null) {
                    if (BeaconBattle.nms.isSword(itemStack2)) {
                        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
                        for (final TeamEnchant teamEnchant2 : this.getSwordsEnchantments()) {
                            itemMeta2.addEnchant(teamEnchant2.getEnchantment(), teamEnchant2.getAmplifier(), true);
                        }
                        itemStack2.setItemMeta(itemMeta2);
                    }
                    key.updateInventory();
                }
            }
        }
        if (!this.getArmorsEnchantments().isEmpty()) {
            for (final ItemStack itemStack3 : key.getInventory().getArmorContents()) {
                if (itemStack3 != null) {
                    if (BeaconBattle.nms.isArmor(itemStack3)) {
                        final ItemMeta itemMeta3 = itemStack3.getItemMeta();
                        for (final TeamEnchant teamEnchant3 : this.getArmorsEnchantments()) {
                            itemMeta3.addEnchant(teamEnchant3.getEnchantment(), teamEnchant3.getAmplifier(), true);
                        }
                        itemStack3.setItemMeta(itemMeta3);
                    }
                    key.updateInventory();
                }
            }
        }
        Bukkit.getPluginManager().callEvent((Event)new PlayerReSpawnEvent(key, this.getArena(), this));
        final Iterator<Player> iterator6;
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            if (this.getArena() != null) {
                BeaconBattle.nms.invisibilityFix(key, this.getArena());
                if (!BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets")) {
                    this.getArena().getShowTime().keySet().iterator();
                    while (iterator6.hasNext()) {
                        BeaconBattle.nms.hideArmor(iterator6.next(), key);
                    }
                }
            }
            return;
        }, 10L);
        Sounds.playSound("player-re-spawn", key);
    }
    
    private ItemStack createArmor(final Material material) {
        final ItemStack itemStack = new ItemStack(material);
        final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)itemStack.getItemMeta();
        leatherArmorMeta.setColor(this.color.bukkitColor());
        BeaconBattle.nms.setUnbreakable((ItemMeta)leatherArmorMeta);
        itemStack.setItemMeta((ItemMeta)leatherArmorMeta);
        return itemStack;
    }
    
    @Override
    public void sendArmor(final Player player) {
        if (player.getInventory().getHelmet() == null) {
            player.getInventory().setHelmet(this.createArmor(Material.LEATHER_HELMET));
        }
        if (player.getInventory().getChestplate() == null) {
            player.getInventory().setChestplate(this.createArmor(Material.LEATHER_CHESTPLATE));
        }
        if (player.getInventory().getLeggings() == null) {
            player.getInventory().setLeggings(this.createArmor(Material.LEATHER_LEGGINGS));
        }
        if (player.getInventory().getBoots() == null) {
            player.getInventory().setBoots(this.createArmor(Material.LEATHER_BOOTS));
        }
    }
    
    @Override
    public void addTeamEffect(final PotionEffectType potionEffectType, final int n, final int n2) {
        this.getTeamEffects().add(new PotionEffect(potionEffectType, n2, n));
        final Iterator<Player> iterator = this.getMembers().iterator();
        while (iterator.hasNext()) {
            iterator.next().addPotionEffect(new PotionEffect(potionEffectType, n2, n));
        }
    }
    
    @Override
    public void addBaseEffect(final PotionEffectType potionEffectType, final int n, final int n2) {
        this.getBaseEffects().add(new PotionEffect(potionEffectType, n2, n));
        for (final Player player : new ArrayList<Player>(this.getMembers())) {
            if (player.getLocation().distance(this.getBed()) <= this.getArena().getIslandRadius()) {
                final Iterator<PotionEffect> iterator2 = this.getBaseEffects().iterator();
                while (iterator2.hasNext()) {
                    player.addPotionEffect((PotionEffect)iterator2.next());
                }
            }
        }
    }
    
    @Override
    public void addBowEnchantment(final Enchantment enchantment, final int n) {
        this.getBowsEnchantments().add(new Enchant(enchantment, n));
        for (final Player player : this.getMembers()) {
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (itemStack.getType() == Material.BOW) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(enchantment, n, true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            player.updateInventory();
        }
    }
    
    @Override
    public void addSwordEnchantment(final Enchantment enchantment, final int n) {
        this.getSwordsEnchantments().add(new Enchant(enchantment, n));
        for (final Player player : this.getMembers()) {
            for (final ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null) {
                    if (BeaconBattle.nms.isSword(itemStack)) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(enchantment, n, true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            player.updateInventory();
        }
    }
    
    @Override
    public void addArmorEnchantment(final Enchantment enchantment, final int n) {
        this.getArmorsEnchantments().add(new Enchant(enchantment, n));
        for (final Player player : this.getMembers()) {
            for (final ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null) {
                    if (BeaconBattle.nms.isArmor(itemStack)) {
                        final ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(enchantment, n, true);
                        itemStack.setItemMeta(itemMeta);
                    }
                }
            }
            player.updateInventory();
        }
        if (!BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets")) {
            final Iterator<Player> iterator2;
            Player player2;
            final Iterator<Player> iterator3;
            final Iterator<Player> iterator4;
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                this.getMembers().iterator();
                while (iterator2.hasNext()) {
                    player2 = iterator2.next();
                    if (player2.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        this.getArena().getPlayers().iterator();
                        while (iterator3.hasNext()) {
                            BeaconBattle.nms.hideArmor(player2, iterator3.next());
                        }
                        this.getArena().getSpectators().iterator();
                        while (iterator4.hasNext()) {
                            BeaconBattle.nms.hideArmor(player2, iterator4.next());
                        }
                    }
                }
            }, 20L);
        }
    }
    
    @Override
    public boolean isMember(final Player player) {
        return player != null && this.members.contains(player);
    }
    
    @Override
    public boolean wasMember(final UUID obj) {
        if (obj == null) {
            return false;
        }
        final Iterator<Player> iterator = this.membersCache.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getUniqueId().equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isBedDestroyed() {
        return this.bedDestroyed;
    }
    
    @Override
    public Location getSpawn() {
        return this.spawn;
    }
    
    @Override
    public Location getShop() {
        return this.shop;
    }
    
    @Override
    public Location getTeamUpgrades() {
        return this.teamUpgrades;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDisplayName(final Language language) {
        final String m = language.m("team-name-{arena}-{team}".replace("{arena}", this.getArena().getArenaName()).replace("{team}", this.getName()));
        return (m == null) ? this.getName() : m;
    }
    
    @Override
    public TeamColor getColor() {
        return this.color;
    }
    
    @Override
    public List<Player> getMembers() {
        return this.members;
    }
    
    @Override
    public Location getBed() {
        return this.bed;
    }
    
    @Override
    public ConcurrentHashMap<String, Integer> getTeamUpgradeTiers() {
        return this.teamUpgradeList;
    }
    
    public BedHolo getBedHolo(@NotNull final Player player) {
        return this.beds.get(player.getUniqueId());
    }
    
    @Override
    public void setBedDestroyed(final boolean bedDestroyed) {
        if (!(this.bedDestroyed = bedDestroyed)) {
            if (!this.getBed().getBlock().getType().toString().contains("BED")) {
                BeaconBattle.plugin.getLogger().severe("Bed not set for team: " + this.getName() + " in arena: " + this.getArena().getArenaName());
                return;
            }
            BeaconBattle.nms.colorBed(this);
        }
        else {
            this.bed.getBlock().setType(Material.AIR);
            if (this.getArena().getConfig().getBoolean("disable-generator-for-empty-teams")) {
                final Iterator<IGenerator> iterator = this.getGenerators().iterator();
                while (iterator.hasNext()) {
                    iterator.next().disable();
                }
            }
        }
        for (final BedHolo bedHolo : this.beds.values()) {
            bedHolo.hide();
            bedHolo.show();
        }
    }
    
    @Deprecated
    @Override
    public IGenerator getIronGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.IRON).toArray();
        if (array.length == 0) {
            return null;
        }
        return array[0];
    }
    
    @Deprecated
    @Override
    public IGenerator getGoldGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.GOLD).toArray();
        if (array.length == 0) {
            return null;
        }
        return array[0];
    }
    
    @Deprecated
    @Override
    public IGenerator getEmeraldGenerator() {
        final IGenerator[] array = (IGenerator[])this.generators.stream().filter(generator -> generator.getType() == GeneratorType.EMERALD).toArray();
        if (array.length == 0) {
            return null;
        }
        return array[0];
    }
    
    @Deprecated
    @Override
    public void setEmeraldGenerator(final IGenerator generator) {
        this.generators.add(generator);
    }
    
    @Override
    public List<IGenerator> getGenerators() {
        return this.generators;
    }
    
    @Override
    public List<PotionEffect> getBaseEffects() {
        return this.base;
    }
    
    public List<PotionEffect> getTeamEffects() {
        return this.teamEffects;
    }
    
    @Override
    public List<TeamEnchant> getBowsEnchantments() {
        return this.bowsEnchantments;
    }
    
    @Override
    public List<TeamEnchant> getSwordsEnchantments() {
        return this.swordsEnchantemnts;
    }
    
    @Override
    public List<TeamEnchant> getArmorsEnchantments() {
        return this.armorsEnchantemnts;
    }
    
    @Override
    public Arena getArena() {
        return this.arena;
    }
    
    @Override
    public int getDragons() {
        return this.dragons;
    }
    
    @Override
    public void setDragons(final int dragons) {
        this.dragons = dragons;
    }
    
    @Override
    public List<Player> getMembersCache() {
        return this.membersCache;
    }
    
    public HashMap<UUID, BedHolo> getBeds() {
        return this.beds;
    }
    
    @Override
    public void destroyData() {
        this.members = null;
        this.spawn = null;
        this.bed = null;
        this.shop = null;
        this.teamUpgrades = null;
        final Iterator<IGenerator> iterator = new ArrayList<IGenerator>(this.generators).iterator();
        while (iterator.hasNext()) {
            iterator.next().destroyData();
        }
        this.arena = null;
        this.teamEffects = null;
        this.base = null;
        this.bowsEnchantments = null;
        this.swordsEnchantemnts = null;
        this.armorsEnchantemnts = null;
        this.enemyBaseEnterTraps.clear();
        this.membersCache = null;
    }
    
    @Override
    public void destroyBedHolo(@NotNull final Player player) {
        if (this.getBeds().get(player.getUniqueId()) != null) {
            this.getBeds().get(player.getUniqueId()).destroy();
        }
    }
    
    @Override
    public LinkedList<EnemyBaseEnterTrap> getActiveTraps() {
        return this.enemyBaseEnterTraps;
    }
    
    @Override
    public Vector getKillDropsLocation() {
        if (this.killDropsLoc != null) {
            return this.killDropsLoc;
        }
        final List<? super Object> list = this.generators.stream().filter(generator -> generator.getType() == GeneratorType.IRON || generator.getType() == GeneratorType.GOLD).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        if (list.isEmpty()) {
            return new Vector(this.getSpawn().getX(), this.getSpawn().getY(), this.getSpawn().getZ());
        }
        return new Vector(((IGenerator)list.get(0)).getLocation().getX(), ((IGenerator)list.get(0)).getLocation().getY(), ((IGenerator)list.get(0)).getLocation().getZ());
    }
    
    @Override
    public void setKillDropsLocation(final Vector vector) {
        if (vector == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(vector.getBlockX() + 0.5, (double)vector.getBlockY(), vector.getBlockZ() + 0.5);
    }
    
    public void setKillDropsLocation(final Location location) {
        if (location == null) {
            this.killDropsLoc = null;
            return;
        }
        this.killDropsLoc = new Vector(location.getBlockX() + 0.5, (double)location.getBlockY(), location.getBlockZ() + 0.5);
    }
    
    static {
        BeaconBattleTeam.antiFallDamageAtRespawn = new HashMap<UUID, Long>();
    }
    
    public class BedHolo
    {
        private ArmorStand a;
        private UUID p;
        private Arena arena;
        private boolean hidden;
        private boolean bedDestroyed;
        
        public BedHolo(final Player player, final Arena arena) {
            this.hidden = false;
            this.bedDestroyed = false;
            this.p = player.getUniqueId();
            this.arena = arena;
            this.spawn();
            BeaconBattleTeam.this.beds.put(player.getUniqueId(), this);
        }
        
        public void spawn() {
            if (!this.arena.getConfig().getBoolean("use-bed-hologram")) {
                return;
            }
            (this.a = (ArmorStand)BeaconBattleTeam.this.bed.getWorld().spawnEntity(BeaconBattleTeam.this.bed.getBlock().getLocation().add(0.5, 1.0, 0.5), EntityType.ARMOR_STAND)).setGravity(false);
            if (BeaconBattleTeam.this.name != null) {
                if (BeaconBattleTeam.this.isBedDestroyed()) {
                    this.a.setCustomName(Language.getMsg(Bukkit.getPlayer(this.p), Messages.BED_HOLOGRAM_DESTROYED));
                    this.bedDestroyed = true;
                }
                else {
                    this.a.setCustomName(Language.getMsg(Bukkit.getPlayer(this.p), Messages.BED_HOLOGRAM_DEFEND));
                }
                this.a.setCustomNameVisible(true);
            }
            this.a.setRemoveWhenFarAway(false);
            this.a.setCanPickupItems(false);
            this.a.setArms(false);
            this.a.setBasePlate(false);
            this.a.setMarker(true);
            this.a.setVisible(false);
            for (final Player player : this.arena.getWorld().getPlayers()) {
                if (this.p != player.getUniqueId()) {
                    BeaconBattle.nms.hideEntity((Entity)this.a, player);
                }
            }
        }
        
        public void hide() {
            if (!this.arena.getConfig().getBoolean("use-bed-hologram")) {
                return;
            }
            if (this.bedDestroyed) {
                return;
            }
            this.hidden = true;
            this.a.remove();
        }
        
        public void destroy() {
            if (!this.arena.getConfig().getBoolean("use-bed-hologram")) {
                return;
            }
            this.a.remove();
            BeaconBattleTeam.this.beds.remove(this.p);
        }
        
        public void show() {
            if (!this.arena.getConfig().getBoolean("use-bed-hologram")) {
                return;
            }
            this.hidden = false;
            this.spawn();
        }
        
        public Arena getArena() {
            return this.arena;
        }
        
        public boolean isHidden() {
            return this.hidden;
        }
    }
    
    public static class Enchant implements TeamEnchant
    {
        Enchantment enchantment;
        int amplifier;
        
        public Enchant(final Enchantment enchantment, final int amplifier) {
            this.enchantment = enchantment;
            this.amplifier = amplifier;
        }
        
        @Override
        public Enchantment getEnchantment() {
            return this.enchantment;
        }
        
        @Override
        public int getAmplifier() {
            return this.amplifier;
        }
    }
}
