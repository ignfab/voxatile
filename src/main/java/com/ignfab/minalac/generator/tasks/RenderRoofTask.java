package com.ignfab.minalac.generator.tasks;

import java.util.Collections;
import java.util.Set;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
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
    private final String altitudeMetadata;
    private final String heightMetadata;

    private final Shape2dVoxelizer voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param type type of roof to render
     * @param altitudeMetadata base altitude of the building
     * @param heightMetadata height of building walls
     * @param placeable what to place as roof
     */
    public RenderRoofTask(ModelSelection selection, RoofType type, String altitudeMetadata, String heightMetadata, Placeable placeable) {
        super(Shape2dConvertibleModel.class, selection);
        this.type = type;
        this.placeable = placeable;
        this.altitudeMetadata = altitudeMetadata;
        this.heightMetadata = heightMetadata;
    }

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slope) {

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            Segment2d selectedSegment = null;
            double lineDistance = 0.0;

            for (LineString2d string: shape.lineStrings())
                for (int index = 0; index < string.size(); index++) {

                    Segment2d segment = string.get(index);

                    double d = segment.signedDistanceTo(x, y); // * Slope

                    // Distance being negative means we are outside the shape
                    // (Shape should be correctly oriented)
                    if (Math.round(d) < 0) continue;

                    // No roof surface for straight segments
                    if (straightSegments.contains(segment)) continue;

                    // We have to "cut" the roof plane according to neightbor segments

                    // Computation is similar for previous and next segment:
                    // If other segment oriented inside the shape, we discart point
                    // if it's closer to the other segment line.
                    // Otherwise, we do the opposite, discard it if it's closer to
                    // cutrrent segment line.
                    // TODO: previous explanation outdated

                    Segment2d previousSegment = string.get(index - 1);
                    if (previousSegment != null) {
                        double dd = previousSegment.signedDistanceTo(x,y);
                        // TODO: Maybe "concave" ? Not sure of signs
                        boolean convex = segment.direction().determinant(previousSegment.direction()) < 0;
                        if (straightSegments.contains(previousSegment)) {
                            // Straight segment cuts current on own axis
                            if (convex ^ dd < 0)
                                continue;
                        } else {
                            // Non straight segment cuts current on bisector
                            if (convex ^ dd < d)
                                continue;
                        }
                    }

                   Segment2d nextSegment = string.get(index + 1);
                    if (nextSegment != null) {
                        double dd = nextSegment.signedDistanceTo(x,y);
                        // TODO: Maybe "concave" ? Not sure of signs
                        boolean convex = segment.direction().determinant(nextSegment.direction()) > 0;
                        if (straightSegments.contains(nextSegment)) {
                            // Straight segment cuts current on own axis
                            if (convex ^ dd < 0)
                                continue;
                        } else {
                            // Non straight segment cuts current on bisector
                            if (convex ^ dd < d)
                                continue;
                        }
                    }

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
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {

        if (model.getMetadata(altitudeMetadata) == null || model.getMetadata(heightMetadata) == null)
            return;

        int altitude = (int) Math.round(((Number) model.getMetadata(altitudeMetadata)).doubleValue()
            + ((Number) model.getMetadata(heightMetadata)).doubleValue() / tile.generation().getVerticalScale()
        );

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
