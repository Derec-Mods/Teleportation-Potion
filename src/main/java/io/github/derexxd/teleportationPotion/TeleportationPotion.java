package io.github.derexxd.teleportationPotion;

import io.github.derexxd.teleportationPotion.commands.GiveCommand;
import io.github.derexxd.teleportationPotion.potion.PotionConsumeListener;
import io.github.derexxd.teleportationPotion.potion.PotionJoinListener;
import io.github.derexxd.teleportationPotion.potion.PotionKeys;
import io.github.derexxd.teleportationPotion.potion.TeleportationPotionItem;
import io.github.derexxd.teleportationPotion.rtp.LocationFinder;
import io.github.derexxd.teleportationPotion.rtp.LocationPool;
import io.github.derexxd.teleportationPotion.rtp.LocationPreloader;
import io.github.derexxd.teleportationPotion.rtp.RtpService;
import io.github.derexxd.teleportationPotion.rtp.SafetyChecker;
import io.github.derexxd.teleportationPotion.rtp.WorldReadyListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportationPotion extends JavaPlugin {

    private LocationPreloader preloader;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        LocationPool pool = new LocationPool();
        SafetyChecker safetyChecker = new SafetyChecker(this);
        LocationFinder finder = new LocationFinder(this, safetyChecker);
        preloader = new LocationPreloader(this, pool, finder);
        RtpService rtpService = new RtpService(this, pool, finder);

        PotionKeys keys = new PotionKeys(this);
        TeleportationPotionItem teleportationPotion = new TeleportationPotionItem(this, keys);

        getCommand("tpgive").setExecutor(new GiveCommand(teleportationPotion));
        getServer().getPluginManager().registerEvents(new PotionJoinListener(teleportationPotion), this);
        getServer().getPluginManager().registerEvents(new PotionConsumeListener(teleportationPotion, rtpService), this);
        getServer().getPluginManager().registerEvents(new WorldReadyListener(this, preloader), this);

        if (getServer().getWorld(getConfig().getString("rtp.world", "world")) != null) {
            preloader.start();
        }

        Bukkit.getLogger().info("");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|                             " +
                "  ");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  | Derex |     TeleportationPotion v" + getDescription().getVersion());
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  |_______|     Running on " + Bukkit.getName() + " - " + Bukkit.getVersion());
        Bukkit.getLogger().info("");
    }

    @Override
    public void onDisable() {
        if (preloader != null) {
            preloader.stop();
        }
    }
}
