package net.nutchi.multidungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class DungeonReplica {
    private final int id;
    private final Location startLocation;
    private final Map<UUID, Location> playerLastLocations = new HashMap<>();
    private boolean locked = false;

    public int getPlayerNumber() {
        return playerLastLocations.size();
    }

    public String getInfo(MultiDungeon plugin) {
        return "id: " + id + "; loc: (" + startLocation.getWorld().getName() + ", " + startLocation.getBlockX() + ", " + startLocation.getBlockY() + ", " + startLocation.getBlockZ() + "); online (" + getPlayerNumber() + "): " + getOnlinePlayers(plugin);
    }

    private List<String> getOnlinePlayers(MultiDungeon plugin) {
        return playerLastLocations.keySet()
                .stream()
                .map(plugin::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(HumanEntity::getName)
                .collect(Collectors.toList());
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
