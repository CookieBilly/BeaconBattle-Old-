

package ws.billy.bedwars.arena;

import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import ws.billy.bedwars.api.arena.team.TeamColor;
import ws.billy.bedwars.api.events.server.SetupSessionStartEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.server.SetupSessionCloseEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.Inventory;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import java.util.ArrayList;
import org.bukkit.Location;
import ws.billy.bedwars.configuration.ArenaConfig;
import ws.billy.bedwars.api.server.SetupType;
import org.bukkit.entity.Player;
import java.util.List;
import ws.billy.bedwars.api.server.ISetupSession;

public class SetupSession implements ISetupSession
{
    private static List<SetupSession> setupSessions;
    private Player player;
    private String worldName;
    private SetupType setupType;
    private ArenaConfig cm;
    private boolean started;
    private boolean autoCreatedEmerald;
    private boolean autoCreatedDiamond;
    private List<Location> skipAutoCreateGen;
    
    public SetupSession(final Player player, final String worldName) {
        this.started = false;
        this.autoCreatedEmerald = false;
        this.autoCreatedDiamond = false;
        this.skipAutoCreateGen = new ArrayList<Location>();
        this.player = player;
        this.worldName = worldName;
        getSetupSessions().add(this);
        openGUI(player);
    }
    
    public void setSetupType(final SetupType setupType) {
        this.setupType = setupType;
    }
    
    public static List<SetupSession> getSetupSessions() {
        return SetupSession.setupSessions;
    }
    
    public static String getInvName() {
        return "§8Choose a setup method";
    }
    
    public static int getAdvancedSlot() {
        return 5;
    }
    
    public static int getAssistedSlot() {
        return 3;
    }
    
    @Override
    public SetupType getSetupType() {
        return this.setupType;
    }
    
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public String getWorldName() {
        return this.worldName;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public boolean startSetup() {
        this.getPlayer().sendMessage("§6 \u25aa §7Loading " + this.getWorldName());
        this.cm = new ArenaConfig((Plugin)BeaconBattle.plugin, this.getWorldName(), BeaconBattle.plugin.getDataFolder().getPath() + "/Arenas");
        BeaconBattle.getAPI().getRestoreAdapter().onSetupSessionStart(this);
        return true;
    }
    
    private static void openGUI(final Player player) {
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, getInvName());
        final ItemStack itemStack = new ItemStack(Material.GLOWSTONE_DUST);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§e§lASSISTED SETUP");
        itemMeta.setLore((List)Arrays.asList("", "§aEasy and quick setup!", "§7For beginners and lazy staff :D", "", "§3Reduced options."));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(getAssistedSlot(), itemStack);
        final ItemStack itemStack2 = new ItemStack(Material.REDSTONE);
        final ItemMeta itemMeta2 = itemStack2.getItemMeta();
        itemMeta2.setDisplayName("§c§lADVANCED SETUP");
        itemMeta2.setLore((List)Arrays.asList("", "§aDetailed setup!", "§7For experienced staff :D", "", "§3Advanced options."));
        itemStack2.setItemMeta(itemMeta2);
        inventory.setItem(getAdvancedSlot(), itemStack2);
        player.openInventory(inventory);
    }
    
    public void cancel() {
        getSetupSessions().remove(this);
        if (this.isStarted()) {
            this.player.sendMessage("§6 \u25aa §7" + this.getWorldName() + " setup cancelled!");
            this.done();
        }
    }
    
