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

    // Beware, direction of line may be not normalized
    private record Line2d(Vector2d origin, Vector2d direction) {
        Line2d(Segment2d segment) {
            this(segment.start().toVector(), segment.direction());
        }

        Double intersectionIndex(Line2d other) {
          double det = direction.determinant(other.direction);
            if (det == 0.0)
                return null;

            return other.direction.determinant(origin.x() - other.origin.x(), origin.y() - other.origin.y()) / det;
        }

        Vector2d at(double index) {
            return origin.add(direction.multiply(index));
        }

        Vector2d intersection(Line2d other) {
            Double index = intersectionIndex(other);
            return index == null ? null : at(index);
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

    private record Bissector(
        Line2d bissector,
        double startIndex,
        double endIndex
    ) {
        boolean includes(double index) {
            return startIndex < endIndex ?
                (index >= startIndex && index <= endIndex) :
                (index >= endIndex && index <= startIndex);
        }

    }

    // Rearange a ring list of segments so it ends with selected segment.
    private List<Segment2d> prepareSegments(List<Segment2d> segments, int selected) {
        List<Segment2d> result = new LinkedList<>();
        int index = selected + 1;
        int count = segments.size();
        while(count > 0) {
            if (index >= segments.size())
                index = 0;
            result.add(segments.get(index));
            count--;
            index++;
        }
        return result;
    }


    // SELECTED SEGMENT IS THE LAST ONE
    private List<Bissector> computeBissectors(List<Segment2d> segments) {
        List<Bissector> result = new LinkedList<>();

        Segment2d selected = segments.get(segments.size() - 1);

        List<Line2d> bissectors = new LinkedList<>();
        Line2d selectedLine = new Line2d(selected);
        System.out.println("\nselected="+selected+" "+selectedLine+"\n");
        System.out.println("Bissectors:"); // Includes selected segment line at the end
        for (Segment2d segment : segments) {
            Line2d line = new Line2d(segment);
            System.out.println("bissector with="+segment+" "+line+" -> "+selectedLine.bissector(line));
            bissectors.add(selectedLine.bissector(line));
        }

        // Selected segment could be excluded from result
        // We start with selected segment as border line
        System.out.println("Indexes:");

        // Walks bissectors, put them on a line string (this will remove some)
        double startIndex = selected.length();
        double endIndex = 0;

        int index = 0;
        Line2d border = null;
        while (index < bissectors.size()) {
            border = bissectors.get(index);
            System.out.println(index + ": " + border);
            double intersectionIndex = Double.MAX_VALUE;
            index++;
            Vector2d intersection = null;
            for (int next = index; next < bissectors.size(); next ++) {
                System.out.println("  test " + next);
                Double ii = border.intersectionIndex(bissectors.get(next));
                if (ii != null) {
                    System.out.println("  intersects at " + ii);
                    if (ii > 0  // Pas prendre le précédent (on est dans un anneau)
                        && ii < intersectionIndex) {
                        System.out.println("  selected!");
                        intersectionIndex = ii;
                        index = next; // Jump to this line
                        intersection = border.at(ii);
                    }
                }
            }
            if (intersection == null)
                break;
            System.out.println("==> From "+startIndex + " to "+endIndex+": "+ border);

            endIndex = selected.nearestPointIndex(intersection);
            result.add(new Bissector(border, startIndex, endIndex));
            startIndex = endIndex;
        }
        if (border != null)
            result.add(new Bissector(border, startIndex, 0.0));

        return result;
    }

    private Map<Segment2d, List<Bissector>> computeRing(LinearRing2d ring) {
        Map<Segment2d, List<Bissector>> result = new HashMap<>();
        List<Segment2d> segments = prepareRing(ring);

        for (int index = 0; index < segments.size(); index ++)
            result.put(segments.get(index), computeBissectors(prepareSegments(segments, index)));

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

    private Line2d getBissector(List<Bissector> bissectors, double index) {
        for (Bissector bissector : bissectors) {
            if (bissector.includes(index))
                return bissector.bissector;
        }
        return null;
    }

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slopeFactor) {
//        List<Element> elements = prepareGeometry(shape);
        System.out.println("\n************************************\n");

        // TODO: Manage multiple polygons
        // TODO: Manage holes
        LinearRing2d ring = shape.polygons().iterator().next().shell();
        Map<Segment2d, List<Bissector>> segmentBissectors = computeRing(ring);


        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            Segment2d selectedSegment = null;

            for (Segment2d segment: ring.segments()) {
                double index = segment.nearestPointIndex(x, y);
                Line2d line = getBissector(segmentBissectors.get(segment), index);

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
