package net.nutchi.doorlock;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SelectingManager {
    private final Map<Player, Consumer<Block>> queue = new HashMap<>();

    public void onSelect(Player player, Consumer<Block> consumer) {
        queue.put(player, consumer);
    }

    public boolean select(Player player, Block block) {
        if (queue.containsKey(player)) {
            queue.get(player).accept(block);
            queue.remove(player);

            return true;
        } else {
            return false;
        }
    }

    public boolean selectTwice(Player player, Block block1, Block block2) {
        if (queue.containsKey(player)) {
            queue.get(player).accept(block1);
            queue.get(player).accept(block2);
            queue.remove(player);

            return true;
        } else {
            return false;
        }
    }

    public void endSelecting(Player player) {
        queue.remove(player);
    }
}
