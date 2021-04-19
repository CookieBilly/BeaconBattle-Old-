

package ws.billy.bedwars.maprestore.internal;

import org.bukkit.entity.Entity;
import java.util.Iterator;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.server.ISetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.WorldCreator;
import java.io.IOException;
import ws.billy.bedwars.api.util.ZipFileUtil;
import ws.billy.bedwars.maprestore.internal.files.WorldZipper;
import ws.billy.bedwars.api.util.FileUtil;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.plugin.Plugin;
import java.io.File;
import ws.billy.bedwars.api.server.RestoreAdapter;

public class InternalAdapter extends RestoreAdapter
{
    private static final String LEGACY_GENERATOR_SETTINGS = "1;0;1";
    private static final String V1_13_GENERATOR_SETTINGS = "{\"layers\": [{\"block\": \"air\", \"height\": 1}, {\"block\": \"air\", \"height\": 1}], \"biome\":\"plains\"}";
    private static final String V1_16_GENERATOR_SETTINGS = "{\"biome\":\"minecraft:plains\",\"layers\":[{\"block\":\"minecraft:air\",\"height\":1}],\"structures\":{\"structures\":{}}}";
    public static File backupFolder;
    private final String generator;
    
    public InternalAdapter(final Plugin plugin) {
        super(plugin);
        this.generator = ((BeaconBattle.nms.getVersion() > 7) ? "{\"biome\":\"minecraft:plains\",\"layers\":[{\"block\":\"minecraft:air\",\"height\":1}],\"structures\":{\"structures\":{}}}" : ((BeaconBattle.nms.getVersion() > 5) ? "{\"layers\": [{\"block\": \"air\", \"height\": 1}, {\"block\": \"air\", \"height\": 1}], \"biome\":\"plains\"}" : "1;0;1"));
    }
    
