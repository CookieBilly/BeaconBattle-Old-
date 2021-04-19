

package ws.billy.bedwars.support.party;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.entity.Player;
import java.util.List;
import ws.billy.bedwars.api.party.Party;

public class Internal implements Party
{
    private static List<Party> parites;
    
    @Override
    public boolean hasParty(final Player player) {
        final Iterator<Party> iterator = getParites().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().members.contains(player)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int partySize(final Player player) {
        for (final Party party : getParites()) {
            if (party.members.contains(player)) {
                return party.members.size();
            }
        }
        return 0;
    }
    
    @Override
    public boolean isOwner(final Player player) {
        for (final Party party : getParites()) {
            if (party.members.contains(player) && party.owner == player) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Player> getMembers(final Player player) {
        for (final Party party : getParites()) {
            if (party.members.contains(player)) {
                return party.members;
            }
        }
        return null;
    }
    
    @Override
    public void createParty(final Player player, final Player... array) {
        final Party party = new Party(player);
        party.addMember(player);
        for (int length = array.length, i = 0; i < length; ++i) {
            party.addMember(array[i]);
        }
    }
    
    @Override
    public void addMember(final Player player, final Player player2) {
        if (player == null || player2 == null) {
            return;
        }
        final Party party = this.getParty(player);
        if (party == null) {
            return;
        }
        party.addMember(player2);
    }
    
    @Override
    public void removeFromParty(final Player player) {
        for (final Party party : new ArrayList<Party>(getParites())) {
            if (party.owner == player) {
                this.disband(player);
            }
            else {
                if (party.members.contains(player)) {
                    for (final Player player2 : party.members) {
                        player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_PARTY_LEAVE_SUCCESS).replace("{player}", player.getDisplayName()));
                    }
                    party.members.remove(player);
                    if (party.members.isEmpty() || party.members.size() == 1) {
                        this.disband(party.owner);
                        Internal.parites.remove(party);
                    }
                    return;
                }
                continue;
            }
        }
    }
    
    @Override
    public void disband(final Player player) {
        final Party party = this.getParty(player);
        if (party == null) {
            return;
        }
        for (final Player player2 : party.members) {
            player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_PARTY_DISBAND_SUCCESS));
        }
        party.members.clear();
        Internal.parites.remove(party);
    }
    
    @Override
    public boolean isMember(final Player player, final Player player2) {
        for (final Party party : Internal.parites) {
            if (party.owner == player && party.members.contains(player2)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void removePlayer(final Player player, final Player player2) {
        final Party party = this.getParty(player);
        if (party != null && party.members.contains(player2)) {
            for (final Player player3 : party.members) {
                player3.sendMessage(Language.getMsg(player3, Messages.COMMAND_PARTY_REMOVE_SUCCESS).replace("{player}", player2.getName()));
            }
            party.members.remove(player);
            if (party.members.isEmpty() || party.members.size() == 1) {
                this.disband(party.owner);
                Internal.parites.remove(party);
            }
        }
    }
    
    @Override
    public boolean isInternal() {
        return true;
    }
    
    @Nullable
    private Party getParty(final Player player) {
        for (final Party party : getParites()) {
            if (party.getOwner() == player) {
                return party;
            }
        }
        return null;
    }
    
    @NotNull
    @Contract(pure = true)
    public static List<Party> getParites() {
        return Collections.unmodifiableList((List<? extends Party>)Internal.parites);
    }
    
    static {
        Internal.parites = new ArrayList<Party>();
    }
    
    class Party
    {
        private List<Player> members;
        private Player owner;
        
        public Party(final Player owner) {
            this.members = new ArrayList<Player>();
            this.owner = owner;
            Internal.parites.add(this);
        }
        
        public Player getOwner() {
            return this.owner;
        }
        
        void addMember(final Player player) {
            this.members.add(player);
        }
    }
}
