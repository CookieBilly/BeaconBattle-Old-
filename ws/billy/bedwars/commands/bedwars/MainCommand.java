

package ws.billy.bedwars.commands.bedwars;

import java.util.ArrayList;
import org.bukkit.Location;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.util.Iterator;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetKillDropsLoc;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdUpgrades;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdTpStaff;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.NPC;
import ws.billy.bedwars.support.citizens.JoinNPC;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.Save;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetType;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.RemoveGenerator;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.AddGenerator;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetUpgrade;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetShop;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetBed;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetSpawn;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetMaxInTeam;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.RemoveTeam;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.WaitingPos;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.CreateTeam;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetSpectatorPos;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.SetWaitingSpawn;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup.AutoCreateTeams;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdList;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.Reload;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.Level;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.Build;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.ArenaGroup;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.CloneArena;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.DisableArena;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.EnableArena;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.DelArena;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.ArenaList;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.SetupArena;
import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.SetLobby;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdStart;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdStats;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdGUI;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdTeleporter;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdLang;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdLeave;
import ws.billy.bedwars.commands.bedwars.subcmds.regular.CmdJoin;
import java.util.Arrays;
import ws.billy.bedwars.api.command.SubCommand;
import java.util.List;
import ws.billy.bedwars.api.command.ParentCommand;
import org.bukkit.command.defaults.BukkitCommand;

public class MainCommand extends BukkitCommand implements ParentCommand
{
    private static List<SubCommand> subCommandList;
    private static MainCommand instance;
    public static char dot;
    
