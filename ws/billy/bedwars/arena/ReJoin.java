

package ws.billy.bedwars.arena;

import java.util.Collections;
import ws.billy.bedwars.api.arena.GameState;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import ws.billy.bedwars.lobbysocket.ArenaSocket;
import com.google.gson.JsonObject;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.entity.Player;
import java.util.List;
import ws.billy.bedwars.shop.ShopCache;
import java.util.ArrayList;
import ws.billy.bedwars.arena.tasks.ReJoinTask;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;

import java.util.UUID;

public class ReJoin
{
    private UUID player;
    private IArena arena;
    private ITeam bwt;
    private ReJoinTask task;
    private final ArrayList<ShopCache.CachedItem> permanentsAndNonDowngradables;
    private int kills;
    private int finalKills;
    private int deaths;
    private int finalDeaths;
    private int beds;
    private static final List<ReJoin> reJoinList;
    
    public ReJoin(final Player player, final IArena arena, final ITeam bwt, final List<ShopCache.CachedItem> c) {
        this.task = null;
        this.permanentsAndNonDowngradables = new ArrayList<ShopCache.CachedItem>();
        this.kills = 0;
        this.finalKills = 0;
        this.deaths = 0;
        this.finalDeaths = 0;
        this.beds = 0;
        final ReJoin player2 = getPlayer(player);
        if (player2 != null) {
            player2.destroy();
        }
        if (bwt == null) {
            return;
        }
        if (bwt.isBedDestroyed()) {
            return;
        }
        this.bwt = bwt;
        this.player = player.getUniqueId();
        this.arena = arena;
        ReJoin.reJoinList.add(this);
        BeaconBattle.debug("Created ReJoin for " + player.getName() + " " + player.getUniqueId() + " at " + arena.getArenaName());
        this.storeStatsDiff(arena.getPlayerKills(player, false), arena.getPlayerKills(player, true), arena.getPlayerDeaths(player, false), arena.getPlayerDeaths(player, true), arena.getPlayerBedsDestroyed(player));
        if (bwt.getMembers().isEmpty()) {
            this.task = new ReJoinTask(arena, bwt);
        }
        this.permanentsAndNonDowngradables.addAll(c);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "RC");
        jsonObject.addProperty("uuid", player.getUniqueId().toString());
        jsonObject.addProperty("arena_id", arena.getWorldName());
        jsonObject.addProperty("server", BeaconBattle.config.getString("bungee-settings.server-id"));
        ArenaSocket.sendMessage(jsonObject.toString());
    }
    
    public static boolean exists(@NotNull final Player player) {
        BeaconBattle.debug("ReJoin exists check " + player.getUniqueId());
        for (final ReJoin reJoin : getReJoinList()) {
            BeaconBattle.debug("ReJoin exists check list scroll: " + reJoin.getPl().toString());
            if (reJoin.getPl().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    public static ReJoin getPlayer(@NotNull final Player player) {
        BeaconBattle.debug("ReJoin getPlayer " + player.getUniqueId());
        for (final ReJoin reJoin : getReJoinList()) {
            if (reJoin.getPl().equals(player.getUniqueId())) {
                return reJoin;
            }
        }
        return null;
    }
    
    public boolean canReJoin() {
        BeaconBattle.debug("ReJoin canReJoin  check.");
        if (this.arena == null) {
            BeaconBattle.debug("ReJoin canReJoin arena is null " + this.player.toString());
            this.destroy();
            return false;
        }
        if (this.arena.getStatus() == GameState.restarting) {
            BeaconBattle.debug("ReJoin canReJoin status is restarting " + this.player.toString());
            this.destroy();
            return false;
        }
        if (this.bwt == null) {
            BeaconBattle.debug("ReJoin canReJoin bwt is null " + this.player.toString());
            this.destroy();
            return false;
        }
        if (this.bwt.isBedDestroyed()) {
            BeaconBattle.debug("ReJoin canReJoin bed is destroyed " + this.player.toString());
            this.destroy();
            return false;
        }
        return true;
    }
    
    public boolean reJoin(final Player player) {
        return this.arena.reJoin(player);
    }
    
    public void destroy() {
        BeaconBattle.debug("ReJoin destroy for " + this.player.toString());
        ReJoin.reJoinList.remove(this);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "RD");
        jsonObject.addProperty("uuid", this.player.toString());
        jsonObject.addProperty("server", BeaconBattle.config.getString("bungee-settings.server-id"));
        ArenaSocket.sendMessage(jsonObject.toString());
    }
    
    public UUID getPlayer() {
        return this.player;
    }
    
    public ITeam getBwt() {
        return this.bwt;
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    private void storeStatsDiff(final int kills, final int finalKills, final int deaths, final int finalDeaths, final int beds) {
        this.kills = kills;
        this.finalKills = finalKills;
        this.deaths = deaths;
        this.finalDeaths = finalDeaths;
        this.beds = beds;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public int getFinalDeaths() {
        return this.finalDeaths;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public int getFinalKills() {
        return this.finalKills;
    }
    
    public int getBeds() {
        return this.beds;
    }
    
    public ReJoinTask getTask() {
        return this.task;
    }
    
    public UUID getPl() {
        return this.player;
    }
    
    public List<ShopCache.CachedItem> getPermanentsAndNonDowngradables() {
        return this.permanentsAndNonDowngradables;
    }
    
    public static List<ReJoin> getReJoinList() {
        return Collections.unmodifiableList((List<? extends ReJoin>)ReJoin.reJoinList);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && this.getClass() == o.getClass() && ((ReJoin)o).getPl().equals(this.getPl());
    }
    
    static {
        reJoinList = new ArrayList<ReJoin>();
    }
}
