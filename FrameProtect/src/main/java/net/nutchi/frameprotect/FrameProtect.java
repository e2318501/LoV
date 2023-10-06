package net.nutchi.frameprotect;

import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class FrameProtect extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        boolean isPlayer = event.getDamager() instanceof Player;
        boolean isItemFrame = event.getEntity() instanceof ItemFrame;
        boolean isCreativeMode = isPlayer && ((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE;

        if (isItemFrame && !isCreativeMode) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        boolean isPlayer = event.getRemover() instanceof Player;
        boolean isPainting = event.getEntity() instanceof Painting;
        boolean isCreativeMode = isPlayer && ((Player) event.getRemover()).getGameMode() == GameMode.CREATIVE;

        if (isPainting && !isCreativeMode) {
            event.setCancelled(true);
        }
    }
}
