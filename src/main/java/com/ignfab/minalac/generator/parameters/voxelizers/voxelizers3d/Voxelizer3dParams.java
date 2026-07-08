package com.ignfab.minalac.generator.parameters.voxelizers.voxelizers3d;

import com.ignfab.minalac.generator.parameters.ParamsParser;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.voxelization.Voxelizer3d;

/**
 * Parameters for a {@link Voxelizer3d}.
 */
public abstract class Voxelizer3dParams extends PolymorphicParams {

    /**
     * Creates a {@link Voxelizer3d} from parameters.
     *
     * @return created voxelizer
     */
    public abstract Voxelizer3d create();

    /**
     * Registers all voxelizers into a given {@link ParamsParser}.
     *
     * @param parser parser into which register voxelizers
     */
    public static void register(ParamsParser parser) {
        parser.registerParams("point", PointVoxelizer3dParams.class);
        parser.registerParams("line", ThickLineVoxelizer3dParams.class);
        parser.registerParams("setAltitude", SetAtlitudeVoxelizer3dParams.class);
    }
}
