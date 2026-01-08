package com.ignfab.minalac.generator.extensions.minetest;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class MTVoxelParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MTVoxelParams("toto"));
    }

    @Test
    public void testValidate() {
        MTVoxelParams params;
        params = new MTVoxelParams("titi");
        assertDoesNotThrow(params::validate);

        params = new MTVoxelParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        MTVoxelParams params = new MTVoxelParams("tata");
        assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
    }
}
