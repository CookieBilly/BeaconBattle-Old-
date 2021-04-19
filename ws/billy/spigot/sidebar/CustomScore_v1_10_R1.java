

package ws.billy.spigot.sidebar;

import net.minecraft.server.v1_10_R1.PlayerConnection;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutScoreboardScore;
import org.jetbrains.annotations.NotNull;
import net.minecraft.server.v1_10_R1.Scoreboard;
import net.minecraft.server.v1_10_R1.ScoreboardObjective;
import net.minecraft.server.v1_10_R1.ScoreboardScore;

public class CustomScore_v1_10_R1 extends ScoreboardScore
{
    private int score;
    
    public CustomScore_v1_10_R1(final ScoreboardObjective scoreboardObjective, final String s, final int score) {
        super((Scoreboard)null, scoreboardObjective, s);
        this.score = score;
    }
    
    public void setScore(final int n) {
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void addScore(final int n) {
    }
    
    public void incrementScore() {
    }
    
    public static void sendScore(@NotNull final Sidebar_v1_10_R1 sidebar_v1_10_R1, final String s, final int n) {
        if (sidebar_v1_10_R1.healthObjective == null) {
            return;
        }
        final PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore((ScoreboardScore)new CustomScore_v1_10_R1(sidebar_v1_10_R1.healthObjective, s, n));
        sidebar_v1_10_R1.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)packetPlayOutScoreboardScore));
    }
}
