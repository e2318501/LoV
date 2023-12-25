package net.nutchi.autodoorcloser.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.autodoorcloser.AutoDoorCloser;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final AutoDoorCloser plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        boolean isTargetAction = event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK);
        Block clicked = event.getClickedBlock();
        if (isTargetAction && clicked != null && clicked.getBlockData() instanceof Door) {
            Door door = (Door) clicked.getBlockData();
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (door.isOpen()) {
                    door.setOpen(false);
                    clicked.setBlockData(door);
                }
            }, 200);
        }
    }
}
