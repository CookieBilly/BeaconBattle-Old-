

package ws.billy.bedwars.arena.tasks;

import java.util.Iterator;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.arena.NextEvent;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.api.arena.generator.GeneratorType;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.gameplay.TeamAssignEvent;
import ws.billy.bedwars.api.arena.team.ITeam;

import java.util.Collections;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.tasks.StartingTask;

public class GameStartingTask implements Runnable, StartingTask
{
    private int countdown;
    private final IArena arena;
    private final BukkitTask task;
    
    public GameStartingTask(final Arena arena) {
        this.arena = arena;
        this.countdown = BeaconBattle.config.getInt("countdowns.game-start-regular");
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, (Runnable)this, 0L, 20L);
    }
    
    @Override
    public int getCountdown() {
        return this.countdown;
    }
    
    @Override
    public void setCountdown(final int countdown) {
        this.countdown = countdown;
    }
    
    @Override
    public IArena getArena() {
        return this.arena;
    }
    
    @Override
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public BukkitTask getBukkitTask() {
        return this.task;
    }
    
    @Override
    public void run() {
        if (this.countdown == 0) {
            final ArrayList<Player> list = new ArrayList<Player>();
            final ArrayList<Player> list2 = new ArrayList<Player>();
            for (final Player player : this.getArena().getPlayers()) {
                if (BeaconBattle.getParty().hasParty(player) && BeaconBattle.getParty().isOwner(player)) {
                    list2.add(player);
                }
            }
            Collections.shuffle(this.getArena().getTeams());
            for (final Player player2 : this.getArena().getPlayers()) {
                if (list2.contains(player2)) {
                    for (final ITeam team : this.getArena().getTeams()) {
                        if (list.contains(player2)) {
                            continue;
                        }
                        if (team.getSize() + BeaconBattle.getParty().partySize(player2) > this.getArena().getMaxInTeam()) {
                            continue;
                        }
                        list.add(player2);
                        player2.closeInventory();
                        final TeamAssignEvent teamAssignEvent = new TeamAssignEvent(player2, team, this.getArena());
                        Bukkit.getPluginManager().callEvent((Event)teamAssignEvent);
                        if (!teamAssignEvent.isCancelled()) {
                            team.addPlayers(player2);
                        }
                        for (final Player player3 : BeaconBattle.getParty().getMembers(player2)) {
                            if (player3 != player2) {
                                final IArena arenaByPlayer = Arena.getArenaByPlayer(player3);
                                if (arenaByPlayer == null) {
                                    continue;
                                }
                                if (!arenaByPlayer.equals(this.getArena())) {
                                    continue;
                                }
                                Bukkit.getPluginManager().callEvent((Event)new TeamAssignEvent(player2, team, this.getArena()));
                                if (!teamAssignEvent.isCancelled()) {
                                    team.addPlayers(player3);
                                }
                                list.add(player3);
                                player3.closeInventory();
                            }
                        }
                    }
                }
            }
            for (final Player player4 : this.getArena().getPlayers()) {
                if (list.contains(player4)) {
                    continue;
                }
                ITeam team2 = this.getArena().getTeams().get(0);
                for (final ITeam team3 : this.getArena().getTeams()) {
                    if (team3.getMembers().size() < this.getArena().getMaxInTeam() && team3.getMembers().size() < team2.getMembers().size()) {
                        team2 = team3;
                    }
                }
                final TeamAssignEvent teamAssignEvent2 = new TeamAssignEvent(player4, team2, this.getArena());
                Bukkit.getPluginManager().callEvent((Event)teamAssignEvent2);
                if (!teamAssignEvent2.isCancelled()) {
                    team2.addPlayers(player4);
                }
                player4.closeInventory();
            }
            for (final ITeam team4 : this.getArena().getTeams()) {
                BeaconBattle.nms.colorBed(team4);
                if (team4.getMembers().isEmpty()) {
                    team4.setBedDestroyed(true);
                    if (!this.getArena().getConfig().getBoolean("disable-generator-for-empty-teams")) {
                        continue;
                    }
                    final Iterator<IGenerator> iterator8 = team4.getGenerators().iterator();
                    while (iterator8.hasNext()) {
                        iterator8.next().disable();
                    }
                }
            }
            final Iterator<IGenerator> iterator9;
            IGenerator generator;
            Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                this.getArena().getOreGenerators().iterator();
                while (iterator9.hasNext()) {
                    generator = iterator9.next();
                    if (generator.getType() == GeneratorType.EMERALD || generator.getType() == GeneratorType.DIAMOND) {
                        generator.enableRotation();
                    }
                }
                return;
            }, 60L);
            BeaconBattle.getAPI().getRestoreAdapter().onLobbyRemoval(this.arena);
            this.spawnPlayers();
            this.task.cancel();
            this.getArena().changeStatus(GameState.playing);
            if (this.getArena().getUpgradeDiamondsCount() < this.getArena().getUpgradeEmeraldsCount()) {
                this.getArena().setNextEvent(NextEvent.DIAMOND_GENERATOR_TIER_II);
            }
            else {
                this.getArena().setNextEvent(NextEvent.EMERALD_GENERATOR_TIER_II);
            }
            final Iterator<ITeam> iterator10 = this.getArena().getTeams().iterator();
            while (iterator10.hasNext()) {
                iterator10.next().spawnNPCs();
            }
            return;
        }
        if (this.getCountdown() % 10 == 0 || this.getCountdown() <= 5) {
            if (this.getCountdown() < 5) {
                Sounds.playSound("game-countdown-s" + this.getCountdown(), this.getArena().getPlayers());
            }
            else {
                Sounds.playSound("game-countdown-others", this.getArena().getPlayers());
            }
            if (this.getCountdown() >= 10) {
                for (final Player player5 : this.getArena().getPlayers()) {
                    BeaconBattle.nms.sendTitle(player5, "§a" + this.getCountdown(), null, 0, 20, 0);
                    player5.sendMessage(Language.getMsg(player5, Messages.ARENA_STATUS_START_COUNTDOWN).replace("{time}", String.valueOf(this.getCountdown())));
                }
            }
            else if (this.getCountdown() > 3) {
                for (final Player player6 : this.getArena().getPlayers()) {
                    BeaconBattle.nms.sendTitle(player6, "§e" + this.getCountdown(), null, 0, 20, 0);
                    player6.sendMessage(Language.getMsg(player6, Messages.ARENA_STATUS_START_COUNTDOWN).replace("{time}", "§c" + this.getCountdown()));
                }
            }
            else {
                for (final Player player7 : this.getArena().getPlayers()) {
                    BeaconBattle.nms.sendTitle(player7, "§c" + this.getCountdown(), null, 0, 20, 0);
                    player7.sendMessage(Language.getMsg(player7, Messages.ARENA_STATUS_START_COUNTDOWN).replace("{time}", "§c" + this.getCountdown()));
                }
            }
        }
        --this.countdown;
    }
    
    private void spawnPlayers() {
        for (final ITeam team : this.getArena().getTeams()) {
            for (final Player player : new ArrayList<Player>(team.getMembers())) {
                team.firstSpawn(player);
                Sounds.playSound("game-countdown-start", player);
                BeaconBattle.nms.sendTitle(player, Language.getMsg(player, Messages.ARENA_STATUS_START_PLAYER_TITLE), null, 0, 20, 0);
                final Iterator<String> iterator3 = Language.getList(player, Messages.ARENA_STATUS_START_PLAYER_TUTORIAL).iterator();
                while (iterator3.hasNext()) {
                    player.sendMessage((String)iterator3.next());
                }
            }
        }
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
