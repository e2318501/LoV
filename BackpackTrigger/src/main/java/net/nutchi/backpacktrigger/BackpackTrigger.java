package net.nutchi.backpacktrigger;

import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class BackpackTrigger extends JavaPlugin implements Listener {
    private static final int triggerSlot = 9;
    private static final Material triggerMaterial = Material.PAPER;
    private static final int triggerItemCustomModelData = 10048;
    private static final String triggerCommand = "backpack";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, () -> getServer().getOnlinePlayers().forEach(this::setTriggerItem) , 0, 100);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == triggerSlot && event.getCurrentItem() != null && isTriggerItem(event.getCurrentItem()) {
            getServer().getScheduler().runTask(this, () -> ((Player) event.getWhoClicked()).performCommand(triggerCommand));
            event.setCancelled(true);
        }
    }

    private void setTriggerItem(Player player) {
        ItemStack current = player.getInventory().getItem(triggerSlot);
        if (current == null || !isTriggerItem(current)) {
            player.getInventory().setItem(triggerSlot, getTriggerItem());
        }
    }

    private ItemStack getTriggerItem() {
        ItemStack item = new ItemStack(triggerMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("クリックでバックパックを開く");
        meta.setCustomModelData(triggerItemCustomModelData);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isTriggerItem(ItemStack item) {
        return item.getType() == triggerMaterial &&
                item.getItemMeta() != null &&
                item.getItemMeta().hasCustomModelData() &&
                item.getItemMeta().getCustomModelData() == triggerItemCustomModelData;
    }
}
