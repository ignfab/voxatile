package fr.ign.voxatile.modules.luanti;

import fr.ign.voxatile.core.parameters.OutputFormat;
import fr.ign.voxatile.core.parameters.ParamsParser;
import fr.ign.voxatile.core.utils.modules.Module;

/**
 * A module for Luanti format output.
 */
public class LuantiOutputModule extends Module {

    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("luanti", new OutputFormat(LuantiVoxelWorld::new, LuantiVoxelParams.class, LuantiVoxelParams::new));
    }
}
