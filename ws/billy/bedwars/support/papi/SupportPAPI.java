

package ws.billy.bedwars.support.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class SupportPAPI
{
    private static supp supportPAPI;
    
    public static supp getSupportPAPI() {
        return SupportPAPI.supportPAPI;
    }
    
    public static void setSupportPAPI(final supp supportPAPI) {
        SupportPAPI.supportPAPI = supportPAPI;
    }
    
    static {
        SupportPAPI.supportPAPI = new noPAPI();
    }
    
    public static class noPAPI implements supp
    {
        @Override
        public String replace(final Player player, final String s) {
            return s;
        }
    }
    
    public static class withPAPI implements supp
    {
        @Override
        public String replace(final Player player, final String s) {
            return PlaceholderAPI.setPlaceholders(player, s);
        }
    }
    
    public interface supp
    {
        String replace(final Player p0, final String p1);
    }
}
