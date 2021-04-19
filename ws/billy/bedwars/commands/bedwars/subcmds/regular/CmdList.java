

package ws.billy.bedwars.commands.bedwars.subcmds.regular;

import java.util.List;
import java.util.Iterator;

import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ws.billy.bedwars.arena.Arena;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.server.SetupType;
import org.bukkit.ChatColor;
import ws.billy.bedwars.api.arena.team.TeamColor;
import java.util.Objects;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class CmdList extends SubCommand
{
    public CmdList(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setPriority(11);
        this.showInList(true);
        this.setDisplayInfo(Misc.msgHoverClick("§6 \u25aa §7/" + MainCommand.getInstance().getName() + " " + this.getSubCommandName() + "         §8 - §e view player cmds", "§fView player commands.", "/" + this.getParent().getName() + " " + this.getSubCommandName(), ClickEvent.Action.RUN_COMMAND));
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        if (SetupSession.isInSetupSession(player.getUniqueId())) {
            final SetupSession session = SetupSession.getSession(player.getUniqueId());
            Objects.requireNonNull(session).getConfig().reload();
            final boolean b = session.getConfig().getYml().get("waiting.Loc") != null;
            final boolean b2 = session.getConfig().getYml().get("waiting.Pos1") != null;
            final boolean b3 = session.getConfig().getYml().get("waiting.Pos2") != null;
            final boolean b4 = b2 && b3;
            final StringBuilder sb = new StringBuilder();
            final StringBuilder obj = new StringBuilder();
            final StringBuilder obj2 = new StringBuilder();
            final StringBuilder obj3 = new StringBuilder();
            final StringBuilder obj4 = new StringBuilder();
            final StringBuilder obj5 = new StringBuilder();
            final StringBuilder obj6 = new StringBuilder();
            int i = 0;
            if (session.getConfig().getYml().get("Team") != null) {
                for (final String s : session.getConfig().getYml().getConfigurationSection("Team").getKeys(true)) {
                    if (session.getConfig().getYml().get("Team." + s + ".Color") == null) {
                        continue;
                    }
                    final ChatColor chatColor = TeamColor.getChatColor(session.getConfig().getYml().getString("Team." + s + ".Color"));
                    if (session.getConfig().getYml().get("Team." + s + ".Spawn") == null) {
                        obj5.append(chatColor).append("\u258b");
                        sb.append(chatColor).append(s).append(" ");
                    }
                    if (session.getConfig().getYml().get("Team." + s + ".Bed") == null) {
                        obj.append(chatColor).append("\u258b");
                    }
                    if (session.getConfig().getYml().get("Team." + s + ".Shop") == null) {
                        obj2.append(chatColor).append("\u258b");
                    }
                    if (session.getConfig().getYml().get("Team." + s + "." + "kill-drops-loc") == null) {
                        obj3.append(chatColor).append("\u258b");
                    }
                    if (session.getConfig().getYml().get("Team." + s + ".Upgrade") == null) {
                        obj4.append(chatColor).append("\u258b");
                    }
                    if (session.getConfig().getYml().get("Team." + s + ".Iron") == null || session.getConfig().getYml().get("Team." + s + ".Gold") == null) {
                        obj6.append(chatColor).append("\u258b");
                    }
                    ++i;
                }
            }
            int size = 0;
            int size2 = 0;
            if (session.getConfig().getYml().get("generator.Emerald") != null) {
                size = session.getConfig().getYml().getStringList("generator.Emerald").size();
            }
            if (session.getConfig().getYml().get("generator.Diamond") != null) {
                size2 = session.getConfig().getYml().getStringList("generator.Diamond").size();
            }
            String s2 = ChatColor.RED + "(NOT SET)";
            String str;
            if (b2 && !b3) {
                str = ChatColor.RED + "(POS 2 NOT SET)";
            }
            else if (!b2 && b3) {
                str = ChatColor.RED + "(POS 1 NOT SET)";
            }
            else if (b2) {
                str = ChatColor.GREEN + "(SET)";
            }
            else {
                str = ChatColor.GRAY + "(NOT SET) " + ChatColor.ITALIC + "OPTIONAL";
            }
            final String string = session.getConfig().getYml().getString("group");
            if (string != null && !string.equalsIgnoreCase("default")) {
                s2 = ChatColor.GREEN + "(" + string + ")";
            }
            final int int1 = session.getConfig().getInt("maxInTeam");
            final String string2 = session.dot() + (b ? ChatColor.STRIKETHROUGH : "") + "setWaitingSpawn" + ChatColor.RESET + " " + (b ? (ChatColor.GREEN + "(SET)") : (ChatColor.RED + "(NOT SET)"));
            final String string3 = session.dot() + (b4 ? ChatColor.STRIKETHROUGH : "") + "waitingPos 1/2" + ChatColor.RESET + " " + str;
            final String string4 = session.dot() + ((obj5.length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setSpawn <teamName>" + ChatColor.RESET + " " + ((obj5.length() == 0) ? (ChatColor.GREEN + "(ALL SET)") : (ChatColor.RED + "(Remaining: " + (Object)obj5 + ChatColor.RED + ")"));
            final String string5 = session.dot() + ((obj.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setBed" + ChatColor.RESET + " " + ((obj.length() == 0) ? (ChatColor.GREEN + "(ALL SET)") : (ChatColor.RED + "(Remaining: " + (Object)obj + ChatColor.RED + ")"));
            final String string6 = session.dot() + ((obj2.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setShop" + ChatColor.RESET + " " + ((obj2.length() == 0) ? (ChatColor.GREEN + "(ALL SET)") : (ChatColor.RED + "(Remaining: " + (Object)obj2 + ChatColor.RED + ")"));
            final String string7 = session.dot() + ((obj3.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setKillDrops" + ChatColor.RESET + " " + ((obj2.length() == 0) ? (ChatColor.GREEN + "(ALL SET)") : (ChatColor.RED + "(Remaining: " + (Object)obj3 + ChatColor.RED + ")"));
            final String string8 = session.dot() + ((obj4.toString().length() == 0) ? ChatColor.STRIKETHROUGH : "") + "setUpgrade" + ChatColor.RESET + " " + ((obj4.length() == 0) ? (ChatColor.GREEN + "(ALL SET)") : (ChatColor.RED + "(Remaining: " + (Object)obj4 + ChatColor.RED + ")"));
            final String string9 = session.dot() + "addGenerator " + ((obj6.toString().length() == 0) ? "" : (ChatColor.RED + "(Remaining: " + (Object)obj6 + ChatColor.RED + ") ")) + ChatColor.YELLOW + "(" + ChatColor.DARK_GREEN + "E" + size + " " + ChatColor.AQUA + "D" + size2 + ChatColor.YELLOW + ")";
            final String string10 = session.dot() + ((session.getConfig().getYml().get("spectator-loc") == null) ? "" : ChatColor.STRIKETHROUGH) + "setSpectSpawn" + ChatColor.RESET + " " + ((session.getConfig().getYml().get("spectator-loc") == null) ? (ChatColor.RED + "(NOT SET)") : (ChatColor.GRAY + "(SET)"));
            commandSender.sendMessage("");
            commandSender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + MainCommand.getDot() + ChatColor.GOLD + ws.billy.bedwars.BeaconBattle.plugin.getDescription().getName() + " v" + ws.billy.bedwars.BeaconBattle.plugin.getDescription().getVersion() + ChatColor.GRAY + '-' + " " + ChatColor.GREEN + session.getWorldName() + " commands");
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string2, ChatColor.WHITE + "Set the place where players have\n" + ChatColor.WHITE + "to wait before the game starts.", "/" + this.getParent().getName() + " setWaitingSpawn", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string3, ChatColor.WHITE + "Make it so the waiting lobby will disappear at start.\n" + ChatColor.WHITE + "Select it as a world edit region.", "/" + this.getParent().getName() + " waitingPos ", ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) {
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string10, ChatColor.WHITE + "Set where to spawn spectators.", "/" + this.getParent().getName() + " setSpectSpawn", ClickEvent.Action.RUN_COMMAND));
            }
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "autoCreateTeams " + ChatColor.YELLOW + "(auto detect)", ChatColor.WHITE + "Create teams based on islands colors.", "/" + this.getParent().getName() + " autoCreateTeams", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "createTeam <name> <color> " + ChatColor.YELLOW + "(" + i + " CREATED)", ChatColor.WHITE + "Create a team.", "/" + this.getParent().getName() + " createTeam ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "removeTeam <name>", ChatColor.WHITE + "Remove a team by name.", "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " removeTeam ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string4, ChatColor.WHITE + "Set a team spawn.\n" + ChatColor.WHITE + "Teams without a spawn set:\n" + sb.toString(), "/" + this.getParent().getName() + " setSpawn ", ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string5, ChatColor.WHITE + "Set a team's bed location.\n" + ChatColor.WHITE + "You don't have to specify the team name.", "/" + this.getParent().getName() + " setBed", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string6, ChatColor.WHITE + "Set a team's NPC.\n" + ChatColor.WHITE + "You don't have to specify the team name.\n" + ChatColor.WHITE + "It will be spawned only when the game starts.", "/" + this.getParent().getName() + " setShop", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string8, ChatColor.WHITE + "Set a team's upgrade NPC.\n" + ChatColor.WHITE + "You don't have to specify the team name.\n" + ChatColor.WHITE + "It will be spawned only when the game starts.", "/" + this.getParent().getName() + " setUpgrade", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) {
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string7, ChatColor.WHITE + "Set a the location where to drop\n" + ChatColor.WHITE + "enemy items after you kill them.", "/" + this.getParent().getName() + " setKillDrops ", ClickEvent.Action.SUGGEST_COMMAND));
            }
            final String string11 = ((session.getSetupType() == SetupType.ADVANCED) ? (ChatColor.WHITE + "Add a generator spawn point.\n" + ChatColor.YELLOW + "/" + this.getParent().getName() + " addGenerator <Iron/ Gold/ Emerald, Diamond>") : (ChatColor.WHITE + "Add a generator spawn point.\n" + ChatColor.YELLOW + "Stay in on a team island to set a team generator")) + "\n" + ChatColor.WHITE + "Stay on a diamond block to set the diamond generator.\n" + ChatColor.WHITE + "Stay on a emerald block to set an emerald generator.";
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(string9, string11, "/" + this.getParent().getName() + " addGenerator ", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "removeGenerator", string11, "/" + this.getParent().getName() + " removeGenerator", (session.getSetupType() == SetupType.ASSISTED) ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND));
            if (session.getSetupType() == SetupType.ADVANCED) {
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "setMaxInTeam <int> (IS SET TO " + int1 + ")", ChatColor.WHITE + "Set the max team size.", "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " setMaxInTeam ", ClickEvent.Action.SUGGEST_COMMAND));
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "arenaGroup " + s2, ChatColor.WHITE + "Set the arena group.", "/" + ws.billy.bedwars.BeaconBattle.mainCmd + " arenaGroup ", ClickEvent.Action.SUGGEST_COMMAND));
            }
            else {
                player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "setType <type> " + s2, ChatColor.WHITE + "Add the arena to a group.", "/" + this.getParent().getName() + " setType", ClickEvent.Action.RUN_COMMAND));
            }
            player.spigot().sendMessage((BaseComponent)Misc.msgHoverClick(session.dot() + "save", ChatColor.WHITE + "Save arena and go back to lobby", "/" + this.getParent().getName() + " save", ClickEvent.Action.SUGGEST_COMMAND));
        }
        else {
            final TextComponent textComponent = new TextComponent(ChatColor.BLUE + "" + ChatColor.BOLD + MainCommand.getDot() + " " + ChatColor.GOLD + ws.billy.bedwars.BeaconBattle.plugin.getName() + " " + ChatColor.GRAY + "v" + ws.billy.bedwars.BeaconBattle.plugin.getDescription().getVersion() + " by CookieBillu");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ws.billy.bedwars.BeaconBattle.link));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Arenas: " + ((Arena.getArenas().size() == 0) ? (ChatColor.RED + "0") : (ChatColor.GREEN + "" + Arena.getArenas().size()))).create()));
            ((Player)commandSender).spigot().sendMessage((BaseComponent)textComponent);
            final Iterator<String> iterator2 = Language.getList((Player)commandSender, Messages.COMMAND_MAIN).iterator();
            while (iterator2.hasNext()) {
                commandSender.sendMessage((String)iterator2.next());
            }
        }
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
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
