

package ws.billy.bedwars;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import ws.billy.bedwars.levels.internal.LevelListeners;
import java.util.Arrays;
import java.util.Random;
import java.util.Objects;
import java.util.ArrayList;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.arena.Arena;
import java.util.Iterator;
import org.bukkit.Location;
import java.lang.reflect.Constructor;
import ws.billy.bedwars.sidebar.ScoreboardListener;
import ws.billy.bedwars.sidebar.SidebarLifeRefresh;
import ws.billy.bedwars.sidebar.SidebarTitleRefresh;
import ws.billy.bedwars.sidebar.SidebarPlaceholderRefresh;
import ws.billy.bedwars.sidebar.SidebarListRefresh;
import ws.billy.bedwars.language.PreLoadedCleaner;
import ws.billy.bedwars.upgrades.UpgradesManager;
import ws.billy.bedwars.arena.ArenaManager;
import ws.billy.bedwars.arena.despawnables.TargetListener;
import ws.billy.bedwars.arena.spectator.SpectatorListeners;
import ws.billy.bedwars.arena.tasks.OneTick;
import ws.billy.bedwars.arena.tasks.Refresh;
import ws.billy.bedwars.arena.upgrades.BaseListener;
import ws.billy.bedwars.bstats.bukkit.Metrics;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.commands.leave.LeaveCommand;
import ws.billy.bedwars.commands.party.PartyCommand;
import ws.billy.bedwars.commands.rejoin.RejoinCommand;
import ws.billy.bedwars.commands.shout.ShoutCommand;
import ws.billy.bedwars.database.Database;
import ws.billy.bedwars.database.MySQL;
import ws.billy.bedwars.database.SQLite;
import ws.billy.bedwars.levels.internal.InternalLevel;
import ws.billy.bedwars.listeners.arenaselector.ArenaSelectorListener;
import ws.billy.bedwars.listeners.blockstatus.BlockStatusListener;
import ws.billy.bedwars.lobbysocket.ArenaSocket;
import ws.billy.bedwars.lobbysocket.LoadedUsersCleaner;
import ws.billy.bedwars.lobbysocket.SendTask;
import ws.billy.bedwars.maprestore.internal.InternalAdapter;
import ws.billy.bedwars.shop.ShopManager;
import ws.billy.bedwars.stats.StatsManager;
import ws.billy.bedwars.support.citizens.CitizensListener;
import ws.billy.bedwars.support.citizens.JoinNPC;
import ws.billy.bedwars.support.papi.PAPISupport;
import ws.billy.bedwars.support.papi.SupportPAPI;
import ws.billy.bedwars.support.party.Internal;
import ws.billy.bedwars.support.party.NoParty;
import ws.billy.bedwars.support.party.Parties;
import ws.billy.bedwars.support.preloadedparty.PrePartyListener;
import ws.billy.bedwars.support.vipfeatures.VipFeatures;
import ws.billy.bedwars.support.vipfeatures.VipListeners;
import ws.billy.spigotutils.SpigotUpdater;
import ws.billy.vipfeatures.api.MiniGameAlreadyRegistered;
import ws.billy.vipfeatures.api.IVipFeatures;
import ws.billy.bedwars.configuration.LevelsConfig;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.entity.Player;
import ws.billy.bedwars.listeners.PlayerChat;
import ws.billy.bedwars.support.vault.NoEconomy;
import ws.billy.bedwars.support.vault.WithEconomy;
import ws.billy.bedwars.support.vault.NoChat;
import ws.billy.bedwars.support.vault.WithChat;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.listeners.InvisibilityPotionListener;
import ws.billy.bedwars.listeners.ChunkLoad;
import ws.billy.bedwars.listeners.WorldLoadListener;
import ws.billy.bedwars.listeners.Ping;
import ws.billy.bedwars.listeners.AutoscaleListener;
import ws.billy.bedwars.language.LangListener;
import ws.billy.bedwars.listeners.EggBridge;
import ws.billy.bedwars.listeners.CmdProcess;
import ws.billy.bedwars.listeners.HungerWeatherSpawn;
import ws.billy.bedwars.listeners.RefreshGUI;
import ws.billy.bedwars.listeners.Interact;
import ws.billy.bedwars.listeners.Inventory;
import ws.billy.bedwars.listeners.DamageDeathMove;
import ws.billy.bedwars.listeners.BreakPlace;
import ws.billy.bedwars.listeners.JoinLeaveTeleport;
import org.bukkit.event.Listener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.WorldCreator;
import org.bukkit.World;
import java.io.File;

