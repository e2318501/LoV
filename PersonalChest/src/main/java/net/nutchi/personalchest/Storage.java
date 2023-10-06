package net.nutchi.personalchest;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class Storage {
    private final PersonalChest plugin;
    private Connection connection;
    private final Gson gson = new Gson();

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

    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS personal_chests (" +
                "location text NOT NULL," +
                "player_uuid varchar(36) NOT NULL," +
                "inventory text NOT NULL," +
                "UNIQUE(location, player_uuid)" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<BlockLocation, Inventory> loadPlayerInventories(UUID uuid) {
        Map<BlockLocation, Inventory> invs = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM personal_chests WHERE player_uuid = ?")) {
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Inventory inv = fromBase64(resultSet.getString("inventory"));
                if (inv != null) {
                    invs.put(gson.fromJson(resultSet.getString("location"), BlockLocation.class), inv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invs;
    }

    public void savePlayerInventories(UUID uuid, BlockLocation loc, Inventory inv) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO personal_chests (location, player_uuid, inventory) VALUES (?, ?, ?)")) {
            String invData = toBase64(inv);
            if (invData != null) {
                statement.setString(1, gson.toJson(loc));
                statement.setString(2, uuid.toString());
                statement.setString(3, invData);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String toBase64(Inventory inv) {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream)
        ) {

            bukkitOutputStream.writeInt(inv.getSize());

            for (int i = 0; i < inv.getSize(); i++) {
                bukkitOutputStream.writeObject(inv.getItem(i));
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private @Nullable Inventory fromBase64(String data) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream)
        ) {
            Inventory inv = plugin.getServer().createInventory(null, bukkitInputStream.readInt());

            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, (ItemStack) bukkitInputStream.readObject());
            }

            return inv;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteChests(Location loc) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM personal_chests WHERE location = ?")) {
            statement.setString(1, gson.toJson(BlockLocation.fromLocation(loc)));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
