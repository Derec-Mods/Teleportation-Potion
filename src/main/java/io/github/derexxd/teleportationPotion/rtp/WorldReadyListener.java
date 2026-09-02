/*
 * Pre-warmed location pool inspired by FastRTP (WinSMP)
 * https://github.com/WinSMP/FastRTP
 *
 * On-demand / async chunk validation referenced from BetterRTP (SuperRonanCraft)
 * https://github.com/SuperRonanCraft/BetterRTP
 */
package io.github.derexxd.teleportationPotion.rtp;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public final class WorldReadyListener implements Listener {

    private final TeleportationPotion plugin;
    private final LocationPreloader preloader;

    public WorldReadyListener(TeleportationPotion plugin, LocationPreloader preloader) {
        this.plugin = plugin;
        this.preloader = preloader;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        String worldName = plugin.getConfig().getString("rtp.world", "world");
        if (event.getWorld().getName().equalsIgnoreCase(worldName)) {
            preloader.start();
        }
    }
}
