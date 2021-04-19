

package ws.billy.bedwars.commands.party;

import java.util.Iterator;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.command.defaults.BukkitCommand;

public class PartyCommand extends BukkitCommand
{
    private static HashMap<UUID, UUID> partySessionRequest;
    
    public PartyCommand(final String s) {
        super(s);
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] array) {
        if (commandSender instanceof ConsoleCommandSender) {
            return true;
        }
        final Player player = (Player)commandSender;
        if (array.length == 0 || array[0].equalsIgnoreCase("help")) {
            this.sendPartyCmds(player);
            return true;
        }
        final String lowerCase = array[0].toLowerCase();
        switch (lowerCase) {
            case "invite": {
                if (array.length == 1) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_USAGE));
                    return true;
                }
                if (BeaconBattle.getParty().hasParty(player) && !BeaconBattle.getParty().isOwner(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
                    return true;
                }
                if (Bukkit.getPlayer(array[1]) == null || !Bukkit.getPlayer(array[1]).isOnline()) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE).replace("{player}", array[1]));
                    break;
                }
                if (player == Bukkit.getPlayer(array[1])) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_DENIED_CANNOT_INVITE_YOURSELF));
                    return true;
                }
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_SENT).replace("{player}", array[1]));
                final TextComponent textComponent = new TextComponent(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_SENT_TARGET_RECEIVE_MSG).replace("{player}", player.getName()));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + player.getName()));
                Bukkit.getPlayer(array[1]).spigot().sendMessage((BaseComponent)textComponent);
                if (PartyCommand.partySessionRequest.containsKey(player.getUniqueId())) {
                    PartyCommand.partySessionRequest.replace(player.getUniqueId(), Bukkit.getPlayer(array[1]).getUniqueId());
                }
                else {
                    PartyCommand.partySessionRequest.put(player.getUniqueId(), Bukkit.getPlayer(array[1]).getUniqueId());
                }
                break;
            }
            case "accept": {
                if (array.length < 2) {
                    return true;
                }
                if (BeaconBattle.getParty().hasParty(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_ACCEPT_DENIED_ALREADY_IN_PARTY));
                    return true;
                }
                if (Bukkit.getPlayer(array[1]) == null || !Bukkit.getPlayer(array[1]).isOnline()) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INVITE_DENIED_PLAYER_OFFLINE).replace("{player}", array[1]));
                    return true;
                }
                if (!PartyCommand.partySessionRequest.containsKey(Bukkit.getPlayer(array[1]).getUniqueId())) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
                    return true;
                }
                if (!PartyCommand.partySessionRequest.get(Bukkit.getPlayer(array[1]).getUniqueId()).equals(player.getUniqueId())) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_ACCEPT_DENIED_NO_INVITE));
                    break;
                }
                PartyCommand.partySessionRequest.remove(Bukkit.getPlayer(array[1]).getUniqueId());
                if (BeaconBattle.getParty().hasParty(Bukkit.getPlayer(array[1]))) {
                    BeaconBattle.getParty().addMember(Bukkit.getPlayer(array[1]), player);
                    final Iterator<Player> iterator = BeaconBattle.getParty().getMembers(Bukkit.getPlayer(array[1])).iterator();
                    while (iterator.hasNext()) {
                        iterator.next().sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", player.getDisplayName()));
                    }
                    break;
                }
                BeaconBattle.getParty().createParty(Bukkit.getPlayer(array[1]), player);
                final Iterator<Player> iterator2 = BeaconBattle.getParty().getMembers(Bukkit.getPlayer(array[1])).iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_ACCEPT_SUCCESS).replace("{player}", player.getDisplayName()));
                }
                break;
            }
            case "leave": {
                if (!BeaconBattle.getParty().hasParty(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
                    return true;
                }
                if (BeaconBattle.getParty().isOwner(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_LEAVE_DENIED_IS_OWNER_NEEDS_DISBAND));
                    return true;
                }
                BeaconBattle.getParty().removeFromParty(player);
                break;
            }
            case "disband": {
                if (!BeaconBattle.getParty().hasParty(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_GENERAL_DENIED_NOT_IN_PARTY));
                    return true;
                }
                if (!BeaconBattle.getParty().isOwner(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
                    return true;
                }
                BeaconBattle.getParty().disband(player);
                break;
            }
            case "remove": {
                if (array.length == 1) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_REMOVE_USAGE));
                    return true;
                }
                if (BeaconBattle.getParty().hasParty(player) && !BeaconBattle.getParty().isOwner(player)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_INSUFFICIENT_PERMISSIONS));
                    return true;
                }
                final Player player2 = Bukkit.getPlayer(array[1]);
                if (player2 == null) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER).replace("{player}", array[1]));
                    return true;
                }
                if (!BeaconBattle.getParty().isMember(player, player2)) {
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_PARTY_REMOVE_DENIED_TARGET_NOT_PARTY_MEMBER).replace("{player}", array[1]));
                    return true;
                }
                BeaconBattle.getParty().removePlayer(player, player2);
                break;
            }
            default: {
                this.sendPartyCmds(player);
                break;
            }
        }
        return false;
    }
    
    private void sendPartyCmds(final Player player) {
        final Iterator<String> iterator = Language.getList(player, Messages.COMMAND_PARTY_HELP).iterator();
        while (iterator.hasNext()) {
            player.sendMessage((String)iterator.next());
        }
    }
    
    static {
        PartyCommand.partySessionRequest = new HashMap<UUID, UUID>();
    }
}
