

package ws.billy.spigot.sidebar;

import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.Packet;
import java.util.Collection;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardTeam;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import javax.annotation.Nullable;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.Scoreboard;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.v1_13_R2.ScoreboardTeamBase;
import org.bukkit.entity.Player;
import java.util.LinkedList;
import net.minecraft.server.v1_13_R2.ScoreboardTeam;

public class PlayerList_v1_13_R2 extends ScoreboardTeam implements PlayerList
{
    private SidebarLine prefix;
    private SidebarLine suffix;
    private Sidebar_v1_13_R2 sidebar;
    private LinkedList<PlaceholderProvider> placeholderProviders;
    private Player player;
    private ScoreboardTeamBase.EnumNameTagVisibility nameTagVisibility;
    
    public PlayerList_v1_13_R2(@NotNull final Sidebar_v1_13_R2 sidebar, @NotNull final Player player, final SidebarLine prefix, final SidebarLine suffix) {
        super((Scoreboard)null, player.getName());
        this.placeholderProviders = new LinkedList<PlaceholderProvider>();
        this.player = null;
        this.nameTagVisibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
        this.suffix = suffix;
        this.prefix = prefix;
        this.sidebar = sidebar;
        if (SidebarManager.getPapiSupport().isSupported()) {
            this.player = player;
        }
        this.getPlayerNameSet().add(player.getName());
    }
    
    public void setPrefix(@Nullable final IChatBaseComponent chatBaseComponent) {
    }
    
    public IChatBaseComponent getPrefix() {
        String s = this.prefix.getLine();
        for (final PlaceholderProvider placeholderProvider : this.placeholderProviders) {
            if (s.contains(placeholderProvider.getPlaceholder())) {
                s = s.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
            }
        }
        String s2 = SidebarManager.getPapiSupport().replacePlaceholders(this.player, s);
        if (s2.length() > 16) {
            s2 = s2.substring(0, 16);
        }
        return (IChatBaseComponent)new ChatComponentText(s2);
    }
    
    public void setSuffix(@Nullable final IChatBaseComponent chatBaseComponent) {
    }
    
    public IChatBaseComponent getSuffix() {
        String s = this.suffix.getLine();
        for (final PlaceholderProvider placeholderProvider : this.placeholderProviders) {
            if (s.contains(placeholderProvider.getPlaceholder())) {
                s = s.replace(placeholderProvider.getPlaceholder(), placeholderProvider.getReplacement());
            }
        }
        String s2 = SidebarManager.getPapiSupport().replacePlaceholders(this.player, s);
        if (s2.length() > 16) {
            s2 = s2.substring(0, 16);
        }
        return (IChatBaseComponent)new ChatComponentText(s2);
    }
    
    public void setAllowFriendlyFire(final boolean b) {
    }
    
    public void setCanSeeFriendlyInvisibles(final boolean b) {
    }
    
    public void setNameTagVisibility(final ScoreboardTeamBase.EnumNameTagVisibility nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }
    
    public ScoreboardTeamBase.EnumNameTagVisibility getNameTagVisibility() {
        return this.nameTagVisibility;
    }
    
    public void setPrefix(final SidebarLine prefix) {
        this.prefix = prefix;
    }
    
    public void setSuffix(final SidebarLine suffix) {
        this.suffix = suffix;
    }
    
    public void addPlayer(final String o) {
        this.getPlayerNameSet().add(o);
        CustomScore_v1_13_R2.sendScore(this.sidebar, o, 20);
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this, (Collection)Collections.singleton(o), 3)));
    }
    
    public void removePlayer(final String o) {
        this.getPlayerNameSet().remove(o);
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this, (Collection)Collections.singleton(o), 4)));
    }
    
    public void refreshAnimations() {
    }
    
    public void addPlaceholderProvider(final PlaceholderProvider placeholderProvider) {
        this.placeholderProviders.remove(placeholderProvider);
        this.placeholderProviders.add(placeholderProvider);
        this.placeholderProviders.forEach(placeholderProvider2 -> {
            if (this.prefix.getLine().contains(placeholderProvider2.getPlaceholder())) {
                this.prefix.setHasPlaceholders(true);
            }
            if (this.suffix.getLine().contains(placeholderProvider2.getPlaceholder())) {
                this.suffix.setHasPlaceholders(true);
            }
        });
    }
    
    public void hideNameTag() {
        this.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        this.sendUpdate();
    }
    
    public void showNameTag() {
        this.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);
        this.sendUpdate();
    }
    
    public void removePlaceholderProvider(final String anotherString) {
        this.placeholderProviders.removeIf(placeholderProvider -> placeholderProvider.getPlaceholder().equalsIgnoreCase(anotherString));
    }
    
    public void sendCreate(@NotNull final PlayerConnection playerConnection) {
        playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this, 0));
    }
    
    public void sendUpdate() {
        this.sidebar.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this, 2)));
    }
    
    public void sendRemove(@NotNull final PlayerConnection playerConnection) {
        playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardTeam((ScoreboardTeam)this, 1));
    }
}
