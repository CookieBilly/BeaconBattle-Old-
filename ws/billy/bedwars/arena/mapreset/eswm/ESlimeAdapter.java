

package ws.billy.bedwars.arena.mapreset.eswm;

import org.bukkit.entity.Entity;
import java.util.Optional;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.CompoundMap;
import java.io.OutputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import java.io.FileOutputStream;
import com.flowpowered.nbt.CompoundTag;
import java.io.InputStream;
import com.flowpowered.nbt.stream.NBTInputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.apache.commons.io.FileUtils;
import ws.billy.bedwars.api.util.ZipFileUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.Material;
import com.grinderwolf.eswm.api.exceptions.WorldLoadedException;
import com.grinderwolf.eswm.api.exceptions.WorldTooBigException;
import com.grinderwolf.eswm.api.exceptions.InvalidWorldException;
import com.grinderwolf.eswm.api.exceptions.WorldAlreadyExistsException;
import java.io.File;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.server.ISetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import com.grinderwolf.eswm.api.world.SlimeWorld;
import com.grinderwolf.eswm.api.loaders.SlimeLoader;
import com.grinderwolf.eswm.api.exceptions.WorldInUseException;
import com.grinderwolf.eswm.api.exceptions.NewerFormatException;
import com.grinderwolf.eswm.api.exceptions.CorruptedWorldException;
import java.io.IOException;
import com.grinderwolf.eswm.api.exceptions.UnknownWorldException;
import com.grinderwolf.eswm.api.world.properties.SlimeProperties;
import com.grinderwolf.eswm.api.world.properties.SlimePropertyMap;
import java.util.logging.Level;
import ws.billy.bedwars.api.util.FileUtil;
import ws.billy.bedwars.api.server.ServerType;
import org.jetbrains.annotations.NotNull;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.BeaconBattle;
import com.grinderwolf.eswm.api.SlimePlugin;
import ws.billy.bedwars.api.server.RestoreAdapter;

public class ESlimeAdapter extends RestoreAdapter
{
    private SlimePlugin slime;
    private BeaconBattle api;
    
    public ESlimeAdapter(final Plugin plugin) {
        super(plugin);
        this.slime = (SlimePlugin)Bukkit.getPluginManager().getPlugin("Enhanced-SlimeWorldManager");
        this.api = (BeaconBattle)Bukkit.getServer().getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
    }
    
