package io.github.derexxd.teleportationPotion;

import io.github.derexxd.teleportationPotion.potion.PotionConsumeListener;
import io.github.derexxd.teleportationPotion.potion.PotionKeys;
import io.github.derexxd.teleportationPotion.potion.TeleportationPotionItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportationPotion extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PotionKeys keys = new PotionKeys(this);
        TeleportationPotionItem teleportationPotion = new TeleportationPotionItem(this, keys);
        getServer().getPluginManager().registerEvents(new PotionConsumeListener(teleportationPotion), this);

        Bukkit.getLogger().info("");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|                             " +
                "  ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  | Derex |     TeleportationPotion v" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|     Running on " + Bukkit.getName() + " - " + Bukkit.getVersion());
        Bukkit.getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
