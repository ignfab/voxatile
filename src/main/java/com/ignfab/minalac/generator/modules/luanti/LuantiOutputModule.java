package com.ignfab.minalac.generator.modules.luanti;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.utils.modules.Module;

/**
 * A module for Luanti format output.
 */
public class LuantiOutputModule extends Module {

    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("luanti", new OutputFormat(LuantiVoxelWorld::new, LuantiVoxelParams.class, LuantiVoxelParams::new));
    }
}
