package com.ignfab.minalac.generator.outputs.minecraft;

import java.util.HashMap;
import java.util.Map;

import net.querz.nbt.tag.CompoundTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelTest {
    private TileMock tileMock;

    @BeforeEach
    public void setUp() throws MapWriteException {
        WorldBBox3d limits = new WorldBBox3d(new WorldCoords3d(-50, -10, 0), new WorldCoords3d(10, 0, 200));
        MCVoxelWorld world = new MCVoxelWorld(null);
        world.setLimits(limits);
        tileMock = new TileMock(world, limits);
    }

    @Test
    public void testPlace() {
        MCVoxel air = new MCVoxel("minecraft:air");
        air.place(tileMock, 3, -7, 64);
        CompoundTag expectedAir = new CompoundTag();
        expectedAir.putString("Name", "minecraft:air");
        tileMock.assertBlockStateAt(3, 64, 6, expectedAir); // X/Y/Z => X/Z/-Y

        MCVoxel stairs = new MCVoxel("minecraft:oak_stairs", Map.of(
            "facing", "north",
            "half", "bottom",
            "shape", "straight",
            "waterlogged", "false"
        ));
        stairs.place(tileMock, -43, 0, 192);
        CompoundTag expectedStairs = new CompoundTag();
        expectedStairs.putString("Name", "minecraft:oak_stairs");
        CompoundTag properties = new CompoundTag();
        properties.putString("facing", "north");
        properties.putString("half", "bottom");
        properties.putString("shape", "straight");
        properties.putString("waterlogged", "false");
        expectedStairs.put("Properties", properties);
        tileMock.assertBlockStateAt(-43, 192, -1, expectedStairs); // X/Y/Z => X/Z/-Y
    }

    private static final class TileMock extends MCVoxelTile {
        private final Map<String, CompoundTag> blockStates = new HashMap<>();

        TileMock(MCVoxelWorld world, WorldBBox3d limits) {
            super(null, limits);
        }

        @Override
        void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block)  {
            if (isOutOfLimits(blockX, blockY, blockZ)) return;
            blockStates.put(key(blockX, blockY, blockZ), block);
        }

        public void assertBlockStateAt(int blockX, int blockY, int blockZ, CompoundTag block) {
            CompoundTag state = blockStates.get(key(blockX, blockY, blockZ));
            assertEquals(block, state, "Block state mismatch at (%d, %d, %d)".formatted(blockX, blockY, blockZ));
        }

        private String key(int x, int y, int z) {
            return x + "," + y + "," + z;
        }
    }
}
