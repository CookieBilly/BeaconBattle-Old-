

package ws.billy.bedwars.listeners;

import org.bukkit.event.EventHandler;
import ws.billy.bedwars.api.arena.team.ITeam;
import ws.billy.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import ws.billy.bedwars.commands.shout.ShoutCommand;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.arena.GameState;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.support.papi.SupportPAPI;
import org.bukkit.ChatColor;
import ws.billy.bedwars.arena.Arena;
import ws.billy.bedwars.api.server.ServerType;
import ws.billy.bedwars.BeaconBattle;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.Listener;

public class PlayerChat implements Listener
{
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (asyncPlayerChatEvent == null) {
            return;
        }
        final Player player2 = asyncPlayerChatEvent.getPlayer();
        if (asyncPlayerChatEvent.isCancelled()) {
            return;
        }
        if (BeaconBattle.getServerType() == ServerType.SHARED && Arena.getArenaByPlayer(player2) == null) {
            asyncPlayerChatEvent.getRecipients().removeIf(player -> Arena.getArenaByPlayer(player) != null);
            return;
        }
        if (player2.hasPermission("bw.chatcolor") || player2.hasPermission("bw.*") || player2.hasPermission("bw.vip")) {
            asyncPlayerChatEvent.setMessage(ChatColor.translateAlternateColorCodes('&', asyncPlayerChatEvent.getMessage()));
        }
        if (BeaconBattle.getServerType() == ServerType.MULTIARENA && player2.getWorld().getName().equalsIgnoreCase(BeaconBattle.getLobbyWorld())) {
            if (!BeaconBattle.config.getBoolean("globalChat")) {
                asyncPlayerChatEvent.getRecipients().clear();
                asyncPlayerChatEvent.getRecipients().addAll(player2.getWorld().getPlayers());
            }
            asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_LOBBY).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))).replace("{message}", "%2$s"));
        }
        else if (Arena.getArenaByPlayer(player2) != null) {
            final IArena arenaByPlayer = Arena.getArenaByPlayer(player2);
            Arena.afkCheck.remove(player2.getUniqueId());
            if (BeaconBattle.getAPI().getAFKUtil().isPlayerAFK(asyncPlayerChatEvent.getPlayer())) {
                Bukkit.getScheduler().runTask((Plugin)BeaconBattle.plugin, () -> BeaconBattle.getAPI().getAFKUtil().setPlayerAFK(asyncPlayerChatEvent.getPlayer(), false));
            }
            if (arenaByPlayer.isSpectator(player2)) {
                if (!BeaconBattle.config.getBoolean("globalChat")) {
                    asyncPlayerChatEvent.getRecipients().clear();
                    asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getSpectators());
                }
                asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_SPECTATOR).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{message}", "%2$s").replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))));
            }
            else {
                if (arenaByPlayer.getStatus() == GameState.waiting || arenaByPlayer.getStatus() == GameState.starting) {
                    if (!BeaconBattle.config.getBoolean("globalChat")) {
                        asyncPlayerChatEvent.getRecipients().clear();
                        asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getPlayers());
                    }
                    asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_WAITING).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))).replace("{message}", "%2$s"));
                    return;
                }
                final ITeam team = arenaByPlayer.getTeam(player2);
                String message = asyncPlayerChatEvent.getMessage();
                if (message.startsWith("!") || message.startsWith("shout") || message.startsWith("SHOUT") || message.startsWith(Language.getMsg(player2, Messages.MEANING_SHOUT))) {
                    if (!player2.hasPermission(Permissions.PERMISSION_SHOUT_COMMAND) && !player2.hasPermission(Permissions.PERMISSION_ALL)) {
                        asyncPlayerChatEvent.setCancelled(true);
                        player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_NOT_FOUND_OR_INSUFF_PERMS));
                        return;
                    }
                    if (ShoutCommand.isShoutCooldown(player2)) {
                        asyncPlayerChatEvent.setCancelled(true);
                        player2.sendMessage(Language.getMsg(player2, Messages.COMMAND_COOLDOWN).replace("{seconds}", String.valueOf(ShoutCommand.getShoutCooldown(player2))));
                        return;
                    }
                    ShoutCommand.updateShout(player2);
                    if (!BeaconBattle.config.getBoolean("globalChat")) {
                        asyncPlayerChatEvent.getRecipients().clear();
                        asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getPlayers());
                        asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getSpectators());
                    }
                    if (message.startsWith("!")) {
                        message = message.replaceFirst("!", "");
                    }
                    if (message.startsWith("shout")) {
                        message = message.replaceFirst("SHOUT", "");
                    }
                    if (message.startsWith("shout")) {
                        message = message.replaceFirst("shout", "");
                    }
                    if (message.startsWith(Language.getMsg(player2, Messages.MEANING_SHOUT))) {
                        message = message.replaceFirst(Language.getMsg(player2, Messages.MEANING_SHOUT), "");
                    }
                    if (message.isEmpty()) {
                        asyncPlayerChatEvent.setCancelled(true);
                        return;
                    }
                    asyncPlayerChatEvent.setMessage(message);
                    asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_SHOUT).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(asyncPlayerChatEvent.getPlayer())).toUpperCase() + "]").replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))).replace("{message}", "%2$s"));
                }
                else if (arenaByPlayer.getMaxInTeam() == 1) {
                    if (!BeaconBattle.config.getBoolean("globalChat")) {
                        asyncPlayerChatEvent.getRecipients().clear();
                        asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getPlayers());
                        asyncPlayerChatEvent.getRecipients().addAll(arenaByPlayer.getSpectators());
                    }
                    asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_TEAM).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(asyncPlayerChatEvent.getPlayer())).toUpperCase() + "]").replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))).replace("{message}", "%2$s"));
                }
                else {
                    if (!BeaconBattle.config.getBoolean("globalChat")) {
                        asyncPlayerChatEvent.getRecipients().clear();
                        asyncPlayerChatEvent.getRecipients().addAll(team.getMembers());
                    }
                    asyncPlayerChatEvent.setFormat(SupportPAPI.getSupportPAPI().replace(asyncPlayerChatEvent.getPlayer(), Language.getMsg(player2, Messages.FORMATTING_CHAT_TEAM).replace("{vPrefix}", BeaconBattle.getChatSupport().getPrefix(player2)).replace("{vSuffix}", BeaconBattle.getChatSupport().getSuffix(player2)).replace("{player}", player2.getDisplayName()).replace("{team}", team.getColor().chat() + "[" + team.getDisplayName(Language.getPlayerLanguage(asyncPlayerChatEvent.getPlayer())).toUpperCase() + "]").replace("{level}", BeaconBattle.getLevelSupport().getLevel(player2))).replace("{message}", "%2$s"));
                }
            }
        }
    }
}
