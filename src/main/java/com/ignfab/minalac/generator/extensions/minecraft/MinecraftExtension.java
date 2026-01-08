package com.ignfab.minalac.generator.extensions.minecraft;

import com.ignfab.minalac.generator.Plugin;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * Adds Minecraft output format.
 *
 * This format is selectable specifying `format: minecraft` in parameters.
 */
public class MinecraftExtension extends Plugin {

    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("minecraft", new OutputFormat((destination) -> new MCVoxelWorld(destination), MCVoxelParams.class, MCVoxelParams::new));
    }
}
