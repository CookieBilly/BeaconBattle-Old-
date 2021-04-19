

package ws.billy.bedwars.lobbysocket;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonParser;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.ArrayList;
import ws.billy.bedwars.BeaconBattle;
import com.google.gson.JsonObject;
import ws.billy.bedwars.api.arena.IArena;
import java.util.Iterator;
import java.io.IOException;
import java.net.Socket;
import ws.billy.bedwars.arena.Misc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public class ArenaSocket
{
    public static List<String> lobbies;
    private static ConcurrentHashMap<String, RemoteLobby> sockets;
    
    public static void sendMessage(final String s) {
        if (s == null) {
            return;
        }
        if (s.isEmpty()) {
            return;
        }
        for (final String key : ArenaSocket.lobbies) {
            final String[] split = key.split(":");
            if (split.length != 2) {
                continue;
            }
            if (!Misc.isNumber(split[1])) {
                continue;
            }
            if (ArenaSocket.sockets.containsKey(key)) {
                ArenaSocket.sockets.get(key).sendMessage(s);
            }
            else {
                try {
                    final RemoteLobby value = new RemoteLobby(new Socket(split[0], Integer.parseInt(split[1])), key);
                    if (value.out == null) {
                        continue;
                    }
                    ArenaSocket.sockets.put(key, value);
                    value.sendMessage(s);
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static String formatUpdateMessage(final IArena arena) {
        if (arena == null) {
            return "";
        }
        if (arena.getWorldName() == null) {
            return "";
        }
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "UPDATE");
        jsonObject.addProperty("server_name", BeaconBattle.config.getString("bungee-settings.server-id"));
        jsonObject.addProperty("arena_name", arena.getArenaName());
        jsonObject.addProperty("arena_identifier", arena.getWorldName());
        jsonObject.addProperty("arena_status", arena.getStatus().toString().toUpperCase());
        jsonObject.addProperty("arena_current_players", (Number)arena.getPlayers().size());
        jsonObject.addProperty("arena_max_players", (Number)arena.getMaxPlayers());
        jsonObject.addProperty("arena_max_in_team", (Number)arena.getMaxInTeam());
        jsonObject.addProperty("arena_group", arena.getGroup().toUpperCase());
        jsonObject.addProperty("spectate", Boolean.valueOf(arena.isAllowSpectate()));
        return jsonObject.toString();
    }
    
    public static void disable() {
        final Iterator<RemoteLobby> iterator = new ArrayList<RemoteLobby>(ArenaSocket.sockets.values()).iterator();
        while (iterator.hasNext()) {
            iterator.next().disable();
        }
    }
    
    static {
        ArenaSocket.lobbies = new ArrayList<String>();
        ArenaSocket.sockets = new ConcurrentHashMap<String, RemoteLobby>();
    }
    
    private static class RemoteLobby
    {
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
        private String lobby;
        private boolean compute;

        private boolean sendMessage(final String x) {
            if (this.socket == null) {
                this.disable();
                return false;
            }
            if (!this.socket.isConnected()) {
                this.disable();
                return false;
            }
            if (this.out == null) {
                this.disable();
                return false;
            }
            if (this.in == null) {
                this.disable();
                return false;
            }
            if (this.out.checkError()) {
                this.disable();
                return false;
            }
            this.out.println(x);
            return true;
        }
        
        private void disable() {
            this.compute = false;
            BeaconBattle.debug("Disabling socket: " + this.socket.toString());
            ArenaSocket.sockets.remove(this.lobby);
            try {
                this.socket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            this.in = null;
            this.out = null;
        }
    }
}
