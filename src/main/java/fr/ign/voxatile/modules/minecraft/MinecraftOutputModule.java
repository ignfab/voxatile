package fr.ign.voxatile.modules.minecraft;

import fr.ign.voxatile.core.parameters.OutputFormat;
import fr.ign.voxatile.core.parameters.ParamsParser;
import fr.ign.voxatile.core.utils.modules.Module;

/**
 * A module for Minecraft format output.
 */
public class MinecraftOutputModule extends Module {
    @Override
    public void registerParams(ParamsParser parser) {
        parser.registerFormat("minecraft", new OutputFormat(MinecraftVoxelWorld::new, MinecraftVoxelParams.class, MinecraftVoxelParams::packed));
    }
}
