# RTP notes

We prewarm random spots so drinking the potion can teleport immediately. The pool only stores coordinates. It does not keep those chunks in memory. But it DOES generate those to the hard drive (including failed choices)

## What we do now

`getChunkAtAsync(chunkX, chunkZ)` loads the chunk, and if it isn't on disk yet, Paper generates it. We then check if the column looks safe (`getHighestBlockYAt`, solid ground, air for feet/head, inside the world border) and shove the Location into the pool.

On drink we `player.teleport(location)` on the main thread. If the chunk already unloaded, the server loads it (and the view-distance neighborhood) as part of the teleport. The client still has to receive those packets. That's the hitch, falling through the floor, and the "Loading terrain..." screen.

If the pool is empty we search on the spot. That can generate a bunch of chunks before the player moves. The bottle is already gone either way; consume fires after the drink.

After a successful TP we kick off another search in the background. That helps the next drink, not this one.

## Generate vs load

We currently generate. The two-arg `getChunkAtAsync` defaults to `gen = true`.

That's why a fresh world still gets RTP spots. It's also why the world grows even if nobody walks out there. Failed searches still create chunks (ocean, bad mountains, etc.). We just don't keep those locations. Worst case one successful pool entry can mean a bunch of new region-file chunks. Over time the refill timer keeps dotted random generation across the 500–5000 ring.

Other fallout from that: Dynmap/BlueMap show speckles, seed-peekers get free samples, villages and such can generate at RTP dots so people land in "new" structures more often than you'd expect, and boot can spike MSPT while the pool fills.

If we switched to `gen = false` we'd only use terrain that already exists. Cheap, no world growth, but a brand new map might not find anything until someone explores the ring. For first-join potions on a new world we probably want generate. For "don't wreck the world" we wouldn't.

## The drink isn't seamless

Seamless on the client is a myth if you're jumping thousands of blocks into new terrain. They still have to get the chunk packets.

What we can actually get: no main-thread freeze, no void-fall, standing on real ground when they arrive.

## Other stuff that bites

Safety is a snapshot. Highest block can be tree leaves, a one-block ledge, ice, honey. We skip magma/cactus/fire/powder snow/water. Someone could hypothetically blow up the spot by the time the player arrives. Rare chance

We only ever validated the one column's chunk. The player still needs a square of chunks around them. Will do this in a 3x3 radius

Finds can overlap. The timer can start two searches, a TP starts another, each search retries. Fine when the server is quiet. Can stampede worldgen if a lot of drinks happen at once.
