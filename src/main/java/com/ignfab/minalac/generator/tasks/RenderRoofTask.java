package com.ignfab.minalac.generator.tasks;

import java.util.Collections;
import java.util.Set;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link ModelTask} drawing roof over a surface.
 * <p>
 * This is a POC for roof rendering. It has many TODOs:
 * - Have more roof types
 * - May be use a layout for material (needs non rectangular layout)
 * - Use model values instead of metadata (needs model values)
 */
public class RenderRoofTask extends ModelTask<Shape2dConvertibleModel> {

    /**
     * Type of roof to render.
     */
    public enum RoofType {
        /**
         * Flat roof.
         */
        FLAT,
        /**
         * Hipped roof. All walls have a corresponding roof section.
         */
        HIPPED
    };

    private final Placeable placeable;
    private final RoofType type;
    private final ModelValue altitudeValue;

    private final Shape2dVoxelizer voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param type type of roof to render
     * @param altitudeValue base altitude of the roof
     * @param placeable what to place as roof
     */
    public RenderRoofTask(
        ModelSelection selection,
        RoofType type,
        ModelValue altitudeValue,
        Placeable placeable
    ) {
        super(Shape2dConvertibleModel.class, selection);
        this.type = type;
        this.placeable = placeable;
        this.altitudeValue = altitudeValue;
    }

//NOTE:
// shell is clockwise
// holes are anticlockwise
// positive determinant of successive vectors : turns anticlockwise -> concave angle
// negative determinant of successive vectors : turns clockwise -> convex angle
// null determinant means vector are aligned, maybe uturn, maybe contine

    private boolean inSection(int x, int y, double d, Segment2d segment, Segment2d other, boolean forward) {
        if (other == null) return true; // Should not occure, we process a polygon

        double dd = other.signedDistanceTo(x,y);
        double det = segment.direction().determinant(other.direction());
        if (det == 0.0) {
            // We cannot rely on segments distances, because segments are on the same line.
            if (segment.direction().dot(other.direction()) > 0) {
                // Same direction,
                // Choose segment according to projected index
                double index = segment.nearestPointIndex(x, y);
                return index >= 0 && index <= segment.length();
            } else {
                // Opposite direction
                // --> there will be a problem, roof could go infinite.
                // Similar problem may occure with very sharp angles.

                // TODO: What to do ?
                // Soluce 1: introduce a fake 0 length segment at 90°, that would add a new roof pane.
                // Soluce 2: use vertex distance, that would create a round roof pane there.
                return true;
            }
        } else {
            boolean convex = forward ^ det < 0;
            return dd == d || (convex ^ dd > d);
        }
    }

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slope) {

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            // For each 2d position, we try to find the corresponding slope (corresponding roof edge segment)
            Segment2d selectedSegment = null;
            double lineDistance = 0.0;

            for (LineString2d string: shape.lineStrings())
                for (int index = 0; index < string.size(); index++) {
                    Segment2d segment = string.get(index);
                    double d = segment.signedDistanceTo(x, y); // * Slope

                    // Distance being negative means we are outside the shape
                    // (Shape should be correctly oriented)
                    // TODO: -1 = margin, should correspond to horizontal scale
                    //   If comparing to 0, we exclude some roof border
                    if (d < -1) continue;

                        // If there is already a selected segment nearest, no need to look further
                    if (selectedSegment != null && d >= lineDistance) continue;

                    // No roof surface for straight segments
                    if (straightSegments.contains(segment)) continue;

                    // We have to "cut" the roof plane according to neightbor segments
                    if (!inSection(x, y, d, segment, string.get(index - 1), false))
                        continue;
                    if (!inSection(x, y, d, segment, string.get(index + 1), true))
                        continue;

                    // Ok, now, we select segment if it has a lower distance than previously
                    if (selectedSegment == null || d < lineDistance) {
                        lineDistance = d;
                        selectedSegment = segment;
                    }
                }

            if (selectedSegment != null)
                placeable.place(tile.voxels(), x, y, altitude + (int) Math.round(slope * lineDistance));

        }
    }


    private void drawFlat(GenerationTile tile, Shape2d shape, int altitude) {
        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            placeable.place(tile.voxels(),  voxel.coords().x(), voxel.coords().y(), altitude);
        }
    }

    private void drawHipped(GenerationTile tile, Shape2d shape, int altitude, double slope) {
        drawSlopes(tile, shape, Collections.emptySet(), altitude, slope);
    }

/*
        Set<Segment2d> straightSegments = new HashSet<>();

        // For test, make some segment straights
        int ix = 0;
        for (LineString2d string: shape.lineStrings())
            for (Segment2d segment: string.segments()) {
                ix ++;
                if (ix%300 == 1)
                    straightSegments.add(segment);
            }
*/

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) throws IgnorableException {
        int altitude = altitudeValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing altitude"));

        Shape2d shape = model.toShape2d();

        switch (type) {
            case FLAT:
                drawFlat(tile, shape, altitude);
                break;
            case HIPPED:
                drawHipped(tile, shape, altitude, 1.0);                            // Skip what is on the next neighbour segment plane
                break;
            default:
                break;
        }
    }
}
