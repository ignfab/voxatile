package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers2d;

import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;

/**
 * Parameters for a {@link Voxelizer2d}.
 */
public abstract class Voxelizer2dParams extends PolymorphicParams {

    /**
     * Creates a {@link Voxelizer2d} from parameters.
     *
     * @return created voxelizer
     */
    public abstract Voxelizer2d create();

    /**
     * Registers all voxelizers into a given {@link ParamsParser}.
     *
     * @param parser parser into which register voxelizers
     */
    public static void register(ParamsParser parser) {
        parser.registerParams("point", PointVoxelizer2dParams.class);
        parser.registerParams("thickLine", ThickLineVoxelizer2dParams.class);
        parser.registerParams("line", ThinLinearVoxelizer2dParams.class);
        parser.registerParams("surface", SurfaceVoxelizer2dParams.class);
        parser.registerParams("to2d", ToVoxelizer2dParams.class);
    }
}
