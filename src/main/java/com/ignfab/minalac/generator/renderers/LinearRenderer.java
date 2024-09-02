package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Structure3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LinearVoxel3d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A renderer for linear stuff. To be used with VertexRenderer for better join rendering.
 */
public class LinearRenderer extends ModelRenderer<ShapesVoxelizable3d> {

    // In structure, X is for linear direction (starting at 0, looping over bbox max x), Y for both orthogonal directions (0 is central axis).
    private final Structure3d structure;
    private final Heightmap renderOnlyWhenAbove;

    /**
     * Creates a new VectorRenderer.
     *
     * @param selection models to be rendered (only Voxelizable2d ones will be)
     * @param placeable what to place
     * @param at where to place stuff around model axis
     * @param renderOnlyWhenAbove render model only if a part of it is above this heightmap (null means always render)
     */
    public LinearRenderer(
        ModelSelection selection,
        Structure3d structure,
        Heightmap renderOnlyWhenAbove
    ) {
        super(ShapesVoxelizable3d.class, selection);
        this.structure = structure;
        this.renderOnlyWhenAbove = renderOnlyWhenAbove;
    }

    // Draws a join voxel at x, y, if it's not out of join.
    // Computes z to be in mean plane between two segments.
    // Linear index will be 0 in joins
    private void drawJoinVoxel(Placeable placeable, WorldCoords3d p0, int x, int y, Vector3d slope1, Vector3d slope2) {
        double component1 = slope1.componentXY(x, y);
        if (component1 < 0) return;
        double component2 = slope2.componentXY(x, y);
        if (component2 > 0) return;

        placeable.place(p0.x() + x, p0.y() + y, p0.z() + (int) Math.round((slope1.z() * component1 + slope2.z() * component2) * 0.5));
    }

    // Draws join voxels in 8 axis symetries
    private void drawJoinVoxel8Syms(Placeable placeable, WorldCoords3d p0, int x, int y, Vector3d slope1, Vector3d slope2) {
        if (placeable == null)
            return;
        drawJoinVoxel(placeable, p0, x, y, slope1, slope2);
        drawJoinVoxel(placeable, p0, -x, y, slope1, slope2);
        drawJoinVoxel(placeable, p0, x, -y, slope1, slope2);
        drawJoinVoxel(placeable, p0, -x, -y, slope1, slope2);
        drawJoinVoxel(placeable, p0, y, x, slope1, slope2);
        drawJoinVoxel(placeable, p0, -y, x, slope1, slope2);
        drawJoinVoxel(placeable, p0, y, -x, slope1, slope2);
        drawJoinVoxel(placeable, p0, -y, -x, slope1, slope2);
    }

    // Draws join between to line segments.
    // Join is a round part drawn between two lines edge.
    private void drawJoin(WorldCoords3d p0, Vector3d slope1, Vector3d slope2, int index) {

        WorldBBox3d bbox = structure.bbox();
        int minYs = Math.max(0, bbox.minY());
        int maxYs = bbox.maxY();

        // Draw external circle using "midpoint circle algorithm" (https://en.wikipedia.org/wiki/Midpoint_circle_algorithm)
        int x = 0;
        int y = maxYs;
        int m = 5 - 4 * y;
        while (x <= y) {

            // Here we have the relative coordinate of one point of the external circle in (x,y)
            // We now draw a ray from this point to the center of the circle (0,0)

            index++; // index goes on increasing along the circle
            int xs = index % bbox.sizeX() + bbox.minX();

            // Draw ray from center to circumference for each Z of the structure
            for (int zs = bbox.minZ(); zs <= bbox.maxZ(); zs++) {
                WorldCoords3d pos = new WorldCoords3d(p0.x(), p0.y(), p0.z() + zs);
                int lastxx = (int) Math.round(x * ((double) minYs) / maxYs);
                // Y axis of the structure is along the ray
                for (int ys = minYs; ys <= maxYs; ys++) {
                    Placeable placeable = structure.get(new WorldCoords3d(xs, ys, zs));
                    // xx, yy  is the point along the ray
                    float ratio = ((float) ys) / maxYs; // maxYs > 0 (checked by while)
                    int xx = Math.round(x * ratio);
                    int yy = Math.round(y * ratio);
                    drawJoinVoxel8Syms(placeable, pos, xx, yy, slope1, slope2);
                    // To avoid moiré effects, we have to add some extra voxels when changing xx.
                    if (lastxx != xx)
                        drawJoinVoxel8Syms(placeable, pos, lastxx, yy, slope1, slope2);
                    lastxx = xx;
                }
            }

            // Back to the "midpoint circle algorithm" to compute next circumference point.
            if (m > 0) {
                y--;
                m += - 8 * y;
            }
            x++;
            m += 8 * x + 4;
        }
    }

