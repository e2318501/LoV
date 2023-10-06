package net.nutchi.multidungeon;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public final class MultiDungeon extends JavaPlugin {
    private final DungeonManager dungeonManager = new DungeonManager(this);
    private final DungeonGenerator dungeonGenerator = new DungeonGenerator(this);
    private final Storage storage = new Storage(this);

    @Override
    public void onEnable() {
        if (storage.connect()) {
            storage.init();
            storage.loadDungeons();
        } else {
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        storage.shutdown();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            if (args[0].equals("create") && args.length == 2) {
                if (dungeonManager.addDungeon(args[1])) {
                    sender.sendMessage(ChatColor.GREEN + "ダンジョンを作成しました！");
                } else {
                    sender.sendMessage(ChatColor.RED + "同じ名前のダンジョンが既に存在しています！");
                }

                return true;
            } else if (args[0].equals("delete") && args.length == 2) {
                dungeonManager.removeDungeon(args[1]);
                sender.sendMessage(ChatColor.GREEN + "ダンジョンを削除しました！");

                return true;
            } else if (args[0].equals("setPlayerLimit") && args.length == 3) {
                if (dungeonManager.setPlayerLimit(args[1], Integer.parseInt(args[2]))) {
                    sender.sendMessage(ChatColor.GREEN + "ダンジョンの定員を" + args[2] + "人に設定しました！");
                } else {
                    sender.sendMessage(ChatColor.RED + "そのような名前のダンジョンは存在しません！");
                }

                return true;
            } else if (args[0].equals("addReplica") && args.length == 2 && sender instanceof Player) {
                Player player = (Player) sender;
                if (dungeonManager.addReplica(args[1], player.getLocation())) {
                    sender.sendMessage(ChatColor.GREEN + "ダンジョンの複製を追加しました！");
                } else {
                    sender.sendMessage(ChatColor.RED + "そのような名前のダンジョンは存在しません！");
                }

                return true;
            } else if (args[0].equals("listReplicas") && args.length == 2) {
                sender.sendMessage(dungeonManager.getDungeonReplicaInfo(args[1]));

                return true;
            } else if (args[0].equals("removeReplica") && args.length == 3) {
                if (dungeonManager.removeDungeonReplica(args[1], Integer.parseInt(args[2]))) {
                    sender.sendMessage(ChatColor.GREEN + "ダンジョンの複製を削除しました！");
                } else {
                    sender.sendMessage(ChatColor.RED + "そのような名前のダンジョンは存在しません！");
                }

                return true;
            } else if (args[0].equals("save") && args.length == 2 && sender instanceof Player) {
                if (dungeonManager.saveDungeon((Player) sender, args[1])) {
                    sender.sendMessage(ChatColor.GREEN + "ダンジョンを保存しました！");
                } else {
                    sender.sendMessage(ChatColor.RED + "そのような名前のダンジョンは存在しません！");
                }

                return true;
            } else if (args[0].equals("finish") && args.length == 3) {
                dungeonManager.finishDungeonReplica(args[1], Integer.parseInt(args[2]));

                return true;
            } else if (args[0].equals("joinOrdered") && args.length == 3) {
                getPlayer(args[2]).ifPresent(p -> dungeonManager.joinDungeonOrdered(args[1], p));

                return true;
            } else if (args[0].equals("joinRoundRobin") && args.length == 3) {
                getPlayer(args[2]).ifPresent(p -> dungeonManager.joinDungeonRoundRobin(args[1], p));

                return true;
            } else if (args[0].equals("joinAlone") && args.length == 3) {
                getPlayer(args[2]).ifPresent(p -> dungeonManager.joinDungeonAlone(args[1], p));

                return true;
            }
        }

        return false;
    }

    Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(getServer().getPlayer(name));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("create", "delete", "setPlayerLimit", "addReplica", "listReplicas", "removeReplica", "save", "finish", "joinOrdered", "joinRoundRobin", "joinAlone"), args[0]);
        } else if (args.length == 2) {
            return filter(dungeonManager.getDungeonNames(), args[1]);
        } else if (args.length == 3) {
            switch (args[0]) {
                case "setPlayerLimit":
                    return filter(Collections.singletonList("10"), args[2]);
                case "removeReplica":
                case "finish":
                    if (dungeonManager.getDungeons().containsKey(args[1])) {
                        return filter(dungeonManager.getDungeons().get(args[1]).getDungeonReplicas().stream().map(r -> String.valueOf(r.getId())).collect(Collectors.toList()), args[2]);
                    } else {
                        return Collections.emptyList();
                    }
                case "joinOrdered":
                case "joinRoundRobin":
                case "joinAlone":
                    return filter(getServer().getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()), args[2]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> source, String typing) {
        return source.stream().filter(s -> s.toLowerCase().startsWith(typing.toLowerCase())).collect(Collectors.toList());
    }
}
