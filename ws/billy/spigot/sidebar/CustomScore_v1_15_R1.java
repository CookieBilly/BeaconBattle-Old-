

package ws.billy.spigot.sidebar;

import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_15_R1.ScoreboardServer;
import org.jetbrains.annotations.NotNull;

public class CustomScore_v1_15_R1
{
    public static void sendScore(@NotNull final Sidebar_v1_15_R1 sidebar_v1_15_R1, final String s, final int n) {
        if (sidebar_v1_15_R1.healthObjective == null) {
            return;
        }
        sidebar_v1_15_R1.players.forEach(playerConnection -> playerConnection.sendPacket((Packet)new PacketPlayOutScoreboardScore(ScoreboardServer.Action.CHANGE, sidebar_v1_15_R1.healthObjective.getName(), s, n)));
    }
}
