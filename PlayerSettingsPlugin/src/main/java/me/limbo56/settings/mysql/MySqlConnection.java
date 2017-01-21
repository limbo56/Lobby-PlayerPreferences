package me.limbo56.settings.mysql;

import me.limbo56.settings.PlayerSettings;
import org.bukkit.Bukkit;

import java.sql.*;

/**
 * Created by lim_bo56
 * On 8/11/2016
 * At 8:05 PM
 */
public class MySqlConnection {

    private static MySqlConnection instance = new MySqlConnection();

    private Connection connection;

    private String host = PlayerSettings.getInstance().getConfig().getString("MySQL.host");
    private String port = PlayerSettings.getInstance().getConfig().getString("MySQL.port");
    private String database = PlayerSettings.getInstance().getConfig().getString("MySQL.database");
    private String name = PlayerSettings.getInstance().getConfig().getString("MySQL.name");
    private String password = PlayerSettings.getInstance().getConfig().getString("MySQL.password");

    public static MySqlConnection getInstance() {
        return instance;
    }

    public void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, name, password);
            PlayerSettings.getInstance().log("Connected to database");
        } catch (SQLException exception) {
            Bukkit.getLogger().severe("Couldn't connect to the database.");
            Bukkit.getLogger().severe("Please, check config.yml for any errors on the database info!");
        }

    }

    public boolean checkTable() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "playersettings", null);
            return tables.next();
        } catch (SQLException exception) {
            return false;
        }
    }

    public void createTable() {
        Bukkit.getScheduler().runTaskAsynchronously(PlayerSettings.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        getCurrentConnection().createStatement()
                                .execute("CREATE TABLE IF NOT EXISTS `PlayerSettings` ("
                                        + "`UUID` varchar(36) NOT NULL,"
                                        + "`Visibility` TINYINT(0) DEFAULT NULL,"
                                        + "`Stacker` TINYINT(0) DEFAULT NULL,"
                                        + "`Chat` TINYINT(0) DEFAULT NULL,"
                                        + "`Vanish` TINYINT(0) DEFAULT NULL,"
                                        + "`Fly` TINYINT(0) DEFAULT NULL,"
                                        + "`Speed` TINYINT(0) DEFAULT NULL,"
                                        + "`Jump` TINYINT(0) DEFAULT NULL,"
                                        + "`Radio` TINYINT(0) DEFAULT NULL, PRIMARY KEY (UUID))");
                    }

                } catch (SQLException e) {
                    Bukkit.getLogger().severe("Couldn't create tables on the database ;(.");
                }
            }
        });
    }

    public void updateTable() {
        try {
            if (connection != null && !connection.isClosed()) {
                ResultSetMetaData table = getCurrentConnection().createStatement().executeQuery("SELECT * FROM `PlayerSettings`").getMetaData();

                boolean found = false;
                for (int i = 1; i <= table.getColumnCount(); i++) {
                    if ("Radio".equals(table.getColumnName(i)))
                        found = true;
                }

                if (!found) {
                    getCurrentConnection().createStatement().executeUpdate("ALTER TABLE `PlayerSettings` ADD `Radio` TINYINT(0) DEFAULT NULL");
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Couldn't update tables on the database ;(.");
        }
    }

    public Connection getCurrentConnection() {
        return connection;
    }
}