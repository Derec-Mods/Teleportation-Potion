/*
 * Pre-warmed location pool inspired by FastRTP (WinSMP)
 * https://github.com/WinSMP/FastRTP
 *
 * On-demand / async chunk validation referenced from BetterRTP (SuperRonanCraft)
 * https://github.com/SuperRonanCraft/BetterRTP
 */
package io.github.derexxd.teleportationPotion.rtp;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class LocationPool {

    private final TeleportationPotion plugin;
    private final ConcurrentLinkedQueue<Location> locations = new ConcurrentLinkedQueue<>();

    public LocationPool(TeleportationPotion plugin) {
        this.plugin = plugin;
    }

    public void offer(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        Location copy = location.clone();
        pin(copy);
        locations.offer(copy);
    }

    public Location poll() {
        Location location = locations.poll();
        return location == null ? null : location.clone();
    }

    public void release(Location location) {
        unpin(location);
    }

    public int size() {
        return locations.size();
    }

    public boolean isEmpty() {
        return locations.isEmpty();
    }

    public void clear() {
        Location location;
        while ((location = locations.poll()) != null) {
            unpin(location);
        }
    }

    private void pin(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        world.getChunkAt(chunkX, chunkZ);
        world.addPluginChunkTicket(chunkX, chunkZ, plugin);
    }

    private void unpin(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        location.getWorld().removePluginChunkTicket(
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4,
                plugin
        );
    }
}
