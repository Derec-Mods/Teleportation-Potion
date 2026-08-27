package io.github.derexxd.pioneerCompass;

import io.github.derexxd.pioneerCompass.potion.PotionConsumeListener;
import io.github.derexxd.pioneerCompass.potion.PotionKeys;
import io.github.derexxd.pioneerCompass.potion.TeleportationPotionItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class PioneerCompass extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PotionKeys keys = new PotionKeys(this);
        TeleportationPotionItem teleportationPotion = new TeleportationPotionItem(this, keys);
        getServer().getPluginManager().registerEvents(new PotionConsumeListener(teleportationPotion), this);

        Bukkit.getLogger().info("");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|                             " +
                "  ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  | Derex |     Pioneer Compass v" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|     Running on " + Bukkit.getName() + " - " + Bukkit.getVersion());
        Bukkit.getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
