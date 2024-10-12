package com.ignfab.minalac.generator.outputs.minetest.mod;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.world.MapWriteException;

import java.io.File;

public class SimpleLuaMod implements LuaMod {
    private final String code;

    public SimpleLuaMod(String code) {
        this.code = code;
    }

    @Override
    public void save(File directory) throws MapWriteException {
        MTVoxelWorld.createFile(new File(directory, "init.lua"), code);
    }
}
