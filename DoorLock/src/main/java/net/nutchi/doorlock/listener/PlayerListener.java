package net.nutchi.doorlock.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.doorlock.DoorLock;
import net.nutchi.doorlock.LockedBlock;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final DoorLock plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            Block clicked = event.getClickedBlock();
            if (clicked != null) {
                if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                    LockedBlock lockedBlock = plugin.getLockManager().getLockedBlock(clicked);
                    if (lockedBlock != null) {
                        if (clicked.getBlockData() instanceof Door) {
                            Door door = (Door) clicked.getBlockData();

                            if (!door.isOpen()) {
                                if (lockedBlock.isCorrectKey(event.getPlayer().getInventory().getItemInMainHand())) {
                                    event.getPlayer().sendMessage(ChatColor.GREEN + "ドアを開錠しました");

                                    event.getPlayer().getInventory().setItemInMainHand(null);

                                    /*
                                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                        door.setOpen(false);
                                        clicked.setBlockData(door);
                                    }, 100);
                                     */
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "正しい鍵が必要です");
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }

                Block pair = getDoorPair(clicked);
                if (pair != null && plugin.getSelectingManager().selectTwice(event.getPlayer(), clicked, pair)) {
                    event.setCancelled(true);
                } else if (plugin.getSelectingManager().select(event.getPlayer(), clicked)) {
                    event.setCancelled(true);
                }

            }
        }
    }

    @Nullable
    private Block getDoorPair(Block block) {
        if (block.getBlockData() instanceof Door) {
            Door door = (Door) block.getBlockData();

            if (door.getHalf() == Bisected.Half.TOP) {
                return block.getRelative(0, -1, 0);
            } else if (door.getHalf() == Bisected.Half.BOTTOM) {
                return block.getRelative(0, 1, 0);
            }
        }

        return null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getSelectingManager().endSelecting(event.getPlayer());
    }
}
