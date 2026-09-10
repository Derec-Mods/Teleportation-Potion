package io.github.derexxd.teleportationPotion.potion;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public final class TeleportationPotionItem {

    private final TeleportationPotion plugin;
    private final PotionKeys keys;

    public TeleportationPotionItem(TeleportationPotion plugin, PotionKeys keys) {
        this.plugin = plugin;
        this.keys = keys;
    }

    public ItemStack create() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setBasePotionData(new PotionData(PotionType.WATER));
        meta.setColor(readColor());
        meta.setDisplayName(colorize(plugin.getConfig().getString("potion.name", "&dTeleportation Potion")));
        meta.setLore(readLore());
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.getPersistentDataContainer().set(keys.teleportationPotion(), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Check if the provided potion is a tp potion (important!!)
     * Can not be faked, we check the PDC
     */
    public boolean isTeleportationPotion(ItemStack item) {
        if (item == null || item.getType() != Material.POTION) {
            return false;
        }
        if (!(item.getItemMeta() instanceof PotionMeta meta)) {
            return false;
        }
        return meta.getPersistentDataContainer().has(keys.teleportationPotion(), PersistentDataType.BYTE);
    }

    private Color readColor() {
        String hex = plugin.getConfig().getString("potion.color", "#9400D3");
        if (hex == null) {
            return Color.fromRGB(0x9400D3);
        }
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            return Color.fromRGB(Integer.parseInt(hex, 16));
        } catch (NumberFormatException ignored) {
            return Color.fromRGB(0x9400D3);
        }
    }

    private List<String> readLore() {
        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("potion.lore")) {
            lore.add(colorize(line));
        }
        if (lore.isEmpty()) {
            lore.add(colorize("&7Drink to relocate"));
        }
        return lore;
    }

    private static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
