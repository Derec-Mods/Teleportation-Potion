/*
 * Pre-warmed location pool inspired by FastRTP (WinSMP)
 * https://github.com/WinSMP/FastRTP
 *
 * On-demand / async chunk validation referenced from BetterRTP (SuperRonanCraft)
 * https://github.com/SuperRonanCraft/BetterRTP
 */
package io.github.derexxd.teleportationPotion.rtp;

import org.bukkit.Location;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class LocationPool {

    private final ConcurrentLinkedQueue<Location> locations = new ConcurrentLinkedQueue<>();

    public void offer(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        locations.offer(location.clone());
    }

    public Location poll() {
        Location location = locations.poll();
        return location == null ? null : location.clone();
    }

    public int size() {
        return locations.size();
    }

    public boolean isEmpty() {
        return locations.isEmpty();
    }

    public void clear() {
        locations.clear();
    }
}