    // Draws a slice of line segment
    private void drawSlice(int x, int y, int z, Vector2d slope, int index) {

        WorldBBox3d bbox = structure.bbox();

        // Structure X is repeated along line axis
        int xs = index % bbox.sizeX() + bbox.minX();

        // Structure Y is 0 on line axis and increases when getting away from it.
        for (int ys = Math.max(0, bbox.minY()); ys <= bbox.maxY(); ys++) {
            int dX = (int) Math.round(ys * slope.y());
            int dY = (int) Math.round(ys * slope.x());
            // We manage structure Z as an offset to line Z.
            for (int zs = bbox.minZ(); zs <= bbox.maxZ(); zs++) {
                Placeable placeable = structure.get(new WorldCoords3d(xs, ys, zs));
                if (placeable == null)
                    continue;
                placeable.place(x - dX, y + dY, z + zs);
                placeable.place(x + dX, y - dY, z + zs);
            }
        }
    }

    // Draws a slice of line segment
    private void drawSlice(WorldCoords3d p0, Vector2d slope, int index) {
        drawSlice(p0.x(), p0.y(), p0.z(), slope, index);
    }

    /**
     * Tells whether model should be rendered or not.
     *
     * @param voxelizable model to test
     * @param bbox bounding box of rendering area
     *
     * @return true if model should be redered.
     */
    // TODO: Not sure it is a good idea to clip model againts bbox. Maybe we should not when tiling.
    private boolean shouldRender(ShapesVoxelizable3d voxelizable, WorldBBox3d bbox) {
        if (renderOnlyWhenAbove == null)
            return true;

        for (LinearVoxel3d voxel : voxelizable.voxelize3d(bbox).borders())
            if (renderOnlyWhenAbove.get(voxel.coords().x(), voxel.coords().y()) + 1 < voxel.coords().z())
                return true;

        return false;
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    @Override
    protected void render(ShapesVoxelizable3d model, WorldBBox3d bbox) {
        // Done if nothing to draw!
        if (structure.bbox().isEmpty())
            return;

        // Don't render if no part is clearly above heightmap
        if (!shouldRender(model, bbox))
            return;

        for (LinearVoxel3d voxel : model.voxelize3d(bbox).borders()) {
            Vector3d slope = voxel.slope();
            WorldCoords3d pos = voxel.coords();
            WorldCoords3d nextPos = voxel.nextCoords();
            Vector2d normalized = slope.toXY().normalized();

            // Draw a slice for each voxel.
            drawSlice(pos, normalized, voxel.index());

            if (!pos.equals(nextPos)) {
                // To keep voxel connected (and avoid moiré effects), we need to draw one more slice each time both x and y change
                if (pos.x() != nextPos.x() && pos.y() != nextPos.y())
                    if (Math.abs(slope.x()) > Math.abs(slope.y()))
                        drawSlice(pos.x() + (int) Math.round(slope.x()), pos.y(), pos.z(), normalized, voxel.index());
                    else
                        drawSlice(pos.x(), pos.y() + (int) Math.round(slope.y()), pos.z(), normalized, voxel.index());
            }

            if (!slope.equals(voxel.nextSlope()))
                // If orientation changes, we need to draw a join in gap.
                drawJoin(pos, slope, voxel.nextSlope(), voxel.index());
        }
    }
}