import org.bukkit.command.Command;
import ws.billy.bedwars.api.server.RestoreAdapter;
import ws.billy.bedwars.configuration.SignsConfig;
import ws.billy.bedwars.configuration.GeneratorsConfig;
import ws.billy.bedwars.language.Russian;
import ws.billy.bedwars.language.Spanish;
import ws.billy.bedwars.language.Polish;
import ws.billy.bedwars.language.Italian;
import ws.billy.bedwars.language.Romanian;
import ws.billy.bedwars.language.English;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.API;
import ws.billy.bedwars.support.vault.Economy;
import ws.billy.bedwars.api.levels.Level;
import ws.billy.bedwars.support.vault.Chat;
import ws.billy.bedwars.api.party.Party;
import ws.billy.bedwars.api.server.VersionSupport;
import ws.billy.bedwars.configuration.MainConfig;
import ws.billy.bedwars.api.configuration.ConfigManager;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.plugin.java.JavaPlugin;

public class BeaconBattle extends JavaPlugin
{
    private static ServerType serverType;
    public static boolean debug;
    public static boolean autoscale;
    public static String mainCmd;
    public static String link;
    public static ConfigManager signs;
    public static ConfigManager generators;
    public static MainConfig config;
    public static ShopManager shop;
    public static StatsManager statsManager;
    public static BeaconBattle plugin;
    public static VersionSupport nms;
    private static Party party;
    private static Chat chat;
    protected static Level level;
    private static Economy economy;
    private static String version;
    private static String lobbyWorld;
    public static ArenaManager arenaManager;
    private static Database remoteDatabase;
    private boolean serverSoftwareSupport;
    private static ws.billy.bedwars.api.BeaconBattle api;
    
    public BeaconBattle() {
        this.serverSoftwareSupport = true;
    }
    
