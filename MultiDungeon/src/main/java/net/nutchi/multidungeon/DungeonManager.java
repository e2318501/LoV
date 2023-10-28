package net.nutchi.multidungeon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class DungeonManager {
    private final MultiDungeon plugin;
    @Getter
    private final Map<String, Dungeon> dungeons = new HashMap<>();

    public boolean addDungeon(String name) {
        if (!dungeons.containsKey(name)) {
            dungeons.put(name, new Dungeon(10));

            plugin.getStorage().saveDungeon(name);
            return true;
        } else {
            return false;
        }
    }

    public void removeDungeon(String name) {
        dungeons.remove(name);
        plugin.getStorage().deleteDungeon(name);
    }

    public boolean setPlayerLimit(String name, int playerLimit) {
        if (dungeons.containsKey(name)) {
            dungeons.put(name, new Dungeon(playerLimit));

            plugin.getStorage().savePlayerLimit(name, playerLimit);
            return true;
        } else {
            return false;
        }
    }

    public boolean addReplica(String name, Location startLoc) {
        if (dungeons.containsKey(name)) {
            int id = dungeons.get(name).addReplica(startLoc);

            plugin.getStorage().saveReplica(id, name, startLoc);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeDungeonReplica(String name, int id) {
        if (dungeons.containsKey(name)) {
            dungeons.get(name).removeReplica(id);

            plugin.getStorage().deleteReplica(id, name);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Location> getPlayerPlayingReplicaStartLocation(UUID uuid) {
        return dungeons.values()
                .stream()
                .map(d -> d.getPlayerPlayingReplicaStartLocation(uuid))
                .filter(Optional::isPresent)
                .findAny()
                .flatMap(l -> l);
    }

    public List<String> getDungeonNames() {
        return new ArrayList<>(dungeons.keySet());
    }

    public String getDungeonInfo(String name) {
        if (dungeons.containsKey(name)) {
            return dungeons.get(name).getInfo();
        } else {
            return "";
        }
    }

    public String getDungeonReplicaInfo(String name) {
        if (dungeons.containsKey(name)) {
            return dungeons.get(name).getReplicaInfo(plugin);
        } else {
            return "";
        }
    }

    public boolean saveDungeon(Player player, String name) {
        if (dungeons.containsKey(name)) {
            plugin.getDungeonGenerator().saveDungeon(player, name);
            return true;
        } else {
            return false;
        }
    }

    public void playSingle(String name, Player player) {
        if (dungeons.containsKey(name)) {
            dungeons.get(name).playSingle(plugin, player);
        }
    }

    public void playMulti(String name, Player player) {
        if (dungeons.containsKey(name)) {
            dungeons.get(name).playMulti(plugin, player);
        }
    }

    public void cancelMultiPlayWaiting(Player player) {
        dungeons.forEach((name, dungeon) -> dungeon.cancelMultiPlayWaiting(player));
    }

    public void joinDungeonOrdered(String name, Player player) {
        if (dungeons.containsKey(name)) {
            if (!dungeons.get(name).joinOrdered(player)) {
                player.sendMessage(ChatColor.GOLD + "ただいまダンジョンは満員です！");
            }
        }
    }

    public void joinDungeonRoundRobin(String name, Player player) {
        if (dungeons.containsKey(name)) {
            if (!dungeons.get(name).joinRoundRobin(player)) {
                player.sendMessage(ChatColor.GOLD + "ただいまダンジョンは満員です！");
            }
        }
    }

    public void joinDungeonAlone(String name, Player player) {
        if (dungeons.containsKey(name)) {
            if (!dungeons.get(name).joinAlone(player)) {
                player.sendMessage(ChatColor.GOLD + "ただいまダンジョンは満員です！");
            }
        }
    }

    public void finishDungeonReplica(String name, int id) {
        if (dungeons.containsKey(name)) {
            dungeons.get(name).leavePlayersFromReplica(plugin, id);

            dungeons.get(name).setReplicaLock(id, false);
            Location loc = dungeons.get(name).getReplicaStartLocation(id);
            if (loc != null) {
                plugin.getDungeonGenerator().restoreDungeon(name, loc);
            }
        }
    }
}
