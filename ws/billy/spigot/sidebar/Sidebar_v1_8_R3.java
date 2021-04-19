

package ws.billy.spigot.sidebar;

import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.jetbrains.annotations.Contract;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import org.bukkit.Bukkit;
import java.util.UUID;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import org.bukkit.ChatColor;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import java.util.LinkedList;

class Sidebar_v1_8_R3 implements Sidebar
{
    private LinkedList<ScoreLine> lines;
    protected LinkedList<PlayerConnection> players;
    protected LinkedList<PlaceholderProvider> placeholderProviders;
    private LinkedList<String> availableColors;
    private SidebarObjective sidebarObjective;
    protected SidebarObjective healthObjective;
    private ConcurrentHashMap<String, PlayerList_v1_8_R3> teamLists;
    
    public Sidebar_v1_8_R3(@NotNull final SidebarLine sidebarLine, @NotNull final Collection<SidebarLine> collection, final Collection<PlaceholderProvider> c) {
        this.lines = new LinkedList<ScoreLine>();
        this.players = new LinkedList<PlayerConnection>();
        this.placeholderProviders = new LinkedList<PlaceholderProvider>();
        this.availableColors = new LinkedList<String>();
        this.healthObjective = null;
        this.teamLists = new ConcurrentHashMap<String, PlayerList_v1_8_R3>();
        final ChatColor[] values = ChatColor.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            this.availableColors.add(values[i].toString());
        }
        this.sidebarObjective = new SidebarObjective("Sidebar", IScoreboardCriteria.b, 1, sidebarLine);
        this.placeholderProviders.addAll(c);
        final Iterator<SidebarLine> iterator = collection.iterator();
        while (iterator.hasNext()) {
            this.addLine(iterator.next());
        }
    }
    
    @Override
    public void setTitle(final SidebarLine sidebarLine) {
        this.sidebarObjective.displayName = sidebarLine;
        this.sidebarObjective.sendUpdate();
    }
    
    @Override
    public void addPlaceholder(final PlaceholderProvider placeholderProvider) {
        this.placeholderProviders.remove(placeholderProvider);
        this.placeholderProviders.add(placeholderProvider);
        final String[] array;
        int length;
        int i = 0;
        this.lines.forEach(scoreLine -> {
            if (!scoreLine.text.isHasPlaceholders()) {
                if (scoreLine.text instanceof SidebarLineAnimated) {
                    ((SidebarLineAnimated)scoreLine.text).getLines();
                    length = array.length;
                    while (i < length) {
                        if (array[i].contains(placeholderProvider.getPlaceholder())) {
                            scoreLine.text.setHasPlaceholders(true);
                            break;
                        }
                        else {
                            ++i;
                        }
                    }
                }
                else if (scoreLine.text.getLine().contains(placeholderProvider.getPlaceholder())) {
                    scoreLine.text.setHasPlaceholders(true);
                }
            }
        });
    }
    
    @Override
    public void addLine(final SidebarLine sidebarLine) {
        final int availableScore = this.getAvailableScore();
        if (availableScore == -1) {
            return;
        }
        scoreOffsetIncrease(this.lines);
        final String s = this.availableColors.get(0);
        this.availableColors.remove(0);
        final ScoreLine e = new ScoreLine(sidebarLine, (availableScore == 0) ? availableScore : (availableScore - 1), s);
        e.sendCreate();
        this.lines.add(e);
        this.order();
    }
    
    @Override
    public void setLine(final SidebarLine sidebarLine, final int index) {
        if (index >= 0 && index < this.lines.size()) {
            final ScoreLine scoreLine = this.lines.get(index);
            this.placeholderProviders.forEach(placeholderProvider -> {
                if (sidebarLine.getLine().contains(placeholderProvider.getPlaceholder())) {
                    sidebarLine.setHasPlaceholders(true);
                }
                return;
            });
            scoreLine.setText(sidebarLine);
        }
    }
    
    private int getAvailableScore() {
        if (this.lines.isEmpty()) {
            return 0;
        }
        if (this.lines.size() == 16) {
            return -1;
        }
        return this.lines.getFirst().getScore();
    }
    
    private void order() {
        Collections.sort(this.lines);
    }
    
    @Override
    public void apply(final Player player) {
        final PlayerConnection playerConnection = ((CraftPlayer)player).getHandle().playerConnection;
        this.sidebarObjective.sendCreate(playerConnection);
        this.lines.forEach(scoreLine -> scoreLine.sendCreate(playerConnection));
        this.players.add(playerConnection);
        if (this.healthObjective != null) {
            this.healthObjective.sendCreate(playerConnection);
            this.teamLists.forEach((p1, playerList_v1_8_R3) -> playerList_v1_8_R3.sendCreate(playerConnection));
        }
    }
    
    @Override
    public void refreshPlaceholders() {
        final Iterator<PlaceholderProvider> iterator;
        PlaceholderProvider placeholderProvider;
        String replace = null;
        this.lines.forEach(scoreLine -> {
            if (scoreLine.text.isHasPlaceholders()) {
                scoreLine.text.getLine();
                this.placeholderProviders.iterator();
                while (iterator.hasNext()) {
                    placeholderProvider = iterator.next();
                    if (replace.contains(placeholderProvider.getPlaceholder())) {
                        replace = replace.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
                    }
                }
                scoreLine.setContent(replace);
                scoreLine.sendUpdate();
            }
        });
    }
    
    @Override
    public void refreshTitle() {
        this.sidebarObjective.sendUpdate();
    }
    
    @Override
    public void refreshAnimatedLines() {
        final Iterator<PlaceholderProvider> iterator;
        PlaceholderProvider placeholderProvider;
        String replace = null;
        this.lines.forEach(scoreLine -> {
            if (scoreLine.text instanceof SidebarLineAnimated) {
                if (scoreLine.text.isHasPlaceholders()) {
                    scoreLine.text.getLine();
                    this.placeholderProviders.iterator();
                    while (iterator.hasNext()) {
                        placeholderProvider = iterator.next();
                        if (replace.contains(placeholderProvider.getPlaceholder())) {
                            replace = replace.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
                        }
                    }
                    scoreLine.setContent(replace);
                }
                else {
                    scoreLine.setContent(scoreLine.text.getLine());
                }
                scoreLine.sendUpdate();
            }
        });
    }
    
    @Override
    public void removeLine(final int n) {
        if (n >= 0 && n < this.lines.size()) {
            this.lines.get(n).remove();
            scoreOffsetDecrease((Collection<ScoreLine>)this.lines.subList(n, this.lines.size()));
        }
    }
    
    @Override
    public int linesAmount() {
        return this.lines.size();
    }
    
    @Override
    public void removePlaceholder(final String anotherString) {
        this.placeholderProviders.removeIf(placeholderProvider -> placeholderProvider.getPlaceholder().equalsIgnoreCase(anotherString));
    }
    
    @Override
    public List<PlaceholderProvider> getPlaceholders() {
        return Collections.unmodifiableList((List<? extends PlaceholderProvider>)this.placeholderProviders);
    }
    
    @Override
    public void playerListCreate(@NotNull final Player player, final SidebarLine sidebarLine, final SidebarLine sidebarLine2) {
        if (this.teamLists.containsKey(player.getName())) {
            this.playerListRemove(player.getName());
        }
        final PlayerList_v1_8_R3 value = new PlayerList_v1_8_R3(this, player, sidebarLine, sidebarLine2);
        this.players.forEach(value::sendCreate);
        this.teamLists.put(player.getName(), value);
    }
    
    @Override
    public void playerListAddPlaceholders(@NotNull final Player player, final PlaceholderProvider... array) {
        final PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(player.getName(), null);
        if (playerList_v1_8_R3 == null) {
            return;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            playerList_v1_8_R3.addPlaceholderProvider(array[i]);
        }
        playerList_v1_8_R3.sendUpdate();
    }
    
    @Override
    public void playerListRemovePlaceholder(@NotNull final Player player, final String s) {
        final PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(player.getName(), null);
        if (playerList_v1_8_R3 == null) {
            return;
        }
        playerList_v1_8_R3.removePlaceholderProvider(s);
        playerList_v1_8_R3.sendUpdate();
    }
    
    @Override
    public void playerListRemove(final String s) {
        final PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.getOrDefault(s, null);
        if (playerList_v1_8_R3 != null) {
            this.players.forEach(playerList_v1_8_R3::sendRemove);
            this.teamLists.remove(s);
        }
    }
    
    @Override
    public void playerListClear() {
        this.teamLists.forEach((p0, playerList_v1_8_R3) -> this.players.forEach(playerList_v1_8_R3::sendRemove));
        this.teamLists.clear();
    }
    
    @Override
    public void showPlayersHealth(final SidebarLine sidebarLine, final boolean b) {
        if (this.healthObjective == null) {
            this.healthObjective = new SidebarObjective(b ? "health" : "health2", IScoreboardCriteria.b, 2, sidebarLine);
            this.players.forEach(playerConnection -> this.healthObjective.sendCreate(playerConnection));
        }
        else {
            this.healthObjective.sendUpdate();
        }
    }
    
    @Override
    public void hidePlayersHealth() {
        if (this.healthObjective != null) {
            this.players.forEach(playerConnection -> this.healthObjective.sendRemove(playerConnection));
            this.healthObjective = null;
        }
    }
    
    @Override
    public void refreshHealthAnimation() {
        if (this.healthObjective != null && this.healthObjective.displayName instanceof SidebarLineAnimated) {
            this.healthObjective.sendUpdate();
        }
    }
    
    @Override
    public void refreshHealth(@NotNull final Player player, int n) {
        if (n < 0) {
            n = 0;
        }
        CustomScore_v1_8_R3.sendScore(this, player.getName(), n);
    }
    
    @Override
    public void playerListRefreshAnimation() {
        this.teamLists.forEach((p0, playerList_v1_8_R3) -> playerList_v1_8_R3.sendUpdate());
    }
    
    @Override
    public void playerListHideNameTag(@NotNull final Player player) {
        final PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.get(player.getName());
        if (playerList_v1_8_R3 != null) {
            playerList_v1_8_R3.hideNameTag();
        }
    }
    
    @Override
    public void playerListRestoreNameTag(@NotNull final Player player) {
        final PlayerList_v1_8_R3 playerList_v1_8_R3 = this.teamLists.get(player.getName());
        if (playerList_v1_8_R3 != null) {
            playerList_v1_8_R3.showNameTag();
        }
    }
    
    @Override
    public void remove(final UUID obj) {
        this.players.removeIf(playerConnection -> playerConnection.player.getUniqueID().equals(obj));
        final Player player = Bukkit.getPlayer(obj);
        if (player != null && player.isOnline()) {
            final PlayerConnection playerConnection2 = ((CraftPlayer)player).getHandle().playerConnection;
            this.sidebarObjective.sendRemove(playerConnection2);
            if (this.healthObjective != null) {
                this.healthObjective.sendRemove(playerConnection2);
            }
            this.teamLists.forEach((p1, playerList_v1_8_R3) -> playerList_v1_8_R3.sendRemove(playerConnection2));
        }
    }
    
    private static void scoreOffsetIncrease(@NotNull final Collection<ScoreLine> collection) {
        collection.forEach(scoreLine -> scoreLine.setScore(scoreLine.getScore() + 1));
    }
    
    private static void scoreOffsetDecrease(@NotNull final Collection<ScoreLine> collection) {
        collection.forEach(scoreLine -> scoreLine.setScore(scoreLine.getScore() - 1));
    }
    
    private class SidebarObjective extends ScoreboardObjective
    {
        private int type;
        private IScoreboardCriteria.EnumScoreboardHealthDisplay health;
        private SidebarLine displayName;
        
        public SidebarObjective(final String s, final IScoreboardCriteria scoreboardCriteria, final int type, final SidebarLine displayName) {
            super((Scoreboard)null, s, scoreboardCriteria);
            this.health = IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
            this.type = type;
            this.displayName = displayName;
        }
        
        public void setDisplayName(final String s) {
        }
        
        public String getDisplayName() {
            String s = this.displayName.getLine();
            if (s.length() > 16) {
                s = s.substring(0, 16);
            }
            return s;
        }
        
        private void sendCreate(@NotNull final PlayerConnection playerConnection) {
            playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardObjective((ScoreboardObjective)this, 0));
            playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardDisplayObjective(this.type, (ScoreboardObjective)this));
            if (this.getName().equalsIgnoreCase("health")) {
                playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardDisplayObjective(0, (ScoreboardObjective)this));
            }
        }
        
        private void sendUpdate() {
            Sidebar_v1_8_R3.this.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardObjective((ScoreboardObjective)this, 2)));
        }
        
        public void sendRemove(@NotNull final PlayerConnection playerConnection) {
            Sidebar_v1_8_R3.this.teamLists.forEach((p0, playerList_v1_8_R3) -> Sidebar_v1_8_R3.this.players.forEach(playerList_v1_8_R3::sendRemove));
            playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardObjective((ScoreboardObjective)this, 1));
        }
        
        public IScoreboardCriteria.EnumScoreboardHealthDisplay e() {
            return this.health;
        }
    }
    
    private class ScoreLine extends ScoreboardScore implements Comparable<ScoreLine>
    {
        private int score;
        private String prefix;
        private String suffix;
        private TeamLine team;
        private SidebarLine text;
        
        public ScoreLine(final SidebarLine text, @NotNull final int score, final String s) {
            super((Scoreboard)null, (ScoreboardObjective)Sidebar_v1_8_R3.this.sidebarObjective, s);
            this.prefix = "";
            this.suffix = "";
            this.score = score;
            this.text = text;
            this.team = new TeamLine(s);
            if (!text.isHasPlaceholders()) {
                Sidebar_v1_8_R3.this.placeholderProviders.forEach(placeholderProvider -> {
                    if (text.getLine().contains(placeholderProvider.getPlaceholder())) {
                        text.setHasPlaceholders(true);
                    }
                    return;
                });
                if (!text.isHasPlaceholders()) {
                    if (text instanceof SidebarLineAnimated) {
                        final String[] lines = ((SidebarLineAnimated)text).getLines();
                        for (int length = lines.length, i = 0; i < length; ++i) {
                            if (SidebarManager.getPapiSupport().hasPlaceholders(lines[i])) {
                                text.setHasPlaceholders(true);
                                break;
                            }
                        }
                    }
                    else if (SidebarManager.getPapiSupport().hasPlaceholders(text.getLine())) {
                        text.setHasPlaceholders(true);
                    }
                }
            }
            if (text.isHasPlaceholders()) {
                String content = text.getLine();
                for (final PlaceholderProvider placeholderProvider2 : Sidebar_v1_8_R3.this.placeholderProviders) {
                    if (content.contains(placeholderProvider2.getPlaceholder())) {
                        content = content.replace(placeholderProvider2.getPlaceholder(), placeholderProvider2.getReplacement());
                    }
                }
                this.setContent(content);
            }
            else {
                this.setContent(text.getLine());
            }
        }
        
        private void setText(@NotNull final SidebarLine text) {
            if (!text.isHasPlaceholders()) {
                if (text instanceof SidebarLineAnimated) {
                    final String[] lines = ((SidebarLineAnimated)text).getLines();
                    for (int length = lines.length, i = 0; i < length; ++i) {
                        if (SidebarManager.getPapiSupport().hasPlaceholders(lines[i])) {
                            text.setHasPlaceholders(true);
                            break;
                        }
                    }
                }
                else if (SidebarManager.getPapiSupport().hasPlaceholders(text.getLine())) {
                    text.setHasPlaceholders(true);
                }
            }
            this.text = text;
            this.setContent(text.getLine());
            this.sendUpdate();
        }
        
        private void sendCreate(@NotNull final PlayerConnection playerConnection) {
            playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this.team, 0));
            playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore((ScoreboardScore)this));
        }
        
        private void sendCreate() {
            final Packet packet;
            final Packet packet2;
            Sidebar_v1_8_R3.this.players.forEach(playerConnection -> {
                packet = (Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this.team, 0);
                packet2 = (Packet)new PacketPlayOutScoreboardScore((ScoreboardScore)this);
                playerConnection.sendPacket(packet);
                playerConnection.sendPacket(packet2);
            });
        }
        
        private void remove() {
            Sidebar_v1_8_R3.this.lines.remove(this);
            final Packet packet;
            final Packet packet2;
            Sidebar_v1_8_R3.this.players.forEach(playerConnection -> {
                packet = (Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this.team, 1);
                packet2 = (Packet)new PacketPlayOutScoreboardScore(this.getPlayerName(), (ScoreboardObjective)Sidebar_v1_8_R3.this.sidebarObjective);
                playerConnection.sendPacket(packet);
                playerConnection.sendPacket(packet2);
                return;
            });
            Sidebar_v1_8_R3.this.availableColors.add(this.getColor());
            this.text = null;
            this.team = null;
            this.prefix = null;
            this.suffix = null;
        }
        
        @Contract(pure = true)
        private void setContent(@NotNull String replacePlaceholders) {
            if (!Sidebar_v1_8_R3.this.players.isEmpty()) {
                replacePlaceholders = SidebarManager.getPapiSupport().replacePlaceholders((Player)Sidebar_v1_8_R3.this.players.get(0).getPlayer(), replacePlaceholders);
            }
            if (replacePlaceholders.length() > 16) {
                this.prefix = replacePlaceholders.substring(0, 16);
                if (this.prefix.charAt(15) == '§') {
                    this.prefix = replacePlaceholders.substring(0, 15);
                    this.setSuffix(replacePlaceholders.substring(15));
                }
                else {
                    this.setSuffix(replacePlaceholders.substring(16));
                }
            }
            else {
                this.prefix = replacePlaceholders;
                this.suffix = "";
            }
        }
        
        public void setSuffix(@NotNull String string) {
            if (string.isEmpty()) {
                this.suffix = "";
                return;
            }
            string = ChatColor.getLastColors(this.prefix) + string;
            this.suffix = ((string.length() > 16) ? string.substring(0, 16) : string);
        }
        
        private void sendUpdate() {
            Sidebar_v1_8_R3.this.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this.team, 2)));
        }
        
        public int compareTo(@NotNull final ScoreLine scoreLine) {
            return Integer.compare(this.score, scoreLine.score);
        }
        
        public void setScore(final int score) {
            this.score = score;
            Sidebar_v1_8_R3.this.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore((ScoreboardScore)this)));
        }
        
        public int getScore() {
            return this.score;
        }
        
        public void updateForList(final List<EntityHuman> list) {
        }
        
        public void addScore(final int n) {
        }
        
        public void incrementScore() {
        }
        
        public String getColor() {
            return (this.team.getName().charAt(0) == '§') ? this.team.getName() : ('§' + this.team.getName());
        }
        
        private class TeamLine extends ScoreboardTeam
        {
            public TeamLine(final String s) {
                super((Scoreboard)null, s);
                this.getPlayerNameSet().add(s);
            }
            
            public void setPrefix(final String s) {
            }
            
            public String getPrefix() {
                return ScoreLine.this.prefix;
            }
            
            public void setSuffix(final String s) {
            }
            
            public String getSuffix() {
                return ScoreLine.this.suffix;
            }
            
            public void setAllowFriendlyFire(final boolean b) {
            }
            
            public void setCanSeeFriendlyInvisibles(final boolean b) {
            }
            
            public void setNameTagVisibility(final ScoreboardTeamBase.EnumNameTagVisibility enumNameTagVisibility) {
            }
        }
    }
}
