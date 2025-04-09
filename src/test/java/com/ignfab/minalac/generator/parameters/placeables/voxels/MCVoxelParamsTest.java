package com.ignfab.minalac.generator.parameters.placeables.voxels;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MCVoxelParams("toto"));
    }

    @Test
    public void testValidate() {
        MCVoxelParams params;
        params = new MCVoxelParams("titi");
        assertDoesNotThrow(params::validate);

        params = new MCVoxelParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        MCVoxelParams params = new MCVoxelParams("tata");
        assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
    }
}
