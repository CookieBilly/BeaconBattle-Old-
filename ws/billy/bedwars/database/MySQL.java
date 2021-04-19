

package ws.billy.bedwars.database;

import ws.billy.bedwars.api.language.Language;
import java.sql.Timestamp;
import ws.billy.bedwars.stats.PlayerStats;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.UUID;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariConfig;
import ws.billy.bedwars.BeaconBattle;
import ws.billy.bedwars.libs.com.zaxxer.hikari.HikariDataSource;

public class MySQL implements Database
{
    private HikariDataSource dataSource;
    private String host;
    private String database;
    private String user;
    private String pass;
    private int port;
    private boolean ssl;
    private boolean certificateVerification;
    private int poolSize;
    private int maxLifetime;
    
    public MySQL() {
        this.host = BeaconBattle.config.getYml().getString("database.host");
        this.database = BeaconBattle.config.getYml().getString("database.database");
        this.user = BeaconBattle.config.getYml().getString("database.user");
        this.pass = BeaconBattle.config.getYml().getString("database.pass");
        this.port = BeaconBattle.config.getYml().getInt("database.port");
        this.ssl = BeaconBattle.config.getYml().getBoolean("database.ssl");
        this.certificateVerification = BeaconBattle.config.getYml().getBoolean("database.verify-certificate", true);
        this.poolSize = BeaconBattle.config.getYml().getInt("database.pool-size", 10);
        this.maxLifetime = BeaconBattle.config.getYml().getInt("database.max-lifetime", 1800);
    }
    