    public void onLoad() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        }
        catch (Exception ex) {
            this.getLogger().severe("I can't run on your server software. Please check:");
            this.serverSoftwareSupport = false;
            return;
        }
        BeaconBattle.plugin = this;
        Class<?> forName;
        try {
            forName = Class.forName("ws.billy.BeaconBattle.support.version." + BeaconBattle.version + "." + BeaconBattle.version);
        }
        catch (ClassNotFoundException ex2) {
            this.serverSoftwareSupport = false;
            this.getLogger().severe("I can't run on your version: " + BeaconBattle.version);
            return;
        }
        BeaconBattle.api = new API();
        Bukkit.getServicesManager().register((Class) ws.billy.bedwars.api.BeaconBattle.class, (Object)BeaconBattle.api, (Plugin)this, ServicePriority.Highest);
        try {
            BeaconBattle.nms = (VersionSupport)forName.getConstructor(Class.forName("org.bukkit.plugin.Plugin"), String.class).newInstance(this, BeaconBattle.version);
        }
        catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException ex3) {
            final Throwable t;
            t.printStackTrace();
            this.serverSoftwareSupport = false;
            this.getLogger().severe("Could not load support for server version: " + BeaconBattle.version);
            return;
        }
        this.getLogger().info("Loading support for paper/spigot: " + BeaconBattle.version);
        new English();
        new Romanian();
        new Italian();
        new Polish();
        new Spanish();
        new Russian();
        BeaconBattle.config = new MainConfig((Plugin)this, "config");
        BeaconBattle.generators = new GeneratorsConfig((Plugin)this, "generators", this.getDataFolder().getPath());
        if (getServerType() != ServerType.BUNGEE) {
            BeaconBattle.signs = new SignsConfig((Plugin)this, "signs", this.getDataFolder().getPath());
        }
    }
    
    public void onEnable() {
        if (!this.serverSoftwareSupport) {
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        BeaconBattle.nms.registerVersionListeners();
        if (Bukkit.getPluginManager().getPlugin("Enhanced-SlimeWorldManager") != null) {
            try {
                final Constructor<?> constructor = Class.forName("ESlimeAdapter").getConstructor(Plugin.class);
                try {
                    BeaconBattle.api.setRestoreAdapter((RestoreAdapter)constructor.newInstance(this));
                }
                catch (InstantiationException ex) {
                    ex.printStackTrace();
                }
            }
            catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException ex3) {
                final Throwable t;
                t.printStackTrace();
            }
            this.getLogger().info("Hook into Enhanced-SlimeWorldManager support!");
        }
        else if (this.checkSWM()) {
            try {
                final Constructor<?> constructor2 = Class.forName("SlimeAdapter").getConstructor(Plugin.class);
                try {
                    BeaconBattle.api.setRestoreAdapter((RestoreAdapter)constructor2.newInstance(this));
                }
                catch (InstantiationException ex2) {
                    ex2.printStackTrace();
                }
            }
            catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException ex4) {
                final Throwable t2;
                t2.printStackTrace();
            }
            this.getLogger().info("Hook into SlimeWorldManager support!");
        }
        else {
            BeaconBattle.api.setRestoreAdapter(new InternalAdapter((Plugin)this));
        }
        BeaconBattle.nms.registerCommand(BeaconBattle.mainCmd, (Command)new MainCommand(BeaconBattle.mainCmd));
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (!BeaconBattle.nms.isBukkitCommandRegistered("shout")) {
                BeaconBattle.nms.registerCommand("shout", (Command)new ShoutCommand("shout"));
            }
            BeaconBattle.nms.registerCommand("rejoin", (Command)new RejoinCommand("rejoin"));
            if (!BeaconBattle.nms.isBukkitCommandRegistered("leave") || getServerType() != ServerType.BUNGEE) {
                BeaconBattle.nms.registerCommand("leave", (Command)new LeaveCommand("leave"));
            }
            if (getServerType() != ServerType.BUNGEE && BeaconBattle.config.getBoolean("enable-party-cmd")) {
                BeaconBattle.nms.registerCommand("party", (Command)new PartyCommand("party"));
            }
            return;
        }, 20L);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        if (BeaconBattle.config.getLobbyWorldName().isEmpty() && BeaconBattle.serverType != ServerType.BUNGEE) {
            BeaconBattle.plugin.getLogger().log(java.util.logging.Level.WARNING, "Lobby location is not set!");
        }
        if (getServerType() == ServerType.MULTIARENA) {
            final File file;
            final Location location;
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                if (!BeaconBattle.config.getLobbyWorldName().isEmpty()) {
                    if (Bukkit.getWorld(BeaconBattle.config.getLobbyWorldName()) == null) {
                        new File(Bukkit.getWorldContainer(), BeaconBattle.config.getLobbyWorldName() + "/level.dat");
                        if (file.exists() && !BeaconBattle.config.getLobbyWorldName().equalsIgnoreCase(Bukkit.getServer().getWorlds().get(0).getName())) {
                            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                                Bukkit.createWorld(new WorldCreator(BeaconBattle.config.getLobbyWorldName()));
                                if (Bukkit.getWorld(BeaconBattle.config.getLobbyWorldName()) != null) {
                                    Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> Bukkit.getWorld(BeaconBattle.config.getLobbyWorldName()).getEntities().stream().filter(entity -> entity instanceof Monster).forEach(Entity::remove), 20L);
                                }
                                return;
                            }, 100L);
                        }
                    }
                    BeaconBattle.config.getConfigLoc("lobbyLoc");
                    if (location != null) {
                        Bukkit.getWorld(BeaconBattle.config.getLobbyWorldName()).setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                    }
                }
                return;
            }, 1L);
        }
        registerEvents((Listener)new JoinLeaveTeleport(), (Listener)new BreakPlace(), (Listener)new DamageDeathMove(), (Listener)new Inventory(), (Listener)new Interact(), (Listener)new RefreshGUI(), (Listener)new HungerWeatherSpawn(), (Listener)new CmdProcess(), (Listener)new EggBridge(), (Listener)new SpectatorListeners(), (Listener)new BaseListener(), (Listener)new TargetListener(), (Listener)new LangListener());
        if (getServerType() == ServerType.BUNGEE) {
            if (BeaconBattle.autoscale) {
                ArenaSocket.lobbies.addAll(BeaconBattle.config.getList("bungee-settings.lobby-sockets"));
                new SendTask();
                registerEvents((Listener)new AutoscaleListener(), (Listener)new PrePartyListener());
                Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new LoadedUsersCleaner(), 60L, 60L);
            }
            else {
                registerEvents((Listener)new Ping());
            }
        }
        else if (getServerType() == ServerType.MULTIARENA || getServerType() == ServerType.SHARED) {
            registerEvents((Listener)new ArenaSelectorListener(), (Listener)new BlockStatusListener());
        }
        registerEvents((Listener)new WorldLoadListener());
        registerEvents((Listener)new ChunkLoad());
        final String version = BeaconBattle.version;
        switch (version) {
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1": {
                registerEvents((Listener)new InvisibilityPotionListener());
                Bukkit.getScheduler().runTaskLater((Plugin)this, () -> System.out.println("\u001b[31m[WARN] BeaconBattle1058 may drop support for this server version in the future.\nPlease consider upgrading to a newer paper/spigot version.\u001b[0m"), 40L);
                break;
            }
        }
        this.loadArenasAndSigns();
        BeaconBattle.statsManager = new StatsManager();
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (BeaconBattle.config.getYml().getBoolean("allow-parties")) {
                if (Bukkit.getPluginManager().getPlugin("Parties") != null && this.getServer().getPluginManager().getPlugin("Parties").isEnabled()) {
                    this.getLogger().info("Hook into Parties (by AlessioDP) support!");
                    BeaconBattle.party = new Parties();
                }
                if (BeaconBattle.party instanceof NoParty) {
                    BeaconBattle.party = new Internal();
                    this.getLogger().info("Loading internal Party system. /party");
                }
            }
            else {
                BeaconBattle.party = new NoParty();
            }
            return;
        }, 10L);
        setLevelAdapter(new InternalLevel());
        Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new Refresh(), 20L, 20L);
        if (BeaconBattle.config.getBoolean("performance-settings.rotate-generators")) {
            Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new OneTick(), 120L, 1L);
        }
        BeaconBattle.nms.registerEntities();
        if (BeaconBattle.config.getBoolean("database.enable")) {
            final MySQL remoteDatabase = new MySQL();
            final long currentTimeMillis = System.currentTimeMillis();
            if (!remoteDatabase.connect()) {
                this.getLogger().severe("Could not connect to database! Please verify your credentials and make sure that the server IP is whitelisted in MySQL.");
                BeaconBattle.remoteDatabase = new SQLite();
            }
            else {
                BeaconBattle.remoteDatabase = remoteDatabase;
            }
            if (System.currentTimeMillis() - currentTimeMillis >= 5000L) {
                this.getLogger().severe("It took " + (System.currentTimeMillis() - currentTimeMillis) / 1000L + " ms to establish a database connection!\nUsing this remote connection is not recommended!");
            }
            BeaconBattle.remoteDatabase.init();
        }
        else {
            BeaconBattle.remoteDatabase = new SQLite();
        }
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            if (this.getServer().getPluginManager().getPlugin("Citizens") != null) {
                JoinNPC.setCitizensSupport(true);
                this.getLogger().info("Hook into Citizens support. /bw npc");
                registerEvents((Listener)new CitizensListener());
            }
            try {
                JoinNPC.spawnNPCs();
            }
            catch (Exception ex5) {
                this.getLogger().severe("Could not spawn CmdJoin NPCs. Make sure you have right version of Citizens for your server!");
                JoinNPC.setCitizensSupport(false);
            }
            return;
        }, 40L);
        Language.setupCustomStatsMessages();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.getLogger().info("Hook into PlaceholderAPI support!");
            new PAPISupport().register();
            SupportPAPI.setSupportPAPI(new SupportPAPI.withPAPI());
        }
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                WithChat.setChat((net.milkbowl.vault.chat.Chat)this.getServer().getServicesManager().getRegistration((Class)net.milkbowl.vault.chat.Chat.class).getProvider());
                BeaconBattle.plugin.getLogger().info("Hook into vault chat support!");
                BeaconBattle.chat = new WithChat();
            }
            catch (Exception ex6) {
                BeaconBattle.chat = new NoChat();
            }
            try {
                WithEconomy.setEconomy((net.milkbowl.vault.economy.Economy)this.getServer().getServicesManager().getRegistration((Class)net.milkbowl.vault.economy.Economy.class).getProvider());
                BeaconBattle.plugin.getLogger().info("Hook into vault economy support!");
                BeaconBattle.economy = new WithEconomy();
            }
            catch (Exception ex7) {
                BeaconBattle.economy = new NoEconomy();
            }
        }
        else {
            BeaconBattle.chat = new NoChat();
            BeaconBattle.economy = new NoEconomy();
        }
        if (BeaconBattle.config.getBoolean("formatChat")) {
            registerEvents((Listener)new PlayerChat());
        }
        BeaconBattle.nms.registerTntWhitelist();
        final Iterator<Player> iterator = (Iterator<Player>)Bukkit.getOnlinePlayers().iterator();
        while (iterator.hasNext()) {
            iterator.next().kickPlayer("BeaconBattle1058 was RELOADED! (do not reload plugins)");
        }
        new Sounds();
        BeaconBattle.shop = new ShopManager();
        for (final Language language : Language.getLanguages()) {
            language.setupUnSetCategories();
            Language.addDefaultMessagesCommandItems(language);
        }
        LevelsConfig.init();
        final Metrics metrics = new Metrics((Plugin)this, 1885);
        metrics.addCustomChart(new Metrics.SimplePie("server_type", () -> getServerType().toString()));
        metrics.addCustomChart(new Metrics.SimplePie("default_language", () -> Language.getDefaultLanguage().getIso()));
        metrics.addCustomChart(new Metrics.SimplePie("auto_scale", () -> String.valueOf(BeaconBattle.autoscale)));
        metrics.addCustomChart(new Metrics.SimplePie("party_adapter", () -> String.valueOf(BeaconBattle.party.getClass().getName())));
        metrics.addCustomChart(new Metrics.SimplePie("chat_adapter", () -> String.valueOf(BeaconBattle.chat.getClass().getName())));
        metrics.addCustomChart(new Metrics.SimplePie("level_adapter", () -> String.valueOf(getLevelSupport().getClass().getName())));
        metrics.addCustomChart(new Metrics.SimplePie("db_adapter", () -> String.valueOf(getRemoteDatabase().getClass().getName())));
        metrics.addCustomChart(new Metrics.SimplePie("map_adapter", () -> String.valueOf(getAPI().getRestoreAdapter().getOwner().getName())));
        if (Bukkit.getPluginManager().getPlugin("VipFeatures") != null) {
            try {
                final IVipFeatures vipFeatures = (IVipFeatures)Bukkit.getServicesManager().getRegistration((Class)IVipFeatures.class).getProvider();
                vipFeatures.registerMiniGame(new VipFeatures((Plugin)this));
                registerEvents((Listener)new VipListeners(vipFeatures));
                this.getLogger().log(java.util.logging.Level.INFO, "Hook into VipFeatures support.");
            }
            catch (Exception ex8) {
                this.getLogger().warning("Could not load support for VipFeatures.");
            }
            catch (MiniGameAlreadyRegistered miniGameAlreadyRegistered) {
                miniGameAlreadyRegistered.printStackTrace();
            }
        }
        new SpigotUpdater((Plugin)this, 50942, true).checkUpdate();
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.getLogger().info("This server is running in " + getServerType().toString() + " with auto-scale " + BeaconBattle.autoscale), 100L);
        UpgradesManager.init();
        PreLoadedCleaner.init();
        final int int1 = BeaconBattle.config.getInt("scoreboard-settings.player-list.names-refresh-interval");
        if (int1 < 1) {
            Bukkit.getLogger().info("Scoreboard names list refresh is disabled. (Is set to " + int1 + ").");
        }
        else {
            if (int1 < 20) {
                Bukkit.getLogger().warning("Scoreboard names list refresh interval is set to: " + int1);
                Bukkit.getLogger().warning("It is not recommended to use a value under 20 ticks.");
                Bukkit.getLogger().warning("If you expect performance issues please increase its timer.");
            }
            Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new SidebarListRefresh(), 23L, (long)int1);
        }
        final int int2 = BeaconBattle.config.getInt("scoreboard-settings.sidebar.placeholders-refresh-interval");
        if (int2 < 1) {
            Bukkit.getLogger().info("Scoreboard placeholders refresh is disabled. (Is set to " + int2 + ").");
        }
        else {
            if (int2 < 20) {
                Bukkit.getLogger().warning("Scoreboard placeholders refresh interval is set to: " + int2);
                Bukkit.getLogger().warning("It is not recommended to use a value under 20 ticks.");
                Bukkit.getLogger().warning("If you expect performance issues please increase its timer.");
            }
            Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new SidebarPlaceholderRefresh(), 28L, (long)int2);
        }
        final int int3 = BeaconBattle.config.getInt("scoreboard-settings.sidebar.title-refresh-interval");
        if (int3 < 1) {
            Bukkit.getLogger().info("Scoreboard title refresh is disabled. (Is set to " + int3 + ").");
        }
        else {
            if (int3 < 4) {
                Bukkit.getLogger().warning("Scoreboard title refresh interval is set to: " + int3);
                Bukkit.getLogger().warning("If you expect performance issues please increase its timer.");
            }
            Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new SidebarTitleRefresh(), 32L, (long)int3);
        }
        final int int4 = BeaconBattle.config.getInt("scoreboard-settings.health.animation-refresh-interval");
        if (int4 < 1) {
            Bukkit.getLogger().info("Scoreboard health animation refresh is disabled. (Is set to " + int4 + ").");
        }
        else {
            if (int4 < 20) {
                Bukkit.getLogger().warning("Scoreboard health animation refresh interval is set to: " + int4);
                Bukkit.getLogger().warning("It is not recommended to use a value under 20 ticks.");
                Bukkit.getLogger().warning("If you expect performance issues please increase its timer.");
            }
            Bukkit.getScheduler().runTaskTimer((Plugin)this, (Runnable)new SidebarLifeRefresh(), 40L, (long)int4);
        }
        registerEvents((Listener)new ScoreboardListener());
    }
    
    public void onDisable() {
        if (!this.serverSoftwareSupport) {
            return;
        }
        if (getServerType() == ServerType.BUNGEE) {
            ArenaSocket.disable();
        }
        try {
            final Iterator<IArena> iterator = Arena.getArenas().iterator();
            while (iterator.hasNext()) {
                iterator.next().disable();
            }
        }
        catch (Exception ex) {}
        if (BeaconBattle.remoteDatabase != null) {
            BeaconBattle.remoteDatabase.close();
        }
    }
    
    private void loadArenasAndSigns() {
        BeaconBattle.api.getRestoreAdapter().convertWorlds();
        final File file = new File(BeaconBattle.plugin.getDataFolder(), "/Arenas");
        if (file.exists()) {
            final ArrayList<File> list = new ArrayList<File>();
            for (final File file2 : Objects.requireNonNull(file.listFiles())) {
                if (file2.isFile() && file2.getName().endsWith(".yml")) {
                    list.add(file2);
                }
            }
            if (BeaconBattle.serverType == ServerType.BUNGEE && !BeaconBattle.autoscale) {
                if (list.isEmpty()) {
                    this.getLogger().log(java.util.logging.Level.WARNING, "Could not find any arena!");
                    return;
                }
                new Arena(((File)list.get(new Random().nextInt(list.size()))).getName().replace(".yml", ""), null);
            }
            else {
                final Iterator<Object> iterator = list.iterator();
                while (iterator.hasNext()) {
                    new Arena(iterator.next().getName().replace(".yml", ""), null);
                }
            }
        }
    }
    
    public static void registerEvents(final Listener... array) {
        Arrays.stream(array).forEach(listener -> BeaconBattle.plugin.getServer().getPluginManager().registerEvents(listener, (Plugin)BeaconBattle.plugin));
    }
    
    public static void setDebug(final boolean debug) {
        BeaconBattle.debug = debug;
    }
    
    public static void setServerType(final ServerType serverType) {
        BeaconBattle.serverType = serverType;
        if (serverType == ServerType.BUNGEE) {
            BeaconBattle.autoscale = true;
        }
    }
    
    public static void setAutoscale(final boolean autoscale) {
        BeaconBattle.autoscale = autoscale;
    }
    
    public static void debug(final String str) {
        if (BeaconBattle.debug) {
            BeaconBattle.plugin.getLogger().info("DEBUG: " + str);
        }
    }
    
    public static String getForCurrentVersion(final String s, final String s2, final String s3) {
        final String serverVersion = getServerVersion();
        switch (serverVersion) {
            case "v1_8_R3": {
                return s;
            }
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            case "v1_12_R1": {
                return s2;
            }
            default: {
                return s3;
            }
        }
    }
    
    public static ServerType getServerType() {
        return BeaconBattle.serverType;
    }
    
    public static Party getParty() {
        return BeaconBattle.party;
    }
    
    public static Chat getChatSupport() {
        return BeaconBattle.chat;
    }
    
    public static Level getLevelSupport() {
        return BeaconBattle.level;
    }
    
    public static void setLevelAdapter(final Level level) {
        if (level instanceof InternalLevel) {
            if (LevelListeners.instance == null) {
                Bukkit.getPluginManager().registerEvents((Listener)new LevelListeners(), (Plugin)BeaconBattle.plugin);
            }
        }
        else if (LevelListeners.instance != null) {
            PlayerJoinEvent.getHandlerList().unregister((Listener)LevelListeners.instance);
            PlayerQuitEvent.getHandlerList().unregister((Listener)LevelListeners.instance);
            LevelListeners.instance = null;
        }
        BeaconBattle.level = level;
    }
    
    public static Economy getEconomy() {
        return BeaconBattle.economy;
    }
    
    public static ConfigManager getGeneratorsCfg() {
        return BeaconBattle.generators;
    }
    
    public static void setLobbyWorld(final String lobbyWorld) {
        BeaconBattle.lobbyWorld = lobbyWorld;
    }
    
    public static String getServerVersion() {
        return BeaconBattle.version;
    }
    
    public static String getLobbyWorld() {
        return BeaconBattle.lobbyWorld;
    }
    
    public static Database getRemoteDatabase() {
        return BeaconBattle.remoteDatabase;
    }
    
    public static StatsManager getStatsManager() {
        return BeaconBattle.statsManager;
    }
    
    public static ws.billy.bedwars.api.BeaconBattle getAPI() {
        return BeaconBattle.api;
    }
    
    private boolean checkSWM() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        if (plugin == null) {
            return false;
        }
        final String version = plugin.getDescription().getVersion();
        switch (version) {
            case "2.2.0":
            case "2.1.3":
            case "2.1.2":
            case "2.1.1":
            case "2.1.0":
            case "2.0.5":
            case "2.0.4":
            case "2.0.3":
            case "2.0.2":
            case "2.0.1":
            case "2.0.0":
            case "1.1.4":
            case "1.1.3":
            case "1.1.2":
            case "1.1.1":
            case "1.1.0":
            case "1.0.2":
            case "1.0.1":
            case "1.0.0-BETA": {
                this.getLogger().warning("Could not hook into SlimeWorldManager support! You are running an unsupported version");
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    static {
        BeaconBattle.serverType = ServerType.MULTIARENA;
        BeaconBattle.debug = true;
        BeaconBattle.autoscale = false;
        BeaconBattle.mainCmd = "bw";
        BeaconBattle.link = "https://www.spigotmc.org/resources/50942/";
        BeaconBattle.party = new NoParty();
        BeaconBattle.chat = new NoChat();
        BeaconBattle.version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        BeaconBattle.lobbyWorld = "";
        BeaconBattle.arenaManager = new ArenaManager();
    }
}
