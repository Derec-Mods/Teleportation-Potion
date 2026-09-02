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
import org.bukkit.entity.Player;

public final class RtpService {

    private final TeleportationPotion plugin;
    private final LocationPool pool;
    private final LocationFinder finder;

    public RtpService(TeleportationPotion plugin, LocationPool pool, LocationFinder finder) {
        this.plugin = plugin;
        this.pool = pool;
        this.finder = finder;
    }

    public void teleport(Player player) {
        Location location = pool.poll();
        if (location != null) {
            player.teleport(location);
            refillAsync(location.getWorld());
            return;
        }

        World world = resolveWorld(player);
        if (world == null) {
            return;
        }

        finder.find(world).thenAccept(found ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline() || found == null) {
                        return;
                    }
                    player.teleport(found);
                    refillAsync(found.getWorld());
                })
        );
    }

    private void refillAsync(World world) {
        if (world == null) {
            return;
        }
        finder.find(world).thenAccept(replacement -> {
            if (replacement != null) {
                pool.offer(replacement);
            }
        });
    }

    private World resolveWorld(Player player) {
        String worldName = plugin.getConfig().getString("rtp.world", "world");
        World configured = plugin.getServer().getWorld(worldName);
        if (configured != null) {
            return configured;
        }
        return player.getWorld();
    }
}
