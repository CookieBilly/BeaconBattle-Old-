

package ws.billy.bedwars.sidebar;

import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.arena.Arena;
import java.util.Map;
import org.bukkit.World;
import java.util.Iterator;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.spigot.sidebar.SidebarLineAnimated;
import java.util.LinkedList;
import java.util.Collection;
import ws.billy.spigot.sidebar.SidebarLine;
import java.util.Collections;
import java.util.Arrays;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.arena.GameState;
import java.util.Date;
import org.bukkit.Bukkit;
import ws.billy.spigot.sidebar.PlaceholderProvider;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import ws.billy.spigot.sidebar.Sidebar;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.HashMap;
import ws.billy.spigot.sidebar.SidebarManager;

public class BeaconBattleScoreboard
{
    private static SidebarManager sidebarManager;
    private static HashMap<UUID, BeaconBattleScoreboard> scoreboards;
    private final Player player;
    private IArena arena;
    private Sidebar handle;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat nextEventDateFormat;
    
    private BeaconBattleScoreboard(@NotNull final Player player, @NotNull final List<String> strings, @Nullable final IArena arena) {
        this.arena = arena;
        this.player = player;
        if (strings.isEmpty()) {
            return;
        }
        final BeaconBattleScoreboard BeaconBattleScoreboard = BeaconBattleScoreboard.scoreboards.get(player.getUniqueId());
        if (BeaconBattleScoreboard != null) {
            BeaconBattleScoreboard.remove();
        }
        if (!player.isOnline()) {
            return;
        }
        this.nextEventDateFormat = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_SCOREBOARD_NEXEVENT_TIMER));
        this.dateFormat = new SimpleDateFormat(Language.getMsg(player, Messages.FORMATTING_SCOREBOARD_DATE));
        final List<PlaceholderProvider> list = Arrays.asList(new PlaceholderProvider("{on}", () -> String.valueOf((this.getArena() == null) ? Bukkit.getOnlinePlayers().size() : this.getArena().getPlayers().size())), new PlaceholderProvider("{max}", () -> String.valueOf((this.getArena() == null) ? Bukkit.getMaxPlayers() : this.getArena().getMaxPlayers())), new PlaceholderProvider("{time}", () -> {
            if (this.arena == null) {
                return this.dateFormat.format(new Date(System.currentTimeMillis()));
            }
            else if (this.arena.getStatus() == GameState.playing || this.arena.getStatus() == GameState.restarting) {
                return this.getNextEventTime();
            }
            else if (this.arena.getStatus() == GameState.starting && this.getArena().getStartingTask() != null) {
                return String.valueOf(this.getArena().getStartingTask().getCountdown() + 1);
            }
            else {
                return this.dateFormat.format(new Date(System.currentTimeMillis()));
            }
        }), new PlaceholderProvider("{nextEvent}", this::getNextEventName), new PlaceholderProvider("{date}", () -> this.dateFormat.format(new Date(System.currentTimeMillis()))), new PlaceholderProvider("{kills}", () -> String.valueOf((this.getArena() == null) ? BeaconBattle.getStatsManager().get(this.getPlayer().getUniqueId()).getKills() : this.getArena().getPlayerKills(this.getPlayer(), false))), new PlaceholderProvider("{finalKills}", () -> String.valueOf((this.getArena() == null) ? BeaconBattle.getStatsManager().get(this.getPlayer().getUniqueId()).getFinalKills() : this.getArena().getPlayerKills(this.getPlayer(), true))), new PlaceholderProvider("{beds}", () -> String.valueOf((this.getArena() == null) ? BeaconBattle.getStatsManager().get(this.getPlayer().getUniqueId()).getBedsDestroyed() : this.getArena().getPlayerBedsDestroyed(this.getPlayer()))), new PlaceholderProvider("{deaths}", () -> String.valueOf((this.getArena() == null) ? BeaconBattle.getStatsManager().get(this.getPlayer().getUniqueId()).getDeaths() : this.getArena().getPlayerDeaths(this.getPlayer(), false))), new PlaceholderProvider("{progress}", () -> BeaconBattle.getLevelSupport().getProgressBar(this.getPlayer())), new PlaceholderProvider("{level}", () -> BeaconBattle.getLevelSupport().getLevel(this.getPlayer())), new PlaceholderProvider("{currentXp}", () -> BeaconBattle.getLevelSupport().getCurrentXpFormatted(this.getPlayer())), new PlaceholderProvider("{requiredXp}", () -> BeaconBattle.getLevelSupport().getRequiredXpFormatted(this.getPlayer())));
        if (BeaconBattleScoreboard.sidebarManager == null) {
            try {
                BeaconBattleScoreboard.sidebarManager = new SidebarManager();
            }
            catch (InstantiationException cause) {
                throw new IllegalStateException(cause);
            }
        }
        this.handle = BeaconBattleScoreboard.sidebarManager.createSidebar(null, (Collection<SidebarLine>)Collections.emptyList(), list);
        BeaconBattleScoreboard.scoreboards.put(player.getUniqueId(), this);
        this.setStrings(strings);
        this.handle.apply(player);
    }
    
    public void setArena(final IArena arena) {
        this.arena = arena;
    }
    
    private void setStrings(@NotNull final List<String> list) {
        while (this.handle.linesAmount() > 0) {
            this.handle.removeLine(0);
        }
        final LinkedList list2 = new LinkedList();
        final List<String> list3;
        this.handle.getPlaceholders().forEach(placeholderProvider -> {
            if (placeholderProvider.getPlaceholder().startsWith("{Team")) {
                list3.add(placeholderProvider.getPlaceholder());
            }
            return;
        });
        list2.forEach(s -> this.handle.removePlaceholder(s));
        final String[] split = list.remove(0).split("[\\n,]");
        if (split.length == 1) {
            this.handle.setTitle(new SidebarLine() {
                @NotNull
                @Override
                public String getLine() {
                    return split[0];
                }
            });
        }
        else {
            this.handle.setTitle(new SidebarLineAnimated(split));
        }
        this.handle.playerListClear();
        if (this.arena != null) {
            this.addHealthIcon();
            this.arena.getPlayers().forEach(player -> this.handle.refreshHealth(player, (int)player.getHealth()));
            final Iterator<ITeam> iterator = this.arena.getTeams().iterator();
            while (iterator.hasNext()) {
                final ITeam team;
                String str;
                this.handle.addPlaceholder(new PlaceholderProvider("{Team" + iterator.next().getName() + "Status}", () -> {
                    if (team.isBedDestroyed()) {
                        if (team.getSize() > 0) {
                            str = Language.getMsg(this.getPlayer(), Messages.FORMATTING_SCOREBOARD_BED_DESTROYED).replace("{remainingPlayers}", String.valueOf(team.getSize()));
                        }
                        else {
                            str = Language.getMsg(this.getPlayer(), Messages.FORMATTING_SCOREBOARD_TEAM_ELIMINATED);
                        }
                    }
                    else {
                        str = Language.getMsg(this.getPlayer(), Messages.FORMATTING_SCOREBOARD_TEAM_ALIVE);
                    }
                    if (team.isMember(this.getPlayer())) {
                        str += Language.getMsg(this.getPlayer(), Messages.FORMATTING_SCOREBOARD_YOUR_TEAM);
                    }
                    return str;
                }));
            }
            if ((this.arena.getStatus() == GameState.playing && BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-playing-list")) || (this.arena.getStatus() == GameState.restarting && BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-restarting-list"))) {
                final String s2 = (this.arena.getStatus() == GameState.playing) ? Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PLAYING : Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_PRESTARTING;
                final String s3 = (this.arena.getStatus() == GameState.playing) ? Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PLAYING : Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_PRESTARTING;
                final Iterator<ITeam> iterator2 = this.arena.getTeams().iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().getMembers().forEach(player3 -> this.addToTabList(player3, s2, s3));
                    this.handle.playerListRefreshAnimation();
                }
                if (this.arena.isSpectator(this.getPlayer())) {
                    final String s4;
                    final String s5;
                    final BeaconBattleScoreboard BeaconBattleScoreboard;
                    this.arena.getSpectators().forEach(player4 -> {
                        this.addToTabList(player4, s4, s5);
                        getSBoard(player4.getUniqueId());
                        if (BeaconBattleScoreboard != null) {
                            BeaconBattleScoreboard.addToTabList(this.getPlayer(), s4, s5);
                        }
                        this.handle.playerListRefreshAnimation();
                        return;
                    });
                }
            }
            else if ((this.arena.getStatus() == GameState.waiting && BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-waiting-list")) || (this.arena.getStatus() == GameState.starting && BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-starting-list"))) {
                this.arena.getPlayers().forEach(player5 -> this.addToTabList(player5, (this.arena.getStatus() == GameState.waiting) ? Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_WAITING : Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_STARTING, (this.arena.getStatus() == GameState.waiting) ? Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_WAITING : Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_STARTING));
                this.handle.playerListRefreshAnimation();
            }
        }
        else if (BeaconBattle.config.getBoolean("scoreboard-settings.player-list.format-lobby-list")) {
            final World world = Bukkit.getWorld(BeaconBattle.config.getLobbyWorldName());
            if (world != null) {
                final BeaconBattleScoreboard BeaconBattleScoreboard2;
                world.getPlayers().forEach(player2 -> {
                    this.addToTabList(player2, Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_LOBBY, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_LOBBY);
                    getSBoard(player2.getUniqueId());
                    if (BeaconBattleScoreboard2 != null) {
                        BeaconBattleScoreboard2.addToTabList(this.getPlayer(), Messages.FORMATTING_SCOREBOARD_TAB_PREFIX_LOBBY, Messages.FORMATTING_SCOREBOARD_TAB_SUFFIX_LOBBY);
                    }
                    this.handle.playerListRefreshAnimation();
                    return;
                });
            }
        }
        final Iterator<String> iterator3 = list.iterator();
        while (iterator3.hasNext()) {
            final String replace = iterator3.next().replace("{server_ip}", BeaconBattle.config.getString("server-ip")).replace("{version}", BeaconBattle.plugin.getDescription().getVersion()).replace("{server}", BeaconBattle.config.getString("bungee-settings.server-id")).replace("{player}", this.player.getDisplayName()).replace("{money}", String.valueOf(BeaconBattle.getEconomy().getMoney(this.player)));
            String s6;
            if (this.arena == null) {
                s6 = Misc.replaceStatsPlaceholders(this.getPlayer(), replace, true);
            }
            else {
                s6 = replace.replace("{map}", this.arena.getDisplayName()).replace("{group}", this.arena.getDisplayGroup(this.player));
                for (final ITeam team2 : this.arena.getTeams()) {
                    s6 = s6.replace("{Team" + team2.getName() + "Color}", team2.getColor().chat().toString()).replace("{Team" + team2.getName() + "Name}", team2.getDisplayName(Language.getPlayerLanguage(this.getPlayer())));
                }
            }
            this.handle.addLine(new SidebarLine() {
                @NotNull
                @Override
                public String getLine() {
                    return s6;
                }
            });
        }
    }
    
    public void addToTabList(final Player player, @NotNull final String s, @NotNull final String s2) {
        this.handle.playerListCreate(player, this.getTeamListText(s, player), this.getTeamListText(s2, player));
        if (this.arena != null) {
            ITeam exTeam = null;
            ITeam exTeam2 = null;
            ITeam exTeam3 = null;
            ITeam exTeam4 = null;
            this.handle.playerListAddPlaceholders(player, new PlaceholderProvider("{team}", () -> {
                if (this.arena.isSpectator(player)) {
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_COLOR) + Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM);
                }
                else {
                    this.arena.getTeam(player);
                    if (exTeam == null) {
                        exTeam = this.arena.getExTeam(player.getUniqueId());
                    }
                    if (exTeam == null) {
                        return "";
                    }
                    else {
                        return exTeam.getColor().chat() + exTeam.getDisplayName(Language.getPlayerLanguage(this.getPlayer()));
                    }
                }
            }), new PlaceholderProvider("{teamLetter}", () -> {
                if (this.arena.isSpectator(player)) {
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM).substring(0, 1);
                }
                else {
                    this.arena.getTeam(player);
                    if (exTeam2 == null) {
                        exTeam2 = this.arena.getExTeam(player.getUniqueId());
                    }
                    if (exTeam2 == null) {
                        return "";
                    }
                    else {
                        return exTeam2.getDisplayName(Language.getPlayerLanguage(this.getPlayer())).substring(0, 1);
                    }
                }
            }), new PlaceholderProvider("{teamName}", () -> {
                if (this.arena.isSpectator(player)) {
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_TEAM);
                }
                else {
                    this.arena.getTeam(player);
                    if (exTeam3 == null) {
                        exTeam3 = this.arena.getExTeam(player.getUniqueId());
                    }
                    if (exTeam3 == null) {
                        return "";
                    }
                    else {
                        return exTeam3.getDisplayName(Language.getPlayerLanguage(this.getPlayer()));
                    }
                }
            }), new PlaceholderProvider("{teamColor}", () -> {
                if (this.arena.isSpectator(player)) {
                    return Language.getMsg(this.getPlayer(), Messages.FORMATTING_SPECTATOR_COLOR);
                }
                else {
                    this.arena.getTeam(player);
                    if (exTeam4 == null) {
                        exTeam4 = this.arena.getExTeam(player.getUniqueId());
                    }
                    if (exTeam4 == null) {
                        return "";
                    }
                    else {
                        return exTeam4.getColor().chat().toString();
                    }
                }
            }));
        }
    }
    
    public void addHealthIcon() {
        if (this.handle != null) {
            final List<String> list = Language.getList(this.player, Messages.FORMATTING_SCOREBOARD_HEALTH);
            if (list.isEmpty()) {
                return;
            }
            SidebarLine sidebarLine;
            if (list.size() > 1) {
                final String[] array = new String[list.size()];
                for (int i = 0; i < list.size(); ++i) {
                    array[i] = list.get(i);
                }
                sidebarLine = new SidebarLineAnimated(array);
            }
            else {
                sidebarLine = new SidebarLine() {
                    final /* synthetic */ String val$text = list.get(0);
                    
                    @NotNull
                    @Override
                    public String getLine() {
                        return this.val$text;
                    }
                };
            }
            this.handle.showPlayersHealth(sidebarLine, BeaconBattle.config.getBoolean("scoreboard-settings.health.display-in-tab"));
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void remove() {
        BeaconBattleScoreboard.scoreboards.remove(this.getPlayer().getUniqueId());
        if (this.handle != null) {
            this.handle.remove(this.player.getUniqueId());
            this.handle = null;
            getScoreboards().values().forEach(BeaconBattleScoreboard -> BeaconBattleScoreboard.handle.playerListRemove(this.getPlayer().getName()));
        }
    }
    
    public IArena getArena() {
        return this.arena;
    }
    
    public static Map<UUID, BeaconBattleScoreboard> getScoreboards() {
        return BeaconBattleScoreboard.scoreboards;
    }
    
    public static BeaconBattleScoreboard getSBoard(final UUID key) {
        return BeaconBattleScoreboard.scoreboards.get(key);
    }
    
    @NotNull
    private String getNextEventName() {
        if (!(this.arena instanceof Arena)) {
            return "-";
        }
        final Arena arena = (Arena)this.arena;
        String s = "-";
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_EMERALD_UPGRADE_II);
                break;
            }
            case EMERALD_GENERATOR_TIER_III: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_EMERALD_UPGRADE_III);
                break;
            }
            case DIAMOND_GENERATOR_TIER_II: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DIAMOND_UPGRADE_II);
                break;
            }
            case DIAMOND_GENERATOR_TIER_III: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DIAMOND_UPGRADE_III);
                break;
            }
            case GAME_END: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_GAME_END);
                break;
            }
            case BEDS_DESTROY: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_BEDS_DESTROY);
                break;
            }
            case ENDER_DRAGON: {
                s = Language.getMsg(this.getPlayer(), Messages.NEXT_EVENT_DRAGON_SPAWN);
                break;
            }
        }
        return s;
    }
    
    @NotNull
    private String getNextEventTime() {
        if (!(this.arena instanceof Arena)) {
            return this.nextEventDateFormat.format(0L);
        }
        final Arena arena = (Arena)this.arena;
        long l = 0L;
        switch (arena.getNextEvent()) {
            case EMERALD_GENERATOR_TIER_II:
            case EMERALD_GENERATOR_TIER_III: {
                l = arena.upgradeEmeraldsCount * 1000L;
                break;
            }
            case DIAMOND_GENERATOR_TIER_II:
            case DIAMOND_GENERATOR_TIER_III: {
                l = arena.upgradeDiamondsCount * 1000L;
                break;
            }
            case GAME_END: {
                l = arena.getPlayingTask().getGameEndCountdown() * 1000L;
                break;
            }
            case BEDS_DESTROY: {
                l = arena.getPlayingTask().getBedsDestroyCountdown() * 1000L;
                break;
            }
            case ENDER_DRAGON: {
                l = arena.getPlayingTask().getDragonSpawnCountdown() * 1000L;
                break;
            }
        }
        return this.nextEventDateFormat.format(l);
    }
    
    public void updateSpectator(final Player player, final boolean b) {
    }
    
    public void invisibilityPotion(@NotNull final ITeam team, final Player player, final boolean b) {
        if (b) {
            this.handle.playerListHideNameTag(player);
        }
        else {
            this.handle.playerListRestoreNameTag(player);
        }
    }
    
    public static void giveScoreboard(@NotNull final Player player, final IArena arena, final boolean b) {
        if (!player.isOnline()) {
            return;
        }
        final BeaconBattleScoreboard sBoard = getSBoard(player.getUniqueId());
        List<String> strings = null;
        if (arena == null) {
            if (BeaconBattle.getServerType() == ServerType.SHARED) {
                return;
            }
            if (!BeaconBattle.config.getBoolean("scoreboard-settings.sidebar.enable-lobby-sidebar")) {
                if (sBoard != null) {
                    sBoard.remove();
                }
                return;
            }
            strings = Language.getList(player, Messages.SCOREBOARD_LOBBY);
        }
        else {
            if (!BeaconBattle.config.getBoolean("scoreboard-settings.sidebar.enable-game-sidebar")) {
                if (sBoard != null) {
                    sBoard.remove();
                }
                return;
            }
            if (arena.getStatus() == GameState.waiting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".waiting", Messages.SCOREBOARD_DEFAULT_WAITING);
            }
            else if (arena.getStatus() == GameState.starting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".starting", Messages.SCOREBOARD_DEFAULT_STARTING);
            }
            else if (arena.getStatus() == GameState.playing || arena.getStatus() == GameState.restarting) {
                strings = Language.getScoreboard(player, "scoreboard." + arena.getGroup() + ".playing", Messages.SCOREBOARD_DEFAULT_PLAYING);
            }
        }
        if (strings == null || strings.isEmpty()) {
            if (sBoard != null) {
                sBoard.remove();
            }
            return;
        }
        if (sBoard == null) {
            if (b) {
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> new BeaconBattleScoreboard(player, strings, arena), 5L);
            }
            else {
                new BeaconBattleScoreboard(player, strings, arena);
            }
        }
        else {
            sBoard.setArena(arena);
            sBoard.setStrings(strings);
            if (arena != null && arena.getStatus() != GameState.playing) {
                sBoard.handle.hidePlayersHealth();
            }
            else if (arena == null) {
                sBoard.handle.hidePlayersHealth();
            }
        }
    }
    
    public Sidebar getHandle() {
        return this.handle;
    }
    
    @NotNull
    private SidebarLine getTeamListText(final String s, final Player player) {
        if (Language.getList(this.getPlayer(), s).isEmpty()) {
            return new SidebarLine() {
                @NotNull
                @Override
                public String getLine() {
                    return "";
                }
            };
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<String> iterator = Language.getList(this.getPlayer(), s).iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player)));
        }
        if (list.size() == 1) {
            return new SidebarLine() {
                final /* synthetic */ String val$line = (String)list.get(0);
                
                @NotNull
                @Override
                public String getLine() {
                    return this.val$line;
                }
            };
        }
        final String[] array = new String[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (String)list.get(i);
        }
        return new SidebarLineAnimated(array);
    }
    
    static {
        BeaconBattleScoreboard.sidebarManager = null;
        BeaconBattleScoreboard.scoreboards = new HashMap<UUID, BeaconBattleScoreboard>();
    }
}
