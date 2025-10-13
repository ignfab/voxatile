package com.ignfab.minalac.generator.modules.minecraft;

import java.util.Map;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftVoxelParamsTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MinecraftVoxelParams("minecraft:dirt"));
    }

    @Test
    public void testValidate() {
        MinecraftVoxelParams validParams = new MinecraftVoxelParams("minecraft:stone");
        assertDoesNotThrow(validParams::validate);

        MinecraftVoxelParams invalidParams = new MinecraftVoxelParams("");
        assertThrows(IllegalArgumentException.class, invalidParams::validate);
    }

    @Test
    public void testCreate() {
        MinecraftVoxelParams airParams = new MinecraftVoxelParams("minecraft:air");
        MinecraftVoxel airVoxel = assertDoesNotThrow(() -> airParams.create(TestingSeed.UNUSED));
        assertEquals(new MinecraftVoxel("minecraft:air"), airVoxel);

        MinecraftVoxelParams oakLeavesParams = new MinecraftVoxelParams("minecraft:oak_leaves");
        oakLeavesParams.properties = Map.of("persistent", "true");
        MinecraftVoxel oakLeavesVoxel = assertDoesNotThrow(() -> oakLeavesParams.create(TestingSeed.UNUSED));
        assertEquals(new MinecraftVoxel("minecraft:oak_leaves", Map.of("persistent", "true")), oakLeavesVoxel);
    }

    @Test
    public void testPacked() {
        MinecraftVoxelParams oakLeavesParams = MinecraftVoxelParams.packed("minecraft:oak_leaves[persistent=true]");
        oakLeavesParams.properties = Map.of("persistent", "true");
        MinecraftVoxel oakLeavesVoxel = assertDoesNotThrow(() -> oakLeavesParams.create(TestingSeed.UNUSED));
        assertEquals(new MinecraftVoxel("minecraft:oak_leaves", Map.of("persistent", "true")), oakLeavesVoxel);

        MinecraftVoxelParams comparatorParams = MinecraftVoxelParams.packed("minecraft:comparator{OutputSignal: 7}");
        MinecraftVoxel comparatorVoxel = assertDoesNotThrow(() -> comparatorParams.create(TestingSeed.UNUSED));
        CompoundTag data = new CompoundTag();
        data.putInt("OutputSignal", 7);
        assertEquals(new MinecraftBlockEntityVoxel("minecraft:comparator", "minecraft:comparator", null, data), comparatorVoxel);
    }
}
