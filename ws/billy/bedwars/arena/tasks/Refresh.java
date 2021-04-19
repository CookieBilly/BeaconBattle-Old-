

package ws.billy.bedwars.arena.tasks;

import java.util.Iterator;
import ws.billy.bedwars.api.entity.Despawnable;
import ws.billy.bedwars.BeaconBattle;

public class Refresh implements Runnable
{
    @Override
    public void run() {
        final Iterator<Despawnable> iterator = BeaconBattle.nms.getDespawnablesList().values().iterator();
        while (iterator.hasNext()) {
            iterator.next().refresh();
        }
    }
}
