package com.ignfab.minalac.generator.tasks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
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

    public class Line2d {
        private final Vector2d origin;
        private final Vector2d direction;

        public Line2d(Vector2d origin, Vector2d direction) {
            this.origin = origin;
            this.direction = direction.normalize();

        }
        public Line2d(Segment2d segment) {
            this.origin = segment.start().toVector();
            this.direction = segment.direction(); // Already normalized
        }

        Vector2d direction() {
            return direction;
        }

        Vector2d origin() {
            return origin;
        }

        Double intersectionIndex(Line2d other) {
          double det = direction.determinant(other.direction);
            if (det == 0.0)
                return null;

            return other.direction.determinant(origin.x() - other.origin.x(), origin.y() - other.origin.y()) / det;
        }

        Vector2d at(double index) {
            // wont work, not normalized direction!
            return new Vector2d(
                direction.x() * index + origin.x(),
                direction.y() * index + origin.y()
            );
        }

        Vector2d intersection(Line2d other) {
            Double index = intersectionIndex(other);
            return index == null ? null : at(index);
        }

        public double nearestPointIndex(Vector2d vector) {
            return direction.dot(vector.x() - origin.x(), vector.y() - origin.y());
        }

        @Override
        public String toString() {
            return "Line2d(org="+origin+" dir="+direction+")";
        }

        // TODO: Missing slope factor
        Line2d bissector(Line2d other) {
            Vector2d intersection = intersection(other);
            if (intersection == null)
                // If lines are parallel, we return a middle line parallel
                return new Line2d(
                    new Vector2d((origin.x() + other.origin.x()) / 2, (origin.y() + other.origin.y()) / 2),
                    other.direction
                );

            // Usual case: bissector is a line passing through the intersection of the two lines
            // With a mean direction.
            return new Line2d(intersection, direction.opposite().add(other.direction));
        }
    }



    // Rearange a ring list of segments so it ends with selected segment.
    private List<Segment2d> listOtherSegments(List<Segment2d> segments, int selected) {
        List<Segment2d> result = new LinkedList<>();
        int index = selected + 1;
        int count = segments.size() - 1;
        while(count > 0) {
            if (index >= segments.size())
                index = 0;
            result.add(segments.get(index));
            count--;
            index++;
        }
        return result;
    }

    private record SlopeBorder(
        Line2d line, // Border line
        double startIndex, // index interval it applies
        double endIndex
    ) {
        boolean includes(double index) {
            return startIndex < endIndex ?
                (index >= startIndex && index <= endIndex) :
                (index >= endIndex && index <= startIndex);
        }

    }

    // START SEGMENT IS THE LAST ONE
    private List<SlopeBorder> computeSlope(Segment2d baseSegment, List<Segment2d> otherSegments) {

        // First compute bissectors of starting segment with other segments.
        List<Line2d> bissectors = new LinkedList<>();
        Line2d baseLine = new Line2d(baseSegment);
        System.out.println("\nBase: "+baseSegment+" "+baseLine+"\n");
        System.out.println("Bissectors:"); // Includes selected segment line at the end
        for (Segment2d segment : otherSegments) {
            Line2d line = new Line2d(segment);
            System.out.println("bissector "+ bissectors.size()+": with="+segment+" "+line+" -> "+baseLine.bissector(line));
            bissectors.add(baseLine.bissector(line));
        }

        // Now compute which of these bissecors will form the slope shape.

        // Slope shape is modelized by a list of starting segment index intervals associated with a border line.
        List<SlopeBorder> result = new LinkedList<>();

        System.out.println("Indexes:");

        double startIndex = baseSegment.length();
        System.out.println("Segment length: " + baseSegment.length());
        double endIndex = 0;

        Line2d border = bissectors.get(0);
        int index = 0;
        double startBorderIndex = 0; // First intersection = start of first bissector
        double det = baseLine.direction().determinant(border.direction());
        System.out.println("Determinant="+det);
        if (det > 0) border = new Line2d(border.origin(), border.direction().opposite());

        do {
            System.out.println(index + ": " + border + " start index = "+startBorderIndex + " "+ border.at(startBorderIndex));
            Vector2d intersection = null;
            double score = Double.MAX_VALUE;

            for (int next = index + 1; next < bissectors.size(); next ++) {
                Double ii = border.intersectionIndex(bissectors.get(next));
                if (ii != null) {
                    System.out.println("  " + next + ": intersects at " + ii + " " + border.at(ii));
                    boolean selected =
                       (ii > startBorderIndex && ii < score);
                    if (selected) {
                        System.out.println("  "+next+": selected!");
                        score = ii;
                        index = next; // Jump to this line
                        intersection = border.at(ii);
                    }
                } else
                    System.out.println("  " + next + ": no intersection");

            }
            if (intersection == null) {
                // No more intersection found, we are over for this shape
                System.out.println("  no more intersection!");
                break;
            }

            // Resulting index interval goes from last intersection index (startIndex) to this intersection index
            endIndex = baseSegment.nearestPointIndex(intersection);
            System.out.println("==> From "+startIndex + " to "+endIndex+": "+ border);
            result.add(new SlopeBorder(border, startIndex, endIndex));

            det = border.direction().determinant(bissectors.get(index).direction());
            System.out.println("Determinant="+det);
            // Proceed to next border (selected intersector)
            border = bissectors.get(index);
            if (det > 0) border = new Line2d(border.origin(), border.direction().opposite());

            // Along border, selected indexes should start from intersection (never go backwards)
            startBorderIndex = border.nearestPointIndex(intersection);
            // --> Semble mauvais, ça donne un index qui ne correspond pas à l'intersection :O

            // Keep current intersection index for next border
            startIndex = endIndex;
        } while (true);

        // End shape with last border connecting to starting segment starting index (0)
        System.out.println("finaly:\n==> From "+startIndex + " to 0.0: "+ border);
        result.add(new SlopeBorder(border, startIndex, 0));

        return result;
    }

    private Map<Segment2d, List<SlopeBorder>> computeSlopes(List<Segment2d> segments) {
        Map<Segment2d, List<SlopeBorder>> result = new HashMap<>();

        for (int index = 0; index < segments.size(); index ++)
            result.put(segments.get(index), computeSlope(segments.get(index), listOtherSegments(segments, index)));
        return result;
    }

    private List<Segment2d> prepareRing(LinearRing2d ring) {

        boolean clockwise = ring.isClockwise();

        Segment2d current, next;
        Iterator<Segment2d> iter;

        List<Segment2d> list = new LinkedList<>();
        iter = ring.segments().iterator();
        current = iter.next();
        while (iter.hasNext()) {
            next = iter.next();

            if (current.direction().equals(next.direction().opposite())) {
                // If consecutive segments have oposite direction, add a 0 lenght segment
                list.add(current);
                list.add(new Segment2d(
                    current.end(),
                    clockwise ? current.direction().normal() : current.direction().normal().opposite()
                ));
                current = next;
            } else if (current.direction().equals(next.direction())) {
                // Merge consecutive segments with same direction
                current = new Segment2d(current.start(), next.end());
            } else {
                // Add current segment to output.
                list.add(current);
                current = next;
            }
        }

        next = list.get(0);

        // Add remaining segment if not in the same direction of the first one
        if (current.direction().equals(next.direction().opposite())) {
                // If consecutive segments have oposite direction, add a 0 lenght segment
                list.add(current);
                list.add(new Segment2d(
                    current.end(),
                    clockwise ? current.direction().normal() : current.direction().normal().opposite()
                ));
            } else if (current.direction().equals(next.direction())) {
                // Merge consecutive segments with same direction
                current = new Segment2d(current.start(), next.end());
                list.set(0, current);
            } else {
                // Add current segment to output.
                list.add(current);
            }

        return list;
    }

    // FIRST WORK ONLY ON SHELL
    // We will see holes later

