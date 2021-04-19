

package ws.billy.bedwars.support.citizens;

import java.util.Map;
import org.bukkit.Bukkit;
import ws.billy.bedwars.arena.Misc;
import ws.billy.bedwars.BeaconBattle;
import org.jetbrains.annotations.Nullable;
import org.bukkit.event.player.PlayerTeleportEvent;
import java.util.Arrays;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.entity.EntityType;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import java.util.Iterator;

import ws.billy.bedwars.commands.bedwars.subcmds.sensitive.NPC;
import ws.billy.bedwars.api.command.SubCommand;
import ws.billy.bedwars.commands.bedwars.MainCommand;
import java.util.List;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;

public class JoinNPC
{
    private static boolean citizensSupport;
    public static HashMap<ArmorStand, List<String>> npcs_holos;
    public static HashMap<Integer, String> npcs;
    
    public static boolean isCitizensSupport() {
        return JoinNPC.citizensSupport;
    }
    
    public static void setCitizensSupport(final boolean citizensSupport) {
        JoinNPC.citizensSupport = citizensSupport;
        final MainCommand instance = MainCommand.getInstance();
        if (instance == null) {
            return;
        }
        if (citizensSupport) {
            if (instance.isRegistered()) {
                boolean b = false;
                final Iterator<SubCommand> iterator = instance.getSubCommands().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getSubCommandName().equalsIgnoreCase("npc")) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    new NPC(instance, "npc");
                }
            }
        }
        else if (instance.isRegistered()) {
            instance.getSubCommands().removeIf(subCommand -> subCommand.getSubCommandName().equalsIgnoreCase("npc"));
        }
    }
    
    @Nullable
    public static net.citizensnpcs.api.npc.NPC spawnNPC(final Location location, final String s, final String s2, final String skinName, final net.citizensnpcs.api.npc.NPC npc) {
        if (!isCitizensSupport()) {
            return null;
        }
        net.citizensnpcs.api.npc.NPC npc2;
        if (npc == null) {
            npc2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        }
        else {
            npc2 = npc;
        }
        if (!npc2.isSpawned()) {
            npc2.spawn(location);
        }
        if (npc2.getEntity() instanceof SkinnableEntity) {
            ((SkinnableEntity)npc2.getEntity()).setSkinName(skinName);
        }
        npc2.setProtected(true);
        npc2.setName("");
        final String[] split = s.split("\\\\n");
        for (final Entity entity : location.getWorld().getNearbyEntities(location, 1.0, 3.0, 1.0)) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                entity.remove();
            }
        }
        if (split.length >= 2) {
            final ArmorStand armorStand = NPC.createArmorStand(location.clone().add(0.0, 0.05, 0.0));
            armorStand.setMarker(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', split[0]).replace("{players}", String.valueOf(Arena.getPlayers(s2))));
            JoinNPC.npcs.put(npc2.getId(), s2);
            final ArmorStand armorStand2 = NPC.createArmorStand(location.clone().subtract(0.0, 0.25, 0.0));
            armorStand2.setMarker(false);
            armorStand2.setCustomName(ChatColor.translateAlternateColorCodes('&', split[1].replace("{players}", String.valueOf(Arena.getPlayers(s2)))));
            armorStand2.setCustomNameVisible(true);
            JoinNPC.npcs_holos.put(armorStand, Arrays.asList(s2, split[0]));
            JoinNPC.npcs_holos.put(armorStand2, Arrays.asList(s2, split[1]));
        }
        else if (split.length == 1) {
            JoinNPC.npcs.put(npc2.getId(), s2);
            final ArmorStand armorStand3 = NPC.createArmorStand(location.clone().subtract(0.0, 0.25, 0.0));
            armorStand3.setMarker(false);
            armorStand3.setCustomName(ChatColor.translateAlternateColorCodes('&', split[0]).replace("{players}", String.valueOf(Arena.getPlayers(s2))));
            armorStand3.setCustomNameVisible(true);
            JoinNPC.npcs_holos.put(armorStand3, Arrays.asList(s2, split[0]));
        }
        npc2.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        npc2.setName("");
        return npc2;
    }
    
    public static void spawnNPCs() {
        if (!isCitizensSupport()) {
            return;
        }
        if (BeaconBattle.config.getYml().get("join-npc-locations") != null) {
            final Iterator<String> iterator = (Iterator<String>)BeaconBattle.config.getYml().getStringList("join-npc-locations").iterator();
            while (iterator.hasNext()) {
                final String[] split = iterator.next().split(",");
                if (split.length < 10) {
                    continue;
                }
                if (!Misc.isNumber(split[0])) {
                    continue;
                }
                if (!Misc.isNumber(split[1])) {
                    continue;
                }
                if (!Misc.isNumber(split[2])) {
                    continue;
                }
                if (!Misc.isNumber(split[3])) {
                    continue;
                }
                if (!Misc.isNumber(split[4])) {
                    continue;
                }
                if (Misc.isNumber(split[5])) {
                    continue;
                }
                if (Misc.isNumber(split[6])) {
                    continue;
                }
                if (Misc.isNumber(split[7])) {
                    continue;
                }
                if (Misc.isNumber(split[8])) {
                    continue;
                }
                if (!Misc.isNumber(split[9])) {
                    continue;
                }
                final Location location = new Location(Bukkit.getWorld(split[5]), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
                final String s = split[6];
                final String s2 = split[7];
                final String s3 = split[8];
                final int int1 = Integer.parseInt(split[9]);
                final net.citizensnpcs.api.npc.NPC byId = CitizensAPI.getNPCRegistry().getById(int1);
                if (byId == null) {
                    BeaconBattle.plugin.getLogger().severe("Invalid npc id: " + int1);
                }
                else {
                    spawnNPC(location, s2, s3, s, byId);
                }
            }
        }
    }
    
    public static void updateNPCs(final String anotherString) {
        final String value = String.valueOf(Arena.getPlayers(anotherString));
        for (final Map.Entry<ArmorStand, List<String>> entry : JoinNPC.npcs_holos.entrySet()) {
            if (entry.getValue().get(0).equalsIgnoreCase(anotherString) && entry.getKey() != null && !entry.getKey().isDead()) {
                entry.getKey().setCustomName(ChatColor.translateAlternateColorCodes('&', entry.getValue().get(1).replace("{players}", value)));
            }
        }
    }
    
    static {
        JoinNPC.citizensSupport = false;
        JoinNPC.npcs_holos = new HashMap<ArmorStand, List<String>>();
        JoinNPC.npcs = new HashMap<Integer, String>();
    }
}
