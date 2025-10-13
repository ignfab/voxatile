package com.ignfab.minalac.generator.modules.minecraft;

import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.utils.modules.Module;

/**
 * A module for Minecraft format output.
 */
public class MinecraftOutputModule extends Module {
    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("minecraft", new OutputFormat(MinecraftVoxelWorld::new, MinecraftVoxelParams.class, MinecraftVoxelParams::packed));
    }
}
