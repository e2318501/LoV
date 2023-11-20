package net.nutchi.itemtransactionlimiter.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.itemtransactionlimiter.ItemTransactionLimiter;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final ItemTransactionLimiter plugin;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType topType = event.getView().getTopInventory().getType();
        InventoryType bottomType = event.getView().getBottomInventory().getType();
        boolean chestAndPlayer = topType.equals(InventoryType.CHEST) && bottomType.equals(InventoryType.PLAYER);
        boolean playerAndPlayer = topType.equals(InventoryType.PLAYER) && bottomType.equals(InventoryType.PLAYER);
        boolean merchantAndPlayer = topType.equals(InventoryType.MERCHANT) && bottomType.equals(InventoryType.PLAYER);
        boolean craftingAndPlayer = topType.equals(InventoryType.CRAFTING) && bottomType.equals(InventoryType.PLAYER);
        boolean workbenchAndPlayer = topType.equals(InventoryType.WORKBENCH) && bottomType.equals(InventoryType.PLAYER);

        if (event.getWhoClicked().getGameMode().equals(GameMode.ADVENTURE) && !(chestAndPlayer || playerAndPlayer || merchantAndPlayer || craftingAndPlayer || workbenchAndPlayer)) {
            event.setCancelled(true);
        }
    }
}
