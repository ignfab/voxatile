package com.ignfab.minalac.generator.parameters.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class MTVoxelTypeParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MTVoxelTypeParams("toto"));
    }

    @Test
    public void testValidate() {
        MTVoxelTypeParams params;
        params = new MTVoxelTypeParams("titi");
        assertDoesNotThrow(params::validate);

        params = new MTVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        MTVoxelTypeParams params = new MTVoxelTypeParams("tata");
        assertDoesNotThrow(() -> params.create(new MTVoxelWorld()));
    }
}
