package net.nutchi.applepie;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ApplePie extends JavaPlugin{
    @Override
    public void onEnable() {
        ItemStack applePie = new ItemStack(Material.PUMPKIN_PIE);
        ItemMeta meta = applePie.getItemMeta();
        meta.setDisplayName("アップルパイ");
        meta.setCustomModelData(1);
        applePie.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(this, "apple_pie");
        ShapedRecipe recipe = new ShapedRecipe(key, applePie);

        recipe.shape("   ", "AS ", " E ");
        recipe.setIngredient('A', Material.APPLE);
        recipe.setIngredient('S', Material.SUGAR);
        recipe.setIngredient('E', Material.EGG);

        getServer().addRecipe(recipe);
    }
}
