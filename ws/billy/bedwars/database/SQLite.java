

package ws.billy.bedwars.database;

import ws.billy.bedwars.api.language.Language;
import java.sql.Timestamp;
import ws.billy.bedwars.stats.PlayerStats;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.sql.Statement;
import java.sql.DriverManager;
import org.bukkit.Bukkit;
import java.sql.Driver;
import java.io.IOException;
import java.io.File;
import ws.billy.bedwars.BeaconBattle;

import java.sql.SQLException;
import java.sql.Connection;

public class SQLite implements Database
{
    private Connection connection;
    
    public boolean isConnected() {
        if (this.connection == null) {
            return false;
        }
        try {
            return !this.connection.isClosed();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void init() {
        final File file = new File(BeaconBattle.plugin.getDataFolder() + "/Cache");
        if (!file.exists() && !file.mkdir()) {
            BeaconBattle.plugin.getLogger().severe("Could not create /Cache folder!");
        }
        final File obj = new File(file.getPath() + "/shop.db");
        if (!obj.exists()) {
            try {
                if (!obj.createNewFile()) {
                    BeaconBattle.plugin.getLogger().severe("Could not create /Cache/shop.db file!");
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
        try {
            DriverManager.registerDriver((Driver)Bukkit.getServer().getClass().getClassLoader().loadClass("org.sqlite.JDBC").newInstance());
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + obj);
        }
        catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException ex8) {
            final ClassNotFoundException ex3;
            final ClassNotFoundException ex2 = ex3;
            if (ex2 instanceof ClassNotFoundException) {
                BeaconBattle.plugin.getLogger().severe("Could Not Found SQLite Driver on your system!");
            }
            ex2.printStackTrace();
            return;
        }
        final String s = "CREATE TABLE IF NOT EXISTS global_stats (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(200), uuid VARCHAR(36), first_play TIMESTAMP NULL DEFAULT NULL, last_play TIMESTAMP DEFAULT NULL, wins INTEGER(10), kills INTEGER(10), final_kills INTEGER(10), looses INTEGER(10), deaths INTEGER(10), final_deaths INTEGER(10), beds_destroyed INTEGER(10), games_played INTEGER(10));";
        try (final Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(s);
        }
        catch (SQLException ex4) {
            ex4.printStackTrace();
        }
        try (final Statement statement2 = this.connection.createStatement()) {
            statement2.executeUpdate("CREATE TABLE IF NOT EXISTS quick_buy (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200),slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200),slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));");
        }
        catch (SQLException ex5) {
            ex5.printStackTrace();
        }
        try (final Statement statement3 = this.connection.createStatement()) {
            statement3.executeUpdate("CREATE TABLE IF NOT EXISTS player_levels (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), level INTEGER, xp INTEGER, name VARCHAR(200), next_cost INTEGER);");
        }
        catch (SQLException ex6) {
            ex6.printStackTrace();
        }
        try (final Statement statement4 = this.connection.createStatement()) {
            statement4.executeUpdate("CREATE TABLE IF NOT EXISTS  player_language (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), iso VARCHAR(200));");
        }
        catch (SQLException ex7) {
            ex7.printStackTrace();
        }
    }
    
    @Override
    public boolean hasStats(final UUID uuid) {
        if (!this.isConnected()) {
            this.init();
        }
        final String s = "SELECT id FROM global_stats WHERE uuid = ?;";
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                return executeQuery.next();
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    @Override
    public void saveStats(final PlayerStats playerStats) {
        if (!this.isConnected()) {
            this.init();
        }
        if (this.hasStats(playerStats.getUuid())) {
            final String s = "UPDATE global_stats SET last_play=?, wins=?, kills=?, final_kills=?, looses=?, deaths=?, final_deaths=?, beds_destroyed=?, games_played=?, name=? WHERE uuid = ?;";
            try (final PreparedStatement prepareStatement = this.connection.prepareStatement(s)) {
                prepareStatement.setTimestamp(1, Timestamp.from(playerStats.getLastPlay()));
                prepareStatement.setInt(2, playerStats.getWins());
                prepareStatement.setInt(3, playerStats.getKills());
                prepareStatement.setInt(4, playerStats.getFinalKills());
                prepareStatement.setInt(5, playerStats.getLosses());
                prepareStatement.setInt(6, playerStats.getDeaths());
                prepareStatement.setInt(7, playerStats.getFinalDeaths());
                prepareStatement.setInt(8, playerStats.getBedsDestroyed());
                prepareStatement.setInt(9, playerStats.getGamesPlayed());
                prepareStatement.setString(10, playerStats.getName());
                prepareStatement.setString(11, playerStats.getUuid().toString());
                prepareStatement.executeUpdate();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        else {
            final String s2 = "INSERT INTO global_stats (name, uuid, first_play, last_play, wins, kills, final_kills, looses, deaths, final_deaths, beds_destroyed, games_played) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (final PreparedStatement prepareStatement2 = this.connection.prepareStatement(s2)) {
                prepareStatement2.setString(1, playerStats.getName());
                prepareStatement2.setString(2, playerStats.getUuid().toString());
                prepareStatement2.setTimestamp(3, Timestamp.from(playerStats.getFirstPlay()));
                prepareStatement2.setTimestamp(4, Timestamp.from(playerStats.getLastPlay()));
                prepareStatement2.setInt(5, playerStats.getWins());
                prepareStatement2.setInt(6, playerStats.getKills());
                prepareStatement2.setInt(7, playerStats.getFinalKills());
                prepareStatement2.setInt(8, playerStats.getLosses());
                prepareStatement2.setInt(9, playerStats.getDeaths());
                prepareStatement2.setInt(10, playerStats.getFinalDeaths());
                prepareStatement2.setInt(11, playerStats.getBedsDestroyed());
                prepareStatement2.setInt(12, playerStats.getGamesPlayed());
                prepareStatement2.executeUpdate();
            }
            catch (SQLException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    @Override
    public PlayerStats fetchStats(final UUID uuid) {
        if (!this.isConnected()) {
            this.init();
        }
        final PlayerStats playerStats = new PlayerStats(uuid);
        final String s = "SELECT * FROM global_stats WHERE uuid = ?;";
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    playerStats.setFirstPlay(executeQuery.getTimestamp("first_play").toInstant());
                    playerStats.setLastPlay(executeQuery.getTimestamp("last_play").toInstant());
                    playerStats.setWins(executeQuery.getInt("wins"));
                    playerStats.setKills(executeQuery.getInt("kills"));
                    playerStats.setFinalKills(executeQuery.getInt("final_kills"));
                    playerStats.setLosses(executeQuery.getInt("looses"));
                    playerStats.setDeaths(executeQuery.getInt("deaths"));
                    playerStats.setFinalDeaths(executeQuery.getInt("final_deaths"));
                    playerStats.setBedsDestroyed(executeQuery.getInt("beds_destroyed"));
                    playerStats.setGamesPlayed(executeQuery.getInt("games_played"));
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return playerStats;
    }
    
    @Override
    public void close() {
        if (this.isConnected()) {
            try {
                this.connection.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Override
    public void setQuickBuySlot(final UUID uuid, final String str, final int n) {
        if (!this.isConnected()) {
            this.init();
        }
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement("SELECT id FROM quick_buy WHERE uuid = ?;")) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (!executeQuery.next()) {
                    try (final PreparedStatement prepareStatement2 = this.connection.prepareStatement("INSERT INTO quick_buy (uuid, slot_19, slot_20, slot_21, slot_22, slot_23, slot_24, slot_25, slot_28, slot_29, slot_30, slot_31, slot_32, slot_33, slot_34, slot_37, slot_38, slot_39, slot_40, slot_41, slot_42, slot_43) VALUES(?,' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ');")) {
                        prepareStatement2.setString(1, uuid.toString());
                        prepareStatement2.execute();
                    }
                }
                BeaconBattle.debug("UPDATE SET SLOT " + n + " identifier " + str);
                try (final PreparedStatement prepareStatement3 = this.connection.prepareStatement("UPDATE quick_buy SET slot_" + n + " = ? WHERE uuid = ?;")) {
                    prepareStatement3.setString(1, str);
                    prepareStatement3.setString(2, uuid.toString());
                    prepareStatement3.executeUpdate();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public String getQuickBuySlots(final UUID uuid, final int n) {
        String string = "";
        if (!this.isConnected()) {
            this.init();
        }
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement("SELECT slot_" + n + " FROM quick_buy WHERE uuid = ?;")) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    string = executeQuery.getString("slot_" + n);
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return string;
    }
    
    @Override
    public boolean hasQuickBuy(final UUID uuid) {
        if (!this.isConnected()) {
            this.init();
        }
        try (final Statement statement = this.connection.createStatement();
             final ResultSet executeQuery = statement.executeQuery("SELECT id FROM quick_buy WHERE uuid = '" + uuid.toString() + "';")) {
            if (executeQuery.next()) {
                executeQuery.close();
                return true;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    @Override
    public Object[] getLevelData(final UUID uuid) {
        if (!this.isConnected()) {
            this.init();
        }
        final Object[] array = { 1, 0, "", 0 };
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement("SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = ?;")) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    array[0] = executeQuery.getInt("level");
                    array[1] = executeQuery.getInt("xp");
                    array[2] = executeQuery.getString("name");
                    array[3] = executeQuery.getInt("next_cost");
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return array;
    }
    
    @Override
    public void setLevelData(final UUID uuid, final int n, final int n2, final String s, final int n3) {
        if (!this.isConnected()) {
            this.init();
        }
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement("SELECT id from player_levels WHERE uuid = ?;")) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (!executeQuery.next()) {
                    try (final PreparedStatement prepareStatement2 = this.connection.prepareStatement("INSERT INTO player_levels (uuid, level, xp, name, next_cost) VALUES (?, ?, ?, ?, ?);")) {
                        prepareStatement2.setString(1, uuid.toString());
                        prepareStatement2.setInt(2, n);
                        prepareStatement2.setInt(3, n2);
                        prepareStatement2.setString(4, s);
                        prepareStatement2.setInt(5, n3);
                        prepareStatement2.executeUpdate();
                    }
                }
                else {
                    try (final PreparedStatement preparedStatement = (s == null) ? this.connection.prepareStatement("UPDATE player_levels SET level=?, xp=? WHERE uuid = '" + uuid.toString() + "';") : this.connection.prepareStatement("UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid = '" + uuid.toString() + "';")) {
                        preparedStatement.setInt(1, n);
                        preparedStatement.setInt(2, n2);
                        if (s != null) {
                            preparedStatement.setString(3, s);
                            preparedStatement.setInt(4, n3);
                        }
                        preparedStatement.executeUpdate();
                    }
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setLanguage(final UUID uuid, final String str) {
        if (!this.isConnected()) {
            this.init();
        }
        try (final Statement statement = this.connection.createStatement();
             final ResultSet executeQuery = statement.executeQuery("SELECT iso FROM player_language WHERE uuid = '" + uuid.toString() + "';")) {
            if (executeQuery.next()) {
                try (final Statement statement2 = this.connection.createStatement()) {
                    statement2.executeUpdate("UPDATE player_language SET iso='" + str + "' WHERE uuid = '" + uuid.toString() + "';");
                }
            }
            else {
                try (final PreparedStatement prepareStatement = this.connection.prepareStatement("INSERT INTO player_language VALUES (0, ?, ?);")) {
                    prepareStatement.setString(1, uuid.toString());
                    prepareStatement.setString(2, str);
                    prepareStatement.execute();
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public String getLanguage(final UUID uuid) {
        if (!this.isConnected()) {
            this.init();
        }
        String s = Language.getDefaultLanguage().getIso();
        try (final PreparedStatement prepareStatement = this.connection.prepareStatement("SELECT iso FROM player_language WHERE uuid = ?;")) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    s = executeQuery.getString("iso");
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return s;
    }
}
