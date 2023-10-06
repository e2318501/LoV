package net.nutchi.doorlock;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Storage {
    private final DoorLock plugin;

    private Connection connection;

    public boolean connect() {
        try {
            File file = new File(plugin.getDataFolder(), "database.db");
            if (!file.exists()) {
                plugin.getDataFolder().mkdir();
                file.createNewFile();
            }

            String url = "jdbc:sqlite:" + file.getAbsolutePath().replace("\\", "/");
            connection = DriverManager.getConnection(url);

            return true;
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS doorlock (" +
                "world TEXT NOT NULL," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "key_type TEXT NOT NULL," +
                "custom_model_data INTEGER NOT NULL," +
                "UNIQUE(world, x, y, z)" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<LockedBlock> loadLockedBlocks() {
        List<LockedBlock> lockedBlocks = new ArrayList<>();

        String sql = "SELECT * FROM doorlock";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String worldString = resultSet.getString("world");
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                Material keyType = Material.valueOf(resultSet.getString("key_type"));
                int customModelData = resultSet.getInt("custom_model_data");

                World world = plugin.getServer().getWorld(worldString);
                if (world != null) {
                    lockedBlocks.add(new LockedBlock(world, x, y, z, keyType, customModelData));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lockedBlocks;
    }

    public void saveLockedBlock(LockedBlock lockedBlock) {
        String sql = "INSERT OR REPLACE INTO doorlock(world, x, y, z, key_type, custom_model_data) values(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, lockedBlock.getWorld().getName());
            statement.setInt(2, lockedBlock.getX());
            statement.setInt(3, lockedBlock.getY());
            statement.setInt(4, lockedBlock.getZ());
            statement.setString(5, lockedBlock.getKeyType().toString());
            statement.setInt(6, lockedBlock.getCustomModelData());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteLockedBlock(String world, int x, int y, int z) {
        String sql = "DELETE FROM doorlock WHERE world = ? AND x = ? AND y = ? AND z = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, world);
            statement.setInt(2, x);
            statement.setInt(3, y);
            statement.setInt(4, z);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
