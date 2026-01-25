package com.easytpa.util;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;

import java.util.Random;

public class SafeLocationFinder {

    private static final Random random = new Random();

    public static Vector3d findSafeLocation(World world, int minDistance, int maxDistance, int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Vector3d location = tryFindLocation(world, minDistance, maxDistance);
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    private static Vector3d tryFindLocation(World world, int minDistance, int maxDistance) {
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = minDistance + random.nextDouble() * (maxDistance - minDistance);

        int blockX = (int) Math.round(Math.cos(angle) * distance);
        int blockZ = (int) Math.round(Math.sin(angle) * distance);

        long chunkKey = ChunkUtil.indexChunkFromBlock(blockX, blockZ);

        WorldChunk chunk = world.getChunkIfLoaded(chunkKey);
        if (chunk == null) {
            return null;
        }

        int localX = ChunkUtil.localCoordinate(blockX);
        int localZ = ChunkUtil.localCoordinate(blockZ);

        int surfaceY = findSurfaceY(chunk, localX, localZ);
        if (surfaceY < 0) {
            return null;
        }

        for (int y = surfaceY; y >= 1; y--) {
            if (isSafePosition(chunk, localX, y, localZ)) {
                return new Vector3d(blockX + 0.5, y, blockZ + 0.5);
            }
        }

        return null;
    }

    private static int findSurfaceY(WorldChunk chunk, int localX, int localZ) {
        for (int y = ChunkUtil.HEIGHT - 1; y >= 0; y--) {
            int blockId = chunk.getBlock(localX, y, localZ);
            if (blockId != 0) {
                BlockType type = BlockType.getAssetMap().getAsset(blockId);
                if (type != null && type.getMaterial() == BlockMaterial.Solid) {
                    return y + 1;
                }
            }
        }
        return -1;
    }

    private static boolean isSafePosition(WorldChunk chunk, int localX, int y, int localZ) {
        if (y < 1 || y >= ChunkUtil.HEIGHT - 1) {
            return false;
        }

        int groundBlockId = chunk.getBlock(localX, y - 1, localZ);
        if (groundBlockId == 0) {
            return false;
        }
        BlockType groundType = BlockType.getAssetMap().getAsset(groundBlockId);
        if (groundType == null || groundType.getMaterial() != BlockMaterial.Solid) {
            return false;
        }
        if (groundType.getDamageToEntities() > 0) {
            return false;
        }

        int feetBlockId = chunk.getBlock(localX, y, localZ);
        if (!isAirOrPassable(feetBlockId)) {
            return false;
        }
        if (isDangerousFluid(chunk, localX, y, localZ)) {
            return false;
        }

        int headBlockId = chunk.getBlock(localX, y + 1, localZ);
        if (!isAirOrPassable(headBlockId)) {
            return false;
        }
        if (isDangerousFluid(chunk, localX, y + 1, localZ)) {
            return false;
        }

        return true;
    }

    private static boolean isAirOrPassable(int blockId) {
        if (blockId == 0) {
            return true;
        }
        BlockType type = BlockType.getAssetMap().getAsset(blockId);
        return type != null && type.getMaterial() == BlockMaterial.Empty;
    }

    private static boolean isDangerousFluid(WorldChunk chunk, int localX, int y, int localZ) {
        int fluidId = chunk.getFluidId(localX, y, localZ);
        if (fluidId == 0) {
            return false;
        }
        Fluid fluid = Fluid.getAssetMap().getAsset(fluidId);
        return fluid != null && fluid.getDamageToEntities() > 0;
    }
}
