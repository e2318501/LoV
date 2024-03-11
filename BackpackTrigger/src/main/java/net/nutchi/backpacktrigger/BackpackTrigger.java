package net.nutchi.backpacktrigger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class BackpackTrigger extends JavaPlugin implements Listener {
    private static final int triggerSlot = 8;
    private static final Material triggerItemMaterial = Material.PAPER;
    private static final String triggerItemDisplayName = "クリックでメニューを開く";
    private static final int triggerItemCustomModelData = 10048;
    private static final String triggerCommand = "commandpanel menu";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, () -> getServer().getOnlinePlayers().forEach(this::setTriggerItem) , 0, 100);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == triggerSlot && event.getCurrentItem() != null && isTriggerItem(event.getCurrentItem())) {
            getServer().getScheduler().runTask(this, () -> ((Player) event.getWhoClicked()).performCommand(triggerCommand));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null && event.getPlayer().getInventory().getHeldItemSlot() == triggerSlot && isTriggerItem(item)) {
                event.getPlayer().performCommand(triggerCommand);
                event.setCancelled(true);
            }
        }
    }

    private void setTriggerItem(Player player) {
        ItemStack current = player.getInventory().getItem(triggerSlot);
        if (current == null || !isTriggerItem(current)) {
            player.getInventory().setItem(triggerSlot, getTriggerItem());
        }
    }

    private ItemStack getTriggerItem() {
        ItemStack item = new ItemStack(triggerItemMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(triggerItemDisplayName);
        meta.setCustomModelData(triggerItemCustomModelData);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isTriggerItem(ItemStack item) {
        return item.getType() == triggerItemMaterial &&
                item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(triggerItemDisplayName) &&
                item.getItemMeta().hasCustomModelData() &&
                item.getItemMeta().getCustomModelData() == triggerItemCustomModelData;
    }
}
