package com.ignfab.minalac.generator.outputs.minetest.mod;

import com.ignfab.minalac.generator.world.MapWriteException;

import java.io.File;

public interface LuaMod {
    void save(File directory) throws MapWriteException;
}
