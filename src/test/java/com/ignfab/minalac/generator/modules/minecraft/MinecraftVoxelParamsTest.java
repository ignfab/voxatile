package com.ignfab.minalac.generator.modules.minecraft;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftVoxelParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MinecraftVoxelParams("toto"));
    }

    @Test
    public void testValidate() {
        MinecraftVoxelParams params;
        params = new MinecraftVoxelParams("titi");
        assertDoesNotThrow(params::validate);

        params = new MinecraftVoxelParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        MinecraftVoxelParams params = new MinecraftVoxelParams("tata");
        assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
    }
}
