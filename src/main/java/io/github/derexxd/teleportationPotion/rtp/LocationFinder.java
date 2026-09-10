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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public final class LocationFinder {

    private final TeleportationPotion plugin;
    private final SafetyChecker safetyChecker;

    public LocationFinder(TeleportationPotion plugin, SafetyChecker safetyChecker) {
        this.plugin = plugin;
        this.safetyChecker = safetyChecker;
    }

    public CompletableFuture<Location> find(World world) {
        CompletableFuture<Location> result = new CompletableFuture<>();
        tryFind(world, 0, result);
        return result;
    }

    private void tryFind(World world, int attempt, CompletableFuture<Location> result) {
        int maxAttempts = plugin.getConfig().getInt("rtp.max-find-attempts", 40);
        if (world == null || attempt >= maxAttempts) {
            result.complete(null);
            return;
        }

        int x;
        int z;
        int chunkX;
        int chunkZ;
        int skips = 0;
        while (true) {
            int[] xz = randomPoint(world);
            x = xz[0];
            z = xz[1];
            chunkX = x >> 4;
            chunkZ = z >> 4;

            // this loop will only run if we do not want to generate new chunks
            // it will keep running until we find a suitable one, or the count goes over
            // this prevents the ungenerated chunk count from being counted as a failure
            if (TeleportationPotion.GENERATE_CHUNKS || world.isChunkGenerated(chunkX, chunkZ)) {
                break;
            }
            skips++;
            int skipLimit = TeleportationPotion.MAX_UNGENERATED_SKIPS;
            if (skipLimit <= 0) {
                skipLimit = 100000;
            }
            if (skips > skipLimit) {
                result.complete(null);
                return;
            }
        }

        final int findX = x;
        final int findZ = z;
        final int findChunkX = chunkX;
        final int findChunkZ = chunkZ;

        world.getChunkAtAsync(findChunkX, findChunkZ, TeleportationPotion.GENERATE_CHUNKS).thenAccept(chunk ->
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (chunk == null || !chunk.isLoaded()) {
                        tryFind(world, attempt + 1, result);
                        return;
                    }

                    Location location = sampleChunk(world, findX, findZ);
                    if (location == null) {
                        tryFind(world, attempt + 1, result);
                        return;
                    }

                    load3x3(world, findChunkX, findChunkZ).thenRun(() ->
                            plugin.getServer().getScheduler().runTask(plugin, () -> result.complete(location))
                    );
                })
        );
    }

    private CompletableFuture<Void> load3x3(World world, int chunkX, int chunkZ) {
        CompletableFuture<?>[] loads = new CompletableFuture[9];
        int i = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                loads[i++] = world.getChunkAtAsync(chunkX + dx, chunkZ + dz, TeleportationPotion.GENERATE_CHUNKS);
            }
        }
        return CompletableFuture.allOf(loads);
    }

    private Location sampleChunk(World world, int originX, int originZ) {
        int samples = plugin.getConfig().getInt("rtp.samples-per-chunk", 8);
        Location first = safetyChecker.findSafe(world, originX, originZ);
        if (first != null) {
            return first;
        }

        int chunkMinX = (originX >> 4) << 4;
        int chunkMinZ = (originZ >> 4) << 4;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 1; i < samples; i++) {
            int x = chunkMinX + random.nextInt(16);
            int z = chunkMinZ + random.nextInt(16);
            Location location = safetyChecker.findSafe(world, x, z);
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    private int[] randomPoint(World world) {
        double minRange = plugin.getConfig().getDouble("rtp.min-range", 500);
        double maxRange = resolveMaxRange(world);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double distance = minRange + random.nextDouble() * Math.max(0, maxRange - minRange);
        double angle = random.nextDouble() * Math.PI * 2;
        int x = (int) Math.round(Math.cos(angle) * distance);
        int z = (int) Math.round(Math.sin(angle) * distance);
        return new int[]{x, z};
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
