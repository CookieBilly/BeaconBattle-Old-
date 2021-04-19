

package ws.billy.bedwars.arena.tasks;

import org.bukkit.Location;
import java.util.Iterator;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import java.util.Map;
import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.Material;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.scheduler.BukkitTask;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.tasks.PlayingTask;

public class GamePlayingTask implements Runnable, PlayingTask
{
    private Arena arena;
    private BukkitTask task;
    private int beds_destroy_countdown;
    private int dragon_spawn_countdown;
    private int game_end_countdown;
    
    public GamePlayingTask(final Arena arena) {
        this.arena = arena;
        this.beds_destroy_countdown = BeaconBattle.config.getInt("countdowns.next-event-beds-destroy");
        this.dragon_spawn_countdown = BeaconBattle.config.getInt("countdowns.next-event-dragon-spawn");
        this.game_end_countdown = BeaconBattle.config.getInt("countdowns.next-event-game-end");
        this.task = Bukkit.getScheduler().runTaskTimer((Plugin)BeaconBattle.plugin, (Runnable)this, 0L, 20L);
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
    public int getTask() {
        return this.task.getTaskId();
    }
    
    @Override
    public int getBedsDestroyCountdown() {
        return this.beds_destroy_countdown;
    }
    
    @Override
    public int getDragonSpawnCountdown() {
        return this.dragon_spawn_countdown;
    }
    
    @Override
    public int getGameEndCountdown() {
        return this.game_end_countdown;
    }
    
    @Override
    public void run() {
        switch (this.getArena().getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III:
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III: {
                if (this.getArena().upgradeDiamondsCount > 0) {
                    final Arena arena = this.getArena();
                    --arena.upgradeDiamondsCount;
                    if (this.getArena().upgradeDiamondsCount == 0) {
                        this.getArena().updateNextEvent();
                    }
                }
                if (this.getArena().upgradeEmeraldsCount <= 0) {
                    break;
                }
                final Arena arena2 = this.getArena();
                --arena2.upgradeEmeraldsCount;
                if (this.getArena().upgradeEmeraldsCount == 0) {
                    this.getArena().updateNextEvent();
                    break;
                }
                break;
            }
            case BEDS_DESTROY: {
                --this.beds_destroy_countdown;
                if (this.getBedsDestroyCountdown() == 0) {
                    for (final Player player : this.getArena().getPlayers()) {
                        BeaconBattle.nms.sendTitle(player, Language.getMsg(player, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), Language.getMsg(player, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 30, 0);
                        player.sendMessage(Language.getMsg(player, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    for (final Player player2 : this.getArena().getSpectators()) {
                        BeaconBattle.nms.sendTitle(player2, Language.getMsg(player2, Messages.NEXT_EVENT_TITLE_ANNOUNCE_BEDS_DESTROYED), Language.getMsg(player2, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_BEDS_DESTROYED), 0, 30, 0);
                        player2.sendMessage(Language.getMsg(player2, Messages.NEXT_EVENT_CHAT_ANNOUNCE_BEDS_DESTROYED));
                    }
                    final Iterator<ITeam> iterator3 = this.getArena().getTeams().iterator();
                    while (iterator3.hasNext()) {
                        iterator3.next().setBedDestroyed(true);
                    }
                    this.getArena().updateNextEvent();
                    break;
                }
                break;
            }
            case ENDER_DRAGON: {
                --this.dragon_spawn_countdown;
                if (this.getDragonSpawnCountdown() == 0) {
                    for (final Player player3 : this.getArena().getPlayers()) {
                        BeaconBattle.nms.sendTitle(player3, Language.getMsg(player3, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), Language.getMsg(player3, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 30, 0);
                        for (final ITeam team : this.getArena().getTeams()) {
                            if (team.getMembers().isEmpty()) {
                                continue;
                            }
                            player3.sendMessage(Language.getMsg(player3, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(team.getDragons())).replace("{TeamColor}", team.getColor().chat().toString()).replace("{TeamName}", team.getDisplayName(Language.getPlayerLanguage(player3))));
                        }
                    }
                    for (final Player player4 : this.getArena().getSpectators()) {
                        BeaconBattle.nms.sendTitle(player4, Language.getMsg(player4, Messages.NEXT_EVENT_TITLE_ANNOUNCE_SUDDEN_DEATH), Language.getMsg(player4, Messages.NEXT_EVENT_SUBTITLE_ANNOUNCE_SUDDEN_DEATH), 0, 30, 0);
                        for (final ITeam team2 : this.getArena().getTeams()) {
                            if (team2.getMembers().isEmpty()) {
                                continue;
                            }
                            player4.sendMessage(Language.getMsg(player4, Messages.NEXT_EVENT_CHAT_ANNOUNCE_SUDDEN_DEATH).replace("{TeamDragons}", String.valueOf(team2.getDragons())).replace("{TeamColor}", team2.getColor().chat().toString()).replace("{TeamName}", team2.getDisplayName(Language.getPlayerLanguage(player4))));
                        }
                    }
                    this.getArena().updateNextEvent();
                    final Iterator<IGenerator> iterator8 = this.arena.getOreGenerators().iterator();
                    while (iterator8.hasNext()) {
                        final Location location = iterator8.next().getLocation();
                        for (int i = 0; i < 20; ++i) {
                            location.clone().subtract(0.0, (double)i, 0.0).getBlock().setType(Material.AIR);
                        }
                    }
                    final Iterator<ITeam> iterator9 = this.arena.getTeams().iterator();
                    while (iterator9.hasNext()) {
                        final Iterator<IGenerator> iterator10 = iterator9.next().getGenerators().iterator();
                        while (iterator10.hasNext()) {
                            final Location location2 = iterator10.next().getLocation();
                            for (int j = 0; j < 20; ++j) {
                                location2.clone().subtract(0.0, (double)j, 0.0).getBlock().setType(Material.AIR);
                            }
                        }
                    }
                    for (final ITeam team3 : this.getArena().getTeams()) {
                        if (team3.getMembers().isEmpty()) {
                            continue;
                        }
                        for (int k = 0; k < team3.getDragons(); ++k) {
                            BeaconBattle.nms.spawnDragon(this.getArena().getConfig().getArenaLoc("waiting.Loc").add(0.0, 10.0, 0.0), team3);
                        }
                    }
                    break;
                }
                break;
            }
            case GAME_END: {
                --this.game_end_countdown;
                if (this.getGameEndCountdown() == 0) {
                    this.getArena().checkWinner();
                    this.getArena().changeStatus(GameState.restarting);
                    break;
                }
                break;
            }
        }
        int l = 0;
        for (final ITeam team4 : this.getArena().getTeams()) {
            if (team4.getSize() > 1) {
                for (final Player player5 : team4.getMembers()) {
                    for (final Player player6 : team4.getMembers()) {
                        if (player6 == player5) {
                            continue;
                        }
                        if (l == 0) {
                            l = (int)player5.getLocation().distance(player6.getLocation());
                        }
                        else {
                            if ((int)player5.getLocation().distance(player6.getLocation()) >= l) {
                                continue;
                            }
                            l = (int)player5.getLocation().distance(player6.getLocation());
                        }
                    }
                    BeaconBattle.nms.playAction(player5, Language.getMsg(player5, Messages.FORMATTING_ACTION_BAR_TRACKING).replace("{team}", team4.getColor().chat() + team4.getDisplayName(Language.getPlayerLanguage(player5))).replace("{distance}", team4.getColor().chat().toString() + l).replace("&", "§"));
                }
            }
            final Iterator<IGenerator> iterator15 = team4.getGenerators().iterator();
            while (iterator15.hasNext()) {
                iterator15.next().spawn();
            }
        }
        int intValue = 0;
        for (final Player player7 : this.getArena().getPlayers()) {
            if (Arena.afkCheck.get(player7.getUniqueId()) == null) {
                Arena.afkCheck.put(player7.getUniqueId(), intValue);
            }
            else {
                intValue = Arena.afkCheck.get(player7.getUniqueId());
                ++intValue;
                Arena.afkCheck.replace(player7.getUniqueId(), intValue);
                if (intValue != 45) {
                    continue;
                }
                BeaconBattle.getAPI().getAFKUtil().setPlayerAFK(player7, true);
            }
        }
        if (!this.getArena().getRespawn().isEmpty()) {
            for (final Map.Entry<Player, Integer> entry : this.getArena().getRespawn().entrySet()) {
                if (entry.getValue() == 0) {
                    final IArena arenaByPlayer = Arena.getArenaByPlayer(entry.getKey());
                    if (arenaByPlayer == null) {
                        this.getArena().getRespawn().remove(entry.getKey());
                    }
                    else {
                        arenaByPlayer.getTeam(entry.getKey()).respawnMember(entry.getKey());
                    }
                }
                else {
                    BeaconBattle.nms.sendTitle(entry.getKey(), Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_TITLE).replace("{time}", String.valueOf(entry.getValue())), Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_SUBTITLE).replace("{time}", String.valueOf(entry.getValue())), 0, 30, 0);
                    entry.getKey().sendMessage(Language.getMsg(entry.getKey(), Messages.PLAYER_DIE_RESPAWN_CHAT).replace("{time}", String.valueOf(entry.getValue())));
                    this.getArena().getRespawn().replace(entry.getKey(), entry.getValue() - 1);
                }
            }
        }
        if (!this.getArena().getShowTime().isEmpty()) {
            for (final Map.Entry<Player, Integer> entry2 : this.getArena().getShowTime().entrySet()) {
                if (entry2.getValue() <= 0) {
                    this.getArena().getShowTime().remove(entry2.getKey());
                    Bukkit.getPluginManager().callEvent((Event)new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.REMOVED, this.getArena().getTeam(entry2.getKey()), entry2.getKey(), this.getArena()));
                    final Iterator iterator19 = entry2.getKey().getWorld().getPlayers().iterator();
                    while (iterator19.hasNext()) {
                        BeaconBattle.nms.showArmor(entry2.getKey(), iterator19.next());
                    }
                }
                else {
                    this.getArena().getShowTime().replace(entry2.getKey(), entry2.getValue() - 1);
                }
            }
        }
        final Iterator<IGenerator> iterator20 = this.getArena().getOreGenerators().iterator();
        while (iterator20.hasNext()) {
            iterator20.next().spawn();
        }
    }
    
    @Override
    public void cancel() {
        this.task.cancel();
    }
}
