

package ws.billy.bedwars.listeners;

import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.event.entity.ItemSpawnEvent;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import java.util.Iterator;
import org.bukkit.event.Event;
import ws.billy.bedwars.api.events.player.PlayerInvisibilityPotionEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.EventHandler;
import ws.billy.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.Listener;

public class HungerWeatherSpawn implements Listener
{
    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent foodLevelChangeEvent) {
        if (BeaconBattle.getServerType() == ServerType.SHARED) {
            if (Arena.getArenaByPlayer((Player)foodLevelChangeEvent.getEntity()) != null) {
                foodLevelChangeEvent.setCancelled(true);
            }
        }
        else {
            foodLevelChangeEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent weatherChangeEvent) {
        if (weatherChangeEvent.toWeatherState()) {
            if (BeaconBattle.getServerType() == ServerType.SHARED) {
                if (Arena.getArenaByIdentifier(weatherChangeEvent.getWorld().getName()) != null) {
                    weatherChangeEvent.setCancelled(true);
                }
            }
            else {
                weatherChangeEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent creatureSpawnEvent) {
        if (creatureSpawnEvent.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            if (BeaconBattle.getServerType() != ServerType.BUNGEE) {
                if (Arena.getArenaByIdentifier(creatureSpawnEvent.getEntity().getWorld().getName()) != null) {
                    creatureSpawnEvent.setCancelled(true);
                }
            }
            else {
                creatureSpawnEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onDrink(final PlayerItemConsumeEvent playerItemConsumeEvent) {
        if (Arena.getArenaByPlayer(playerItemConsumeEvent.getPlayer()) == null) {
            return;
        }
        switch (playerItemConsumeEvent.getItem().getType()) {
            case POTION: {
                Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> BeaconBattle.nms.minusAmount(playerItemConsumeEvent.getPlayer(), new ItemStack(Material.GLASS_BOTTLE), 1), 5L);
                if (!BeaconBattle.config.getBoolean("performance-settings.disable-armor-packets")) {
                    final PotionMeta potionMeta = (PotionMeta)playerItemConsumeEvent.getItem().getItemMeta();
                    if (potionMeta.hasCustomEffects() && potionMeta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                        final Iterator<PotionEffect> iterator;
                        PotionEffect potionEffect;
                        final IArena arena;
                        final ITeam team;
                        final Iterator<Player> iterator2;
                        Player player;
                        final ITeam team2;
                        Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                            playerItemConsumeEvent.getPlayer().getActivePotionEffects().iterator();
                            while (iterator.hasNext()) {
                                potionEffect = iterator.next();
                                if (potionEffect.getType().toString().contains("INVISIBILITY")) {
                                    if (arena.getShowTime().containsKey(playerItemConsumeEvent.getPlayer())) {
                                        arena.getTeam(playerItemConsumeEvent.getPlayer());
                                        arena.getShowTime().replace(playerItemConsumeEvent.getPlayer(), potionEffect.getDuration() / 20);
                                        Bukkit.getPluginManager().callEvent((Event)new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, team, playerItemConsumeEvent.getPlayer(), team.getArena()));
                                        break;
                                    }
                                    else {
                                        arena.getTeam(playerItemConsumeEvent.getPlayer());
                                        arena.getShowTime().put(playerItemConsumeEvent.getPlayer(), potionEffect.getDuration() / 20);
                                        playerItemConsumeEvent.getPlayer().getWorld().getPlayers().iterator();
                                        while (iterator2.hasNext()) {
                                            player = iterator2.next();
                                            if (arena.isSpectator(player)) {
                                                continue;
                                            }
                                            else {
                                                if (team2 != arena.getTeam(player)) {
                                                    BeaconBattle.nms.hideArmor(playerItemConsumeEvent.getPlayer(), player);
                                                }
                                                Bukkit.getPluginManager().callEvent((Event)new PlayerInvisibilityPotionEvent(PlayerInvisibilityPotionEvent.Type.ADDED, team2, playerItemConsumeEvent.getPlayer(), team2.getArena()));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            return;
                        }, 5L);
                    }
                    break;
                }
                break;
            }
            case GLASS_BOTTLE: {
                BeaconBattle.nms.minusAmount(playerItemConsumeEvent.getPlayer(), playerItemConsumeEvent.getItem(), 1);
                break;
            }
            case MILK_BUCKET: {
                playerItemConsumeEvent.setCancelled(true);
                BeaconBattle.nms.minusAmount(playerItemConsumeEvent.getPlayer(), playerItemConsumeEvent.getItem(), 1);
                Arena.magicMilk.put(playerItemConsumeEvent.getPlayer().getUniqueId(), Bukkit.getScheduler().runTaskLater((Plugin)BeaconBattle.plugin, () -> {
                    Arena.magicMilk.remove(playerItemConsumeEvent.getPlayer().getUniqueId());
                    BeaconBattle.debug("PlayerItemConsumeEvent player " + playerItemConsumeEvent.getPlayer() + " was removed from magicMilk");
                    return;
                }, 600L).getTaskId());
                break;
            }
        }
    }
    
    @EventHandler
    public void onItemSpawn(final ItemSpawnEvent itemSpawnEvent) {
        final IArena arenaByIdentifier = Arena.getArenaByIdentifier(itemSpawnEvent.getEntity().getLocation().getWorld().getName());
        if (arenaByIdentifier == null) {
            return;
        }
        if (arenaByIdentifier.getStatus() != GameState.playing) {
            itemSpawnEvent.setCancelled(true);
        }
    }
}
