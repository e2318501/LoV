package net.nutchi.multidungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class DungeonReplica {
    private final int id;
    private final Location startLocation;
    private final Map<UUID, Location> playerLastLocations = new HashMap<>();

    public int getPlayerNumber() {
        return playerLastLocations.size();
    }

    public String getInfo() {
        return id + " " + startLocation.getWorld().getName() + " " + startLocation.getX() + " / " + startLocation.getY() + " / " + startLocation.getZ() + " online: " + getPlayerNumber();
    }

    public void joinPlayer(UUID uuid, Location originalLocation) {
        playerLastLocations.put(uuid, originalLocation);
    }

    public void leavePlayers(JavaPlugin plugin) {
        playerLastLocations.forEach(((uuid, loc) -> {
           Player player = plugin.getServer().getPlayer(uuid);
           if (player != null) {
               // player.teleport(loc);
           }
        }));

        playerLastLocations.clear();
    }
}