    public boolean connect() {
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("BeaconBattle1058MySQLPool");
        hikariConfig.setMaximumPoolSize(this.poolSize);
        hikariConfig.setMaxLifetime(this.maxLifetime * 1000);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
        hikariConfig.setUsername(this.user);
        hikariConfig.setPassword(this.pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(this.ssl));
        if (!this.certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }
        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30L)));
        this.dataSource = new HikariDataSource(hikariConfig);
        try {
            this.dataSource.getConnection();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    @Override
    public boolean hasStats(final UUID uuid) {
        final String s = "SELECT id FROM global_stats WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
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
    public void init() {
        try (final Connection connection = this.dataSource.getConnection()) {
            final String s = "CREATE TABLE IF NOT EXISTS global_stats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(200), uuid VARCHAR(200), first_play TIMESTAMP NULL DEFAULT NULL, last_play TIMESTAMP NULL DEFAULT NULL, wins INT(200), kills INT(200), final_kills INT(200), looses INT(200), deaths INT(200), final_deaths INT(200), beds_destroyed INT(200), games_played INT(200));";
            try (final Statement statement = connection.createStatement()) {
                statement.executeUpdate(s);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            final String s2 = "CREATE TABLE IF NOT EXISTS quick_buy (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200),slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200),slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));";
            try (final Statement statement2 = connection.createStatement()) {
                statement2.executeUpdate(s2);
            }
            catch (SQLException ex2) {
                ex2.printStackTrace();
            }
            final String s3 = "CREATE TABLE IF NOT EXISTS player_levels (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), level INT(200), xp INT(200), name VARCHAR(200) CHARACTER SET utf8, next_cost INT(200));";
            try (final Statement statement3 = connection.createStatement()) {
                statement3.executeUpdate(s3);
            }
            catch (SQLException ex3) {
                ex3.printStackTrace();
            }
            final String s4 = "CREATE TABLE IF NOT EXISTS player_language (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(200), iso VARCHAR(200));";
            try (final Statement statement4 = connection.createStatement()) {
                statement4.executeUpdate(s4);
            }
            catch (SQLException ex4) {
                ex4.printStackTrace();
            }
        }
        catch (SQLException ex5) {
            ex5.printStackTrace();
        }
    }
    
    @Override
    public void saveStats(final PlayerStats playerStats) {
        if (this.hasStats(playerStats.getUuid())) {
            final String s = "UPDATE global_stats SET first_play=?, last_play=?, wins=?, kills=?, final_kills=?, looses=?, deaths=?, final_deaths=?, beds_destroyed=?, games_played=?, name=? WHERE uuid = ?;";
            try (final Connection connection = this.dataSource.getConnection();
                 final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
                prepareStatement.setTimestamp(1, (playerStats.getFirstPlay() != null) ? Timestamp.from(playerStats.getFirstPlay()) : null);
                prepareStatement.setTimestamp(2, (playerStats.getLastPlay() != null) ? Timestamp.from(playerStats.getLastPlay()) : null);
                prepareStatement.setInt(3, playerStats.getWins());
                prepareStatement.setInt(4, playerStats.getKills());
                prepareStatement.setInt(5, playerStats.getFinalKills());
                prepareStatement.setInt(6, playerStats.getLosses());
                prepareStatement.setInt(7, playerStats.getDeaths());
                prepareStatement.setInt(8, playerStats.getFinalDeaths());
                prepareStatement.setInt(9, playerStats.getBedsDestroyed());
                prepareStatement.setInt(10, playerStats.getGamesPlayed());
                prepareStatement.setString(11, playerStats.getName());
                prepareStatement.setString(12, playerStats.getUuid().toString());
                prepareStatement.executeUpdate();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        else {
            final String s2 = "INSERT INTO global_stats (name, uuid, first_play, last_play, wins, kills, final_kills, looses, deaths, final_deaths, beds_destroyed, games_played) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            try (final Connection connection2 = this.dataSource.getConnection();
                 final PreparedStatement prepareStatement2 = connection2.prepareStatement(s2)) {
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
        final PlayerStats playerStats = new PlayerStats(uuid);
        final String s = "SELECT first_play, last_play, wins, kills, final_kills, looses, deaths, final_deaths,beds_destroyed, games_played FROM global_stats WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    final Timestamp timestamp = executeQuery.getTimestamp(1);
                    final Timestamp timestamp2 = executeQuery.getTimestamp(2);
                    playerStats.setFirstPlay((timestamp != null) ? timestamp.toInstant() : null);
                    playerStats.setLastPlay((timestamp2 != null) ? timestamp2.toInstant() : null);
                    playerStats.setWins(executeQuery.getInt(3));
                    playerStats.setKills(executeQuery.getInt(4));
                    playerStats.setFinalKills(executeQuery.getInt(5));
                    playerStats.setLosses(executeQuery.getInt(6));
                    playerStats.setDeaths(executeQuery.getInt(7));
                    playerStats.setFinalDeaths(executeQuery.getInt(8));
                    playerStats.setBedsDestroyed(executeQuery.getInt(9));
                    playerStats.setGamesPlayed(executeQuery.getInt(10));
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
        this.dataSource.close();
    }
    
    @Override
    public void setQuickBuySlot(final UUID uuid, final String str, final int n) {
        final String s = "SELECT id FROM quick_buy WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (!executeQuery.next()) {
                    try (final PreparedStatement prepareStatement2 = connection.prepareStatement("INSERT INTO quick_buy VALUES(0, ?, ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ');")) {
                        prepareStatement2.setString(1, uuid.toString());
                        prepareStatement2.executeUpdate();
                    }
                }
                BeaconBattle.debug("UPDATE SET SLOT " + n + " identifier " + str);
                try (final PreparedStatement prepareStatement3 = connection.prepareStatement("UPDATE quick_buy SET slot_" + n + " = ? WHERE uuid = ?;")) {
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
    public String getQuickBuySlots(final UUID uuid, final int i) {
        final String string = "SELECT slot_" + i + " FROM quick_buy WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(string)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    return executeQuery.getString(1);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    @Override
    public boolean hasQuickBuy(final UUID uuid) {
        final String s = "SELECT id FROM quick_buy WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
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
    public Object[] getLevelData(final UUID uuid) {
        final String s = "SELECT level, xp, name, next_cost FROM player_levels WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    return new Object[] { executeQuery.getInt(1), executeQuery.getInt(2), executeQuery.getString(3), executeQuery.getInt(4) };
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new Object[] { 1, 0, "", 0 };
    }
    
    @Override
    public void setLevelData(final UUID uuid, final int n, final int n2, final String s, final int n3) {
        final String s2 = "SELECT id from player_levels WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s2)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (!executeQuery.next()) {
                    try (final PreparedStatement prepareStatement2 = connection.prepareStatement("INSERT INTO player_levels VALUES (?, ?, ?, ?, ?, ?);")) {
                        prepareStatement2.setInt(1, 0);
                        prepareStatement2.setString(2, uuid.toString());
                        prepareStatement2.setInt(3, n);
                        prepareStatement2.setInt(4, n2);
                        prepareStatement2.setString(5, s);
                        prepareStatement2.setInt(6, n3);
                        prepareStatement2.executeUpdate();
                    }
                }
                else {
                    if (s == null) {
                        final String s3 = "UPDATE player_levels SET level=?, xp=? WHERE uuid = ?;";
                    }
                    else {
                        final String s3 = "UPDATE player_levels SET level=?, xp=?, name=?, next_cost=? WHERE uuid = ?;";
                    }
                    String s3;
                    try (final PreparedStatement prepareStatement3 = connection.prepareStatement(s3)) {
                        prepareStatement3.setInt(1, n);
                        prepareStatement3.setInt(2, n2);
                        if (s != null) {
                            prepareStatement3.setString(3, s);
                            prepareStatement3.setInt(4, n3);
                            prepareStatement3.setString(5, uuid.toString());
                        }
                        else {
                            prepareStatement3.setString(3, uuid.toString());
                        }
                        prepareStatement3.executeUpdate();
                    }
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void setLanguage(final UUID uuid, final String s) {
        final String s2 = "SELECT iso FROM player_language WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s2)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    try (final PreparedStatement prepareStatement2 = connection.prepareStatement("UPDATE player_language SET iso = ? WHERE uuid = ?;")) {
                        prepareStatement2.setString(1, s);
                        prepareStatement2.setString(2, uuid.toString());
                        prepareStatement2.executeUpdate();
                    }
                }
                else {
                    try (final PreparedStatement prepareStatement3 = connection.prepareStatement("INSERT INTO player_language VALUES (0, ?, ?);")) {
                        prepareStatement3.setString(1, uuid.toString());
                        prepareStatement3.setString(2, s);
                        prepareStatement3.executeUpdate();
                    }
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public String getLanguage(final UUID uuid) {
        final String s = "SELECT iso FROM player_language WHERE uuid = ?;";
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement prepareStatement = connection.prepareStatement(s)) {
            prepareStatement.setString(1, uuid.toString());
            try (final ResultSet executeQuery = prepareStatement.executeQuery()) {
                if (executeQuery.next()) {
                    return executeQuery.getString(1);
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Language.getDefaultLanguage().getIso();
    }
}
