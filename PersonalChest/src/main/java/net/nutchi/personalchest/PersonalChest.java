package net.nutchi.personalchest;

import lombok.Getter;
import net.nutchi.personalchest.listener.BlockListener;
import net.nutchi.personalchest.listener.InventoryListener;
import net.nutchi.personalchest.listener.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class PersonalChest extends JavaPlugin {
    private final PChestManager pChestManager = new PChestManager(this);
    private final Storage storage = new Storage(this);

    @Override
    public void onEnable() {
        if (storage.connect()) {
            storage.init();
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
}
