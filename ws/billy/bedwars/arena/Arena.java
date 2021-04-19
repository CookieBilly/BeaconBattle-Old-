

package ws.billy.bedwars.arena;

import ws.billy.bedwars.api.entity.Despawnable;
import ws.billy.bedwars.arena.tasks.ReJoinTask;
import java.util.Comparator;
import org.bukkit.Sound;
import ws.billy.bedwars.api.events.gameplay.NextEventChangeEvent;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.events.gameplay.GameEndEvent;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import ws.billy.bedwars.listeners.blockstatus.BlockStatusListener;
import org.bukkit.scheduler.BukkitScheduler;
import ws.billy.bedwars.arena.tasks.GameRestartingTask;
import ws.billy.bedwars.arena.tasks.GamePlayingTask;
import ws.billy.bedwars.levels.internal.InternalLevel;
import ws.billy.bedwars.arena.tasks.GameStartingTask;
import ws.billy.bedwars.api.events.gameplay.GameStateChangeEvent;
import ws.billy.bedwars.api.events.server.ArenaRestartEvent;
import ws.billy.bedwars.api.events.server.ArenaDisableEvent;
import ws.billy.bedwars.api.events.player.PlayerReJoinEvent;
import org.bukkit.potion.PotionEffect;
import ws.billy.bedwars.api.events.player.PlayerLeaveArenaEvent;
import ws.billy.bedwars.shop.ShopCache;
import java.util.Map;
import ws.billy.bedwars.api.arena.shop.ShopHolo;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import ws.billy.bedwars.support.citizens.JoinNPC;
import org.bukkit.inventory.ItemStack;
import ws.billy.bedwars.sidebar.BeaconBattleScoreboard;
import org.bukkit.event.player.PlayerTeleportEvent;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ws.billy.bedwars.arena.upgrades.BaseListener;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.server.ArenaEnableEvent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.generator.GeneratorType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import java.util.Iterator;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import java.util.Arrays;
import ws.billy.bedwars.api.arena.team.TeamColor;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import ws.billy.bedwars.levels.internal.PerMinuteTask;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.api.tasks.RestartingTask;
import ws.billy.bedwars.api.tasks.PlayingTask;
import ws.billy.bedwars.api.tasks.StartingTask;
import org.bukkit.Location;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.api.arena.NextEvent;
import ws.billy.bedwars.api.region.Region;
import org.bukkit.util.Vector;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.World;
import ws.billy.bedwars.configuration.ArenaConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.block.Block;
import java.util.List;
import java.util.UUID;
import java.util.LinkedList;
import org.bukkit.entity.Player;
import java.util.HashMap;
import ws.billy.bedwars.api.arena.IArena;

public class Arena implements IArena
{
    private static HashMap<String, IArena> arenaByName;
    private static HashMap<Player, IArena> arenaByPlayer;
    private static HashMap<String, IArena> arenaByIdentifier;
    private static LinkedList<IArena> arenas;
    private static int gamesBeforeRestart;
    public static HashMap<UUID, Integer> afkCheck;
    public static HashMap<UUID, Integer> magicMilk;
    private List<Player> players;
    private List<Player> spectators;
    private List<Block> signs;
    private GameState status;
    private YamlConfiguration yml;
    private ArenaConfig cm;
    private int minPlayers;
    private int maxPlayers;
    private int maxInTeam;
    private int islandRadius;
    public int upgradeDiamondsCount;
    public int upgradeEmeraldsCount;
    public boolean allowSpectate;
    private World world;
    private String group;
    private String arenaName;
    private String worldName;
    private List<ITeam> teams;
    private LinkedList<Vector> placed;
    private List<String> nextEvents;
    private List<Region> regionsList;
    private NextEvent nextEvent;
    private int diamondTier;
    private int emeraldTier;
    private ConcurrentHashMap<Player, Integer> respawn;
    private ConcurrentHashMap<Player, Integer> showTime;
    private static HashMap<Player, Location> playerLocation;
    private HashMap<Player, Integer> playerKills;
    private HashMap<Player, Integer> playerBedsDestroyed;
    private HashMap<Player, Integer> playerFinalKills;
    private HashMap<Player, Integer> playerDeaths;
    private HashMap<Player, Integer> playerFinalKillDeaths;
    private StartingTask startingTask;
    private PlayingTask playingTask;
    private RestartingTask restartingTask;
    private List<IGenerator> oreGenerators;
    private PerMinuteTask perMinuteTask;
    private static LinkedList<IArena> enableQueue;
    
