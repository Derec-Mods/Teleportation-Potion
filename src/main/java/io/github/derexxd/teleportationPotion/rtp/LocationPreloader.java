/*
 * Pre-warmed location pool inspired by FastRTP (WinSMP)
 * https://github.com/WinSMP/FastRTP
 *
 * On-demand / async chunk validation referenced from BetterRTP (SuperRonanCraft)
 * https://github.com/SuperRonanCraft/BetterRTP
 */
package io.github.derexxd.teleportationPotion.rtp;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public final class LocationPreloader {

    private final TeleportationPotion plugin;
    private final LocationPool pool;
    private final LocationFinder finder;
    private BukkitTask task;

    public LocationPreloader(TeleportationPotion plugin, LocationPool pool, LocationFinder finder) {
        this.plugin = plugin;
        this.pool = pool;
        this.finder = finder;
    }

    public void start() {
        if (task != null) {
            return;
        }
        long interval = plugin.getConfig().getLong("rtp.refill-interval-ticks", 100);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 20L, Math.max(1L, interval));
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void refill() {
        tick();
    }

    private void tick() {
        int poolSize = plugin.getConfig().getInt("rtp.pool-size", 16);
        if (pool.size() >= poolSize) {
            stop();
            return;
        }

        World world = plugin.getServer().getWorld(plugin.getConfig().getString("rtp.world", "world"));
        if (world == null) {
            return;
        }

        int missing = poolSize - pool.size();
        int attempts = Math.min(plugin.getConfig().getInt("rtp.attempts-per-cycle", 2), missing);
        for (int i = 0; i < attempts; i++) {
            finder.find(world).thenAccept(location -> {
                if (location != null && pool.size() < poolSize) {
                    pool.offer(location);
                }
            });
        }
    }
}
