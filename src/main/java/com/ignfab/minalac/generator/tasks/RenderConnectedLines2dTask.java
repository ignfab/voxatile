package com.ignfab.minalac.generator.tasks;

import java.util.HashSet;
import java.util.Set;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ConnectedLine2dVoxelizer;

/**
 * A task rendering lines by placing structure along them.
 */
public class RenderConnectedLines2dTask extends ModelTask<Shape2dConvertibleModel> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final Placeable placeable;
    private final Set<ConnectedLine2dVoxelizer> voxelizers = new HashSet<>();

    /**
     * Creates a new {@code RenderConnectedLinesTask}.
     *
     * @param selection selection of models to render
     * @param heightmapSpec heighmap on which draw the lines
     * @param placeable what to place on the lines
     * @param distances list of distances from main line at which draw lines (0.0 is on the main line)
     */
    public RenderConnectedLines2dTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        Placeable placeable,
        Set<Double> distances
    ) {
        super(Shape2dConvertibleModel.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.placeable = placeable;

        for (Double distance : distances)
            if (distance != null)
                voxelizers.add(new ConnectedLine2dVoxelizer(distance));
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);

        for (ConnectedLine2dVoxelizer voxelizer : voxelizers)
            for (Positioned2d pos : tile.limits().to2d().filterInside(voxelizer.voxelize(model)))
                placeable.place(tile.voxels(), pos.coords().to3d(heightmap.get(pos.coords())));
    }
}
