

package ws.billy.bedwars.stats;

import java.time.Instant;
import java.sql.Timestamp;
import java.util.UUID;
import ws.billy.bedwars.api.BeaconBattle;

public class StatsAPI implements BeaconBattle.IStats
{
    private static StatsAPI instance;
    
    public static StatsAPI getInstance() {
        if (StatsAPI.instance == null) {
            StatsAPI.instance = new StatsAPI();
        }
        return StatsAPI.instance;
    }
    
    private StatsAPI() {
    }
    
    private PlayerStats getData(final UUID uuid) {
        PlayerStats playerStats = ws.billy.bedwars.BeaconBattle.getStatsManager().getUnsafe(uuid);
        if (playerStats == null) {
            playerStats = ws.billy.bedwars.BeaconBattle.getRemoteDatabase().fetchStats(uuid);
        }
        return playerStats;
    }
    
    @Override
    public Timestamp getPlayerFirstPlay(final UUID uuid) {
        final Instant firstPlay = this.getData(uuid).getFirstPlay();
        if (firstPlay == null) {
            return null;
        }
        return Timestamp.from(firstPlay);
    }
    
    @Override
    public Timestamp getPlayerLastPlay(final UUID uuid) {
        final Instant lastPlay = this.getData(uuid).getLastPlay();
        if (lastPlay == null) {
            return null;
        }
        return Timestamp.from(lastPlay);
    }
    
    @Override
    public int getPlayerWins(final UUID uuid) {
        return this.getData(uuid).getWins();
    }
    
    @Override
    public int getPlayerKills(final UUID uuid) {
        return this.getData(uuid).getKills();
    }
    
    @Override
    public int getPlayerFinalKills(final UUID uuid) {
        return this.getData(uuid).getFinalKills();
    }
    
    @Override
    public int getPlayerLoses(final UUID uuid) {
        return this.getData(uuid).getLosses();
    }
    
    @Override
    public int getPlayerDeaths(final UUID uuid) {
        return this.getData(uuid).getDeaths();
    }
    
    @Override
    public int getPlayerFinalDeaths(final UUID uuid) {
        return this.getData(uuid).getFinalDeaths();
    }
    
    @Override
    public int getPlayerBedsDestroyed(final UUID uuid) {
        return this.getData(uuid).getBedsDestroyed();
    }
    
    @Override
    public int getPlayerGamesPlayed(final UUID uuid) {
        return this.getData(uuid).getGamesPlayed();
    }
}
