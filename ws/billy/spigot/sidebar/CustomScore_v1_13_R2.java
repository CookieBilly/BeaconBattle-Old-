

package ws.billy.spigot.sidebar;

import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_13_R2.ScoreboardServer;
import org.jetbrains.annotations.NotNull;

public class CustomScore_v1_13_R2
{
    public static void sendScore(@NotNull final Sidebar_v1_13_R2 sidebar_v1_13_R2, final String s, final int n) {
        if (sidebar_v1_13_R2.healthObjective == null) {
            return;
        }
        sidebar_v1_13_R2.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, sidebar_v1_13_R2.healthObjective.getName(), s, n)));
    }
}
