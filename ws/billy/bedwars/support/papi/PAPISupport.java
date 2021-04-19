

package ws.billy.bedwars.support.papi;

import ws.billy.bedwars.api.arena.team.ITeam;
import java.time.Instant;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.commands.shout.ShoutCommand;
import ws.billy.bedwars.api.arena.GameState;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.BeaconBattle;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPISupport extends PlaceholderExpansion
{
    public String getIdentifier() {
        return "e";
    }
    
    public String getAuthor() {
        return "CookieBilly";
    }
    
    public String getVersion() {
        return BeaconBattle.plugin.getDescription().getVersion();
    }
    
    public boolean persist() {
        return true;
    }
    
    public String onPlaceholderRequest(final Player player, final String s) {
        if (s == null) {
            return "";
        }
        if (s.startsWith("arena_status_")) {
            final IArena arenaByName = Arena.getArenaByName(s.replace("arena_status_", ""));
            if (arenaByName == null) {
                return Language.getMsg(player, Messages.ARENA_STATUS_RESTARTING_NAME);
            }
            return arenaByName.getDisplayStatus(Language.getDefaultLanguage());
        }
        else {
            if (s.startsWith("arena_count_")) {
                int i = 0;
                final String[] split = s.replace("arena_count_", "").split("\\+");
                for (int length = split.length, j = 0; j < length; ++j) {
                    final IArena arenaByName2 = Arena.getArenaByName(split[j]);
                    if (arenaByName2 != null) {
                        i += arenaByName2.getPlayers().size();
                    }
                }
                return String.valueOf(i);
            }
            if (s.startsWith("group_count_")) {
                return String.valueOf(Arena.getPlayers(s.replace("group_count_", "")));
            }
            if (!s.startsWith("arena_group_")) {
                String s2 = "";
                final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
                switch (s) {
                    case "stats_firstplay": {
                        final Instant firstPlay = BeaconBattle.getStatsManager().get(player.getUniqueId()).getFirstPlay();
                        s2 = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((firstPlay != null) ? Timestamp.from(firstPlay) : null);
                        break;
                    }
                    case "stats_lastplay": {
                        final Instant lastPlay = BeaconBattle.getStatsManager().get(player.getUniqueId()).getLastPlay();
                        s2 = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_STATS_DATE_FORMAT)).format((lastPlay != null) ? Timestamp.from(lastPlay) : null);
                        break;
                    }
                    case "stats_kills": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getKills());
                        break;
                    }
                    case "stats_wins": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getWins());
                        break;
                    }
                    case "stats_finalkills": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getFinalKills());
                        break;
                    }
                    case "stats_deaths": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getDeaths());
                        break;
                    }
                    case "stats_losses": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getLosses());
                        break;
                    }
                    case "stats_finaldeaths": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getFinalDeaths());
                        break;
                    }
                    case "stats_bedsdestroyed": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getBedsDestroyed());
                        break;
                    }
                    case "stats_gamesplayed": {
                        s2 = String.valueOf(BeaconBattle.getStatsManager().get(player.getUniqueId()).getGamesPlayed());
                        break;
                    }
                    case "current_online": {
                        s2 = String.valueOf(Arena.getArenaByPlayer().size());
                        break;
                    }
                    case "current_arenas": {
                        s2 = String.valueOf(Arena.getArenas().size());
                        break;
                    }
                    case "player_team_color": {
                        if (arenaByPlayer != null && arenaByPlayer.isPlayer(player) && arenaByPlayer.getStatus() == GameState.playing) {
                            final ITeam team = arenaByPlayer.getTeam(player);
                            if (team != null) {
                                s2 += String.valueOf(team.getColor().chat());
                            }
                            break;
                        }
                        break;
                    }
                    case "player_team": {
                        if (arenaByPlayer == null) {
                            break;
                        }
                        if (ShoutCommand.isShout(player)) {
                            s2 += Language.getMsg(player, Messages.FORMAT_PAPI_PLAYER_TEAM_SHOUT);
                        }
                        if (!arenaByPlayer.isPlayer(player)) {
                            s2 += Language.getMsg(player, Messages.FORMAT_PAPI_PLAYER_TEAM_SPECTATOR);
                            break;
                        }
                        if (arenaByPlayer.getStatus() == GameState.playing) {
                            final ITeam team2 = arenaByPlayer.getTeam(player);
                            if (team2 != null) {
                                s2 += Language.getMsg(player, Messages.FORMAT_PAPI_PLAYER_TEAM_TEAM).replace("{TeamName}", team2.getDisplayName(Language.getPlayerLanguage(player))).replace("{TeamColor}", String.valueOf(team2.getColor().chat()));
                            }
                            break;
                        }
                        break;
                    }
                    case "player_level": {
                        s2 = BeaconBattle.getLevelSupport().getLevel(player);
                        break;
                    }
                    case "player_level_raw": {
                        s2 = String.valueOf(BeaconBattle.getLevelSupport().getPlayerLevel(player));
                        break;
                    }
                    case "player_progress": {
                        s2 = BeaconBattle.getLevelSupport().getProgressBar(player);
                        break;
                    }
                    case "player_xp_formatted": {
                        s2 = BeaconBattle.getLevelSupport().getCurrentXpFormatted(player);
                        break;
                    }
                    case "player_xp": {
                        s2 = String.valueOf(BeaconBattle.getLevelSupport().getCurrentXp(player));
                        break;
                    }
                    case "player_rerq_xp_formatted": {
                        s2 = BeaconBattle.getLevelSupport().getRequiredXpFormatted(player);
                        break;
                    }
                    case "player_rerq_xp": {
                        s2 = String.valueOf(BeaconBattle.getLevelSupport().getRequiredXp(player));
                        break;
                    }
                    case "current_arena_group": {
                        if (arenaByPlayer != null) {
                            s2 = arenaByPlayer.getGroup();
                            break;
                        }
                        break;
                    }
                }
                return s2;
            }
            final IArena arenaByName3 = Arena.getArenaByName(s.replace("arena_group_", ""));
            if (arenaByName3 != null) {
                return arenaByName3.getGroup();
            }
            return "-";
        }
    }
}
