package net.nutchi.dropbroker;

import net.nutchi.dropbroker.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropBroker extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
