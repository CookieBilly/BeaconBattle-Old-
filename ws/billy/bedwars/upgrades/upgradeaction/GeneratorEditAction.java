

package ws.billy.bedwars.upgrades.upgradeaction;

import java.util.Iterator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import ws.billy.bedwars.arena.OreGenerator;
import org.bukkit.Location;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import ws.billy.bedwars.api.arena.generator.GeneratorType;
import java.util.List;
import ws.billy.bedwars.api.arena.generator.IGenerator;
import java.util.ArrayList;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.upgrades.UpgradeAction;

public class GeneratorEditAction implements UpgradeAction
{
    private int amount;
    private int delay;
    private int limit;
    private ApplyType type;
    
    public GeneratorEditAction(final ApplyType type, final int amount, final int delay, final int limit) {
        this.type = type;
        this.amount = amount;
        this.delay = delay;
        this.limit = limit;
    }
    
    @Override
    public void onBuy(final ITeam team) {
        Object o = new ArrayList<IGenerator>();
        if (this.type == ApplyType.IRON) {
            o = team.getGenerators().stream().filter(generator -> generator.getType() == GeneratorType.IRON).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        }
        else if (this.type == ApplyType.GOLD) {
            o = team.getGenerators().stream().filter(generator2 -> generator2.getType() == GeneratorType.GOLD).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        }
        else if (this.type == ApplyType.EMERALD) {
            if (!team.getArena().getConfig().getArenaLocations("Team." + team.getName() + ".Emerald").isEmpty()) {
                final Iterator<Location> iterator = team.getArena().getConfig().getArenaLocations("Team." + team.getName() + ".Emerald").iterator();
                while (iterator.hasNext()) {
                    final OreGenerator oreGenerator = new OreGenerator(iterator.next(), team.getArena(), GeneratorType.CUSTOM, team);
                    oreGenerator.setOre(new ItemStack(Material.EMERALD));
                    oreGenerator.setType(GeneratorType.EMERALD);
                    team.getGenerators().add(oreGenerator);
                    ((List<OreGenerator>)o).add(oreGenerator);
                }
            }
            else {
                final OreGenerator oreGenerator2 = new OreGenerator(team.getGenerators().get(0).getLocation(), team.getArena(), GeneratorType.CUSTOM, team);
                oreGenerator2.setOre(new ItemStack(Material.EMERALD));
                oreGenerator2.setType(GeneratorType.EMERALD);
                team.getGenerators().add(oreGenerator2);
                ((List<OreGenerator>)o).add(oreGenerator2);
            }
        }
        for (final IGenerator generator3 : o) {
            generator3.setAmount(this.amount);
            generator3.setDelay(this.delay);
            generator3.setSpawnLimit(this.limit);
        }
    }
    
    public enum ApplyType
    {
        IRON, 
        GOLD, 
        EMERALD;
    }
}
