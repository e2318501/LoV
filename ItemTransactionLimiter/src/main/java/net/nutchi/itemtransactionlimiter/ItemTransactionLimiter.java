package net.nutchi.itemtransactionlimiter;

import net.nutchi.itemtransactionlimiter.listener.InventoryListener;
import net.nutchi.itemtransactionlimiter.listener.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemTransactionLimiter extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
