package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.OutOfWorldException;
import net.querz.nbt.tag.CompoundTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelTypeTest {
    private WorldMock worldMock;

    @BeforeEach
    public void setUp() {
        worldMock = new WorldMock();
    }

    @Test
    public void testPlace() {
        MCVoxelType air = new MCVoxelType(worldMock, "minecraft:air");
        assertDoesNotThrow(() -> air.place(3, -7, 64));
        CompoundTag expectedAir = new CompoundTag();
        expectedAir.putString("Name", "minecraft:air");
        worldMock.assertBlockStateAt(3, 64, -7, expectedAir); // XYZ => XZY

        MCVoxelType stairs = new MCVoxelType(worldMock, "minecraft:oak_stairs", Map.of(
            "facing", "north",
            "half", "bottom",
            "shape", "straight",
            "waterlogged", "false"
        ));
        assertDoesNotThrow(() -> stairs.place(-43, 0, 192));
        CompoundTag expectedStairs = new CompoundTag();
        expectedStairs.putString("Name", "minecraft:oak_stairs");
        CompoundTag properties = new CompoundTag();
        properties.putString("facing", "north");
        properties.putString("half", "bottom");
        properties.putString("shape", "straight");
        properties.putString("waterlogged", "false");
        expectedStairs.put("Properties", properties);
        worldMock.assertBlockStateAt(-43, 192, 0, expectedStairs); // XYZ => XZY
    }

    private static final class WorldMock extends MCVoxelWorld {
        private final Map<String, CompoundTag> blockStates = new HashMap<>();

        @Override
        void setBlockState(int blockX, int blockY, int blockZ, CompoundTag block) throws OutOfWorldException {
            super.setBlockState(blockX, blockY, blockZ, block);
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
