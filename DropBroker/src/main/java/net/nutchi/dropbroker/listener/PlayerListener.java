package net.nutchi.dropbroker.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.dropbroker.DropBroker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final DropBroker plugin;

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
