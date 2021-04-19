

package ws.billy.bedwars.arena.tasks;

import java.util.Iterator;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.arena.Misc;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.api.arena.shop.ShopHolo;
import ws.billy.bedwars.api.server.ServerType;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import ws.billy.bedwars.configuration.Sounds;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import org.jetbrains.annotations.NotNull;
import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.tasks.RestartingTask;

public class GameRestartingTask implements Runnable, RestartingTask
{
    private Arena arena;
    private int restarting;
    private BukkitTask task;
    
    public GameRestartingTask(@NotNull final Arena arena) {
        this.restarting = BeaconBattle.config.getInt("countdowns.game-restart") + 3;
        this.arena = arena;
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, (Runnable)this, 0L, 20L);
        Sounds.playSound("game-end", arena.getPlayers());
        Sounds.playSound("game-end", arena.getSpectators());
    }
    
    @Override
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public int getRestarting() {
        return this.restarting;
    }
    
    @Override
    public Arena getArena() {
        return this.arena;
    }
    
    @Override
    public BukkitTask getBukkitTask() {
        return this.task;
    }
    
    @Override
    public void run() {
        --this.restarting;
        if (this.getArena().getPlayers().isEmpty() && this.restarting > 9) {
            this.restarting = 9;
        }
        if (this.restarting == 3) {
            final Iterator<Player> iterator = new ArrayList<Player>(this.getArena().getPlayers()).iterator();
            while (iterator.hasNext()) {
                this.getArena().removePlayer(iterator.next(), BeaconBattle.getServerType() == ServerType.BUNGEE);
            }
            final Iterator<Player> iterator2 = new ArrayList<Player>(this.getArena().getSpectators()).iterator();
            while (iterator2.hasNext()) {
                this.getArena().removeSpectator(iterator2.next(), BeaconBattle.getServerType() == ServerType.BUNGEE);
            }
        }
        else if (this.restarting == 1) {
            ShopHolo.clearForArena(this.getArena());
            for (final Entity entity : this.getArena().getWorld().getEntities()) {
                if (entity.getType() == EntityType.PLAYER) {
                    final Player player = (Player)entity;
                    Misc.moveToLobbyOrKick(player);
                    if (this.getArena().isSpectator(player)) {
                        this.getArena().removeSpectator(player, false);
                    }
                    if (!this.getArena().isPlayer(player)) {
                        continue;
                    }
                    this.getArena().removePlayer(player, false);
                }
            }
            final Iterator<IGenerator> iterator4 = this.getArena().getOreGenerators().iterator();
            while (iterator4.hasNext()) {
                iterator4.next().disable();
            }
            final Iterator<ITeam> iterator5 = this.getArena().getTeams().iterator();
            while (iterator5.hasNext()) {
                final Iterator<IGenerator> iterator6 = iterator5.next().getGenerators().iterator();
                while (iterator6.hasNext()) {
                    iterator6.next().disable();
                }
            }
        }
        else if (this.restarting == 0) {
            this.getArena().restart();
            this.task.cancel();
            this.arena = null;
        }
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
