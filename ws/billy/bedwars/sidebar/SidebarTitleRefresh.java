

package ws.billy.bedwars.sidebar;

import java.util.Iterator;

public class SidebarTitleRefresh implements Runnable
{
    @Override
    public void run() {
        final Iterator<BeaconBattleScoreboard> iterator = BeaconBattleScoreboard.getScoreboards().values().iterator();
        while (iterator.hasNext()) {
            iterator.next().getHandle().refreshTitle();
        }
    }
}
