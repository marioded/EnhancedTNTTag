package tech.zmario.enhancedtnttag.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DatabaseManager {

    private final EnhancedTNTTag plugin;
    private HikariDataSource dataSource;
    private Connection connection;

    public DatabaseManager(EnhancedTNTTag plugin) {
        this.plugin = plugin;

        setup(plugin.getConfig().getBoolean("mysql.enabled"));
        makeTables();
    }

    private void setup(boolean mysql) {
        if (mysql) {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:mysql://" + SettingsConfiguration.MYSQL_HOST.getString() + ":" +
                    SettingsConfiguration.MYSQL_PORT.getInt() + "/" + SettingsConfiguration.MYSQL_DATABASE.getString());
            config.setDriverClassName(SettingsConfiguration.MYSQL_DRIVER.getString());
            config.setUsername(SettingsConfiguration.MYSQL_USERNAME.getString());
            config.setPassword(SettingsConfiguration.MYSQL_PASSWORD.getString());
            config.setPoolName("EnhancedTNTTag");

            dataSource = new HikariDataSource(config);
        } else {
            connection = getConnection();
        }
    }

    private void makeTables() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement("CREATE TABLE IF NOT EXISTS `players_data` (uuid varchar(36) NOT NULL, wins integer NOT NULL)")) {

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to create tables. Error message: " + e.getMessage());
            }
        });
    }

    public boolean isPresent(Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players_data WHERE uuid = ?")) {
                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to check if player is present. Error message: " + e.getMessage());
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return false;
    }

    public void createPlayer(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO players_data (uuid, wins) VALUES (?, ?);")) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, 0);

                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to add player to database. Error message: " + e.getMessage());
            }
        });
    }

    public Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            } else {
                File databaseFile = new File(plugin.getDataFolder(), "data.db");

                if (!databaseFile.exists()) {
                    try {
                        databaseFile.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                        plugin.getLogger().severe("Failed to create database file. Error message: " + e.getMessage());

                    }
                }

                try {
                    if (connection != null && !connection.isClosed()) {
                        return connection;
                    }

                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

                    return connection;
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().severe("Failed to connect to database. Error message: " + e.getMessage());
                }

                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Failed to get connection. Error message: " + e.getMessage());
        }

        return null;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        } else {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to close connection. Error message: " + e.getMessage());
            }
        }
    }

    public int getWins(Player player) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT wins FROM players_data WHERE uuid = ?")) {
                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("wins");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to get wins. Error message: " + e.getMessage());
            }
            return 0;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return 0;
    }

    public void updateWins(Player player, int wins) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET wins = ? WHERE uuid = ?")) {
                preparedStatement.setInt(1, wins);
                preparedStatement.setString(2, player.getUniqueId().toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to update player wins. Error message: " + e.getMessage());
            }
        });
    }
}
