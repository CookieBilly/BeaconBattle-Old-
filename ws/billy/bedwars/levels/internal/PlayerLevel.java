

package ws.billy.bedwars.levels.internal;

import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.events.player.PlayerLevelUpEvent;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.events.player.PlayerXpGainEvent;
import org.bukkit.ChatColor;
import ws.billy.bedwars.configuration.LevelsConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class PlayerLevel
{
    private UUID uuid;
    private int level;
    private int nextLevelCost;
    private String levelName;
    private int currentXp;
    private String progressBar;
    private String requiredXp;
    private String formattedCurrentXp;
    private boolean modified;
    private static ConcurrentHashMap<UUID, PlayerLevel> levelByPlayer;
    
    public PlayerLevel(final UUID key, int n, int currentXp) {
        this.modified = false;
        this.uuid = key;
        this.setLevelName(n);
        this.setNextLevelCost(n, true);
        if (n < 1) {
            n = 1;
        }
        if (currentXp < 0) {
            currentXp = 0;
        }
        this.level = n;
        this.currentXp = currentXp;
        this.updateProgressBar();
        if (!PlayerLevel.levelByPlayer.containsKey(key)) {
            PlayerLevel.levelByPlayer.put(key, this);
        }
    }
    
    public void setLevelName(final int i) {
        this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(i)).replace("{number}", String.valueOf(i));
    }
    
    public void setNextLevelCost(final int n, final boolean b) {
        if (!b) {
            this.modified = true;
        }
        this.nextLevelCost = LevelsConfig.getNextCost(n);
    }
    
    public void lazyLoad(int n, int currentXp) {
        this.modified = false;
        if (n < 1) {
            n = 1;
        }
        if (currentXp < 0) {
            currentXp = 0;
        }
        this.setLevelName(n);
        this.setNextLevelCost(n, true);
        this.level = n;
        this.currentXp = currentXp;
        this.updateProgressBar();
        this.modified = false;
    }
    
    private void updateProgressBar() {
        int n = (int)((this.nextLevelCost - this.currentXp) / (double)this.nextLevelCost * 10.0);
        int n2 = 10 - n;
        if (n < 0 || n2 < 0) {
            n = 10;
            n2 = 0;
        }
        this.progressBar = ChatColor.translateAlternateColorCodes('&', LevelsConfig.levels.getString("progress-bar.format").replace("{progress}", LevelsConfig.levels.getString("progress-bar.unlocked-color") + String.valueOf(new char[n2]).replace("\u0000", LevelsConfig.levels.getString("progress-bar.symbol")) + LevelsConfig.levels.getString("progress-bar.locked-color") + String.valueOf(new char[n]).replace("\u0000", LevelsConfig.levels.getString("progress-bar.symbol"))));
        this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
        this.formattedCurrentXp = ((this.currentXp >= 1000) ? ((this.currentXp % 1000 == 0) ? (this.currentXp / 1000 + "k") : (this.currentXp / 1000.0 + "k")) : String.valueOf(this.currentXp));
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getNextLevelCost() {
        return this.nextLevelCost;
    }
    
    public static PlayerLevel getLevelByPlayer(final UUID key) {
        return PlayerLevel.levelByPlayer.getOrDefault(key, new PlayerLevel(key, 1, 0));
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public String getLevelName() {
        return this.levelName;
    }
    
    public int getCurrentXp() {
        return this.currentXp;
    }
    
    public String getProgress() {
        return this.progressBar;
    }
    
    public String getFormattedRequiredXp() {
        return this.requiredXp;
    }
    
    public void addXp(final int n, final PlayerXpGainEvent.XpSource xpSource) {
        if (n < 0) {
            return;
        }
        this.currentXp += n;
        this.upgradeLevel();
        this.updateProgressBar();
        Bukkit.getPluginManager().callEvent((Event)new PlayerXpGainEvent(Bukkit.getPlayer(this.uuid), n, xpSource));
        this.modified = true;
    }
    
    public void setXp(int currentXp) {
        if (currentXp <= 0) {
            currentXp = 0;
        }
        this.currentXp = currentXp;
        this.upgradeLevel();
        this.updateProgressBar();
        this.modified = true;
    }
    
    public void setLevel(final int n) {
        this.level = n;
        this.nextLevelCost = LevelsConfig.getNextCost(n);
        this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(n)).replace("{number}", String.valueOf(n));
        this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
        this.updateProgressBar();
        this.modified = true;
    }
    
    public String getFormattedCurrentXp() {
        return this.formattedCurrentXp;
    }
    
    public void upgradeLevel() {
        if (this.currentXp >= this.nextLevelCost) {
            ++this.level;
            this.nextLevelCost = LevelsConfig.getNextCost(this.level);
            this.currentXp -= this.nextLevelCost;
            this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(this.level)).replace("{number}", String.valueOf(this.level));
            this.requiredXp = ((this.nextLevelCost >= 1000) ? ((this.nextLevelCost % 1000 == 0) ? (this.nextLevelCost / 1000 + "k") : (this.nextLevelCost / 1000.0 + "k")) : String.valueOf(this.nextLevelCost));
            this.formattedCurrentXp = ((this.currentXp >= 1000) ? ((this.currentXp % 1000 == 0) ? (this.currentXp / 1000 + "k") : (this.currentXp / 1000.0 + "k")) : String.valueOf(this.currentXp));
            Bukkit.getPluginManager().callEvent((Event)new PlayerLevelUpEvent(Bukkit.getPlayer(this.getUuid()), this.level, this.nextLevelCost));
            this.modified = true;
        }
    }
    
    public int getPlayerLevel() {
        return this.level;
    }
    
    public void destroy() {
        PlayerLevel.levelByPlayer.remove(this.uuid);
        this.updateDatabase();
    }
    
    public void updateDatabase() {
        if (this.modified) {
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)BeaconBattle.plugin, () -> BeaconBattle.getRemoteDatabase().setLevelData(this.uuid, this.level, this.currentXp, LevelsConfig.getLevelName(this.level), this.nextLevelCost));
            this.modified = false;
        }
    }
    
    static {
        PlayerLevel.levelByPlayer = new ConcurrentHashMap<UUID, PlayerLevel>();
    }
}
