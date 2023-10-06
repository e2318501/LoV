package net.nutchi.doorlock;

import lombok.Getter;
import net.nutchi.doorlock.listener.BlockListener;
import net.nutchi.doorlock.listener.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class DoorLock extends JavaPlugin {
    private final LockManager lockManager = new LockManager(this);
    private final SelectingManager selectingManager = new SelectingManager();
    private final Storage storage = new Storage(this);

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new BlockListener(this), this);

        if (storage.connect()) {
            storage.init();
            lockManager.loadFromStorage();
        } else {
            pm.disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && sender instanceof Player) {
            Player player = (Player) sender;

            if (args[0].equals("create")) {
                if (args.length == 3) {
                    Material keyType = Material.valueOf(args[1]);
                    int customModelData = Integer.parseInt(args[2]);

                    sender.sendMessage(ChatColor.GREEN + "ロックするドアをクリックしてください");

                    selectingManager.onSelect(player, block -> {
                        lockManager.createLock(block, keyType, customModelData);
                        sender.sendMessage(ChatColor.GREEN + "ドアをロックしました！");
                    });

                    return true;
                }
            } else if (args[0].equals("delete")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.GREEN + "ロックを削除するドアをクリックしてください");

                    selectingManager.onSelect(player, block -> {
                        lockManager.deleteLock(block);
                        sender.sendMessage(ChatColor.GREEN + "ドアのロックを削除しました！");
                    });

                    return true;
                }
            } else if (args[0].equals("cancel")) {
                if (args.length == 1) {
                    selectingManager.endSelecting(player);

                    sender.sendMessage(ChatColor.GREEN + "キャンセルしました");

                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Stream.of("create", "delete", "cancel"), args[0]);
        } else if (args.length == 2 && args[0].equals("create")) {
            return filter(Arrays.stream(Material.values()).map(Objects::toString), args[1]);
        } else if (args.length == 3 && args[0].equals("create")) {
            return filter(Stream.of("1"), args[2]);
        } else {
            return Collections.emptyList();
        }
    }

    private List<String> filter(Stream<String> source, String typing) {
        return source.filter(s -> s.toLowerCase().startsWith(typing.toLowerCase())).collect(Collectors.toList());
    }
}
