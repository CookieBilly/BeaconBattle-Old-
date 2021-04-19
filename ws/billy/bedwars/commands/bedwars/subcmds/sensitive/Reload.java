

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive;

import ws.billy.bedwars.arena.SetupSession;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.BeaconBattle;
import java.util.List;

import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class Reload extends SubCommand
{
    public Reload(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(11);
        this.showInList(true);
        this.setPermission(Permissions.PERMISSION_RELOAD);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + this.getParent().getName() + " " + this.getSubCommandName() + "       §8 - §ereload messages", "§fRealod messages.\n§cNot recommended!", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (!MainCommand.isLobbySet(player)) {
            return true;
        }
        for (final Language language : Language.getLanguages()) {
            language.reload();
            player.sendMessage("§6 \u25aa §7" + language.getLangName() + " reloaded!");
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        return !Arena.isInArena(player) && !SetupSession.isInSetupSession(player.getUniqueId()) && this.hasPermission(commandSender);
    }
}
