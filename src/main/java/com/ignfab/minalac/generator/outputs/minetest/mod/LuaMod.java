package com.ignfab.minalac.generator.outputs.minetest.mod;

import java.io.File;

import com.ignfab.minalac.generator.world.MapWriteException;

public interface LuaMod {
    void save(File directory) throws MapWriteException;
}
