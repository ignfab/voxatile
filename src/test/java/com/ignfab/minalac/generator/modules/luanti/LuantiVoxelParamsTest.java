package com.ignfab.minalac.generator.modules.luanti;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.utils.random.TestingSeed;

import static org.junit.jupiter.api.Assertions.*;

public class LuantiVoxelParamsTest {

    @Test
    public void testConstructor() {
        assertDoesNotThrow(() -> new LuantiVoxelParams("toto"));
    }

    @Test
    public void testValidate() {
        LuantiVoxelParams params;
        params = new LuantiVoxelParams("titi");
        assertDoesNotThrow(params::validate);

        params = new LuantiVoxelParams("");
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() {
        LuantiVoxelParams params = new LuantiVoxelParams("tata");
        assertDoesNotThrow(() -> params.create(TestingSeed.UNUSED));
    }
}
