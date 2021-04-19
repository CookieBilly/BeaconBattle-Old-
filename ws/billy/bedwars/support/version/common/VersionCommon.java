

package ws.billy.bedwars.support.version.common;

import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.listeners.Interact_1_13Plus;
import ws.billy.bedwars.shop.defaultrestore.ShopItemRestoreListener;
import ws.billy.bedwars.listeners.ItemDropPickListener;
import ws.billy.bedwars.listeners.SwapItem;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.server.VersionSupport;
import ws.billy.bedwars.api.BeaconBattle;

public class VersionCommon
{
    public static BeaconBattle api;
    
    public VersionCommon(final VersionSupport versionSupport) {
        VersionCommon.api = (BeaconBattle)Bukkit.getServicesManager().getRegistration((Class)BeaconBattle.class).getProvider();
        if (versionSupport.getVersion() > 1) {
            this.registerListeners(versionSupport.getPlugin(), (Listener)new SwapItem(), (Listener)new ItemDropPickListener.ArrowCollect());
        }
        if (versionSupport.getVersion() < 5) {
            this.registerListeners(versionSupport.getPlugin(), (Listener)new ItemDropPickListener.PlayerPickup(), (Listener)new ShopItemRestoreListener.PlayerPickup());
        }
        if (versionSupport.getVersion() > 5) {
            this.registerListeners(versionSupport.getPlugin(), (Listener)new ShopItemRestoreListener.EntityDrop(), (Listener)new Interact_1_13Plus(), (Listener)new ItemDropPickListener.EntityDrop());
        }
        if (versionSupport.getVersion() > 4) {
            this.registerListeners(versionSupport.getPlugin(), (Listener)new ItemDropPickListener.EntityPickup(), (Listener)new ShopItemRestoreListener.EntityPickup());
        }
        this.registerListeners(versionSupport.getPlugin(), (Listener)new ItemDropPickListener.PlayerDrop(), (Listener)new ShopItemRestoreListener.PlayerDrop());
        this.registerListeners(versionSupport.getPlugin(), (Listener)new ItemDropPickListener.GeneratorCollect(), (Listener)new ShopItemRestoreListener.DefaultRestoreInvClose());
    }
    
    private void registerListeners(final Plugin plugin, final Listener... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            plugin.getServer().getPluginManager().registerEvents(array[i], plugin);
        }
    }
}
