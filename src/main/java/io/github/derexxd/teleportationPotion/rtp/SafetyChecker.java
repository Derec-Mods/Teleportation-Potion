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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.EnumSet;
import java.util.Set;

public final class SafetyChecker {

    private static final Set<Material> UNSAFE = EnumSet.of(
            Material.WATER,
            Material.LAVA,
            Material.KELP,
            Material.KELP_PLANT,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.MAGMA_BLOCK,
            Material.CACTUS,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.CAMPFIRE,
            Material.SOUL_CAMPFIRE,
            Material.SWEET_BERRY_BUSH,
            Material.POWDER_SNOW,
            Material.VOID_AIR
    );

    private final TeleportationPotion plugin;

    public SafetyChecker(TeleportationPotion plugin) {
        this.plugin = plugin;
    }

    public Location findSafe(World world, int x, int z) {
        if (world == null) {
            return null;
        }

        int y = world.getHighestBlockYAt(x, z);
        if (y <= world.getMinHeight()) {
            return null;
        }

        Block ground = world.getBlockAt(x, y, z);
        Block feet = world.getBlockAt(x, y + 1, z);
        Block head = world.getBlockAt(x, y + 2, z);

        if (!isSafeGround(ground) || !feet.getType().isAir() || !head.getType().isAir()) {
            return null;
        }

        Location location = new Location(world, x + 0.5, y + 1.0, z + 0.5);
        if (!isInsideRange(location) || !world.getWorldBorder().isInside(location)) {
            return null;
        }
        if (TeleportationPotion.MAX_INHABITED_TICKS > 0
                && world.getChunkAt(x >> 4, z >> 4).getInhabitedTime() > TeleportationPotion.MAX_INHABITED_TICKS) {
            return null;
        }
        return location;
    }

    private boolean isSafeGround(Block ground) {
        Material type = ground.getType();
        return type.isSolid() && !UNSAFE.contains(type);
    }

    private boolean isInsideRange(Location location) {
        double minRange = plugin.getConfig().getDouble("rtp.min-range", 500);
        double maxRange = resolveMaxRange(location.getWorld());
        double distance = Math.hypot(location.getX(), location.getZ());
        return distance >= minRange && distance <= maxRange;
    }

    private double resolveMaxRange(World world) {
        double maxRange = plugin.getConfig().getDouble("rtp.max-range", 5000);
        double borderRadius = world.getWorldBorder().getSize() / 2.0;
        if (maxRange <= 0) {
            return borderRadius;
        }
        return Math.min(maxRange, borderRadius);
    }
}
