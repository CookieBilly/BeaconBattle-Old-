

package ws.billy.bedwars.arena.upgrades;

import java.util.WeakHashMap;
import ws.billy.bedwars.arena.BeaconBattleTeam;
import org.bukkit.potion.PotionEffect;
import java.util.Iterator;
import ws.billy.bedwars.api.events.player.PlayerBaseEnterEvent;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerBaseLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.IArena;
import ws.billy.bedwars.api.arena.GameState;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.event.player.PlayerMoveEvent;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.entity.Player;
import java.util.Map;
import org.bukkit.event.Listener;

public class BaseListener implements Listener
{
    public static Map<Player, ITeam> isOnABase;
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent playerMoveEvent) {
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(playerMoveEvent.getPlayer().getWorld().getName());
        if (arenaByIdentifier == null) {
            return;
        }
        if (arenaByIdentifier.getStatus() != GameState.playing) {
            return;
        }
        checkEvents(playerMoveEvent.getPlayer(), arenaByIdentifier);
    }
    
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        final Player player = playerTeleportEvent.getPlayer();
        if (BaseListener.isOnABase.containsKey(player)) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player);
            if (arenaByPlayer == null) {
                BaseListener.isOnABase.remove(player);
                return;
            }
            checkEvents(player, arenaByPlayer);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent playerDeathEvent) {
        final IArena arenaByPlayer = Arena.getArenaByPlayer(playerDeathEvent.getEntity());
        if (arenaByPlayer == null) {
            return;
        }
        checkEvents(playerDeathEvent.getEntity(), arenaByPlayer);
    }
    
    private static void checkEvents(final Player key, final IArena arena) {
        if (key == null || arena == null) {
            return;
        }
        if (arena.isSpectator(key)) {
            return;
        }
        boolean b = true;
        for (final ITeam value : arena.getTeams()) {
            if (key.getLocation().distance(value.getBed()) <= arena.getIslandRadius()) {
                b = false;
                if (BaseListener.isOnABase.containsKey(key)) {
                    if (BaseListener.isOnABase.get(key) == value) {
                        continue;
                    }
                    Bukkit.getPluginManager().callEvent((Event)new PlayerBaseLeaveEvent(key, BaseListener.isOnABase.get(key)));
                    if (!Arena.magicMilk.containsKey(key.getUniqueId())) {
                        Bukkit.getPluginManager().callEvent((Event)new PlayerBaseEnterEvent(key, value));
                    }
                    BaseListener.isOnABase.replace(key, value);
                }
                else {
                    if (Arena.magicMilk.containsKey(key.getUniqueId())) {
                        continue;
                    }
                    Bukkit.getPluginManager().callEvent((Event)new PlayerBaseEnterEvent(key, value));
                    BaseListener.isOnABase.put(key, value);
                }
            }
        }
        if (b && BaseListener.isOnABase.containsKey(key)) {
            Bukkit.getPluginManager().callEvent((Event)new PlayerBaseLeaveEvent(key, BaseListener.isOnABase.get(key)));
            BaseListener.isOnABase.remove(key);
        }
    }
    
    @EventHandler
    public void onBaseEnter(final PlayerBaseEnterEvent playerBaseEnterEvent) {
        if (playerBaseEnterEvent == null) {
            return;
        }
        final ITeam team = playerBaseEnterEvent.getTeam();
        if (team.isMember(playerBaseEnterEvent.getPlayer())) {
            final Iterator<PotionEffect> iterator = team.getBaseEffects().iterator();
            while (iterator.hasNext()) {
                playerBaseEnterEvent.getPlayer().addPotionEffect((PotionEffect)iterator.next());
            }
        }
        else if (!team.getActiveTraps().isEmpty() && !team.isBedDestroyed()) {
            team.getActiveTraps().get(0).trigger(team, playerBaseEnterEvent.getPlayer());
            team.getActiveTraps().remove(0);
        }
    }
    
    @EventHandler
    public void onBaseLeave(final PlayerBaseLeaveEvent playerBaseLeaveEvent) {
        if (playerBaseLeaveEvent == null) {
            return;
        }
        final BeaconBattleTeam BeaconBattleTeam = (BeaconBattleTeam)playerBaseLeaveEvent.getTeam();
        if (BeaconBattleTeam.isMember(playerBaseLeaveEvent.getPlayer())) {
            for (final PotionEffect potionEffect : playerBaseLeaveEvent.getPlayer().getActivePotionEffects()) {
                for (final PotionEffect potionEffect2 : BeaconBattleTeam.getBaseEffects()) {
                    if (potionEffect.getType() == potionEffect2.getType()) {
                        playerBaseLeaveEvent.getPlayer().removePotionEffect(potionEffect2.getType());
                    }
                }
            }
        }
    }
    
    static {
        BaseListener.isOnABase = new WeakHashMap<Player, ITeam>();
    }
}
