

package ws.billy.bedwars.support.citizens;

import net.citizensnpcs.api.npc.NPC;
import ws.billy.bedwars.configuration.Sounds;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.arena.Arena;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.EventHandler;

import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.arena.Misc;

import java.util.ArrayList;
import ws.billy.bedwars.BeaconBattle;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener
{
    @EventHandler
    public void removeNPC(final NPCRemoveEvent npcRemoveEvent) {
        if (npcRemoveEvent == null) {
            return;
        }
        final List stringList = BeaconBattle.config.getYml().getStringList("join-npc-locations");
        boolean b = false;
        if (JoinNPC.npcs.containsKey(npcRemoveEvent.getNPC().getId())) {
            JoinNPC.npcs.remove(npcRemoveEvent.getNPC().getId());
            b = true;
        }
        for (final String s : new ArrayList<String>(stringList)) {
            final String[] split = s.split(",");
            if (split.length >= 10 && Misc.isNumber(split[9]) && Integer.parseInt(split[9]) == npcRemoveEvent.getNPC().getId()) {
                stringList.remove(s);
                b = true;
            }
        }
        for (final Entity entity : npcRemoveEvent.getNPC().getEntity().getNearbyEntities(0.0, 3.0, 0.0)) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                entity.remove();
            }
        }
        if (b) {
            BeaconBattle.config.set("join-npc-locations", stringList);
        }
    }
    
    @EventHandler
    public void onNPCInteract(final PlayerInteractEntityEvent playerInteractEntityEvent) {
        if (!JoinNPC.isCitizensSupport()) {
            return;
        }
        if (playerInteractEntityEvent.getPlayer().isSneaking()) {
            return;
        }
        if (!playerInteractEntityEvent.getRightClicked().hasMetadata("NPC")) {
            return;
        }
        final NPC npc = CitizensAPI.getNPCRegistry().getNPC(playerInteractEntityEvent.getRightClicked());
        if (npc == null) {
            return;
        }
        if (JoinNPC.npcs.containsKey(npc.getId())) {
            if (!Arena.joinRandomFromGroup(playerInteractEntityEvent.getPlayer(), JoinNPC.npcs.get(npc.getId()))) {
                playerInteractEntityEvent.getPlayer().sendMessage(Language.getMsg(playerInteractEntityEvent.getPlayer(), Messages.COMMAND_JOIN_NO_EMPTY_FOUND));
                Sounds.playSound("join-denied", playerInteractEntityEvent.getPlayer());
            }
            else {
                Sounds.playSound("join-allowed", playerInteractEntityEvent.getPlayer());
            }
        }
    }
}
