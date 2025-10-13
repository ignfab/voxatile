package com.ignfab.minalac.generator.modules.minecraft;

import io.github.ensgijs.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftBlockEntityVoxelParamsTest {
    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MinecraftBlockEntityVoxelParams("minecraft:beacon", "true"));
        assertDoesNotThrow(() -> new MinecraftBlockEntityVoxelParams("minecraft:beacon", "minecraft:beacon"));
    }

    @Test
    public void testValidate() {
        MinecraftBlockEntityVoxelParams validParams = new MinecraftBlockEntityVoxelParams("minecraft:daylight_detector", "true");
        assertDoesNotThrow(validParams::validate);

        MinecraftBlockEntityVoxelParams invalidParams1 = new MinecraftBlockEntityVoxelParams("", "id");
        assertThrows(IllegalArgumentException.class, invalidParams1::validate);

        MinecraftBlockEntityVoxelParams invalidParams2 = new MinecraftBlockEntityVoxelParams("type", "");
        assertThrows(IllegalArgumentException.class, invalidParams2::validate);
    }

    @Test
    public void testCreate() {
        MinecraftBlockEntityVoxelParams beaconParams = new MinecraftBlockEntityVoxelParams("minecraft:beacon", "true");
        MinecraftBlockEntityVoxel beaconVoxel = assertDoesNotThrow(() -> beaconParams.create(TestingSeed.UNUSED));
        assertEquals(new MinecraftBlockEntityVoxel("minecraft:beacon", "minecraft:beacon", null, null), beaconVoxel);

        MinecraftBlockEntityVoxelParams comparatorParams = new MinecraftBlockEntityVoxelParams("minecraft:comparator", "true");
        comparatorParams.dataTags = "{ OutputSignal: 7 }";
        MinecraftBlockEntityVoxel comparatorVoxel = assertDoesNotThrow(() -> comparatorParams.create(TestingSeed.UNUSED));
        CompoundTag data = new CompoundTag();
        data.putInt("OutputSignal", 7);
        assertEquals(new MinecraftBlockEntityVoxel("minecraft:comparator", "minecraft:comparator", null, data), comparatorVoxel);
    }
}
