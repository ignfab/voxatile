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

//NOTE:
// shell is clockwise
// holes are anticlockwise
// positive determinant of successive vectors : turns anticlockwise -> concave angle
// negative determinant of successive vectors : turns clockwise -> convex angle
// null determinant means vector are aligned, maybe uturn, maybe contine

private String segmentstr(Segment2d segment) {
    return "(%d, %d)-(%d, %d)".formatted(segment.start().x(), segment.start().y(), segment.end().x(), segment.end().y());
}

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slope) {

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            // For each 2d position, we try to find the corresponding slope (corresponding roof edge segment)
            Segment2d selectedSegment = null;
            double lineDistance = 0.0;

//boolean debug = (x == -363) && (y == 95 || y == 96);
boolean debug = false;
if (debug) System.out.println("(" + x  + ", " + y +") VOXEL");

            for (LineString2d string: shape.lineStrings())
                for (int index = 0; index < string.size(); index++) {
                    Segment2d segment = string.get(index);
                    double d = segment.signedDistanceTo(x, y); // * Slope
                    if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " SEGMENT " + segmentstr(segment) +" d="+d);

                    // Distance being negative means we are outside the shape
                    // (Shape should be correctly oriented)
                    // TODO: -1 = margin, should correspond to horizontal scale
                    //   If comparing to 0, we exclude some roof border
                    if (d < -1) {
                        if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " -> Out because wrong side");
                        continue;
                    }

                    // If there is already a selected segment nearest, no need to look further
                    if (selectedSegment != null && d >= lineDistance) {
                        if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " -> Too far");
                        continue;
                    }

                    // No roof surface for straight segments
                    if (straightSegments.contains(segment)) continue;

                    // We have to "cut" the roof plane according to neightbor segments

                    // Computation is similar for previous and next segment:
                    // If other segment oriented inside the shape, we discart point
                    // if it's closer to the other segment line.
                    // Otherwise, we do the opposite, discard it if it's closer to
                    // cutrrent segment line.
                    // TODO: previous explanation outdated



                    // TODO: On pourrait éviter les test suivants si de toutes façons d est plus grand que la distance sélectionnée

                    Segment2d previousSegment = string.get(index - 1);
                    if (previousSegment != null) { // Should be true, we process a polygon
                        double dd = previousSegment.signedDistanceTo(x,y);
                        double det = segment.direction().determinant(previousSegment.direction());
                        if (det == 0.0) {
                            // We cannot rely on segments distances, because segments are on the same line.
                            if (segment.direction().dot(previousSegment.direction()) > 0) {
                                // Same direction,
                                // Choose segment according to projected index
                                if (segment.nearestPointIndex(x, y) < 0) {
                                    // Choose previous segment
                                    continue;
                                }

                            } else {
                                if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " UTURN");
                                // Opposite direction
                                // --> there will be a problem, roof could go infinite.
                                // Similar problem may occure with very sharp angles.
                            }
                        } else {
                            boolean convex = det < 0;

                            if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " Prevous: " + segmentstr(previousSegment) + " dd="+dd+" det=" + det + " convex="+convex);

                            if (straightSegments.contains(previousSegment)) {

                                // Straight segment cuts current on own axis
                                if (dd != 0 && (convex ^ dd < 0))
                                    continue;
                            } else {
                                // Non straight segment cuts current on bisector

                                if (dd != d && (convex ^ dd < d)) { // we want to test dd < d or dd > d according to convex
                                    if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " -> Out because of previous");
                                    continue;
                                }
                            }
                        }
                    }

                   Segment2d nextSegment = string.get(index + 1);
                    if (nextSegment != null) { // Should be true, we process a polygon
                        double dd = nextSegment.signedDistanceTo(x,y);
                        double det = segment.direction().determinant(nextSegment.direction());
                        if (det == 0.0) {
                            if (segment.direction().dot(nextSegment.direction()) > 0) {
                                // Same direction
                                // Choose segment according to projected index
                                if (segment.nearestPointIndex(x, y) > segment.length()) {
                                    // Choose next segment
                                    continue;
                                }

                            } else {
                                if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " UTURN");

                                // Opposite direction
                                // --> there will be a problem, roof could go infinite.
                                // Similar problem may occure with very sharp angles.
                            }
                        } else {

                            boolean convex = det > 0;

                            if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " Next: " + segmentstr(nextSegment) + " dd="+dd+" det=" + det + " convex="+convex);

                            if (straightSegments.contains(nextSegment)) {
                                // Straight segment cuts current on own axis
                                if (dd != 0 && (convex ^ dd < 0)) // 0 = d / infinite slope
                                    continue;
                            } else {
                                // Non straight segment cuts current on bisector
                                if (dd != d && (convex ^ dd < d)) {
                                    if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " -> Out because of next");
                                    continue;
                                }
                            }
                        }
                    }

                    // Ok, now, we select segment if it has a lower distance than previously
                    if (selectedSegment == null || d < lineDistance) {
                        if (debug) System.out.println("(" + x  + ", " + y +") #" + index + " **** SELECTED **** Distance=" + d);

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
