

package ws.billy.spigotutils;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerJoinEvent;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class SpigotUpdater
{
    private int resourceID;
    private Plugin plugin;
    private String currentVersion;
    private String newVersion;
    private boolean updateAvailable;
    private boolean updateMessage;
    
    public SpigotUpdater(final Plugin plugin, final int resourceID, final boolean updateMessage) {
        this.newVersion = null;
        this.updateAvailable = false;
        this.resourceID = resourceID;
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.updateMessage = updateMessage;
    }
    
    public void checkUpdate() {
        if (!this.plugin.isEnabled()) {
            this.plugin.getLogger().log(Level.WARNING, "Could not check for updates. #checkUpdate cannot be used when the plugin is disabled.");
            return;
        }
        Bukkit.getPluginManager().registerEvents((Listener)new JoinListener(), this.plugin);
        final URL url;
        HttpURLConnection httpURLConnection;
        final BufferedReader bufferedReader;
        int i = 0;
        final String[] array;
        final String[] array2;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceID);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                this.newVersion = bufferedReader.readLine();
                if (!this.newVersion.equalsIgnoreCase(this.currentVersion)) {
                    this.newVersion.split("\\.");
                    this.currentVersion.split("\\.");
                    while (i < array.length) {
                        if (i < array2.length) {
                            try {
                                if (Integer.parseInt(array[i]) > Integer.parseInt(array2[i])) {
                                    this.updateAvailable = true;
                                }
                                else if (Integer.parseInt(array[i]) != Integer.parseInt(array2[i])) {
                                    break;
                                }
                            }
                            catch (NumberFormatException ex) {}
                        }
                        else {
                            this.updateAvailable = true;
                        }
                        ++i;
                    }
                    if (this.updateAvailable && this.updateMessage) {
                        this.plugin.getLogger().log(Level.WARNING, "                                    ");
                        this.plugin.getLogger().log(Level.WARNING, "------------------------------------");
                        this.plugin.getLogger().log(Level.WARNING, " ");
                        this.plugin.getLogger().log(Level.WARNING, "There is a new version available!");
                        this.plugin.getLogger().log(Level.WARNING, "New version: " + this.newVersion);
                        this.plugin.getLogger().log(Level.WARNING, "You are running: " + this.currentVersion);
                        this.plugin.getLogger().log(Level.WARNING, " ");
                        this.plugin.getLogger().log(Level.WARNING, "https://www.spigotmc.org/resources/" + this.resourceID);
                        this.plugin.getLogger().log(Level.WARNING, "------------------------------------");
                        this.plugin.getLogger().log(Level.WARNING, "                                    ");
                    }
                }
                httpURLConnection.disconnect();
            }
            catch (IOException ex2) {
                this.plugin.getLogger().log(Level.INFO, "Could not check for updates.");
            }
        });
    }
    
    public boolean isUpdateAvailable() {
        return this.updateAvailable;
    }
    
    @Nullable
    public String getNewVersion() {
        return this.newVersion;
    }
    
    public String getCurrentVersion() {
        return this.currentVersion;
    }
    
    private class JoinListener implements Listener
    {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
            if (playerJoinEvent.getPlayer().isOp() && SpigotUpdater.this.isUpdateAvailable() && SpigotUpdater.this.updateMessage) {
                playerJoinEvent.getPlayer().sendMessage("");
                playerJoinEvent.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + SpigotUpdater.this.plugin.getName() + ChatColor.GRAY + "]" + ChatColor.WHITE + " there is a new version available: " + ChatColor.GREEN + SpigotUpdater.this.getNewVersion());
            }
        }
    }
}
