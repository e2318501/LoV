package net.nutchi.personalchest;

import lombok.Getter;
import net.nutchi.personalchest.listener.BlockListener;
import net.nutchi.personalchest.listener.InventoryListener;
import net.nutchi.personalchest.listener.PlayerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class PersonalChest extends JavaPlugin {
    private final PChestManager pChestManager = new PChestManager(this);
    private final Storage storage = new Storage(this);

    @Override
    public void onEnable() {
        if (storage.connect()) {
            storage.init();
            saveDefaultConfig();
            register();
            startSaveTask();
        } else {
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
    }

    private void startSaveTask() {
        getServer().getScheduler().runTaskTimer(this, pChestManager::saveChests, 0, 1200);
    }

    @Override
    public void onDisable() {
        pChestManager.unloadChests();
        storage.shutdown();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage("PersonalChest config reloaded.");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