    public void done() {
        BeaconBattle.getAPI().getRestoreAdapter().onSetupSessionClose(this);
        getSetupSessions().remove(this);
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            try {
                this.getPlayer().teleport(BeaconBattle.config.getConfigLoc("lobbyLoc"), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
            catch (Exception ex) {
                this.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
        this.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA) {
            Arena.sendLobbyCommandItems(this.getPlayer());
        }
        Bukkit.getPluginManager().callEvent((Event)new SetupSessionCloseEvent(this));
    }
    
    public static boolean isInSetupSession(final UUID obj) {
        final Iterator<SetupSession> iterator = getSetupSessions().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPlayer().getUniqueId().equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    public static SetupSession getSession(final UUID obj) {
        for (final SetupSession setupSession : getSetupSessions()) {
            if (setupSession.getPlayer().getUniqueId().equals(obj)) {
                return setupSession;
            }
        }
        return null;
    }
    
    public void setStarted(final boolean started) {
        this.started = started;
    }
    
    @Override
    public ArenaConfig getConfig() {
        return this.cm;
    }
    
    @Override
    public void teleportPlayer() {
        this.player.getInventory().clear();
        this.player.teleport(Bukkit.getWorld(this.getWorldName()).getSpawnLocation());
        this.player.setGameMode(GameMode.CREATIVE);
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        this.player.sendMessage("\n" + ChatColor.WHITE + "\n");
        for (int i = 0; i < 10; ++i) {
            this.getPlayer().sendMessage(" ");
        }
        this.player.sendMessage(ChatColor.GREEN + "You were teleported to the " + ChatColor.GOLD + this.getWorldName() + ChatColor.GREEN + "'s spawn.");
        if (this.getSetupType() == SetupType.ASSISTED && this.getConfig().getYml().get("waiting.Loc") == null) {
            this.player.sendMessage("");
            this.player.sendMessage(ChatColor.GREEN + "Hello " + this.player.getDisplayName() + "!");
            this.player.sendMessage(ChatColor.WHITE + "Please set the waiting spawn.");
            this.player.sendMessage(ChatColor.WHITE + "It is the place where players will wait the game to start.");
            this.player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(ChatColor.BLUE + "     \u25aa     " + ChatColor.GOLD + "CLICK HERE TO SET THE WAITING LOBBY    " + ChatColor.BLUE + " \u25aa", ChatColor.LIGHT_PURPLE + "Click to set the waiting spawn.", "/" + BeaconBattle.mainCmd + " setWaitingSpawn", ClickEvent.Action.RUN_COMMAND));
            this.player.spigot().sendMessage((BaseComponent)MainCommand.createTC(ChatColor.YELLOW + "Or type: " + ChatColor.GRAY + "/" + BeaconBattle.mainCmd + " to see the command list.", "/" + BeaconBattle.mainCmd + "", ChatColor.WHITE + "Show commands list."));
        }
        else {
            Bukkit.dispatchCommand((CommandSender)this.player, BeaconBattle.mainCmd + " cmds");
        }
        final World world = Bukkit.getWorld(this.getWorldName());
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> world.getEntities().stream().filter(entity -> entity.getType() != EntityType.PLAYER).filter(entity2 -> entity2.getType() != EntityType.PAINTING).filter(entity3 -> entity3.getType() != EntityType.ITEM_FRAME).forEach(Entity::remove), 30L);
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        Bukkit.getPluginManager().callEvent((Event)new SetupSessionStartEvent(this));
        this.setStarted(true);
        final Iterator<String> iterator;
        String s;
        String[] array;
        int length;
        int j = 0;
        String s2;
        final Iterator<String> iterator2;
        String s3;
        final String[] array2;
        int length2;
        int k = 0;
        String str;
        final Iterator<String> iterator3;
        String s4;
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
            this.getTeams().iterator();
            while (iterator.hasNext()) {
                s = iterator.next();
                array = new String[] { "Iron", "Gold", "Emerald" };
                for (length = array.length; j < length; ++j) {
                    s2 = array[j];
                    if (this.getConfig().getYml().get("Team." + s + "." + s2) != null) {
                        this.getConfig().getList("Team." + s + ".Iron").iterator();
                        while (iterator2.hasNext()) {
                            s3 = iterator2.next();
                            ws.billy.bedwars.commands.Misc.createArmorStand(ChatColor.GOLD + s2 + " generator added for team: " + this.getTeamColor(s) + s, this.getConfig().convertStringToArenaLocation(s3), s3);
                        }
                    }
                    if (this.getConfig().getYml().get("Team." + s + ".Spawn") != null) {
                        ws.billy.bedwars.commands.Misc.createArmorStand(this.getTeamColor(s) + s + " " + ChatColor.GOLD + "SPAWN SET", this.getConfig().getArenaLoc("Team." + s + ".Spawn"), this.getConfig().getString("Team." + s + ".Spawn"));
                    }
                    if (this.getConfig().getYml().get("Team." + s + ".Bed") != null) {
                        ws.billy.bedwars.commands.Misc.createArmorStand(this.getTeamColor(s) + s + " " + ChatColor.GOLD + "BED SET", this.getConfig().getArenaLoc("Team." + s + ".Bed"), this.getConfig().getString("Team." + s + ".Bed"));
                    }
                }
                if (this.getConfig().getYml().get("Team." + s + ".Shop") != null) {
                    ws.billy.bedwars.commands.Misc.createArmorStand(this.getTeamColor(s) + s + " " + ChatColor.GOLD + "SHOP SET", this.getConfig().getArenaLoc("Team." + s + ".Shop"), null);
                }
                if (this.getConfig().getYml().get("Team." + s + ".Upgrade") != null) {
                    ws.billy.bedwars.commands.Misc.createArmorStand(this.getTeamColor(s) + s + " " + ChatColor.GOLD + "UPGRADE SET", this.getConfig().getArenaLoc("Team." + s + ".Upgrade"), null);
                }
                if (this.getConfig().getYml().get("Team." + s + "." + "kill-drops-loc") != null) {
                    ws.billy.bedwars.commands.Misc.createArmorStand(ChatColor.GOLD + "Kill drops " + s, this.getConfig().getArenaLoc("Team." + s + "." + "kill-drops-loc"), null);
                }
            }
            array2 = new String[] { "Emerald", "Diamond" };
            for (length2 = array2.length; k < length2; ++k) {
                str = array2[k];
                if (this.getConfig().getYml().get("generator." + str) != null) {
                    this.getConfig().getList("generator." + str).iterator();
                    while (iterator3.hasNext()) {
                        s4 = iterator3.next();
                        ws.billy.bedwars.commands.Misc.createArmorStand(ChatColor.GOLD + str + " SET", this.getConfig().convertStringToArenaLocation(s4), s4);
                    }
                }
            }
        }, 90L);
    }
    
    @Override
    public void close() {
        this.cancel();
    }
    
    public List<Location> getSkipAutoCreateGen() {
        return new ArrayList<Location>(this.skipAutoCreateGen);
    }
    
    public void addSkipAutoCreateGen(final Location location) {
        this.skipAutoCreateGen.add(location);
    }
    
    public void setAutoCreatedEmerald(final boolean autoCreatedEmerald) {
        this.autoCreatedEmerald = autoCreatedEmerald;
    }
    
    public boolean isAutoCreatedEmerald() {
        return this.autoCreatedEmerald;
    }
    
    public void setAutoCreatedDiamond(final boolean autoCreatedDiamond) {
        this.autoCreatedDiamond = autoCreatedDiamond;
    }
    
    public boolean isAutoCreatedDiamond() {
        return this.autoCreatedDiamond;
    }
    
    public String getPrefix() {
        return ChatColor.GREEN + "[" + this.getWorldName() + ChatColor.GREEN + "] " + ChatColor.GOLD;
    }
    
    public ChatColor getTeamColor(final String str) {
        return TeamColor.getChatColor(this.getConfig().getString("Team." + str + ".Color"));
    }
    
    public void displayAvailableTeams() {
        if (this.getConfig().getYml().get("Team") != null) {
            this.getPlayer().sendMessage(this.getPrefix() + "Available teams: ");
            for (final String s : Objects.requireNonNull(this.getConfig().getYml().getConfigurationSection("Team")).getKeys(false)) {
                this.getPlayer().sendMessage(this.getPrefix() + TeamColor.getChatColor(Objects.requireNonNull(this.getConfig().getYml().getString("Team." + s + ".Color"))) + s);
            }
        }
    }
    
    public String getNearestTeam() {
        String s = "";
        final ConfigurationSection configurationSection = this.getConfig().getYml().getConfigurationSection("Team");
        if (configurationSection == null) {
            return s;
        }
        double n = 100.0;
        for (final String s2 : configurationSection.getKeys(false)) {
            if (this.getConfig().getYml().get("Team." + s2 + ".Spawn") == null) {
                continue;
            }
            final double distance = this.getConfig().getArenaLoc("Team." + s2 + ".Spawn").distance(this.getPlayer().getLocation());
            if (distance > this.getConfig().getInt("island-radius") || distance >= n) {
                continue;
            }
            n = distance;
            s = s2;
        }
        return s;
    }
    
    public String dot() {
        return ChatColor.BLUE + " " + '\u25aa' + " " + ChatColor.GRAY + "/" + BeaconBattle.mainCmd + " ";
    }
    
    public List<String> getTeams() {
        if (this.getConfig().getYml().get("Team") == null) {
            return new ArrayList<String>();
        }
        return new ArrayList<String>(this.getConfig().getYml().getConfigurationSection("Team").getKeys(false));
    }
    
    static {
        SetupSession.setupSessions = new ArrayList<SetupSession>();
    }
}
