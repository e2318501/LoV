package net.nutchi.multidungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Dungeon {
    private final List<DungeonReplica> dungeonReplicas = new ArrayList<>();
    private final int playerLimit;

    public int addReplica(Location startLoc) {
        int id = dungeonReplicas.stream().mapToInt(DungeonReplica::getId).max().orElse(0) + 1;
        dungeonReplicas.add(new DungeonReplica(id, startLoc));
        return id;
    }

    public void removeReplica(int id) {
        dungeonReplicas.removeIf(r -> r.getId() == id);
    }

    @Nullable
    public Location getReplicaStartLocation(int id) {
        return dungeonReplicas.stream()
                .filter(r -> r.getId() == id)
                .findAny()
                .map(DungeonReplica::getStartLocation)
                .orElse(null);
    }

    public void leavePlayersFromReplica(JavaPlugin plugin, int id) {
        dungeonReplicas.stream()
                .filter(r -> r.getId() == id)
                .findAny()
                .ifPresent(r -> r.leavePlayers(plugin));
    }

    public String getReplicaInfo() {
        return dungeonReplicas.stream().map(DungeonReplica::getInfo).collect(Collectors.joining("\n"));
    }

    public boolean joinOrdered(Player player) {
        DungeonReplica replica = dungeonReplicas.stream()
                .filter(r -> r.getPlayerNumber() < playerLimit)
                .findAny()
                .orElse(null);

        if (replica != null) {
            replica.joinPlayer(player.getUniqueId(), player.getLocation());
            player.teleport(replica.getStartLocation());
            return true;
        } else {
            return false;
        }
    }

    public boolean joinRoundRobin(Player player) {
        DungeonReplica replica = dungeonReplicas.stream()
                .filter(r -> r.getPlayerNumber() < playerLimit)
                .min(Comparator.comparingInt(DungeonReplica::getPlayerNumber))
                .orElse(null);

        if (replica != null) {
            replica.joinPlayer(player.getUniqueId(), player.getLocation());
            player.teleport(replica.getStartLocation());
            return true;
        } else {
            return false;
        }
    }

    public boolean joinAlone(Player player) {
        DungeonReplica replica = dungeonReplicas.stream()
                .filter(r -> r.getPlayerNumber() == 0)
                .findAny()
                .orElse(null);

        if (replica != null) {
            replica.joinPlayer(player.getUniqueId(), player.getLocation());
            player.teleport(replica.getStartLocation());
            return true;
        } else {
            return false;
        }
    }
}
