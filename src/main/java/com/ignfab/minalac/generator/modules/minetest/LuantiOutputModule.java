package com.ignfab.minalac.generator.modules.minetest;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.utils.modules.Module;

/**
 * A module for Luanti format output.
 */
public class LuantiOutputModule extends Module {

    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("minetest", new OutputFormat(MTVoxelWorld::new, MTVoxelParams.class, MTVoxelParams::new));
    }
}
