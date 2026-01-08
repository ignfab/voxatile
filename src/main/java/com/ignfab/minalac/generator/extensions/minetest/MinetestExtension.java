package com.ignfab.minalac.generator.extensions.minetest;

import com.ignfab.minalac.generator.Plugin;
import com.ignfab.minalac.generator.parameters.OutputFormat;
import com.ignfab.minalac.generator.parameters.ParamsParser;

/**
 * Adds Minetest output format.
 *
 * This format is selectable specifying `format: minetest` in parameters.
 */

public class MinetestExtension extends Plugin {
    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("minetest", new OutputFormat((destination) -> new MTVoxelWorld(destination), MTVoxelParams.class, MTVoxelParams::new));
    }
}
