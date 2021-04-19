

package ws.billy.bedwars.sidebar;

import java.util.Iterator;

public class SidebarLifeRefresh implements Runnable
{
    @Override
    public void run() {
        for (final BeaconBattleScoreboard BeaconBattleScoreboard : BeaconBattleScoreboard.getScoreboards().values()) {
            if (BeaconBattleScoreboard.getArena() != null) {
                BeaconBattleScoreboard.getHandle().refreshHealthAnimation();
            }
        }
    }
}
