

package ws.billy.bedwars.lobbysocket;

import org.bukkit.Bukkit;
import java.util.concurrent.ConcurrentHashMap;
import ws.billy.bedwars.api.language.Language;
import java.util.UUID;

public class LoadedUser
{
    private UUID uuid;
    private String partyOwnerOrSpectateTarget;
    private long requestTime;
    private String arenaIdentifier;
    private Language language;
    private static ConcurrentHashMap<UUID, LoadedUser> loaded;
    
    public LoadedUser(final String name, final String arenaIdentifier, final String s, final String partyOwnerOrSpectateTarget) {
        this.partyOwnerOrSpectateTarget = null;
        this.language = null;
        if (Bukkit.getWorld(arenaIdentifier) == null) {
            return;
        }
        this.arenaIdentifier = arenaIdentifier;
        this.uuid = UUID.fromString(name);
        if (partyOwnerOrSpectateTarget != null && !partyOwnerOrSpectateTarget.isEmpty()) {
            this.partyOwnerOrSpectateTarget = partyOwnerOrSpectateTarget;
        }
        this.requestTime = System.currentTimeMillis();
        final Language lang = Language.getLang(s);
        if (lang != null) {
            this.language = lang;
        }
        LoadedUser.loaded.put(this.uuid, this);
    }
    
    public static boolean isPreLoaded(final UUID key) {
        return LoadedUser.loaded.containsKey(key);
    }
    
    public long getRequestTime() {
        return this.requestTime;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public String getArenaIdentifier() {
        return this.arenaIdentifier;
    }
    
    public void destroy() {
        LoadedUser.loaded.remove(this.uuid);
    }
    
    public Language getLanguage() {
        return this.language;
    }
    
    public static LoadedUser getPreLoaded(final UUID key) {
        return LoadedUser.loaded.getOrDefault(key, null);
    }
    
    public String getPartyOwnerOrSpectateTarget() {
        return this.partyOwnerOrSpectateTarget;
    }
    
    public static ConcurrentHashMap<UUID, LoadedUser> getLoaded() {
        return LoadedUser.loaded;
    }
    
    static {
        LoadedUser.loaded = new ConcurrentHashMap<UUID, LoadedUser>();
    }
}
