

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;
import ws.billy.bedwars.configuration.ArenaConfig;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class ArenaList extends SubCommand
{
    public ArenaList(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(3);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + ((this.getArenas().size() == 0) ? " §c(0 set)" : (" §a(" + this.getArenas().size() + " set)")), "§fShow available arenas", "/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (this.getArenas().size() == 0) {
            player.sendMessage("§6 \u25aa §7You didn't set any arena yet!");
            return true;
        }
        player.sendMessage("§6 \u25aa §7Available arenas:");
        for (final String str : this.getArenas()) {
            final String str2 = (Arena.getArenaByName(str) == null) ? "§cDisabled" : "§aEnabled";
            String string = "Default";
            final ArenaConfig arenaConfig = new ArenaConfig((Plugin) ws.billy.bedwars.BeaconBattle.plugin, str, ws.billy.bedwars.BeaconBattle.plugin.getDataFolder().getPath() + "/Arenas");
            if (arenaConfig.getYml().get("group") != null) {
                string = arenaConfig.getYml().getString("group");
            }
            int size = 0;
            if (arenaConfig.getYml().get("Team") != null) {
                size = arenaConfig.getYml().getConfigurationSection("Team").getKeys(false).size();
            }
            player.sendMessage("§6 \u25aa    §f" + str + " §7[" + str2 + "§7] [§eGroup: §d" + string + "§7] [§eTeams: §d" + size + "§7]");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @NotNull
    private List<String> getArenas() {
        final ArrayList<String> list = new ArrayList<String>();
        final File file = new File(ws.billy.bedwars.BeaconBattle.plugin.getDataFolder(), "/Arenas");
        if (file.exists()) {
            for (final File file2 : Objects.requireNonNull(file.listFiles())) {
                if (file2.isFile() && file2.getName().contains(".yml")) {
                    list.add(file2.getName().replace(".yml", ""));
                }
            }
        }
        return list;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        if (commandSender instanceof Player) {
            final Player player = (Player)commandSender;
            if (Arena.isInArena(player)) {
                return false;
            }
            if (SetupSession.isInSetupSession(player.getUniqueId())) {
                return false;
            }
        }
        return this.hasPermission(commandSender);
    }
}
