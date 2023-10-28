package net.nutchi.multidungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class Dungeon {
    private final List<DungeonReplica> dungeonReplicas = new ArrayList<>();
    private final int playerLimit;
    private final List<String> multiPlayWaitingPlayers = new ArrayList<>();

    public int addReplica(Location startLoc) {
        int id = dungeonReplicas.stream().mapToInt(DungeonReplica::getId).max().orElse(0) + 1;
        dungeonReplicas.add(new DungeonReplica(id, startLoc));
        return id;
    }

    public void removeReplica(int id) {
        dungeonReplicas.removeIf(r -> r.getId() == id);
    }

    public Optional<Location> getPlayerPlayingReplicaStartLocation(UUID uuid) {
        return dungeonReplicas.stream()
                .filter(r -> r.getPlayerLastLocations().containsKey(uuid))
                .map(DungeonReplica::getStartLocation)
                .findAny();
    }

    @Nullable
    public Location getReplicaStartLocation(int id) {
        return dungeonReplicas.stream()
                .filter(r -> r.getId() == id)
                .findAny()
                .map(DungeonReplica::getStartLocation)
                .orElse(null);
    }

    public void setReplicaLock(int id, boolean locked) {
        dungeonReplicas.stream()
                .filter(r -> r.getId() == id)
                .findAny()
                .ifPresent(r -> r.setLocked(locked));
    }

    public void leavePlayersFromReplica(JavaPlugin plugin, int id) {
        dungeonReplicas.stream()
                .filter(r -> r.getId() == id)
                .findAny()
                .ifPresent(r -> r.leavePlayers(plugin));
    }

    public String getReplicaInfo(MultiDungeon plugin) {
        return dungeonReplicas.stream().map(r -> r.getInfo(plugin)).collect(Collectors.joining("\n"));
    }

    public void playSingle(MultiDungeon plugin, Player player) {
        DungeonReplica replica = getAvailableReplica();

        if (replica != null) {
            replica.joinPlayer(player.getUniqueId(), player.getLocation());
            replica.setLocked(true);
            player.teleport(replica.getStartLocation());

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getStorage().savePlayerLastLocations(replica));
        } else {
            player.sendMessage(ChatColor.GREEN + "ダンジョンが定員に達しているためしばらくお待ちください");
        }
    }

    public void playMulti(MultiDungeon plugin, Player player) {
        if (multiPlayWaitingPlayers.isEmpty() || multiPlayWaitingPlayers.size() < playerLimit) {
            multiPlayWaitingPlayers.add(player.getName());
            player.sendMessage(ChatColor.GREEN + "他のプレイヤーを待機しています...");
        } else {
            DungeonReplica replica = getAvailableReplica();

            if (replica != null) {
                replica.joinPlayer(player.getUniqueId(), player.getLocation());
                player.teleport(replica.getStartLocation());

                multiPlayWaitingPlayers.forEach(name -> plugin.getPlayer(name).ifPresent(p -> {
                    replica.joinPlayer(p.getUniqueId(), p.getLocation());
                    p.teleport(replica.getStartLocation());
                }));

                replica.setLocked(true);

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getStorage().savePlayerLastLocations(replica));
            } else {
                player.sendMessage(ChatColor.GREEN + "ダンジョンが定員に達しているためしばらくお待ちください");
            }
        }
    }

    public void cancelMultiPlayWaiting(Player player) {
        if (multiPlayWaitingPlayers.contains(player.getName())) {
            multiPlayWaitingPlayers.remove(player.getName());
            player.sendMessage(ChatColor.GREEN + "ダンジョンへの参加をキャンセルしました");
        }
    }

    public boolean joinOrdered(Player player) {
        DungeonReplica replica = dungeonReplicas.stream()
                .filter(r -> r.getPlayerNumber() < playerLimit)
                .filter(r -> !r.isLocked())
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
                .filter(r -> !r.isLocked())
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
                .filter(r -> !r.isLocked())
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

    @Nullable
    private DungeonReplica getAvailableReplica() {
        return dungeonReplicas.stream()
                .filter(r -> r.getPlayerNumber() < playerLimit)
                .filter(r -> !r.isLocked())
                .min(Comparator.comparingInt(DungeonReplica::getPlayerNumber))
                .orElse(null);
    }

    public String getInfo() {
        return "replicas (" + dungeonReplicas.size() + "): " + dungeonReplicas.stream().map(DungeonReplica::getId).collect(Collectors.toList()) + " (`/md listReplicas <dungeon>` for more info)\n" +
                "player limit: " + playerLimit + "\n" +
                "waiting players (" + multiPlayWaitingPlayers.size() + "): " + multiPlayWaitingPlayers;
    }
}
