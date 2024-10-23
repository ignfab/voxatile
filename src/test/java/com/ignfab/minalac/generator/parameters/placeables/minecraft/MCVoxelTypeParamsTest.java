package com.ignfab.minalac.generator.parameters.placeables.minecraft;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class MCVoxelTypeParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new MCVoxelTypeParams("toto"));
    }

    @Test
    public void testValidate() {
        MCVoxelTypeParams params;
        params = new MCVoxelTypeParams("titi");
        assertDoesNotThrow(params::validate);

        params = new MCVoxelTypeParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        MCVoxelTypeParams params = new MCVoxelTypeParams("tata");
        assertDoesNotThrow(() -> params.create(new MCVoxelWorld()));
    }
}