    public MainCommand(final String s) {
        super(s);
        this.setAliases((List)Arrays.asList("BeaconBattle", "BeaconBattle1058"));
        MainCommand.instance = this;
        new CmdJoin(this, "join");
        new CmdLeave(this, "leave");
        new CmdLang(this, "lang");
        new CmdTeleporter(this, "teleporter");
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            new CmdGUI(this, "gui");
        }
        new CmdStats(this, "stats");
        new CmdStart(this, "forceStart");
        new CmdStart(this, "start");
        if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
            new SetLobby(this, "setLobby");
        }
        new SetupArena(this, "setupArena");
        new ArenaList(this, "arenaList");
        new DelArena(this, "delArena");
        new EnableArena(this, "enableArena");
        new DisableArena(this, "disableArena");
        new CloneArena(this, "cloneArena");
        new ArenaGroup(this, "arenaGroup");
        new Build(this, "build");
        new Level(this, "level");
        new Reload(this, "reload");
        new CmdList(this, "cmds");
        new AutoCreateTeams(this, "autoCreateTeams");
        new SetWaitingSpawn(this, "setWaitingSpawn");
        new SetSpectatorPos(this, "setSpectSpawn");
        new CreateTeam(this, "createTeam");
        new WaitingPos(this, "waitingPos");
        new RemoveTeam(this, "removeTeam");
        new SetMaxInTeam(this, "setMaxInTeam");
        new SetSpawn(this, "setSpawn");
        new SetBed(this, "setBed");
        new SetShop(this, "setShop");
        new SetUpgrade(this, "setUpgrade");
        new AddGenerator(this, "addGenerator");
        new RemoveGenerator(this, "removeGenerator");
        new SetType(this, "setType");
        new Save(this, "save");
        if (JoinNPC.isCitizensSupport() && BeaconBattle.getServerType() != ServerType.BUNGEE) {
            new NPC(this, "npc");
        }
        new CmdTpStaff(this, "tp");
        new CmdUpgrades(this, "upgradesmenu");
        new SetKillDropsLoc(this, "setKillDrops");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] original) {
        if (original.length == 0) {
            if (commandSender.isOp() || commandSender.hasPermission(BeaconBattle.mainCmd + ".*")) {
                if (commandSender instanceof Player) {
                    if (SetupSession.isInSetupSession(((Player)commandSender).getUniqueId())) {
                        Bukkit.dispatchCommand(commandSender, this.getName() + " cmds");
                    }
                    else {
                        commandSender.sendMessage("");
                        commandSender.sendMessage("§8§l" + MainCommand.dot + " §6" + BeaconBattle.plugin.getDescription().getName() + " v" + BeaconBattle.plugin.getDescription().getVersion() + " §7- §c Admin Commands");
                        commandSender.sendMessage("");
                        this.sendSubCommands((Player)commandSender);
                    }
                }
                else {
                    commandSender.sendMessage("§f   bw safemode §eenable/ disable");
                }
            }
            else {
                if (commandSender instanceof ConsoleCommandSender) {
                    commandSender.sendMessage("§fNo console commands available atm.");
                    return true;
                }
                Bukkit.dispatchCommand(commandSender, BeaconBattle.mainCmd + " cmds");
            }
            return true;
        }
        boolean execute = false;
        for (final SubCommand subCommand : this.getSubCommands()) {
            if (subCommand.getSubCommandName().equalsIgnoreCase(original[0]) && subCommand.hasPermission(commandSender)) {
                execute = subCommand.execute(Arrays.copyOfRange(original, 1, original.length), commandSender);
            }
        }
        if (!execute) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(Language.getMsg((Player)commandSender, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            }
            else {
                commandSender.sendMessage(Language.getDefaultLanguage().m(Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
            }
        }
        return true;
    }
    
    public static boolean isArenaGroup(final String s) {
        if (BeaconBattle.config.getYml().get("arenaGroups") != null) {
            return BeaconBattle.config.getYml().getStringList("arenaGroups").contains(s);
        }
        return s.equalsIgnoreCase("default");
    }
    
    public static TextComponent createTC(final String s, final String s2, final String s3) {
        final TextComponent textComponent = new TextComponent(s);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, s2));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s3).create()));
        return textComponent;
    }
    
    public void addSubCommand(final SubCommand subCommand) {
        MainCommand.subCommandList.add(subCommand);
    }
    
    public void sendSubCommands(final Player player) {
        for (int i = 0; i <= 20; ++i) {
            for (final SubCommand subCommand : this.getSubCommands()) {
                if (subCommand.getPriority() == i && subCommand.isShow() && subCommand.canSee((CommandSender)player, BeaconBattle.getAPI())) {
                    player.spigot().sendMessage((BaseComponent)subCommand.getDisplayInfo());
                }
            }
        }
    }
    
    public List<String> tabComplete(final CommandSender commandSender, final String s, final String[] array, final Location location) {
        if (array.length == 1) {
            final ArrayList<String> list = new ArrayList<String>();
            for (final SubCommand subCommand : this.getSubCommands()) {
                if (subCommand.canSee(commandSender, BeaconBattle.getAPI())) {
                    list.add(subCommand.getSubCommandName());
                }
            }
            return list;
        }
        if (array.length == 2 && this.hasSubCommand(array[0]) && this.getSubCommand(array[0]).canSee(commandSender, BeaconBattle.getAPI())) {
            return this.getSubCommand(array[0]).getTabComplete();
        }
        return null;
    }
    
    public List<SubCommand> getSubCommands() {
        return MainCommand.subCommandList;
    }
    
    public static MainCommand getInstance() {
        return MainCommand.instance;
    }
    
    public static boolean isLobbySet(final Player player) {
        if (BeaconBattle.getServerType() == ServerType.BUNGEE) {
            return true;
        }
        if (BeaconBattle.config.getLobbyWorldName().isEmpty()) {
            if (player != null) {
                player.sendMessage("§c\u25aa §7You have to set the lobby location first!");
            }
            return false;
        }
        return true;
    }
    
    public boolean hasSubCommand(final String anotherString) {
        final Iterator<SubCommand> iterator = this.getSubCommands().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getSubCommandName().equalsIgnoreCase(anotherString)) {
                return true;
            }
        }
        return false;
    }
    
    public SubCommand getSubCommand(final String anotherString) {
        for (final SubCommand subCommand : this.getSubCommands()) {
            if (subCommand.getSubCommandName().equalsIgnoreCase(anotherString)) {
                return subCommand;
            }
        }
        return null;
    }
    
    public static char getDot() {
        return MainCommand.dot;
    }
    
    static {
        MainCommand.subCommandList = new ArrayList<SubCommand>();
        MainCommand.dot = '\u00fe';
    }
}