    @Override
    public void onEnable(final IArena arena) {
        File file2;
        final File file3;
        WorldCreator worldCreator;
        final World world;
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            if (Bukkit.getWorld(arena.getWorldName()) != null) {
                Bukkit.getScheduler().runTask(this.getOwner(), () -> arena.init(Bukkit.getWorld(arena.getWorldName())));
            }
            else {
                Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
                    new File(InternalAdapter.backupFolder, arena.getArenaName() + ".zip");
                    file2 = new File(Bukkit.getWorldContainer(), arena.getArenaName());
                    if (file3.exists()) {
                        FileUtil.delete(file2);
                    }
                    if (!file3.exists()) {
                        new WorldZipper(arena.getArenaName(), true);
                    }
                    else {
                        try {
                            ZipFileUtil.unzipFileIntoDirectory(file3, new File(Bukkit.getWorldContainer(), arena.getWorldName()));
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    this.deleteWorldTrash(arena.getWorldName());
                    Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, () -> {
                        worldCreator = new WorldCreator(arena.getWorldName());
                        worldCreator.type(WorldType.FLAT);
                        worldCreator.generatorSettings(this.generator);
                        worldCreator.generateStructures(false);
                        Bukkit.createWorld(worldCreator);
                        world.setKeepSpawnInMemory(true);
                        world.setAutoSave(false);
                    });
                });
            }
        });
    }
    
    @Override
    public void onRestart(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
                if (Arena.getGamesBeforeRestart() == 0) {
                    if (Arena.getArenas().isEmpty()) {
                        BeaconBattle.plugin.getLogger().info("Dispatching command: " + BeaconBattle.config.getString("bungee-settings.restart-cmd"));
                        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), BeaconBattle.config.getString("bungee-settings.restart-cmd"));
                    }
                }
                else {
                    if (Arena.getGamesBeforeRestart() != -1) {
                        Arena.setGamesBeforeRestart(Arena.getGamesBeforeRestart() - 1);
                    }
                    Bukkit.unloadWorld(arena.getWorldName(), false);
                    if (Arena.canAutoScale(arena.getArenaName())) {
                        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> new Arena(arena.getArenaName(), null), 80L);
                    }
                }
            }
            else {
                Bukkit.unloadWorld(arena.getWorldName(), false);
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> new Arena(arena.getArenaName(), null), 80L);
            }
            if (!arena.getWorldName().equals(arena.getArenaName())) {
                Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> this.deleteWorld(arena.getWorldName()));
            }
        });
    }
    
    @Override
    public void onDisable(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(arena.getWorldName(), false));
    }
    
    @Override
    public void onSetupSessionStart(final ISetupSession setupSession) {
        final File file2;
        final File file3;
        final WorldCreator worldCreator;
        final File file4;
        final WorldCreator worldCreator2;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            new File(InternalAdapter.backupFolder, setupSession.getWorldName() + ".zip");
            file2 = new File(Bukkit.getWorldContainer(), setupSession.getWorldName());
            if (file3.exists()) {
                FileUtil.delete(file2);
                try {
                    ZipFileUtil.unzipFileIntoDirectory(file3, new File(Bukkit.getWorldContainer(), setupSession.getWorldName()));
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            worldCreator = new WorldCreator(setupSession.getWorldName());
            worldCreator.type(WorldType.FLAT);
            worldCreator.generatorSettings(this.generator);
            worldCreator.generateStructures(false);
            Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                try {
                    new File(Bukkit.getWorldContainer(), setupSession.getWorldName() + "/region");
                    if (file4.exists()) {
                        setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Loading " + setupSession.getWorldName() + " from Bukkit worlds container.");
                        this.deleteWorldTrash(setupSession.getWorldName());
                        Bukkit.createWorld(worldCreator2).setKeepSpawnInMemory(true);
                    }
                    else {
                        try {
                            setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Creating a new void map: " + setupSession.getWorldName());
                            Bukkit.createWorld(worldCreator2).setKeepSpawnInMemory(true);
                            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, setupSession::teleportPlayer, 20L);
                        }
                        catch (Exception ex2) {
                            ex2.printStackTrace();
                            setupSession.close();
                        }
                        return;
                    }
                }
                catch (Exception ex3) {
                    ex3.printStackTrace();
                    setupSession.close();
                    return;
                }
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, setupSession::teleportPlayer, 20L);
            });
        });
    }
    
    @Override
    public void onSetupSessionClose(final ISetupSession setupSession) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
            Bukkit.getWorld(setupSession.getWorldName()).save();
            Bukkit.unloadWorld(setupSession.getWorldName(), true);
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> new WorldZipper(setupSession.getWorldName(), true));
        });
    }
    
    @Override
    public void onLobbyRemoval(final IArena arena) {
        final Location arenaLoc = arena.getConfig().getArenaLoc("waiting.Pos1");
        final Location arenaLoc2 = arena.getConfig().getArenaLoc("waiting.Pos2");
        if (arenaLoc == null || arenaLoc2 == null) {
            return;
        }
        final Location location;
        final Location location2;
        int i = 0;
        final int n;
        int j = 0;
        final int n2;
        int k = 0;
        final int n3;
        Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, () -> {
            Math.min(location.getBlockX(), location2.getBlockX());
            Math.max(location.getBlockX(), location2.getBlockX());
            Math.min(location.getBlockY(), location2.getBlockY());
            Math.max(location.getBlockY(), location2.getBlockY());
            Math.min(location.getBlockZ(), location2.getBlockZ());
            Math.max(location.getBlockZ(), location2.getBlockZ());
            while (i < n) {
                while (j < n2) {
                    while (k < n3) {
                        location.getWorld().getBlockAt(i, j, k).setType(Material.AIR);
                        ++k;
                    }
                    ++j;
                }
                ++i;
            }
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> location.getWorld().getEntities().forEach(entity -> {
                if (entity instanceof Item) {
                    entity.remove();
                }
            }), 15L);
        });
    }
    
    @Override
    public boolean isWorld(final String str) {
        return new File(Bukkit.getWorldContainer(), str + "/region").exists();
    }
    
    @Override
    public void deleteWorld(final String child) {
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            try {
                FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), child));
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public void cloneArena(final String child, final String child2) {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> {
            try {
                FileUtils.copyDirectory(new File(Bukkit.getWorldContainer(), child), new File(Bukkit.getWorldContainer(), child2));
                this.deleteWorldTrash(child2);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public List<String> getWorldsList() {
        final ArrayList<String> list = new ArrayList<String>();
        final File worldContainer = Bukkit.getWorldContainer();
        if (worldContainer.exists()) {
            for (final File file : Objects.requireNonNull(worldContainer.listFiles())) {
                if (file.isDirectory() && new File(file.getName() + "/region").exists() && !file.getName().startsWith("bw_temp")) {
                    list.add(file.getName());
                }
            }
        }
        return list;
    }
    
    @Override
    public void convertWorlds() {
        final File file = new File(BeaconBattle.plugin.getDataFolder(), "/Arenas");
        if (file.exists()) {
            final ArrayList<File> list = new ArrayList<File>();
            for (final File file2 : Objects.requireNonNull(file.listFiles())) {
                if (file2.isFile() && file2.getName().contains(".yml")) {
                    list.add(file2);
                }
            }
            final ArrayList<File> list2 = new ArrayList<File>();
            final ArrayList<File> list3 = new ArrayList<File>();
            for (final File file3 : list) {
                if (!file3.getName().equals(file3.getName().toLowerCase())) {
                    final File dest = new File(file.getPath() + "/" + file3.getName().toLowerCase());
                    if (!file3.renameTo(dest)) {
                        list2.add(file3);
                        BeaconBattle.plugin.getLogger().severe("Could not rename " + file3.getName() + " to " + file3.getName().toLowerCase() + "! Please do it manually!");
                    }
                    else {
                        list3.add((Object)dest);
                        list2.add(file3);
                    }
                    final File file4 = new File(BeaconBattle.plugin.getServer().getWorldContainer(), file3.getName().replace(".yml", ""));
                    if (file4.exists() && !file4.getName().equals(file4.getName().toLowerCase()) && !file4.renameTo(new File(BeaconBattle.plugin.getServer().getWorldContainer().getPath() + "/" + file4.getName().toLowerCase()))) {
                        BeaconBattle.plugin.getLogger().severe("Could not rename " + file4.getName() + " folder to " + file4.getName().toLowerCase() + "! Please do it manually!");
                        list2.add(file3);
                        return;
                    }
                    continue;
                }
            }
            final Iterator<Object> iterator2 = list2.iterator();
            while (iterator2.hasNext()) {
                list.remove(iterator2.next());
            }
            list.addAll((Collection<?>)list3);
        }
        final File[] array2;
        final File[] array3;
        int length2;
        int j = 0;
        File file5;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            Bukkit.getWorldContainer().listFiles();
            if (array2 != null) {
                for (length2 = array3.length; j < length2; ++j) {
                    file5 = array3[j];
                    if (file5 != null && file5.isDirectory() && file5.getName().contains("bw_temp_")) {
                        this.deleteWorld(file5.getName());
                    }
                }
            }
        });
    }
    
    private void deleteWorldTrash(final String str) {
        for (final File file : new File[] { new File(Bukkit.getWorldContainer(), str + "/level.dat"), new File(Bukkit.getWorldContainer(), str + "/level.dat_mcr"), new File(Bukkit.getWorldContainer(), str + "/level.dat_old"), new File(Bukkit.getWorldContainer(), str + "/session.lock"), new File(Bukkit.getWorldContainer(), str + "/uid.dat") }) {
            if (file.exists() && !file.delete()) {
                this.getOwner().getLogger().warning("Could not delete: " + file.getPath());
                this.getOwner().getLogger().warning("This may cause issues!");
            }
        }
    }
    
    static {
        InternalAdapter.backupFolder = new File(BeaconBattle.plugin.getDataFolder() + "/Cache");
    }
}
