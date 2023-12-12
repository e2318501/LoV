package net.nutchi.regionmusic.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.regionmusic.RegionMusic;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class RegionListener implements Listener {
    private final RegionMusic plugin;

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        if (event.getPlayer() != null) {
            plugin.getMusicManager().updateMusicLater(event.getPlayer());
        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        plugin.getMusicManager().updateMusicLater(event.getPlayer());
    }
}
