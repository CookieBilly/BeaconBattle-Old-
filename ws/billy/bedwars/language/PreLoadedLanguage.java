

package ws.billy.bedwars.language;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PreLoadedLanguage
{
    private static ConcurrentHashMap<UUID, PreLoadedLanguage> preLoadedLanguageConcurrentHashMap;
    private String iso;
    private long timeout;
    
    public PreLoadedLanguage(final UUID uuid, final String iso) {
        PreLoadedLanguage.preLoadedLanguageConcurrentHashMap.remove(uuid);
        this.iso = iso;
        this.timeout = System.currentTimeMillis() + 3000L;
        PreLoadedLanguage.preLoadedLanguageConcurrentHashMap.put(uuid, this);
    }
    
    public long getTimeout() {
        return this.timeout;
    }
    
    public String getIso() {
        return this.iso;
    }
    
    public static ConcurrentHashMap<UUID, PreLoadedLanguage> getPreLoadedLanguage() {
        return PreLoadedLanguage.preLoadedLanguageConcurrentHashMap;
    }
    
    public static PreLoadedLanguage getByUUID(final UUID key) {
        return PreLoadedLanguage.preLoadedLanguageConcurrentHashMap.getOrDefault(key, null);
    }
    
    public static void clear(final UUID key) {
        PreLoadedLanguage.preLoadedLanguageConcurrentHashMap.remove(key);
    }
    
    static {
        PreLoadedLanguage.preLoadedLanguageConcurrentHashMap = new ConcurrentHashMap<UUID, PreLoadedLanguage>();
    }
}