    public Arena(final String s, final Player player) {
        this.players = new ArrayList<Player>();
        this.spectators = new ArrayList<Player>();
        this.signs = new ArrayList<Block>();
        this.status = GameState.restarting;
        this.minPlayers = 2;
        this.maxPlayers = 10;
        this.maxInTeam = 1;
        this.islandRadius = 10;
        this.upgradeDiamondsCount = 0;
        this.upgradeEmeraldsCount = 0;
        this.allowSpectate = true;
        this.group = "Default";
        this.teams = new ArrayList<ITeam>();
        this.placed = new LinkedList<Vector>();
        this.nextEvents = new ArrayList<String>();
        this.regionsList = new ArrayList<Region>();
        this.nextEvent = NextEvent.DIAMOND_GENERATOR_TIER_II;
        this.diamondTier = 1;
        this.emeraldTier = 1;
        this.respawn = new ConcurrentHashMap<Player, Integer>();
        this.showTime = new ConcurrentHashMap<Player, Integer>();
        this.playerKills = new HashMap<Player, Integer>();
        this.playerBedsDestroyed = new HashMap<Player, Integer>();
        this.playerFinalKills = new HashMap<Player, Integer>();
        this.playerDeaths = new HashMap<Player, Integer>();
        this.playerFinalKillDeaths = new HashMap<Player, Integer>();
        this.startingTask = null;
        this.playingTask = null;
        this.restartingTask = null;
        this.oreGenerators = new ArrayList<IGenerator>();
        if (!BeaconBattle.autoscale) {
            final Iterator<IArena> iterator = (Iterator<IArena>)Arena.enableQueue.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getArenaName().equalsIgnoreCase(s)) {
                    BeaconBattle.plugin.getLogger().severe("Tried to load arena " + s + " but it is already in the enable queue.");
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "Tried to load arena " + s + " but it is already in the enable queue.");
                    }
                    return;
                }
            }
            if (getArenaByName(s) != null) {
                BeaconBattle.plugin.getLogger().severe("Tried to load arena " + s + " but it is already enabled.");
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Tried to load arena " + s + " but it is already enabled.");
                }
                return;
            }
        }
        this.arenaName = s;
        if (BeaconBattle.autoscale) {
            this.worldName = BeaconBattle.arenaManager.generateGameID();
        }
        else {
            this.worldName = this.arenaName;
        }
        this.cm = new ArenaConfig((Plugin)BeaconBattle.plugin, s, BeaconBattle.plugin.getDataFolder().getPath() + "/Arenas");
        this.yml = this.cm.getYml();
        if (this.yml.get("Team") == null) {
            if (player != null) {
                player.sendMessage("You didn't set any team for arena: " + s);
            }
            BeaconBattle.plugin.getLogger().severe("You didn't set any team for arena: " + s);
            return;
        }
        if (this.yml.getConfigurationSection("Team").getKeys(false).size() < 2) {
            if (player != null) {
                player.sendMessage("§cYou must set at least 2 teams on: " + s);
            }
            BeaconBattle.plugin.getLogger().severe("You must set at least 2 teams on: " + s);
            return;
        }
        this.maxInTeam = this.yml.getInt("maxInTeam");
        this.maxPlayers = this.yml.getConfigurationSection("Team").getKeys(false).size() * this.maxInTeam;
        this.minPlayers = this.yml.getInt("minPlayers");
        this.allowSpectate = this.yml.getBoolean("allowSpectate");
        this.islandRadius = this.yml.getInt("island-radius");
        if (BeaconBattle.config.getYml().get("arenaGroups") != null && BeaconBattle.config.getYml().getStringList("arenaGroups").contains(this.yml.getString("group"))) {
            this.group = this.yml.getString("group");
        }
        if (!BeaconBattle.getAPI().getRestoreAdapter().isWorld(s)) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "There isn't any map called " + s);
            }
            BeaconBattle.plugin.getLogger().log(Level.WARNING, "There isn't any map called " + s);
            return;
        }
        boolean b = false;
        for (final String s2 : this.yml.getConfigurationSection("Team").getKeys(false)) {
            final String string = this.yml.getString("Team." + s2 + ".Color");
            if (string == null) {
                continue;
            }
            final String upperCase = string.toUpperCase();
            try {
                TeamColor.valueOf(upperCase);
            }
            catch (Exception ex) {
                if (player != null) {
                    player.sendMessage("§cInvalid color at team: " + s2 + " in arena: " + s);
                }
                BeaconBattle.plugin.getLogger().severe("Invalid color at team: " + s2 + " in arena: " + s);
                b = true;
            }
            for (final String str : Arrays.asList("Color", "Spawn", "Bed", "Shop", "Upgrade", "Iron", "Gold")) {
                if (this.yml.get("Team." + s2 + "." + str) == null) {
                    if (player != null) {
                        player.sendMessage("§c" + str + " not set for " + s2 + " team on: " + s);
                    }
                    BeaconBattle.plugin.getLogger().severe(str + " not set for " + s2 + " team on: " + s);
                    b = true;
                }
            }
        }
        if (this.yml.get("generator.Diamond") == null) {
            if (player != null) {
                player.sendMessage("§cThere isn't set any Diamond generator on: " + s);
            }
            BeaconBattle.plugin.getLogger().severe("There isn't set any Diamond generator on: " + s);
        }
        if (this.yml.get("generator.Emerald") == null) {
            if (player != null) {
                player.sendMessage("§cThere isn't set any Emerald generator on: " + s);
            }
            BeaconBattle.plugin.getLogger().severe("There isn't set any Emerald generator on: " + s);
        }
        if (this.yml.get("waiting.Loc") == null) {
            if (player != null) {
                player.sendMessage("§cWaiting spawn not set on: " + s);
            }
            BeaconBattle.plugin.getLogger().severe("Waiting spawn not set on: " + s);
            return;
        }
        if (b) {
            return;
        }
        addToEnableQueue(this);
        Language.saveIfNotExists(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase(), String.valueOf(this.getGroup().charAt(0)).toUpperCase() + this.group.substring(1).toLowerCase());
    }
    
    @Override
    public void init(final World world) {
        if (!BeaconBattle.autoscale && getArenaByName(this.arenaName) != null) {
            return;
        }
        removeFromEnableQueue(this);
        BeaconBattle.debug("Initialized arena " + this.getArenaName() + " with map " + world.getName());
        this.world = world;
        this.worldName = world.getName();
        this.getConfig().setName(this.worldName);
        world.getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).filter(entity2 -> entity2.getType() != EntityType.PAINTING).filter(entity3 -> entity3.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove);
        final Iterator<String> iterator = this.getConfig().getList("game-rules").iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split(":");
            if (split.length == 2) {
                world.setGameRuleValue(split[0], split[1]);
            }
        }
        world.setAutoSave(false);
        for (final Entity entity4 : world.getEntities()) {
            if (entity4.getType() == EntityType.ARMOR_STAND && !((ArmorStand)entity4).isVisible()) {
                entity4.remove();
            }
        }
        for (final String s : this.yml.getConfigurationSection("Team").getKeys(false)) {
            if (this.getTeam(s) != null) {
                BeaconBattle.plugin.getLogger().severe("A team with name: " + s + " was already loaded for arena: " + this.getArenaName());
            }
            else {
                final BeaconBattleTeam BeaconBattleTeam = new BeaconBattleTeam(s, TeamColor.valueOf(this.yml.getString("Team." + s + ".Color").toUpperCase()), this.cm.getArenaLoc("Team." + s + ".Spawn"), this.cm.getArenaLoc("Team." + s + ".Bed"), this.cm.getArenaLoc("Team." + s + ".Shop"), this.cm.getArenaLoc("Team." + s + ".Upgrade"), this);
                this.teams.add(BeaconBattleTeam);
                BeaconBattleTeam.spawnGenerators();
            }
        }
        for (final String str : Arrays.asList("Diamond", "Emerald")) {
            if (this.yml.get("generator." + str) != null) {
                for (final String str2 : this.yml.getStringList("generator." + str)) {
                    final Location convertStringToArenaLocation = this.cm.convertStringToArenaLocation(str2);
                    if (convertStringToArenaLocation == null) {
                        BeaconBattle.plugin.getLogger().severe("Invalid location for " + str + " generator: " + str2);
                    }
                    else {
                        this.oreGenerators.add(new OreGenerator(convertStringToArenaLocation, this, GeneratorType.valueOf(str.toUpperCase()), null));
                    }
                }
            }
        }
        Arena.arenas.add(this);
        Arena.arenaByName.put(this.getArenaName(), this);
        Arena.arenaByIdentifier.put(this.worldName, this);
        world.getWorldBorder().setCenter(this.cm.getArenaLoc("waiting.Loc"));
        world.getWorldBorder().setSize((double)this.yml.getInt("worldBorder"));
        if (!this.getConfig().getYml().isSet("waiting.Pos1") && this.getConfig().getYml().isSet("waiting.Pos2")) {
            BeaconBattle.plugin.getLogger().severe("Lobby Pos1 isn't set! The arena's lobby won't be removed!");
        }
        if (this.getConfig().getYml().isSet("waiting.Pos1") && !this.getConfig().getYml().isSet("waiting.Pos2")) {
            BeaconBattle.plugin.getLogger().severe("Lobby Pos2 isn't set! The arena's lobby won't be removed!");
        }
        this.registerSigns();
        Bukkit.getPluginManager().callEvent((Event)new ArenaEnableEvent(this));
        this.changeStatus(GameState.waiting);
        final NextEvent[] values = NextEvent.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            this.nextEvents.add(values[i].toString());
        }
        this.upgradeDiamondsCount = BeaconBattle.getGeneratorsCfg().getInt((BeaconBattle.getGeneratorsCfg().getYml().get(this.getGroup() + "." + "diamond.tierII.start") == null) ? "Default.diamond.tierII.start" : (this.getGroup() + "." + "diamond.tierII.start"));
        this.upgradeEmeraldsCount = BeaconBattle.getGeneratorsCfg().getInt((BeaconBattle.getGeneratorsCfg().getYml().get(this.getGroup() + "." + "emerald.tierII.start") == null) ? "Default.emerald.tierII.start" : (this.getGroup() + "." + "emerald.tierII.start"));
        BeaconBattle.plugin.getLogger().info("Load done: " + this.getArenaName());
    }
    
    @Override
    public boolean addPlayer(final Player key, final boolean b) {
        if (key == null) {
            return false;
        }
        BeaconBattle.debug("Player added: " + key.getName() + " arena: " + this.getArenaName());
        BaseListener.isOnABase.remove(key);
        if (getArenaByPlayer(key) != null) {
            return false;
        }
        if (BeaconBattle.getParty().hasParty(key) && !b) {
            if (!BeaconBattle.getParty().isOwner(key)) {
                key.sendMessage(Language.getMsg(key, Messages.COMMAND_JOIN_DENIED_NOT_PARTY_LEADER));
                return false;
            }
            if (BeaconBattle.getParty().partySize(key) > this.maxInTeam * this.getTeams().size() - this.getPlayers().size()) {
                key.sendMessage(Language.getMsg(key, Messages.COMMAND_JOIN_DENIED_PARTY_TOO_BIG));
                return false;
            }
            for (final Player player : BeaconBattle.getParty().getMembers(key)) {
                if (player == key) {
                    continue;
                }
                final IArena arenaByPlayer = getArenaByPlayer(player);
                if (arenaByPlayer != null) {
                    if (arenaByPlayer.isPlayer(player)) {
                        arenaByPlayer.removePlayer(player, false);
                    }
                    else if (arenaByPlayer.isSpectator(player)) {
                        arenaByPlayer.removeSpectator(player, false);
                    }
                }
                this.addPlayer(player, true);
            }
        }
        if (this.status == GameState.waiting || (this.status == GameState.starting && this.startingTask != null && this.startingTask.getCountdown() > 1)) {
            if (this.players.size() >= this.maxPlayers && !isVip(key)) {
                final TextComponent textComponent = new TextComponent(Language.getMsg(key, Messages.COMMAND_JOIN_DENIED_IS_FULL));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BeaconBattle.config.getYml().getString("storeLink")));
                key.spigot().sendMessage((BaseComponent)textComponent);
                return false;
            }
            if (this.players.size() >= this.maxPlayers && isVip(key)) {
                boolean b2 = false;
                for (final Player player2 : new ArrayList<Player>(this.players)) {
                    if (!isVip(player2)) {
                        b2 = true;
                        this.removePlayer(player2, false);
                        final TextComponent textComponent2 = new TextComponent(Language.getMsg(key, Messages.ARENA_JOIN_VIP_KICK));
                        textComponent2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, BeaconBattle.config.getYml().getString("storeLink")));
                        key.spigot().sendMessage((BaseComponent)textComponent2);
                        break;
                    }
                }
                if (!b2) {
                    key.sendMessage(Language.getMsg(key, Messages.COMMAND_JOIN_DENIED_IS_FULL_OF_VIPS));
                    return false;
                }
            }
            final PlayerJoinArenaEvent playerJoinArenaEvent = new PlayerJoinArenaEvent(this, key, false);
            Bukkit.getPluginManager().callEvent((Event)playerJoinArenaEvent);
            if (playerJoinArenaEvent.isCancelled()) {
                return false;
            }
            final ReJoin player3 = ReJoin.getPlayer(key);
            if (player3 != null) {
                player3.destroy();
            }
            final Location arenaLoc = this.cm.getArenaLoc("waiting.Loc");
            if (arenaLoc == null) {
                key.sendMessage(ChatColor.RED + "Could not join the arena. Waiting lobby is not set. Contact the server ADMIN.");
                return false;
            }
            key.closeInventory();
            this.players.add(key);
            key.setFlying(false);
            key.setAllowFlight(false);
            for (final Player player4 : this.players) {
                player4.sendMessage(Language.getMsg(player4, Messages.COMMAND_JOIN_PLAYER_JOIN_MSG).replace("{player}", key.getDisplayName()).replace("{on}", String.valueOf(this.getPlayers().size())).replace("{max}", String.valueOf(this.getMaxPlayers())));
            }
            setArenaByPlayer(key, this);
            if (this.status == GameState.waiting) {
                int n = 0;
                int n2 = 0;
                for (final Player player5 : this.getPlayers()) {
                    if (BeaconBattle.getParty().isOwner(player5)) {
                        ++n;
                    }
                    if (BeaconBattle.getParty().hasParty(player5)) {
                        ++n2;
                    }
                }
                if (this.minPlayers <= this.players.size() && n > 0 && this.players.size() != n2 / n) {
                    this.changeStatus(GameState.starting);
                }
                else if (this.players.size() >= this.minPlayers && n == 0) {
                    this.changeStatus(GameState.starting);
                }
            }
            if (this.players.size() >= this.teams.size() * this.maxInTeam / 2 && this.startingTask != null && Bukkit.getScheduler().isCurrentlyRunning(this.startingTask.getTask()) && this.startingTask.getCountdown() > this.getConfig().getInt("countdowns.game-start-half-arena")) {
                this.startingTask.setCountdown(BeaconBattle.config.getInt("countdowns.game-start-half-arena"));
            }
            if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
                new PlayerGoods(key, true);
                Arena.playerLocation.put(key, key.getLocation());
            }
            key.teleport(arenaLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            BeaconBattleScoreboard.giveScoreboard(key, this, false);
            this.sendPreGameCommandItems(key);
        }
        else if (this.status == GameState.playing) {
            this.addSpectator(key, false, null);
            return false;
        }
        final Iterator<Player> iterator5;
        Player player6;
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            key.getInventory().setArmorContents((ItemStack[])null);
            Bukkit.getOnlinePlayers().iterator();
            while (iterator5.hasNext()) {
                player6 = iterator5.next();
                if (this.getPlayers().contains(player6)) {
                    player6.showPlayer(key);
                    key.showPlayer(player6);
                }
                else {
                    player6.hidePlayer(key);
                    key.hidePlayer(player6);
                }
            }
            return;
        }, 30L);
        if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
            key.getEnderChest().clear();
        }
        if (this.getPlayers().size() == this.getMaxInTeam() * this.getTeams().size() && this.startingTask != null && Bukkit.getScheduler().isCurrentlyRunning(this.startingTask.getTask())) {
            this.startingTask.setCountdown(BeaconBattle.config.getInt("countdowns.game-start-shortened"));
        }
        this.refreshSigns();
        JoinNPC.updateNPCs(this.getGroup());
        return true;
    }
    
    @Override
    public boolean addSpectator(@NotNull final Player player, final boolean b, final Location location) {
        if (this.allowSpectate || b) {
            BeaconBattle.debug("Spectator added: " + player.getName() + " arena: " + this.getArenaName());
            if (!b) {
                final PlayerJoinArenaEvent playerJoinArenaEvent = new PlayerJoinArenaEvent(this, player, true);
                Bukkit.getPluginManager().callEvent((Event)playerJoinArenaEvent);
                if (playerJoinArenaEvent.isCancelled()) {
                    return false;
                }
            }
            if (ReJoin.exists(player)) {
                ReJoin.getPlayer(player).destroy();
            }
            player.closeInventory();
            this.spectators.add(player);
            this.players.remove(player);
            this.updateSpectatorCollideRule(player, false);
            if (!b) {
                if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
                    new PlayerGoods(player, true);
                    Arena.playerLocation.put(player, player.getLocation());
                }
                setArenaByPlayer(player, this);
            }
            BeaconBattleScoreboard.giveScoreboard(player, this, false);
            BeaconBattle.nms.setCollide(player, this, false);
            if (!b) {
                if (location == null) {
                    Location location2 = this.cm.getArenaLoc("spectator-loc");
                    if (location2 == null) {
                        location2 = this.cm.getArenaLoc("waiting.Loc");
                    }
                    if (location2 != null) {
                        player.teleport(location2, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
                else {
                    player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }
            player.setGameMode(GameMode.ADVENTURE);
            if (b) {
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    return;
                }, 10L);
            }
            else {
                player.setAllowFlight(true);
                player.setFlying(true);
            }
            if (player.getPassenger() != null && player.getPassenger().getType() == EntityType.ARMOR_STAND) {
                player.getPassenger().remove();
            }
            final Iterator<Player> iterator;
            Player player2;
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                Bukkit.getOnlinePlayers().iterator();
                while (iterator.hasNext()) {
                    player2 = iterator.next();
                    if (player2 == player) {
                        continue;
                    }
                    else if (this.getSpectators().contains(player2)) {
                        player2.showPlayer(player);
                        player.showPlayer(player2);
                    }
                    else if (this.getPlayers().contains(player2)) {
                        player2.hidePlayer(player);
                        player.showPlayer(player2);
                    }
                    else {
                        player2.hidePlayer(player);
                        player.hidePlayer(player2);
                    }
                }
                if (!b) {
                    if (location == null) {
                        player.teleport(this.cm.getArenaLoc("waiting.Loc"), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                    else {
                        player.teleport(location);
                    }
                }
                else {
                    player.teleport(player.getLocation());
                }
                this.sendSpectatorCommandItems(player);
                return;
            }, 25L);
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_MSG).replace("{arena}", this.getDisplayName()));
            final String iso = Language.getPlayerLanguage(player).getIso();
            final Iterator<IGenerator> iterator2 = this.getOreGenerators().iterator();
            while (iterator2.hasNext()) {
                iterator2.next().updateHolograms(player, iso);
            }
            final Iterator<ITeam> iterator3 = this.getTeams().iterator();
            while (iterator3.hasNext()) {
                final Iterator<IGenerator> iterator4 = iterator3.next().getGenerators().iterator();
                while (iterator4.hasNext()) {
                    iterator4.next().updateHolograms(player, iso);
                }
            }
            for (final ShopHolo shopHolo : ShopHolo.getShopHolo()) {
                if (shopHolo.getA() == this) {
                    shopHolo.updateForPlayer(player, iso);
                }
            }
            this.showTime.remove(player);
            this.refreshSigns();
            JoinNPC.updateNPCs(this.getGroup());
            final Iterator<Map.Entry<Player, Integer>> iterator6 = this.getShowTime().entrySet().iterator();
            while (iterator6.hasNext()) {
                BeaconBattle.nms.hidePlayer(iterator6.next().getKey(), player);
            }
            return true;
        }
        player.sendMessage(Language.getMsg(player, Messages.COMMAND_JOIN_SPECTATOR_DENIED_MSG));
        return false;
    }
    
    @Override
    public void removePlayer(@NotNull final Player player, final boolean b) {
        BeaconBattle.debug("Player removed: " + player.getName() + " arena: " + this.getArenaName());
        this.respawn.remove(player);
        ITeam team = null;
        if (this.getStatus() == GameState.playing) {
            for (final ITeam team2 : this.getTeams()) {
                if (team2.isMember(player)) {
                    team = team2;
                    team2.getMembers().remove(player);
                    team2.destroyBedHolo(player);
                }
            }
        }
        List<ShopCache.CachedItem> cachedPermanents = new ArrayList<ShopCache.CachedItem>();
        if (ShopCache.getShopCache(player.getUniqueId()) != null) {
            cachedPermanents = ShopCache.getShopCache(player.getUniqueId()).getCachedPermanents();
        }
        Bukkit.getPluginManager().callEvent((Event)new PlayerLeaveArenaEvent(player, this));
        this.players.remove(player);
        removeArenaByPlayer(player, this);
        final Iterator<PotionEffect> iterator2 = (Iterator<PotionEffect>)player.getActivePotionEffects().iterator();
        while (iterator2.hasNext()) {
            player.removePotionEffect(iterator2.next().getType());
        }
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            final PlayerGoods playerGoods = PlayerGoods.getPlayerGoods(player);
            if (playerGoods != null) {
                playerGoods.restore();
            }
        }
        if (player.getPassenger() != null && player.getPassenger().getType() == EntityType.ARMOR_STAND) {
            player.getPassenger().remove();
        }
        boolean b2 = false;
        final Iterator<Player> iterator3 = this.getPlayers().iterator();
        while (iterator3.hasNext()) {
            if (BeaconBattle.getParty().hasParty(iterator3.next())) {
                b2 = true;
            }
        }
        if (this.status == GameState.starting && ((this.maxInTeam > this.players.size() && b2) || (this.players.size() < this.minPlayers && !b2))) {
            this.changeStatus(GameState.waiting);
            for (final Player player2 : this.players) {
                player2.sendMessage(Language.getMsg(player2, Messages.ARENA_START_COUNTDOWN_STOPPED_INSUFF_PLAYERS));
            }
        }
        else if (this.status == GameState.playing) {
            int n = 0;
            for (final ITeam team3 : this.getTeams()) {
                if (team3 == null) {
                    continue;
                }
                if (team3.getMembers().isEmpty()) {
                    continue;
                }
                ++n;
            }
            if (n == 1) {
                this.checkWinner();
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.changeStatus(GameState.restarting), 10L);
                if (team != null && !team.isBedDestroyed()) {
                    for (final Player player3 : this.getPlayers()) {
                        player3.sendMessage(Language.getMsg(player3, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                    }
                    for (final Player player4 : this.getSpectators()) {
                        player4.sendMessage(Language.getMsg(player4, Messages.TEAM_ELIMINATED_CHAT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player4))));
                    }
                }
            }
            else if (n == 0) {
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.changeStatus(GameState.restarting), 10L);
            }
            else {
                new ReJoin(player, this, this.getTeam(player), cachedPermanents);
            }
        }
        if (this.status == GameState.starting || this.status == GameState.waiting) {
            for (final Player player5 : this.players) {
                player5.sendMessage(Language.getMsg(player5, Messages.COMMAND_LEAVE_MSG).replace("{player}", player.getDisplayName()));
            }
        }
        if (BeaconBattle.getServerType() == ServerType.SHARED) {
            final BeaconBattleScoreboard sBoard = BeaconBattleScoreboard.getSBoard(player.getUniqueId());
            if (sBoard != null) {
                sBoard.remove();
            }
            player.teleport((Location)Arena.playerLocation.get(player));
        }
        else {
            if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
                Misc.moveToLobbyOrKick(player);
                return;
            }
            if (BeaconBattle.getLobbyWorld().isEmpty()) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                BeaconBattle.plugin.getLogger().log(Level.SEVERE, player.getName() + " was teleported to the main world because lobby location is not set!");
            }
            else {
                player.teleport(BeaconBattle.config.getConfigLoc("lobbyLoc"));
            }
        }
        Arena.playerLocation.remove(player);
        final Iterator<Player> iterator9;
        Player player6;
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)BeaconBattle.plugin, () -> {
            Bukkit.getOnlinePlayers().iterator();
            while (iterator9.hasNext()) {
                player6 = iterator9.next();
                if (getArenaByPlayer(player6) == null) {
                    player6.showPlayer(player);
                    player.showPlayer(player6);
                }
                else {
                    player.hidePlayer(player6);
                    player6.hidePlayer(player);
                }
            }
            if (!b) {
                BeaconBattleScoreboard.giveScoreboard(player, null, false);
            }
            return;
        }, 5L);
        if (BeaconBattle.getParty().hasParty(player) && BeaconBattle.getParty().isOwner(player) && this.status != GameState.restarting) {
            for (final Player player7 : new ArrayList<Player>(BeaconBattle.getParty().getMembers(player))) {
                player7.sendMessage(Language.getMsg(player7, Messages.ARENA_LEAVE_PARTY_DISBANDED));
            }
            BeaconBattle.getParty().disband(player);
            boolean b3 = false;
            final Iterator<Player> iterator11 = this.getPlayers().iterator();
            while (iterator11.hasNext()) {
                if (BeaconBattle.getParty().hasParty(iterator11.next())) {
                    b3 = true;
                }
            }
            if (this.status == GameState.starting && ((this.maxInTeam > this.players.size() && b3) || (this.players.size() < this.minPlayers && !b3))) {
                this.changeStatus(GameState.waiting);
                for (final Player player8 : this.players) {
                    player8.sendMessage(Language.getMsg(player8, Messages.ARENA_START_COUNTDOWN_STOPPED_INSUFF_PLAYERS));
                }
            }
        }
        player.setFlying(false);
        player.setAllowFlight(false);
        if (this.status == GameState.restarting && ReJoin.exists(player) && ReJoin.getPlayer(player).getArena() == this) {
            ReJoin.getPlayer(player).destroy();
        }
        if (Arena.magicMilk.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask((int)Arena.magicMilk.get(player.getUniqueId()));
            Arena.magicMilk.remove(player.getUniqueId());
        }
        this.showTime.remove(player);
        this.refreshSigns();
        JoinNPC.updateNPCs(this.getGroup());
        if ((this.getStatus() == GameState.waiting || this.getStatus() == GameState.starting) && BeaconBattle.getParty().hasParty(player) && !BeaconBattle.getParty().isOwner(player)) {
            for (final Player player9 : BeaconBattle.getParty().getMembers(player)) {
                if (BeaconBattle.getParty().isOwner(player9) && player9.getWorld().getName().equalsIgnoreCase(this.getArenaName())) {
                    BeaconBattle.getParty().removeFromParty(player);
                    break;
                }
            }
        }
        final LastHit lastHit = LastHit.getLastHit(player);
        if (lastHit != null) {
            lastHit.remove();
        }
    }
    
    @Override
    public void removeSpectator(@NotNull final Player player, final boolean b) {
        BeaconBattle.debug("Spectator removed: " + player.getName() + " arena: " + this.getArenaName());
        Bukkit.getPluginManager().callEvent((Event)new PlayerLeaveArenaEvent(player, this));
        this.spectators.remove(player);
        removeArenaByPlayer(player, this);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        if (PlayerGoods.hasGoods(player)) {
            PlayerGoods.getPlayerGoods(player).restore();
        }
        BeaconBattle.nms.setCollide(player, this, true);
        if (BeaconBattle.getServerType() == ServerType.SHARED) {
            final BeaconBattleScoreboard sBoard = BeaconBattleScoreboard.getSBoard(player.getUniqueId());
            if (sBoard != null) {
                sBoard.remove();
            }
            player.teleport((Location)Arena.playerLocation.get(player));
        }
        else if (BeaconBattle.getServerType() == ServerType.MULTIARENA) {
            if (BeaconBattle.getLobbyWorld().isEmpty()) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                BeaconBattle.plugin.getLogger().log(Level.SEVERE, player.getName() + " was teleported to the main world because lobby location is not set!");
            }
            else {
                player.teleport(BeaconBattle.config.getConfigLoc("lobbyLoc"));
            }
        }
        if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
            Misc.moveToLobbyOrKick(player);
            return;
        }
        Arena.playerLocation.remove(player);
        final Iterator<Player> iterator;
        Player player2;
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            Bukkit.getOnlinePlayers().iterator();
            while (iterator.hasNext()) {
                player2 = iterator.next();
                if (getArenaByPlayer(player2) == null) {
                    player2.showPlayer(player);
                    player.showPlayer(player2);
                }
                else {
                    player2.hidePlayer(player);
                    player.hidePlayer(player2);
                }
            }
            if (!b) {
                BeaconBattleScoreboard.giveScoreboard(player, null, true);
            }
            return;
        }, 10L);
        if (BeaconBattle.getParty().hasParty(player) && BeaconBattle.getParty().isOwner(player) && this.status != GameState.restarting) {
            for (final Player player3 : new ArrayList<Player>(BeaconBattle.getParty().getMembers(player))) {
                player3.sendMessage(Language.getMsg(player3, Messages.ARENA_LEAVE_PARTY_DISBANDED));
            }
            BeaconBattle.getParty().disband(player);
        }
        player.setFlying(false);
        player.setAllowFlight(false);
        if (ReJoin.exists(player) && ReJoin.getPlayer(player).getArena() == this) {
            ReJoin.getPlayer(player).destroy();
        }
        if (Arena.magicMilk.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().cancelTask((int)Arena.magicMilk.get(player.getUniqueId()));
            Arena.magicMilk.remove(player.getUniqueId());
        }
        this.refreshSigns();
        JoinNPC.updateNPCs(this.getGroup());
    }
    
    @Override
    public boolean reJoin(final Player key) {
        final ReJoin player3 = ReJoin.getPlayer(key);
        if (player3 == null) {
            return false;
        }
        if (player3.getArena() != this) {
            return false;
        }
        if (!player3.canReJoin()) {
            return false;
        }
        if (player3.getTask() != null) {
            player3.getTask().destroy();
        }
        final PlayerReJoinEvent playerReJoinEvent = new PlayerReJoinEvent(key, this);
        Bukkit.getPluginManager().callEvent((Event)playerReJoinEvent);
        if (playerReJoinEvent.isCancelled()) {
            return false;
        }
        for (final Player player4 : Bukkit.getOnlinePlayers()) {
            if (this.getPlayers().contains(player4)) {
                player4.showPlayer(key);
                key.showPlayer(player4);
            }
            else {
                player4.hidePlayer(key);
                key.hidePlayer(player4);
            }
        }
        key.closeInventory();
        this.players.add(key);
        for (final Player player5 : this.players) {
            player5.sendMessage(Language.getMsg(player5, Messages.COMMAND_REJOIN_PLAYER_RECONNECTED).replace("{player}", key.getDisplayName()).replace("{on}", String.valueOf(this.getPlayers().size())).replace("{max}", String.valueOf(this.getMaxPlayers())));
        }
        for (final Player player6 : this.spectators) {
            player6.sendMessage(Language.getMsg(player6, Messages.COMMAND_REJOIN_PLAYER_RECONNECTED).replace("{player}", key.getDisplayName()).replace("{on}", String.valueOf(this.getPlayers().size())).replace("{max}", String.valueOf(this.getMaxPlayers())));
        }
        setArenaByPlayer(key, this);
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            new PlayerGoods(key, true);
            Arena.playerLocation.put(key, key.getLocation());
        }
        key.teleport(this.getConfig().getArenaLoc("waiting.Loc"));
        key.getInventory().clear();
        final ShopCache shopCache = ShopCache.getShopCache(key.getUniqueId());
        if (shopCache != null) {
            shopCache.destroy();
        }
        final ShopCache shopCache2 = new ShopCache(key.getUniqueId());
        final Iterator<ShopCache.CachedItem> iterator4 = player3.getPermanentsAndNonDowngradables().iterator();
        while (iterator4.hasNext()) {
            shopCache2.getCachedItems().add(iterator4.next());
        }
        player3.getBwt().reJoin(key);
        player3.destroy();
        BeaconBattleScoreboard.giveScoreboard(key, this, false);
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.getPlayers().forEach(player -> BeaconBattle.nms.hidePlayer(key, player)), 10L);
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.getSpectators().forEach(player2 -> BeaconBattle.nms.hidePlayer(key, player2)), 10L);
        return true;
    }
    
    @Override
    public void disable() {
        if (this.getRestartingTask() != null) {
            this.getRestartingTask().cancel();
        }
        BeaconBattle.plugin.getLogger().log(Level.WARNING, "Disabling arena: " + this.getArenaName());
        final Iterator<Player> iterator = new ArrayList<Player>(this.players).iterator();
        while (iterator.hasNext()) {
            this.removePlayer(iterator.next(), false);
        }
        final Iterator<Player> iterator2 = new ArrayList<Player>(this.spectators).iterator();
        while (iterator2.hasNext()) {
            this.removeSpectator(iterator2.next(), false);
        }
        BeaconBattle.getAPI().getRestoreAdapter().onDisable(this);
        Bukkit.getPluginManager().callEvent((Event)new ArenaDisableEvent(this.getArenaName(), this.getWorldName()));
        this.destroyData();
    }
    
    @Override
    public void restart() {
        BeaconBattle.plugin.getLogger().log(Level.FINE, "Restarting arena: " + this.getArenaName());
        Bukkit.getPluginManager().callEvent((Event)new ArenaRestartEvent(this.getArenaName(), this.getWorldName()));
        this.destroyData();
        BeaconBattle.getAPI().getRestoreAdapter().onRestart(this);
    }
    
    @Override
    public World getWorld() {
        return this.world;
    }
    
    @Override
    public int getMaxInTeam() {
        return this.maxInTeam;
    }
    
    public static IArena getArenaByName(final String key) {
        return Arena.arenaByName.get(key);
    }
    
    public static IArena getArenaByIdentifier(final String key) {
        return Arena.arenaByIdentifier.get(key);
    }
    
    public static IArena getArenaByPlayer(final Player key) {
        return Arena.arenaByPlayer.get(key);
    }
    
    public static LinkedList<IArena> getArenas() {
        return Arena.arenas;
    }
    
    @Override
    public String getDisplayStatus(final Language language) {
        String s = "";
        switch (this.status) {
            case waiting: {
                s = language.m(Messages.ARENA_STATUS_WAITING_NAME);
                break;
            }
            case starting: {
                s = language.m(Messages.ARENA_STATUS_STARTING_NAME);
                break;
            }
            case restarting: {
                s = language.m(Messages.ARENA_STATUS_RESTARTING_NAME);
                break;
            }
            case playing: {
                s = language.m(Messages.ARENA_STATUS_PLAYING_NAME);
                break;
            }
        }
        return s.replace("{full}", (this.getPlayers().size() == this.getMaxPlayers()) ? language.m(Messages.MEANING_FULL) : "");
    }
    
    @Override
    public String getDisplayGroup(final Player player) {
        return Language.getPlayerLanguage(player).m(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase());
    }
    
    @Override
    public String getDisplayGroup(@NotNull final Language language) {
        return language.m(Messages.ARENA_DISPLAY_GROUP_PATH + this.getGroup().toLowerCase());
    }
    
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }
    
    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    @Override
    public String getDisplayName() {
        return this.getConfig().getYml().getString("display-name", (Character.toUpperCase(this.arenaName.charAt(0)) + this.arenaName.substring(1)).replace("_", " ").replace("-", " ")).trim().isEmpty() ? (Character.toUpperCase(this.arenaName.charAt(0)) + this.arenaName.substring(1)).replace("_", " ").replace("-", " ") : this.getConfig().getString("display-name");
    }
    
    @Override
    public void setWorldName(final String worldName) {
        this.worldName = worldName;
    }
    
    @Override
    public String getGroup() {
        return this.group;
    }
    
    @Override
    public String getArenaName() {
        return this.arenaName;
    }
    
    @Override
    public List<ITeam> getTeams() {
        return this.teams;
    }
    
    @Override
    public ArenaConfig getConfig() {
        return this.cm;
    }
    
    @Override
    public void addPlacedBlock(final Block block) {
        if (block == null) {
            return;
        }
        this.placed.add(new Vector(block.getX(), block.getY(), block.getZ()));
    }
    
    @Override
    public void removePlacedBlock(final Block block) {
        if (block == null) {
            return;
        }
        this.placed.remove(new Vector(block.getX(), block.getY(), block.getZ()));
    }
    
    @Override
    public boolean isBlockPlaced(final Block block) {
        for (final Vector vector : this.getPlaced()) {
            if (vector.getX() == block.getX() && vector.getY() == block.getY() && vector.getZ() == block.getZ()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getPlayerKills(final Player player, final boolean b) {
        if (b) {
            return this.playerFinalKills.getOrDefault(player, 0);
        }
        return this.playerKills.getOrDefault(player, 0);
    }
    
    @Override
    public int getPlayerBedsDestroyed(final Player player) {
        if (this.playerBedsDestroyed.containsKey(player)) {
            return this.playerBedsDestroyed.get(player);
        }
        return 0;
    }
    
    @Override
    public List<Block> getSigns() {
        return this.signs;
    }
    
    @Override
    public int getIslandRadius() {
        return this.islandRadius;
    }
    
    @Override
    public void setGroup(final String group) {
        this.group = group;
    }
    
    public static void setArenaByPlayer(final Player key, final IArena value) {
        Arena.arenaByPlayer.put(key, value);
        value.refreshSigns();
        JoinNPC.updateNPCs(value.getGroup());
    }
    
    public static void setArenaByName(final IArena value) {
        Arena.arenaByName.put(value.getArenaName(), value);
    }
    
    public static void removeArenaByName(@NotNull final String s) {
        Arena.arenaByName.remove(s.replace("_clone", ""));
    }
    
    public static void removeArenaByPlayer(final Player key, @NotNull final IArena arena) {
        Arena.arenaByPlayer.remove(key);
        arena.refreshSigns();
        JoinNPC.updateNPCs(arena.getGroup());
    }
    
    @Override
    public void setStatus(final GameState status) {
        this.status = status;
    }
    
    @Override
    public void changeStatus(final GameState status) {
        this.status = status;
        Bukkit.getPluginManager().callEvent((Event)new GameStateChangeEvent(this, status, status));
        this.refreshSigns();
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        if (this.startingTask != null && (scheduler.isCurrentlyRunning(this.startingTask.getTask()) || scheduler.isQueued(this.startingTask.getTask()))) {
            this.startingTask.cancel();
        }
        this.startingTask = null;
        if (this.playingTask != null && (scheduler.isCurrentlyRunning(this.playingTask.getTask()) || scheduler.isQueued(this.playingTask.getTask()))) {
            this.playingTask.cancel();
        }
        this.playingTask = null;
        if (this.restartingTask != null && (scheduler.isCurrentlyRunning(this.restartingTask.getTask()) || scheduler.isQueued(this.restartingTask.getTask()))) {
            this.restartingTask.cancel();
        }
        this.restartingTask = null;
        this.players.forEach(player -> BeaconBattleScoreboard.giveScoreboard(player, this, false));
        this.spectators.forEach(player2 -> BeaconBattleScoreboard.giveScoreboard(player2, this, false));
        if (status == GameState.starting) {
            this.startingTask = new GameStartingTask(this);
        }
        else if (status == GameState.playing) {
            if (BeaconBattle.getLevelSupport() instanceof InternalLevel) {
                this.perMinuteTask = new PerMinuteTask(this);
            }
            this.playingTask = new GamePlayingTask(this);
        }
        else if (status == GameState.restarting) {
            this.restartingTask = new GameRestartingTask(this);
            if (this.perMinuteTask != null) {
                this.perMinuteTask.cancel();
            }
        }
    }
    
    public static boolean isVip(final Player player) {
        return player.hasPermission(BeaconBattle.mainCmd + ".*") || player.hasPermission(BeaconBattle.mainCmd + ".vip");
    }
    
    @Override
    public boolean isPlayer(final Player player) {
        return this.players.contains(player);
    }
    
    @Override
    public boolean isSpectator(final Player player) {
        return this.spectators.contains(player);
    }
    
    @Override
    public boolean isSpectator(final UUID obj) {
        final Iterator<Player> iterator = this.getSpectators().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getUniqueId().equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isReSpawning(final UUID obj) {
        final Iterator<Map.Entry<Player, Integer>> iterator = this.getRespawn().entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getKey().getUniqueId().equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isRespawning(final Player key) {
        return this.respawn.containsKey(key);
    }
    
    @Override
    public void addSign(final Location location) {
        if (location == null) {
            return;
        }
        if (location.getBlock() == null) {
            return;
        }
        if (location.getBlock().getType().toString().endsWith("_SIGN") || location.getBlock().getType().toString().endsWith("_WALL_SIGN")) {
            this.signs.add(location.getBlock());
            this.refreshSigns();
            BlockStatusListener.updateBlock(this);
        }
    }
    
    @Override
    public GameState getStatus() {
        return this.status;
    }
    
    @Override
    public synchronized void refreshSigns() {
        for (final Block block : this.getSigns()) {
            if (block == null) {
                continue;
            }
            if (!block.getType().toString().endsWith("_SIGN") && !block.getType().toString().endsWith("_WALL_SIGN")) {
                continue;
            }
            if (!(block.getState() instanceof Sign)) {
                continue;
            }
            final Sign sign = (Sign)block.getState();
            if (sign == null) {
                return;
            }
            int n = 0;
            for (final String s : BeaconBattle.signs.getList("format")) {
                if (s == null) {
                    continue;
                }
                if (this.getPlayers() == null) {
                    continue;
                }
                sign.setLine(n, s.replace("[on]", String.valueOf(this.getPlayers().size())).replace("[max]", String.valueOf(this.getMaxPlayers())).replace("[arena]", this.getDisplayName()).replace("[status]", this.getDisplayStatus(Language.getDefaultLanguage())));
                ++n;
            }
            try {
                sign.update(true);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public List<Player> getSpectators() {
        return this.spectators;
    }
    
    @Override
    public void addPlayerKill(final Player player, final boolean b, final Player key) {
        if (player == null) {
            return;
        }
        if (this.playerKills.containsKey(player)) {
            this.playerKills.replace(player, this.playerKills.get(player) + 1);
        }
        else {
            this.playerKills.put(player, 1);
        }
        if (b) {
            if (this.playerFinalKills.containsKey(player)) {
                this.playerFinalKills.replace(player, this.playerFinalKills.get(player) + 1);
            }
            else {
                this.playerFinalKills.put(player, 1);
            }
            this.playerFinalKillDeaths.put(key, 1);
        }
    }
    
    @Override
    public void addPlayerBedDestroyed(final Player player) {
        if (this.playerBedsDestroyed.containsKey(player)) {
            this.playerBedsDestroyed.replace(player, this.playerBedsDestroyed.get(player) + 1);
            return;
        }
        this.playerBedsDestroyed.put(player, 1);
    }
    
    public static void sendLobbyCommandItems(final Player player) {
        if (BeaconBattle.config.getYml().get("lobby-items") == null) {
            return;
        }
        player.getInventory().clear();
        final Iterator<String> iterator;
        String replacement;
        Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin)BeaconBattle.plugin, () -> {
            BeaconBattle.config.getYml().getConfigurationSection("lobby-items").getKeys(false).iterator();
            while (iterator.hasNext()) {
                replacement = iterator.next();
                if (BeaconBattle.config.getYml().get("lobby-items.%path%.material".replace("%path%", replacement)) == null) {
                    BeaconBattle.plugin.getLogger().severe("lobby-items.%path%.material".replace("%path%", replacement) + " is not set!");
                }
                else if (BeaconBattle.config.getYml().get("lobby-items.%path%.data".replace("%path%", replacement)) == null) {
                    BeaconBattle.plugin.getLogger().severe("lobby-items.%path%.data".replace("%path%", replacement) + " is not set!");
                }
                else if (BeaconBattle.config.getYml().get("lobby-items.%path%.slot".replace("%path%", replacement)) == null) {
                    BeaconBattle.plugin.getLogger().severe("lobby-items.%path%.slot".replace("%path%", replacement) + " is not set!");
                }
                else if (BeaconBattle.config.getYml().get("lobby-items.%path%.enchanted".replace("%path%", replacement)) == null) {
                    BeaconBattle.plugin.getLogger().severe("lobby-items.%path%.enchanted".replace("%path%", replacement) + " is not set!");
                }
                else if (BeaconBattle.config.getYml().get("lobby-items.%path%.command".replace("%path%", replacement)) == null) {
                    BeaconBattle.plugin.getLogger().severe("lobby-items.%path%.command".replace("%path%", replacement) + " is not set!");
                }
                else {
                    player.getInventory().setItem(BeaconBattle.config.getInt("lobby-items.%path%.slot".replace("%path%", replacement)), Misc.createItem(Material.valueOf(BeaconBattle.config.getYml().getString("lobby-items.%path%.material".replace("%path%", replacement))), (byte)BeaconBattle.config.getInt("lobby-items.%path%.data".replace("%path%", replacement)), BeaconBattle.config.getBoolean("lobby-items.%path%.enchanted".replace("%path%", replacement)), Language.getMsg(player, "lobby-items-%path%-name".replace("%path%", replacement)), Language.getList(player, "lobby-items-%path%-lore".replace("%path%", replacement)), player, "RUNCOMMAND", BeaconBattle.config.getYml().getString("lobby-items.%path%.command".replace("%path%", replacement))));
                }
            }
        }, 15L);
    }
    
    @Override
    public void sendPreGameCommandItems(final Player player) {
        if (BeaconBattle.config.getYml().get("pre-game-items") == null) {
            return;
        }
        player.getInventory().clear();
        for (final String replacement : BeaconBattle.config.getYml().getConfigurationSection("pre-game-items").getKeys(false)) {
            if (BeaconBattle.config.getYml().get("pre-game-items.%path%.material".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("pre-game-items.%path%.material".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("pre-game-items.%path%.data".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("pre-game-items.%path%.data".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("pre-game-items.%path%.slot".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("pre-game-items.%path%.slot".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("pre-game-items.%path%.enchanted".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("pre-game-items.%path%.enchanted".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("pre-game-items.%path%.command".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("pre-game-items.%path%.command".replace("%path%", replacement) + " is not set!");
            }
            else {
                player.getInventory().setItem(BeaconBattle.config.getInt("pre-game-items.%path%.slot".replace("%path%", replacement)), Misc.createItem(Material.valueOf(BeaconBattle.config.getYml().getString("pre-game-items.%path%.material".replace("%path%", replacement))), (byte)BeaconBattle.config.getInt("pre-game-items.%path%.data".replace("%path%", replacement)), BeaconBattle.config.getBoolean("pre-game-items.%path%.enchanted".replace("%path%", replacement)), Language.getMsg(player, "pre-game-items-%path%-name".replace("%path%", replacement)), Language.getList(player, "pre-game-items-%path%-lore".replace("%path%", replacement)), player, "RUNCOMMAND", BeaconBattle.config.getYml().getString("pre-game-items.%path%.command".replace("%path%", replacement))));
            }
        }
    }
    
    @Override
    public void sendSpectatorCommandItems(final Player player) {
        if (BeaconBattle.config.getYml().get("spectator-items") == null) {
            return;
        }
        player.getInventory().clear();
        for (final String replacement : BeaconBattle.config.getYml().getConfigurationSection("spectator-items").getKeys(false)) {
            if (BeaconBattle.config.getYml().get("spectator-items.%path%.material".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("spectator-items.%path%.material".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("spectator-items.%path%.data".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("spectator-items.%path%.data".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("spectator-items.%path%.slot".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("spectator-items.%path%.slot".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("spectator-items.%path%.enchanted".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("spectator-items.%path%.enchanted".replace("%path%", replacement) + " is not set!");
            }
            else if (BeaconBattle.config.getYml().get("spectator-items.%path%.command".replace("%path%", replacement)) == null) {
                BeaconBattle.plugin.getLogger().severe("spectator-items.%path%.command".replace("%path%", replacement) + " is not set!");
            }
            else {
                player.getInventory().setItem(BeaconBattle.config.getInt("spectator-items.%path%.slot".replace("%path%", replacement)), Misc.createItem(Material.valueOf(BeaconBattle.config.getYml().getString("spectator-items.%path%.material".replace("%path%", replacement))), (byte)BeaconBattle.config.getInt("spectator-items.%path%.data".replace("%path%", replacement)), BeaconBattle.config.getBoolean("spectator-items.%path%.enchanted".replace("%path%", replacement)), Language.getMsg(player, "spectator-items-%path%-name".replace("%path%", replacement)), Language.getList(player, "spectator-items-%path%-lore".replace("%path%", replacement)), player, "RUNCOMMAND", BeaconBattle.config.getYml().getString("spectator-items.%path%.command".replace("%path%", replacement))));
            }
        }
    }
    
    public static boolean isInArena(final Player key) {
        return Arena.arenaByPlayer.containsKey(key);
    }
    
    @Override
    public ITeam getTeam(final Player player) {
        for (final ITeam team : this.getTeams()) {
            if (team.isMember(player)) {
                return team;
            }
        }
        return null;
    }
    
    @Override
    public ITeam getExTeam(final UUID uuid) {
        for (final ITeam team : this.getTeams()) {
            if (team.wasMember(uuid)) {
                return team;
            }
        }
        return null;
    }
    
    @Deprecated
    @Override
    public ITeam getPlayerTeam(final String anObject) {
        for (final ITeam team : this.getTeams()) {
            final Iterator<Player> iterator2 = team.getMembersCache().iterator();
            while (iterator2.hasNext()) {
                if (iterator2.next().getName().equals(anObject)) {
                    return team;
                }
            }
        }
        return null;
    }
    
    @Override
    public void checkWinner() {
        if (this.getStatus() != GameState.restarting) {
            final int size = this.getTeams().size();
            int n = 0;
            ITeam team = null;
            for (final ITeam team2 : this.getTeams()) {
                if (team2.getMembers().isEmpty()) {
                    ++n;
                }
                else {
                    team = team2;
                }
            }
            if (size - n == 1) {
                if (team != null) {
                    if (!team.getMembers().isEmpty()) {
                        for (final Player player : team.getMembers()) {
                            if (!player.isOnline()) {
                                continue;
                            }
                            player.getInventory().clear();
                        }
                    }
                    String displayName = "";
                    String displayName2 = "";
                    String displayName3 = "";
                    StringBuilder sb = new StringBuilder();
                    for (final Player player2 : team.getMembers()) {
                        BeaconBattle.nms.sendTitle(player2, Language.getMsg(player2, Messages.GAME_END_VICTORY_PLAYER_TITLE), null, 0, 40, 0);
                        sb.append(player2.getDisplayName()).append(" ");
                    }
                    if (sb.toString().endsWith(" ")) {
                        sb = new StringBuilder(sb.substring(0, sb.length() - 1));
                    }
                    int intValue = 0;
                    int intValue2 = 0;
                    int intValue3 = 0;
                    if (!this.playerKills.isEmpty()) {
                        for (final Map.Entry<Player, Integer> entry : this.playerKills.entrySet()) {
                            if (entry.getKey() == null) {
                                continue;
                            }
                            if (entry.getValue() > intValue) {
                                displayName = entry.getKey().getDisplayName();
                                intValue = entry.getValue();
                            }
                            else if (entry.getValue() > intValue2) {
                                displayName2 = entry.getKey().getDisplayName();
                                intValue2 = entry.getValue();
                            }
                            else {
                                if (entry.getValue() <= intValue3) {
                                    continue;
                                }
                                displayName3 = entry.getKey().getDisplayName();
                                intValue3 = entry.getValue();
                            }
                        }
                    }
                    for (final Player player3 : this.world.getPlayers()) {
                        player3.sendMessage(Language.getMsg(player3, Messages.GAME_END_TEAM_WON_CHAT).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                        if (!team.getMembers().contains(player3)) {
                            BeaconBattle.nms.sendTitle(player3, Language.getMsg(player3, Messages.GAME_END_GAME_OVER_PLAYER_TITLE), null, 0, 40, 0);
                        }
                        final Iterator<String> iterator6 = Language.getList(player3, Messages.GAME_END_TOP_PLAYER_CHAT).iterator();
                        while (iterator6.hasNext()) {
                            player3.sendMessage(iterator6.next().replace("{firstName}", displayName.isEmpty() ? Language.getMsg(player3, Messages.MEANING_NOBODY) : displayName).replace("{firstKills}", String.valueOf(intValue)).replace("{secondName}", displayName2.isEmpty() ? Language.getMsg(player3, Messages.MEANING_NOBODY) : displayName2).replace("{secondKills}", String.valueOf(intValue2)).replace("{thirdName}", displayName3.isEmpty() ? Language.getMsg(player3, Messages.MEANING_NOBODY) : displayName3).replace("{thirdKills}", String.valueOf(intValue3)).replace("{winnerFormat}", (this.getMaxInTeam() > 1) ? Language.getMsg(player3, Messages.FORMATTING_TEAM_WINNER_FORMAT).replace("{members}", sb.toString()) : Language.getMsg(player3, Messages.FORMATTING_SOLO_WINNER_FORMAT).replace("{members}", sb.toString())).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                        }
                    }
                }
                this.changeStatus(GameState.restarting);
                final ArrayList<UUID> list = new ArrayList<UUID>();
                final ArrayList<UUID> list2 = new ArrayList<UUID>();
                final ArrayList<UUID> list3 = new ArrayList<UUID>();
                final Iterator<Player> iterator7 = this.getPlayers().iterator();
                while (iterator7.hasNext()) {
                    list3.add(iterator7.next().getUniqueId());
                }
                if (team != null) {
                    final Iterator<Player> iterator8 = team.getMembersCache().iterator();
                    while (iterator8.hasNext()) {
                        list.add(iterator8.next().getUniqueId());
                    }
                }
                for (final ITeam team3 : this.getTeams()) {
                    if (team != null && team3 == team) {
                        continue;
                    }
                    final Iterator<Player> iterator10 = team3.getMembersCache().iterator();
                    while (iterator10.hasNext()) {
                        list2.add(iterator10.next().getUniqueId());
                    }
                }
                Bukkit.getPluginManager().callEvent((Event)new GameEndEvent(this, list, list2, team, list3));
            }
            if (this.players.size() == 0 && this.getStatus() != GameState.restarting) {
                this.changeStatus(GameState.restarting);
            }
        }
    }
    
    @Override
    public void addPlayerDeath(final Player player) {
        if (this.playerDeaths.containsKey(player)) {
            this.playerDeaths.replace(player, this.playerDeaths.get(player) + 1);
        }
        else {
            this.playerDeaths.put(player, 1);
        }
    }
    
    @Override
    public void setNextEvent(final NextEvent nextEvent) {
        if (this.nextEvent != null) {
            final Sound sound = Sounds.getSound(this.nextEvent.getSoundPath());
            Sounds.playSound(sound, this.getPlayers());
            Sounds.playSound(sound, this.getSpectators());
        }
        Bukkit.getPluginManager().callEvent((Event)new NextEventChangeEvent(this, nextEvent, this.nextEvent));
        this.nextEvent = nextEvent;
    }
    
    @Override
    public void updateNextEvent() {
        BeaconBattle.debug("---");
        BeaconBattle.debug("updateNextEvent called");
        if (this.nextEvent == NextEvent.EMERALD_GENERATOR_TIER_II && this.upgradeEmeraldsCount == 0) {
            final int int1 = BeaconBattle.getGeneratorsCfg().getInt((BeaconBattle.getGeneratorsCfg().getYml().get(this.getGroup() + "." + "emerald.tierIII.start") == null) ? "Default.emerald.tierIII.start" : (this.getGroup() + "." + "emerald.tierIII.start"));
            if (this.upgradeDiamondsCount < int1 && this.diamondTier == 1) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            }
            else if (this.upgradeDiamondsCount < int1 && this.diamondTier == 2) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            }
            else {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            }
            this.upgradeEmeraldsCount = int1;
            this.emeraldTier = 2;
            this.sendEmeraldsUpgradeMessages();
            for (final IGenerator generator : this.getOreGenerators()) {
                if (generator.getType() == GeneratorType.EMERALD && generator.getBwt() == null) {
                    generator.upgrade();
                }
            }
        }
        else if (this.nextEvent == NextEvent.DIAMOND_GENERATOR_TIER_II && this.upgradeDiamondsCount == 0) {
            final int int2 = BeaconBattle.getGeneratorsCfg().getInt((BeaconBattle.getGeneratorsCfg().getYml().get(this.getGroup() + "." + "diamond.tierIII.start") == null) ? "Default.diamond.tierIII.start" : (this.getGroup() + "." + "diamond.tierIII.start"));
            if (this.upgradeEmeraldsCount < int2 && this.emeraldTier == 1) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            }
            else if (this.upgradeEmeraldsCount < int2 && this.emeraldTier == 2) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            }
            else {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            }
            this.upgradeDiamondsCount = int2;
            this.diamondTier = 2;
            this.sendDiamondsUpgradeMessages();
            for (final IGenerator generator2 : this.getOreGenerators()) {
                if (generator2.getType() == GeneratorType.DIAMOND && generator2.getBwt() == null) {
                    generator2.upgrade();
                }
            }
        }
        else if (this.nextEvent == NextEvent.EMERALD_GENERATOR_TIER_III && this.upgradeEmeraldsCount == 0) {
            this.emeraldTier = 3;
            this.sendEmeraldsUpgradeMessages();
            if (this.diamondTier == 1 && this.upgradeDiamondsCount > 0) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            }
            else if (this.diamondTier == 2 && this.upgradeDiamondsCount > 0) {
                this.setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_III);
            }
            else {
                this.setNextEvent(NextEvent.BEDS_DESTROY);
            }
            for (final IGenerator generator3 : this.getOreGenerators()) {
                if (generator3.getType() == GeneratorType.EMERALD && generator3.getBwt() == null) {
                    generator3.upgrade();
                }
            }
        }
        else if (this.nextEvent == NextEvent.DIAMOND_GENERATOR_TIER_III && this.upgradeDiamondsCount == 0) {
            this.diamondTier = 3;
            this.sendDiamondsUpgradeMessages();
            if (this.emeraldTier == 1 && this.upgradeEmeraldsCount > 0) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            }
            else if (this.emeraldTier == 2 && this.upgradeEmeraldsCount > 0) {
                this.setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_III);
            }
            else {
                this.setNextEvent(NextEvent.BEDS_DESTROY);
            }
            for (final IGenerator generator4 : this.getOreGenerators()) {
                if (generator4.getType() == GeneratorType.DIAMOND && generator4.getBwt() == null) {
                    generator4.upgrade();
                }
            }
        }
        else if (this.nextEvent == NextEvent.BEDS_DESTROY && this.getPlayingTask().getBedsDestroyCountdown() == 0) {
            this.setNextEvent(NextEvent.ENDER_DRAGON);
        }
        else if (this.nextEvent == NextEvent.ENDER_DRAGON && this.getPlayingTask().getDragonSpawnCountdown() == 0) {
            this.setNextEvent(NextEvent.GAME_END);
        }
        BeaconBattle.debug("---");
        BeaconBattle.debug(this.nextEvent.toString());
    }
    
    public static HashMap<Player, IArena> getArenaByPlayer() {
        return Arena.arenaByPlayer;
    }
    
    @Override
    public NextEvent getNextEvent() {
        return this.nextEvent;
    }
    
    public static int getPlayers(@NotNull final String s) {
        int n = 0;
        for (final String anotherString : s.split("\\+")) {
            for (final IArena arena : getArenas()) {
                if (arena.getGroup().equalsIgnoreCase(anotherString)) {
                    n += arena.getPlayers().size();
                }
            }
        }
        return n;
    }
    
    private void registerSigns() {
        if (BeaconBattle.getServerType() != ServerType.BUNGEE && BeaconBattle.signs.getYml().get("locations") != null) {
            final Iterator<String> iterator = (Iterator<String>)BeaconBattle.signs.getYml().getStringList("locations").iterator();
            while (iterator.hasNext()) {
                final String[] split = iterator.next().split(",");
                if (split[0].equals(this.getArenaName())) {
                    Location location;
                    try {
                        location = new Location(Bukkit.getWorld(split[6]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                    }
                    catch (Exception ex) {
                        BeaconBattle.plugin.getLogger().severe("Could not load sign at: " + split.toString());
                        continue;
                    }
                    this.addSign(location);
                }
            }
        }
    }
    
    @Override
    public ITeam getTeam(final String anObject) {
        for (final ITeam team : this.getTeams()) {
            if (team.getName().equals(anObject)) {
                return team;
            }
        }
        return null;
    }
    
    @Override
    public ConcurrentHashMap<Player, Integer> getRespawn() {
        return this.respawn;
    }
    
    @Override
    public void updateSpectatorCollideRule(final Player player, final boolean b) {
        if (!this.isSpectator(player)) {
            return;
        }
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (BeaconBattleScoreboard.getArena() == this) {
                BeaconBattleScoreboard.updateSpectator(player, b);
            }
        }
    }
    
    @Override
    public ConcurrentHashMap<Player, Integer> getShowTime() {
        return this.showTime;
    }
    
    @Override
    public StartingTask getStartingTask() {
        return this.startingTask;
    }
    
    @Override
    public PlayingTask getPlayingTask() {
        return this.playingTask;
    }
    
    @Override
    public RestartingTask getRestartingTask() {
        return this.restartingTask;
    }
    
    @Override
    public List<IGenerator> getOreGenerators() {
        return this.oreGenerators;
    }
    
    public static boolean joinRandomArena(final Player player) {
        final List<IArena> sorted = getSorted(getArenas());
        final int n = BeaconBattle.getParty().hasParty(player) ? BeaconBattle.getParty().getMembers(player).size() : 1;
        for (final IArena arena : sorted) {
            if (arena.getPlayers().size() == arena.getMaxPlayers()) {
                continue;
            }
            if (arena.getMaxPlayers() - arena.getPlayers().size() >= n && arena.addPlayer(player, false)) {
                break;
            }
        }
        return true;
    }
    
    public static List<IArena> getSorted(final List<IArena> c) {
        final ArrayList<Object> list = (ArrayList<Object>)new ArrayList<IArena>(c);
        list.sort((Comparator<? super IArena>)new Comparator<IArena>() {
            @Override
            public int compare(final IArena arena, final IArena arena2) {
                if (arena.getStatus() == GameState.starting && arena2.getStatus() == GameState.starting) {
                    return Integer.compare(arena2.getPlayers().size(), arena.getPlayers().size());
                }
                if (arena.getStatus() == GameState.starting && arena2.getStatus() != GameState.starting) {
                    return -1;
                }
                if (arena2.getStatus() == GameState.starting && arena.getStatus() != GameState.starting) {
                    return 1;
                }
                if (arena.getStatus() == GameState.waiting && arena2.getStatus() == GameState.waiting) {
                    return Integer.compare(arena2.getPlayers().size(), arena.getPlayers().size());
                }
                if (arena.getStatus() == GameState.waiting && arena2.getStatus() != GameState.waiting) {
                    return -1;
                }
                if (arena2.getStatus() == GameState.waiting && arena.getStatus() != GameState.waiting) {
                    return 1;
                }
                if (arena.getStatus() == GameState.playing && arena2.getStatus() == GameState.playing) {
                    return 0;
                }
                if (arena.getStatus() == GameState.playing && arena2.getStatus() != GameState.playing) {
                    return -1;
                }
                return 1;
            }
            
            @Override
            public boolean equals(final Object o) {
                return o instanceof IArena;
            }
        });
        return (List<IArena>)list;
    }
    
    public static boolean joinRandomFromGroup(final Player player, @NotNull final String s) {
        final List<IArena> sorted = getSorted(getArenas());
        final int n = BeaconBattle.getParty().hasParty(player) ? BeaconBattle.getParty().getMembers(player).size() : 1;
        final String[] split = s.split("\\+");
        for (final IArena arena : sorted) {
            if (arena.getPlayers().size() == arena.getMaxPlayers()) {
                continue;
            }
            final String[] array = split;
            for (int length = array.length, i = 0; i < length; ++i) {
                if (arena.getGroup().equalsIgnoreCase(array[i]) && arena.getMaxPlayers() - arena.getPlayers().size() >= n && arena.addPlayer(player, false)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public List<String> getNextEvents() {
        return new ArrayList<String>(this.nextEvents);
    }
    
    @Override
    public int getPlayerDeaths(final Player player, final boolean b) {
        if (b) {
            return this.playerFinalKillDeaths.getOrDefault(player, 0);
        }
        return this.playerDeaths.getOrDefault(player, 0);
    }
    
    @Override
    public void sendDiamondsUpgradeMessages() {
        for (final Player player : this.getPlayers()) {
            player.sendMessage(Language.getMsg(player, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player, Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND)).replace("{tier}", Language.getMsg(player, (this.diamondTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        }
        for (final Player player2 : this.getSpectators()) {
            player2.sendMessage(Language.getMsg(player2, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player2, Messages.GENERATOR_HOLOGRAM_TYPE_DIAMOND)).replace("{tier}", Language.getMsg(player2, (this.diamondTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        }
    }
    
    @Override
    public void sendEmeraldsUpgradeMessages() {
        for (final Player player : this.getPlayers()) {
            player.sendMessage(Language.getMsg(player, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player, Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD)).replace("{tier}", Language.getMsg(player, (this.emeraldTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        }
        for (final Player player2 : this.getSpectators()) {
            player2.sendMessage(Language.getMsg(player2, Messages.GENERATOR_UPGRADE_CHAT_ANNOUNCEMENT).replace("{generatorType}", Language.getMsg(player2, Messages.GENERATOR_HOLOGRAM_TYPE_EMERALD)).replace("{tier}", Language.getMsg(player2, (this.emeraldTier == 2) ? Messages.FORMATTING_GENERATOR_TIER2 : Messages.FORMATTING_GENERATOR_TIER3)));
        }
    }
    
    public static int getGamesBeforeRestart() {
        return Arena.gamesBeforeRestart;
    }
    
    public static void setGamesBeforeRestart(final int gamesBeforeRestart) {
        Arena.gamesBeforeRestart = gamesBeforeRestart;
    }
    
    @Override
    public List<Region> getRegionsList() {
        return this.regionsList;
    }
    
    @Override
    public LinkedList<Vector> getPlaced() {
        return this.placed;
    }
    
    public static LinkedList<IArena> getEnableQueue() {
        return Arena.enableQueue;
    }
    
    @Override
    public void destroyData() {
        this.destroyReJoins();
        if (this.worldName != null) {
            Arena.arenaByIdentifier.remove(this.worldName);
        }
        Arena.arenas.remove(this);
        for (final ReJoinTask reJoinTask : ReJoinTask.getReJoinTasks()) {
            if (reJoinTask.getArena() == this) {
                reJoinTask.destroy();
            }
        }
        for (final Despawnable despawnable : new ArrayList<Despawnable>(BeaconBattle.nms.getDespawnablesList().values())) {
            if (despawnable.getTeam().getArena() == this) {
                despawnable.destroy();
            }
        }
        Arena.arenaByName.remove(this.arenaName);
        Arena.arenaByPlayer.entrySet().removeIf(entry -> entry.getValue() == this);
        this.players = null;
        this.spectators = null;
        this.signs = null;
        this.yml = null;
        this.cm = null;
        this.world = null;
        final Iterator<IGenerator> iterator3 = this.oreGenerators.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().destroyData();
        }
        final Iterator<ITeam> iterator4 = this.teams.iterator();
        while (iterator4.hasNext()) {
            iterator4.next().destroyData();
        }
        Arena.playerLocation.entrySet().removeIf(entry2 -> entry2.getValue().getWorld().getName().equalsIgnoreCase(this.worldName));
        this.teams = null;
        this.placed = null;
        this.nextEvents = null;
        this.regionsList = null;
        this.respawn = null;
        this.showTime = null;
        this.playerKills = null;
        this.playerBedsDestroyed = null;
        this.playerFinalKills = null;
        this.playerDeaths = null;
        this.playerFinalKillDeaths = null;
        this.startingTask = null;
        this.playingTask = null;
        this.restartingTask = null;
        this.oreGenerators = null;
        this.perMinuteTask = null;
    }
    
    public static void removeFromEnableQueue(final IArena o) {
        Arena.enableQueue.remove(o);
        if (!Arena.enableQueue.isEmpty()) {
            BeaconBattle.getAPI().getRestoreAdapter().onEnable(Arena.enableQueue.get(0));
            BeaconBattle.plugin.getLogger().info("Loading arena: " + Arena.enableQueue.get(0).getWorldName());
        }
    }
    
    public static void addToEnableQueue(final IArena e) {
        Arena.enableQueue.add(e);
        BeaconBattle.plugin.getLogger().info("Arena " + e.getWorldName() + " was added to the enable queue.");
        if (Arena.enableQueue.size() == 1) {
            BeaconBattle.getAPI().getRestoreAdapter().onEnable(e);
            BeaconBattle.plugin.getLogger().info("Loading arena: " + e.getWorldName());
        }
    }
    
    @Override
    public int getUpgradeDiamondsCount() {
        return this.upgradeDiamondsCount;
    }
    
    @Override
    public int getUpgradeEmeraldsCount() {
        return this.upgradeEmeraldsCount;
    }
    
    @Override
    public void setAllowSpectate(final boolean allowSpectate) {
        this.allowSpectate = allowSpectate;
    }
    
    @Override
    public boolean isAllowSpectate() {
        return this.allowSpectate;
    }
    
    @Override
    public String getWorldName() {
        return this.worldName;
    }
    
    public static boolean canAutoScale(final String s) {
        if (!BeaconBattle.autoscale) {
            return true;
        }
        if (getArenas().isEmpty()) {
            return true;
        }
        final Iterator<IArena> iterator = getEnableQueue().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getArenaName().equalsIgnoreCase(s)) {
                return false;
            }
        }
        if (getArenas().size() >= BeaconBattle.config.getInt("bungee-settings.auto-scale-clone-limit")) {
            return false;
        }
        for (final IArena arena : getArenas()) {
            if (arena.getArenaName().equalsIgnoreCase(s) && (arena.getStatus() == GameState.waiting || arena.getStatus() == GameState.starting)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof IArena && ((IArena)o).getWorldName().equals(this.getWorldName());
    }
    
    private void destroyReJoins() {
        for (final ReJoin reJoin : new ArrayList<ReJoin>(ReJoin.getReJoinList())) {
            if (reJoin.getArena() == this) {
                reJoin.destroy();
            }
        }
    }
    
    static {
        Arena.arenaByName = new HashMap<String, IArena>();
        Arena.arenaByPlayer = new HashMap<Player, IArena>();
        Arena.arenaByIdentifier = new HashMap<String, IArena>();
        Arena.arenas = new LinkedList<IArena>();
        Arena.gamesBeforeRestart = BeaconBattle.config.getInt("bungee-settings.games-before-restart");
        Arena.afkCheck = new HashMap<UUID, Integer>();
        Arena.magicMilk = new HashMap<UUID, Integer>();
        Arena.playerLocation = new HashMap<Player, Location>();
        Arena.enableQueue = new LinkedList<IArena>();
    }
}
