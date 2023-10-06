package net.nutchi.doorlock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@RequiredArgsConstructor
public class LockedBlock {
    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final Material keyType;
    private final int customModelData;

    public boolean isSameBlock(Block block) {
        return block.getWorld().equals(world) && block.getX() == x && block.getY() == y && block.getZ() == z;
    }

    public boolean isCorrectKey(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return item.getType().equals(keyType) && meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == customModelData;
    }
}
