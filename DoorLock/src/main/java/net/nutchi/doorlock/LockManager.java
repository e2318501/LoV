package net.nutchi.doorlock;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LockManager {
    private final DoorLock plugin;

    private final List<LockedBlock> lockedBlocks = new ArrayList<>();

    public void loadFromStorage() {
        lockedBlocks.addAll(plugin.getStorage().loadLockedBlocks());
    }

    @Nullable
    public LockedBlock getLockedBlock(Block block) {
        return lockedBlocks.stream()
                .filter(b -> b.isSameBlock(block))
                .findAny()
                .orElse(null);
    }

    public void createLock(Block block, Material keyType, int customModelData) {
        lockedBlocks.removeIf(b -> b.isSameBlock(block));

        LockedBlock lockedBlock = new LockedBlock(block.getWorld(), block.getX(), block.getY(), block.getZ(), keyType, customModelData);
        lockedBlocks.add(lockedBlock);

        plugin.getStorage().saveLockedBlock(lockedBlock);
    }

    public void deleteLock(Block block) {
        lockedBlocks.removeIf(b -> b.isSameBlock(block));

        plugin.getStorage().deleteLockedBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }
}
