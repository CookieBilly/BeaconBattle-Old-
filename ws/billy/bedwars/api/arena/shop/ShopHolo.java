

package ws.billy.bedwars.api.arena.shop;

import java.util.ArrayList;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import ws.billy.bedwars.api.BeaconBattle;

import java.util.List;

public class ShopHolo
{
    private static List<ShopHolo> shopHolo;
    private static BeaconBattle api;
    private String iso;
    private ArmorStand a1;
    private ArmorStand a2;
    private Location l;
    private IArena a;
    
    public ShopHolo(final String s, final ArmorStand a1, final ArmorStand a2, final Location l, final IArena a3) {
        this.l = l;
        for (final ShopHolo shopHolo : getShopHolo()) {
            if (shopHolo.l == l && shopHolo.iso.equalsIgnoreCase(s)) {
                if (a1 != null) {
                    a1.remove();
                }
                if (a2 != null) {
                    a2.remove();
                }
                return;
            }
        }
        this.a1 = a1;
        this.a2 = a2;
        this.iso = s;
        this.a = a3;
        if (a1 != null) {
            a1.setMarker(true);
        }
        if (a2 != null) {
            a2.setMarker(true);
        }
        ShopHolo.shopHolo.add(this);
        if (ShopHolo.api == null) {
            ShopHolo.api = (BeaconBattle)Bukkit.getServer().getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
        }
    }
    
    public void update() {
        if (this.l == null) {
            Bukkit.broadcastMessage("LOCATION IS NULL");
        }
        for (final Player player : this.l.getWorld().getPlayers()) {
            if (Language.getPlayerLanguage(player).getIso().equalsIgnoreCase(this.iso)) {
                continue;
            }
            if (this.a1 != null) {
                ShopHolo.api.getVersionSupport().hideEntity((Entity)this.a1, player);
            }
            if (this.a2 == null) {
                continue;
            }
            ShopHolo.api.getVersionSupport().hideEntity((Entity)this.a2, player);
        }
    }
    
    public void updateForPlayer(final Player player, final String s) {
        if (s.equalsIgnoreCase(this.iso)) {
            return;
        }
        if (this.a1 != null) {
            ShopHolo.api.getVersionSupport().hideEntity((Entity)this.a1, player);
        }
        if (this.a2 != null) {
            ShopHolo.api.getVersionSupport().hideEntity((Entity)this.a2, player);
        }
    }
    
    public static void clearForArena(final IArena arena) {
        for (final ShopHolo shopHolo : new ArrayList<ShopHolo>(getShopHolo())) {
            if (shopHolo.a == arena) {
                ShopHolo.shopHolo.remove(shopHolo);
            }
        }
    }
    
    public IArena getA() {
        return this.a;
    }
    
    public String getIso() {
        return this.iso;
    }
    
    public static List<ShopHolo> getShopHolo() {
        return ShopHolo.shopHolo;
    }
    
    static {
        ShopHolo.shopHolo = new ArrayList<ShopHolo>();
        ShopHolo.api = null;
    }
}
