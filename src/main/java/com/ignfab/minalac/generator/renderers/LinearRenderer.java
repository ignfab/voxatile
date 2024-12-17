package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.IntegerIntervals;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LinearVoxel3d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A renderer for linear stuff. To be used with VertexRenderer for better join rendering.
 */
public class LinearRenderer extends ModelRenderer {
    private Placeable placeable;
    private IntegerIntervals at;
    private Heightmap renderOnlyWhenAbove;

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
        Placeable placeable,
        IntegerIntervals at,
        Heightmap renderOnlyWhenAbove
    ) {
        super(selection);
        this.placeable = placeable;
        this.at = at.merged();
        this.renderOnlyWhenAbove = renderOnlyWhenAbove;
    }

    // Draws a join voxel at x, y, if it's not out of join.
    // Computes z to be in mean plane between two segments.
    private void drawJoinVoxel(WorldCoords3d p0, int x, int y, Vector3d slope1, Vector3d slope2) {
        double component1 = slope1.componentXY(x, y);
        if (component1 < 0) return;
        double component2 = slope2.componentXY(x, y);
        if (component2 > 0) return;

        placeable.place(p0.x() + x, p0.y() + y, p0.z() + (int) Math.round((slope1.z() * component1 + slope2.z() * component2) * 0.5));
    }

    // Draws join voxels in 8 axis symetries
    private void drawJoinVoxel8Syms(WorldCoords3d p0, int x, int y, Vector3d slope1, Vector3d slope2) {
        drawJoinVoxel(p0, x, y, slope1, slope2);
        drawJoinVoxel(p0, -x, y, slope1, slope2);
        drawJoinVoxel(p0, x, -y, slope1, slope2);
        drawJoinVoxel(p0, -x, -y, slope1, slope2);
        drawJoinVoxel(p0, y, x, slope1, slope2);
        drawJoinVoxel(p0, -y, x, slope1, slope2);
        drawJoinVoxel(p0, y, -x, slope1, slope2);
        drawJoinVoxel(p0, -y, -x, slope1, slope2);
    }

    // Draws join between to line segments.
    // Join is a round part drawn between two lines edge.
    private void drawJoin(WorldCoords3d p0, Vector3d slope1, Vector3d slope2) {

        for (IntegerInterval interval : at) {

            // Draw circles using adapted "midpoint circle algorithm" (https://en.wikipedia.org/wiki/Midpoint_circle_algorithm)
            // We are drawing two parallel circles using this algorithm, one for begin() and one for end().
            int yMin = interval.begin();
            int yMax = interval.end();
            int mMin = 5 - 4 * yMin;
            int mMax = 5 - 4 * yMax;

            for (int x = 0; x <= yMax; x++) {
                if (x <= yMin) {
                    if (mMin > 0) {
                        yMin--;
                        mMin -= 8 * yMin;
                    }
                    mMin += 9 * x + 4; // The algorythm uses 8 but after having x increased (here, x is incremented in loop)

                    // Draw voxel between the two circles.
                    for (int y = yMin; y <= yMax; y++)
                        drawJoinVoxel8Syms(p0, x, y, slope1, slope2);
                } else {
                    // Here we draw voxel between the symetry diagonal and the large circle (we are out of small circle).
                    for (int y = x; y <= yMax; y++)
                        drawJoinVoxel8Syms(p0, x, y, slope1, slope2);
                }

                if (mMax > 0) {
                    yMax--;
                    mMax -= 8 * yMax;
                }
                mMax += 9 * x + 4; // The algorythm uses 8 but after having x increased (here, x is incremented in loop)
            }
        }
    }

    // Draws a slice of line segment
    private void drawSlice(int x, int y, int z, Vector2d slope) {
        for (IntegerInterval interval : at)
            for (int p = interval.begin(); p <= interval.end(); p++) {
                int dX = (int) Math.round(p * slope.y());
                int dY = (int) Math.round(p * slope.x());
                placeable.place(x - dX, y + dY, z);
                placeable.place(x + dX, y - dY, z);
            }
    }

    // Draws a slice of line segment
    private void drawSlice(WorldCoords3d p0, Vector2d slope) {
        drawSlice(p0.x(), p0.y(), p0.z(), slope);
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
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }

        // Don't render if no part is clearly above heightmap
        if (!shouldRender(voxelizable, bbox))
            return;

        for (LinearVoxel3d voxel : voxelizable.voxelize3d(bbox).borders()) {
            Vector3d slope = voxel.slope();
            WorldCoords3d pos = voxel.coords();
            WorldCoords3d nextPos = voxel.nextCoords();
            Vector2d normalized = slope.toXY().normalized();

            // Draw a slice for each voxel.
            drawSlice(pos, normalized);

            if (!pos.equals(nextPos)) {
                // To keep voxel connected (and avoid moiré effects), we need to draw one more slice each time both x and y change
                if (pos.x() != nextPos.x() && pos.y() != nextPos.y())
                    if (Math.abs(slope.x()) > Math.abs(slope.y()))
                        drawSlice(pos.x() + (int) Math.round(slope.x()), pos.y(), pos.z(), normalized);
                    else
                        drawSlice(pos.x(), pos.y() + (int) Math.round(slope.y()), pos.z(), normalized);
            }

            if (!slope.equals(voxel.nextSlope()))
                // If orientation changes, we need to draw a join in gap.
                drawJoin(pos, slope, voxel.nextSlope());

        }
    }
}
