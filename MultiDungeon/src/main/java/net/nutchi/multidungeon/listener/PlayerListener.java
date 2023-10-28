package net.nutchi.multidungeon.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.multidungeon.MultiDungeon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final MultiDungeon plugin;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (plugin.getBossRoom().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
