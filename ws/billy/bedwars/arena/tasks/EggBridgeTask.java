

package ws.billy.bedwars.arena.tasks;

import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.gameplay.EggBridgeBuildEvent;
import org.bukkit.Material;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.listeners.EggBridge;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.arena.team.TeamColor;
import org.bukkit.entity.Egg;

public class EggBridgeTask implements Runnable
{
    private Egg projectile;
    private TeamColor teamColor;
    private Player player;
    private IArena arena;
    private BukkitTask task;
    
    public EggBridgeTask(final Player player, final Egg projectile, final TeamColor teamColor) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
        if (arenaByPlayer == null) {
            return;
        }
        this.arena = arenaByPlayer;
        this.projectile = projectile;
        this.teamColor = teamColor;
        this.player = player;
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, (Runnable)this, 0L, 1L);
    }
    
    public TeamColor getTeamColor() {
        return this.teamColor;
    }
    
    public Egg getProjectile() {
        return this.projectile;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    @Override
    public void run() {
        final Location location = this.getProjectile().getLocation();
        if (this.getProjectile().isDead() || !this.arena.isPlayer(this.getPlayer()) || this.getPlayer().getLocation().distance(this.getProjectile().getLocation()) > 27.0 || this.getPlayer().getLocation().getY() - this.getProjectile().getLocation().getY() > 9.0) {
            EggBridge.removeEgg(this.projectile);
            return;
        }
        if (this.getPlayer().getLocation().distance(location) > 4.0) {
            final Block block = location.clone().subtract(0.0, 2.0, 0.0).getBlock();
            if (!Misc.isBuildProtected(block.getLocation(), this.getArena()) && block.getType() == Material.AIR) {
                block.setType(BeaconBattle.nms.woolMaterial());
                BeaconBattle.nms.setBlockTeamColor(block, this.getTeamColor());
                this.getArena().addPlacedBlock(block);
                Bukkit.getPluginManager().callEvent((Event)new EggBridgeBuildEvent(this.getTeamColor(), this.getArena(), block));
                location.getWorld().playEffect(block.getLocation(), BeaconBattle.nms.eggBridge(), 3);
            }
            final Block block2 = location.clone().subtract(1.0, 2.0, 0.0).getBlock();
            if (!Misc.isBuildProtected(block2.getLocation(), this.getArena()) && block2.getType() == Material.AIR) {
                block2.setType(BeaconBattle.nms.woolMaterial());
                BeaconBattle.nms.setBlockTeamColor(block2, this.getTeamColor());
                this.getArena().addPlacedBlock(block2);
                Bukkit.getPluginManager().callEvent((Event)new EggBridgeBuildEvent(this.getTeamColor(), this.getArena(), block2));
                location.getWorld().playEffect(block2.getLocation(), BeaconBattle.nms.eggBridge(), 3);
            }
            final Block block3 = location.clone().subtract(0.0, 2.0, 1.0).getBlock();
            if (!Misc.isBuildProtected(block3.getLocation(), this.getArena()) && block3.getType() == Material.AIR) {
                block3.setType(BeaconBattle.nms.woolMaterial());
                BeaconBattle.nms.setBlockTeamColor(block3, this.getTeamColor());
                this.getArena().addPlacedBlock(block3);
                Bukkit.getPluginManager().callEvent((Event)new EggBridgeBuildEvent(this.getTeamColor(), this.getArena(), block3));
                location.getWorld().playEffect(block3.getLocation(), BeaconBattle.nms.eggBridge(), 3);
            }
        }
    }
    
    public void cancel() {
        this.task.cancel();
    }
}
