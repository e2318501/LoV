package net.nutchi.itemtransactionlimiter.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.itemtransactionlimiter.ItemTransactionLimiter;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final ItemTransactionLimiter plugin;

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            event.setCancelled(true);
        }
    }
}
