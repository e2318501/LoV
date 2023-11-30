package net.nutchi.regionmusic.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.regionmusic.RegionMusic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final RegionMusic plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getMusicManager().clearPlayer(event.getPlayer());
    }
}
