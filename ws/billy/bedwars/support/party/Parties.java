

package ws.billy.bedwars.support.party;

import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.entity.Player;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import ws.billy.bedwars.api.party.Party;

public class Parties implements Party
{
    private PartiesAPI api;
    
    public Parties() {
        this.api = com.alessiodp.parties.api.Parties.getApi();
    }
    
    @Override
    public boolean hasParty(final Player player) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        return partyPlayer == null || this.api.getParty(partyPlayer.getPartyName()) != null;
    }
    
    @Override
    public int partySize(final Player player) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return 0;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        if (party == null) {
            return 0;
        }
        return party.getMembers().size();
    }
    
    @Override
    public boolean isOwner(final Player player) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return false;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        return party != null && party.getLeader() == player.getUniqueId();
    }
    
    @Override
    public List<Player> getMembers(final Player player) {
        final ArrayList<Player> list = new ArrayList<Player>();
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return list;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        if (party == null) {
            return list;
        }
        final Iterator iterator = party.getMembers().iterator();
        while (iterator.hasNext()) {
            final Player player2 = Bukkit.getPlayer((UUID)iterator.next());
            if (player2 == null) {
                continue;
            }
            if (!player2.isOnline()) {
                continue;
            }
            list.add(player2);
        }
        return list;
    }
    
    @Override
    public void createParty(final Player player, final Player... array) {
    }
    
    @Override
    public void addMember(final Player player, final Player player2) {
    }
    
    @Override
    public void removeFromParty(final Player player) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        if (party == null) {
            return;
        }
        if (party.getLeader() == player.getUniqueId()) {
            this.disband(player);
        }
        else {
            party.removeMember(partyPlayer);
            final Iterator iterator = party.getMembers().iterator();
            while (iterator.hasNext()) {
                final Player player2 = Bukkit.getPlayer((UUID)iterator.next());
                if (player2 == null) {
                    continue;
                }
                if (!player2.isOnline()) {
                    continue;
                }
                player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_PARTY_LEAVE_SUCCESS).replace("{player}", player.getDisplayName()));
            }
        }
    }
    
    @Override
    public void disband(final Player player) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        if (party == null) {
            return;
        }
        final Iterator iterator = party.getMembers().iterator();
        while (iterator.hasNext()) {
            final Player player2 = Bukkit.getPlayer((UUID)iterator.next());
            if (player2 == null) {
                continue;
            }
            if (!player2.isOnline()) {
                continue;
            }
            player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_PARTY_DISBAND_SUCCESS));
        }
        party.delete();
    }
    
    @Override
    public boolean isMember(final Player player, final Player player2) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null) {
            return false;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        return party != null && party.getMembers().contains(player2.getUniqueId());
    }
    
    @Override
    public void removePlayer(final Player player, final Player player2) {
        final PartyPlayer partyPlayer = this.api.getPartyPlayer(player2.getUniqueId());
        if (partyPlayer == null) {
            return;
        }
        final com.alessiodp.parties.api.interfaces.Party party = this.api.getParty(partyPlayer.getPartyName());
        if (party == null) {
            return;
        }
        party.removeMember(partyPlayer);
        final Iterator iterator = party.getMembers().iterator();
        while (iterator.hasNext()) {
            final Player player3 = Bukkit.getPlayer((UUID)iterator.next());
            if (player3 == null) {
                continue;
            }
            if (!player3.isOnline()) {
                continue;
            }
            player3.sendMessage(Language.getMsg(player3, Messages.COMMAND_PARTY_REMOVE_SUCCESS));
        }
    }
    
    @Override
    public boolean isInternal() {
        return false;
    }
}
