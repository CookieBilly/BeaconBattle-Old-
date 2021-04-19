

package ws.billy.bedwars.support.vipfeatures;

import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.vipfeatures.api.event.BlockChangeEvent;
import ws.billy.bedwars.api.events.player.PlayerJoinArenaEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.player.PlayerJoinEvent;
import ws.billy.vipfeatures.api.IVipFeatures;
import org.bukkit.event.Listener;

public class VipListeners implements Listener
{
    private IVipFeatures api;
    
    public VipListeners(final IVipFeatures api) {
        this.api = api;
    }
    
    @EventHandler
    public void onServerJoin(final PlayerJoinEvent playerJoinEvent) {
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA) {
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.api.givePlayerItemStack(playerJoinEvent.getPlayer()), 10L);
        }
    }
    
    @EventHandler
    public void onArenaJoin(final PlayerJoinArenaEvent playerJoinArenaEvent) {
        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> this.api.givePlayerItemStack(playerJoinArenaEvent.getPlayer()), 10L);
    }
    
    @EventHandler
    public void onBockChange(final BlockChangeEvent blockChangeEvent) {
        if (BeaconBattle.getAPI().getArenaUtil().getArenaByName(blockChangeEvent.getLocation().getWorld().getName()) != null) {
            for (final ITeam team : BeaconBattle.getAPI().getArenaUtil().getArenaByName(blockChangeEvent.getLocation().getWorld().getName()).getTeams()) {
                for (int i = -1; i < 2; ++i) {
                    int j = -1;
                    while (j < 2) {
                        if (blockChangeEvent.getLocation().getBlockX() == team.getBed().getBlockX() && blockChangeEvent.getLocation().getBlockY() == team.getBed().getBlockY() && blockChangeEvent.getLocation().getBlockZ() == team.getBed().getBlockZ()) {
                            if (BeaconBattle.nms.isBed(team.getBed().clone().add((double)i, 0.0, (double)j).getBlock().getType())) {
                                blockChangeEvent.setCancelled(true);
                                break;
                            }
                            break;
                        }
                        else {
                            ++j;
                        }
                    }
                }
            }
        }
    }
}
