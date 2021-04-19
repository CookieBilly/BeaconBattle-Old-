

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.SetupSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdLang extends SubCommand
{
    public CmdLang(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(18);
        this.showInList(false);
        this.setDisplayInfo(MainCommand.createTC("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName(), "/" + this.getParent().getName() + " " + this.getSubCommandName(), "§fChange your language."));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (Arena.getArenaByPlayer(player) != null) {
            return false;
        }
        if (array.length == 0) {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_HEADER));
            for (final Language language : Language.getLanguages()) {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_LIST_FORMAT).replace("{iso}", language.getIso()).replace("{name}", language.getLangName()));
            }
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_USAGE));
            return true;
        }
        if (Language.isLanguageExist(array[0])) {
            if (Arena.getArenaByPlayer(player) == null) {
                Language.setPlayerLanguage(player, array[0], false);
                final Player player2;
                Bukkit.getScheduler().runTaskLater((Plugin) ws.billy.bedwars.BeaconBattle.plugin, () -> player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_LANG_SELECTED_SUCCESSFULLY)), 10L);
            }
            else {
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_USAGE_DENIED));
            }
        }
        else {
            player.sendMessage(Language.getMsg(player, Messages.COMMAND_LANG_SELECTED_NOT_EXIST));
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator<Language> iterator = Language.getLanguages().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().getIso());
        }
        return list;
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