    @Override
    public void onEnable(@NotNull final IArena arena) {
        if (this.api.getVersionSupport().getMainLevel().equalsIgnoreCase(arena.getWorldName()) && (this.api.getServerType() != ServerType.BUNGEE || this.api.getArenaUtil().getGamesBeforeRestart() != 1)) {
            FileUtil.setMainLevel("ignore_main_level", this.api.getVersionSupport());
            this.getOwner().getLogger().log(Level.SEVERE, "Cannot use level-name as arenas. Automatically creating a new void map for level-name.");
            this.getOwner().getLogger().log(Level.SEVERE, "The server is restarting...");
            Bukkit.getServer().spigot().restart();
            return;
        }
        SlimePropertyMap slimePropertyMap;
        final String[] array;
        final SlimeLoader slimeLoader;
        SlimeWorld clone = null;
        final Throwable t;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            if (Bukkit.getWorld(arena.getWorldName()) != null) {
                Bukkit.getScheduler().runTask(this.getOwner(), () -> arena.init(Bukkit.getWorld(arena.getWorldName())));
            }
            else {
                this.slime.getLoader("file");
                arena.getConfig().getString("waiting.Loc").split(",");
                slimePropertyMap = new SlimePropertyMap();
                slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)3);
                try {
                    slimePropertyMap.set(SlimeProperties.SPAWN_X, (Object)(int)Double.parseDouble(array[0]));
                    slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)(int)Double.parseDouble(array[1]));
                    slimePropertyMap.set(SlimeProperties.SPAWN_Z, (Object)(int)Double.parseDouble(array[2]));
                }
                catch (NumberFormatException ex) {
                    slimePropertyMap.set(SlimeProperties.SPAWN_X, (Object)0);
                    slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)1);
                    slimePropertyMap.set(SlimeProperties.SPAWN_Z, (Object)0);
                }
                slimePropertyMap.set(SlimeProperties.ALLOW_ANIMALS, (Object)false);
                slimePropertyMap.set(SlimeProperties.ALLOW_MONSTERS, (Object)false);
                slimePropertyMap.set(SlimeProperties.DIFFICULTY, (Object)"easy");
                slimePropertyMap.set(SlimeProperties.PVP, (Object)true);
                try {
                    this.slime.loadWorld(slimeLoader, arena.getArenaName(), true, slimePropertyMap);
                    if (this.api.getServerType() == ServerType.BUNGEE) {
                        clone = clone.clone(arena.getWorldName());
                    }
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> this.slime.generateWorld(clone));
                }
                catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException ex2) {
                    this.api.getArenaUtil().removeFromEnableQueue(arena);
                    t.printStackTrace();
                }
            }
        });
    }
    
    @Override
    public void onRestart(final IArena arena) {
        if (this.api.getServerType() == ServerType.BUNGEE) {
            if (this.api.getArenaUtil().getGamesBeforeRestart() == 0) {
                if (this.api.getArenaUtil().getArenas().isEmpty()) {
                    this.getOwner().getLogger().info("Dispatching command: " + this.api.getConfigs().getMainConfig().getString("bungee-settings.restart-cmd"));
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), this.api.getConfigs().getMainConfig().getString("bungee-settings.restart-cmd"));
                }
            }
            else {
                if (this.api.getArenaUtil().getGamesBeforeRestart() != -1) {
                    this.api.getArenaUtil().setGamesBeforeRestart(this.api.getArenaUtil().getGamesBeforeRestart() - 1);
                }
                Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                    Bukkit.unloadWorld(arena.getWorldName(), false);
                    if (this.api.getArenaUtil().canAutoScale(arena.getArenaName())) {
                        Bukkit.getScheduler().runTaskLater(this.getOwner(), () -> this.api.getArenaUtil().loadArena(arena.getArenaName(), null), 80L);
                    }
                });
            }
        }
        else {
            Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                Bukkit.unloadWorld(arena.getWorldName(), false);
                Bukkit.getScheduler().runTaskLater(this.getOwner(), () -> this.api.getArenaUtil().loadArena(arena.getArenaName(), null), 80L);
            });
        }
    }
    
    @Override
    public void onDisable(final IArena arena) {
        Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(arena.getWorldName(), false));
    }
    
    @Override
    public void onSetupSessionStart(final ISetupSession setupSession) {
        String[] split;
        final SlimePropertyMap slimePropertyMap;
        final SlimeLoader slimeLoader;
        final File file;
        SlimeWorld slimeWorld = null;
        final Throwable t;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            this.slime.getLoader("file");
            split = new String[] { "0", "50", "0" };
            if (setupSession.getConfig().getYml().getString("waiting.Loc") != null) {
                split = setupSession.getConfig().getString("waiting.Loc").split(",");
            }
            slimePropertyMap = new SlimePropertyMap();
            slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)3);
            try {
                slimePropertyMap.set(SlimeProperties.SPAWN_X, (Object)(int)Double.parseDouble(split[0]));
                slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)(int)Double.parseDouble(split[1]));
                slimePropertyMap.set(SlimeProperties.SPAWN_Z, (Object)(int)Double.parseDouble(split[2]));
            }
            catch (NumberFormatException ex) {
                slimePropertyMap.set(SlimeProperties.SPAWN_X, (Object)0);
                slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)1);
                slimePropertyMap.set(SlimeProperties.SPAWN_Z, (Object)0);
            }
            slimePropertyMap.set(SlimeProperties.ALLOW_ANIMALS, (Object)false);
            slimePropertyMap.set(SlimeProperties.ALLOW_MONSTERS, (Object)false);
            slimePropertyMap.set(SlimeProperties.DIFFICULTY, (Object)"easy");
            slimePropertyMap.set(SlimeProperties.PVP, (Object)true);
            try {
                if (Bukkit.getWorld(setupSession.getWorldName()) != null) {
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(setupSession.getWorldName(), false));
                }
                if (slimeLoader.worldExists(setupSession.getWorldName())) {
                    this.slime.loadWorld(slimeLoader, setupSession.getWorldName(), false, slimePropertyMap);
                    Bukkit.getScheduler().runTask(this.getOwner(), () -> setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Loading world from Enhanced-SlimeWorldManager container."));
                }
                else {
                    new File(Bukkit.getWorldContainer(), setupSession.getWorldName() + "/level.dat");
                    if (file.exists()) {
                        Bukkit.getScheduler().runTask(this.getOwner(), () -> setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Importing world to the Enhanced-SlimeWorldManager container."));
                        this.slime.importWorld(new File(Bukkit.getWorldContainer(), setupSession.getWorldName()), setupSession.getWorldName().toLowerCase(), slimeLoader);
                        slimeWorld = this.slime.loadWorld(slimeLoader, setupSession.getWorldName(), false, slimePropertyMap);
                    }
                    else {
                        Bukkit.getScheduler().runTask(this.getOwner(), () -> setupSession.getPlayer().sendMessage(ChatColor.GREEN + "Creating anew void map."));
                        slimeWorld = this.slime.createEmptyWorld(slimeLoader, setupSession.getWorldName(), false, slimePropertyMap);
                    }
                }
                Bukkit.getScheduler().runTask(this.getOwner(), () -> {
                    this.slime.generateWorld(slimeWorld);
                    setupSession.teleportPlayer();
                });
            }
            catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException | WorldAlreadyExistsException | InvalidWorldException | WorldTooBigException | WorldLoadedException ex2) {
                setupSession.getPlayer().sendMessage(ChatColor.RED + "An error occurred! Please check console.");
                t.printStackTrace();
                setupSession.close();
            }
        });
    }
    
    @Override
    public void onSetupSessionClose(@NotNull final ISetupSession setupSession) {
        Bukkit.getWorld(setupSession.getWorldName()).save();
        Bukkit.getScheduler().runTask(this.getOwner(), () -> Bukkit.unloadWorld(setupSession.getWorldName(), true));
    }
    
    @Override
    public void onLobbyRemoval(@NotNull final IArena arena) {
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
        Bukkit.getScheduler().runTask(this.getOwner(), () -> {
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
            Bukkit.getScheduler().runTaskLater(this.getOwner(), () -> location.getWorld().getEntities().forEach(entity -> {
                if (entity instanceof Item) {
                    entity.remove();
                }
            }), 15L);
        });
    }
    
    @Override
    public boolean isWorld(final String s) {
        try {
            return this.slime.getLoader("file").worldExists(s);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void deleteWorld(final String s) {
        final Throwable t;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            try {
                this.slime.getLoader("file").deleteWorld(s);
            }
            catch (UnknownWorldException | IOException ex) {
                t.printStackTrace();
            }
        });
    }
    
    @Override
    public void cloneArena(final String s, final String s2) {
        final SlimePropertyMap slimePropertyMap;
        final Throwable t;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            slimePropertyMap = new SlimePropertyMap();
            slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)3);
            slimePropertyMap.set(SlimeProperties.SPAWN_X, (Object)0);
            slimePropertyMap.set(SlimeProperties.SPAWN_Y, (Object)10);
            slimePropertyMap.set(SlimeProperties.SPAWN_Z, (Object)0);
            slimePropertyMap.set(SlimeProperties.ALLOW_ANIMALS, (Object)false);
            slimePropertyMap.set(SlimeProperties.ALLOW_MONSTERS, (Object)false);
            slimePropertyMap.set(SlimeProperties.DIFFICULTY, (Object)"easy");
            slimePropertyMap.set(SlimeProperties.PVP, (Object)true);
            try {
                this.slime.loadWorld(this.slime.getLoader("file"), s, true, slimePropertyMap).clone(s2, this.slime.getLoader("file"));
            }
            catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException | WorldAlreadyExistsException ex) {
                t.printStackTrace();
            }
        });
    }
    
    @Override
    public List<String> getWorldsList() {
        try {
            return (List<String>)this.slime.getLoader("file").listWorlds();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<String>();
        }
    }
    
    @Override
    public void convertWorlds() {
        final File parent = new File(this.getOwner().getDataFolder(), "/Arenas");
        final SlimeLoader loader = this.slime.getLoader("file");
        if (parent.exists()) {
            final File[] listFiles = parent.listFiles();
            if (listFiles != null) {
                for (final File file : listFiles) {
                    if (file.isFile() && file.getName().endsWith(".yml")) {
                        final String lowerCase = file.getName().replace(".yml", "").toLowerCase();
                        final File file2 = new File(Bukkit.getWorldContainer(), file.getName().replace(".yml", ""));
                        try {
                            if (!loader.worldExists(lowerCase)) {
                                if (!file.getName().equals(lowerCase) && !file.renameTo(new File(parent, lowerCase + ".yml"))) {
                                    this.getOwner().getLogger().log(Level.WARNING, "Could not rename " + file.getName() + ".yml to " + lowerCase + ".yml");
                                }
                                final File file3 = new File(this.getOwner().getDataFolder() + "/Cache", file2.getName() + ".zip");
                                if (file2.exists() && file3.exists()) {
                                    FileUtil.delete(file2);
                                    ZipFileUtil.unzipFileIntoDirectory(file3, new File(Bukkit.getWorldContainer(), lowerCase));
                                }
                                this.deleteWorldTrash(lowerCase);
                                this.handleLevelDat(lowerCase);
                                this.convertWorld(lowerCase, null);
                            }
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        final File[] array2;
        final File[] array3;
        int length2;
        int j = 0;
        File file4;
        Bukkit.getScheduler().runTaskAsynchronously(this.getOwner(), () -> {
            Bukkit.getWorldContainer().listFiles();
            if (array2 != null) {
                for (length2 = array3.length; j < length2; ++j) {
                    file4 = array3[j];
                    if (file4 != null && file4.isDirectory() && file4.getName().contains("bw_temp_")) {
                        try {
                            FileUtils.deleteDirectory(file4);
                        }
                        catch (IOException ex2) {
                            ex2.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    
    private void convertWorld(final String s, @Nullable final Player player) {
        final SlimeLoader loader = this.slime.getLoader("file");
        try {
            this.getOwner().getLogger().log(Level.INFO, "Converting " + s + " to the Slime format.");
            this.slime.importWorld(new File(Bukkit.getWorldContainer(), s), s, loader);
        }
        catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException ex) {
            final Object o2;
            final Object o = o2;
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Could not convert " + s + " to the Slime format.");
                player.sendMessage(ChatColor.RED + "Check the console for details.");
                final ISetupSession setupSession = this.api.getSetupSession(player.getUniqueId());
                if (setupSession != null) {
                    setupSession.close();
                }
            }
            this.getOwner().getLogger().log(Level.WARNING, "Could not convert " + s + " to the Slime format.");
            ((Throwable)o).printStackTrace();
        }
    }
    
    private void deleteWorldTrash(final String s) {
        for (final File file : new File[] { new File(Bukkit.getWorldContainer(), s + "/level.dat_mcr"), new File(Bukkit.getWorldContainer(), s + "/level.dat_old"), new File(Bukkit.getWorldContainer(), s + "/session.lock"), new File(Bukkit.getWorldContainer(), s + "/uid.dat") }) {
            if (file.exists() && !file.delete()) {
                this.getOwner().getLogger().warning("Could not delete: " + file.getPath());
                this.getOwner().getLogger().warning("This may cause issues!");
            }
        }
    }
    
    private void handleLevelDat(final String s3) {
        final File file = new File(Bukkit.getWorldContainer(), s3 + "/level.dat");
        if (!file.exists() && file.createNewFile()) {
            final File file2 = new File(Bukkit.getWorldContainer(), "world/region");
            if (file2.exists() && Objects.requireNonNull(file2.list()).length > 0 && Arrays.stream((Object[])Objects.requireNonNull((T[])file2.list())).filter(s -> s.endsWith(".mca")).toArray().length > 0) {
                final NBTInputStream nbtInputStream = new NBTInputStream((InputStream)new FileInputStream(new File(Bukkit.getWorldContainer(), s3 + "/" + Arrays.stream((Object[])Objects.requireNonNull((T[])file2.list())).filter(s2 -> s2.endsWith(".mca")).toArray()[0])));
                final Optional asCompoundTag = nbtInputStream.readTag().getAsCompoundTag();
                nbtInputStream.close();
                if (asCompoundTag.isPresent()) {
                    final Optional asCompoundTag2 = asCompoundTag.get().getAsCompoundTag("Chunk");
                    if (asCompoundTag2.isPresent()) {
                        final int intValue = asCompoundTag2.get().getIntValue("DataVersion").orElse(-1);
                        final NBTOutputStream nbtOutputStream = new NBTOutputStream((OutputStream)new FileOutputStream(file));
                        final CompoundMap compoundMap = new CompoundMap();
                        compoundMap.put((Tag)new IntTag("SpawnX", 0));
                        compoundMap.put((Tag)new IntTag("SpawnY", 255));
                        compoundMap.put((Tag)new IntTag("SpawnZ", 0));
                        if (intValue != -1) {
                            compoundMap.put((Tag)new IntTag("DataVersion", intValue));
                        }
                        nbtOutputStream.writeTag((Tag)new CompoundTag("Data", compoundMap));
                        nbtOutputStream.flush();
                        nbtOutputStream.close();
                    }
                }
            }
        }
    }
}