//NOTE:
// shell is clockwise
// holes are anticlockwise
// positive determinant of successive vectors : turns anticlockwise -> concave angle
// negative determinant of successive vectors : turns clockwise -> convex angle
// null determinant means vector are aligned, maybe uturn, maybe contine

    private Line2d getSlopeBorder(List<SlopeBorder> borders, double index) {
        for (SlopeBorder border : borders) {
            if (border.includes(index))
                return border.line;
        }
        return null;
    }

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slopeFactor) {
//        List<Element> elements = prepareGeometry(shape);
        System.out.println("\n************************************\n");

        // TODO: Manage multiple polygons
        // TODO: Manage holes
        List<Segment2d> segments = prepareRing(shape.polygons().iterator().next().shell());
        Map<Segment2d, List<SlopeBorder>> segmentBissectors = computeSlopes(segments);


        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            Segment2d selectedSegment = null;

            for (Segment2d segment: segments) {
                double index = segment.nearestPointIndex(x, y);
                Line2d line = getSlopeBorder(segmentBissectors.get(segment), index);

                // Not in selected roof pane (index out of bounds)
                if (line == null)
                    continue;

                // (x, y) must be on left side of every selected bissector (there should be only one + selected segment)
                // Determinant is signed distance
                if (line.direction.determinant(x - line.origin.x(), y - line.origin.y()) > 0)
                    continue;

                selectedSegment = segment;
                break;
            }

            if (selectedSegment != null)
                placeable.place(tile.voxels(), x, y,
                    altitude + (int) Math.round(slopeFactor * selectedSegment.signedDistanceTo(x, y)));

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
