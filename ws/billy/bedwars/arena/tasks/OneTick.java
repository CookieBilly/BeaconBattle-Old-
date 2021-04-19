

package ws.billy.bedwars.arena.tasks;

import java.util.Iterator;
import ws.billy.bedwars.arena.OreGenerator;

public class OneTick implements Runnable
{
    @Override
    public void run() {
        final Iterator<OreGenerator> iterator = OreGenerator.getRotation().iterator();
        while (iterator.hasNext()) {
            iterator.next().rotate();
        }
    }
}
