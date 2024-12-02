package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class LinearRenderer extends ModelRenderer {
    private final Heightmap heightmap;

    private Placeable placeable;
    private int width;
    private int verticalOffset;
    private boolean onlyIfAboveHeightmap;
    private boolean raiseAboveHeightmap;

    /**
     * Creates a new VectorRenderer.
     *
     * @param selection models to be rendered (only Voxelizable2d ones will be)
     * @param heightmap heightmap of the ground (on which features will be placed)
     * @param placeable what to place
     * @param width width of linear drawing (actually doubled)
     * @param verticalOffset vertical offset added to heights
     * @param onlyIfAboveHeightmap render model only if a part of it is above heightmap height
     * @param raiseAboveHeightmap raise part of the model that are under heightmap at its level
     */
    public LinearRenderer(
        ModelSelection selection,
        Heightmap heightmap,
        Placeable placeable,
        int width,
        int verticalOffset,
        boolean onlyIfAboveHeightmap,
        boolean raiseAboveHeightmap
    ) {
        super(selection);
        this.heightmap = heightmap;
        this.placeable = placeable;
        this.width = width;
        this.verticalOffset = verticalOffset;
        this.onlyIfAboveHeightmap = onlyIfAboveHeightmap;
        this.raiseAboveHeightmap = raiseAboveHeightmap;
    }

    private boolean isAboveHeightmap(Iterable<LineVoxel3d> iterable) {
        for (LineVoxel3d voxel : iterable)
            if (heightmap.get(voxel.coords().x(), voxel.coords().y()) + 1 < voxel.coords().z())
                return true;
        return false;
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }

        // Don't render if no part is clearly above heightmap
        if (onlyIfAboveHeightmap && ! isAboveHeightmap(voxelizable.voxelize3d(bbox).borders()))
            return;

        for (LineVoxel3d voxel : voxelizable.voxelize3d(bbox).connectedBorders()) {
            WorldCoords3d pos = voxel.coords();
            Line3d line = voxel.line();

            int z = pos.z();
            int alti = heightmap.get(pos.x(), pos.y());

            // Eventually raise if below heigthmap
            if (z <= alti && raiseAboveHeightmap)
                z = alti;

            z += verticalOffset;

            if (voxel.index() > 0 && voxel.index() < line.maxIndex()) {
                // Draw slices between ends
                for (int w = - width + 1; w < width; w++)
                    placeable.place(
                        (int) Math.round(pos.x() - w * line.slopeY()),
                        (int) Math.round(pos.y() + w * line.slopeX()),
                        z);
            } else {
                // Draw a circle at both ends (a bit overkill but ensures good joints)
                int w2 = width * width;
                for (int x = 0; x < width; x++) {
                    int y2max = w2 - x*x;
                    for (int y = 0; y * y < y2max; y++) {
                        placeable.place(pos.x() + x, pos.y() + y, z);
                        placeable.place(pos.x() - x, pos.y() + y, z);
                        placeable.place(pos.x() + x, pos.y() - y, z);
                        placeable.place(pos.x() - x, pos.y() - y, z);
                    }
            }
            }
        }
    }
}
