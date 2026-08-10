package com.ignfab.minalac.generator.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
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

/*
Il faut faire une première passe qui mâche le travail et élimine les cas limite (det=0):
- Créer un ensemble de "segments" avec toujours un précédent/suivant (sinon c'est cas d'erreur);
- Fusionner les segments successifs de la même direction;
- Insérer un segment de longueur 0, à 90°, dans le cas de deux segments successifs de direction opposée;
- Ajouter éventuellement les calculs pouvant être factorisés;

Pour pouvoir stoquer le segment de longeur 0, il faut une structure spécifique qui conserve la direction.

Une fois cela fait, on peut lancer le calcul voxel par voxel.


Par segment, il nous faut pouvoir calculer:
- La distance signée (-> ligne)
- L'appartenance au segment du point projeté (-> ligne + intervale indice)
- La direction (-> ligne)
*/

    private record Element(
        Segment2d current,
        Segment2d previous,
        double detPrevious,
        Segment2d next,
        double detNext
    ) {
        Element(
            Segment2d current,
            Segment2d previous,
            Segment2d next
        ) {
            this(
                current,
                previous,
                previous.direction().determinant(current.direction()),
                next,
                current.direction().determinant(next.direction())
            );
        }

    }

    private List<Element> prepareGeometry(Shape2d shape) {

        List<Element> result = new LinkedList<>();

        for (LineString2d string: shape.lineStrings()) {
            LinearRing2d ring = (LinearRing2d)string; // Should always work with polygons
            boolean clockwise = ring.isClockwise();

            Segment2d current, previous, next;
            Iterator<Segment2d> iter;

            // First create a list of segments per ring
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

            // Then populate the resulting record list with values
            previous = list.get(list.size()-1);
            iter = list.iterator();
            current = iter.next();
            while (iter.hasNext()) {
                next = iter.next();
                result.add(new Element(current, previous, next));
                previous = current;
                current = next;
            }
            next = list.get(0);
            result.add(new Element(current, previous, next));
        }

        return result;
    }

//NOTE:
// shell is clockwise
// holes are anticlockwise
// positive determinant of successive vectors : turns anticlockwise -> concave angle
// negative determinant of successive vectors : turns clockwise -> convex angle
// null determinant means vector are aligned, maybe uturn, maybe contine

    private double distance(Segment2d segment, int x, int y) {
        double d = segment.signedDistanceTo(x, y) + 1;
        if (d < 0)
            return d;

        double i = segment.nearestPointIndex(x, y);
        if (i < 0)
            return -i;

        if (i > segment.length())
            return i - segment.length();

        return d;
    }

    private void drawSlopes(GenerationTile tile, Shape2d shape, Set<Segment2d> straightSegments, int altitude, double slopeFactor) {
        List<Element> elements = prepareGeometry(shape);

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            // For each 2d position, we try to find the corresponding slope (corresponding roof edge segment)
            Segment2d selectedSegment = null;
            double distance = 1000.0;
            double d2 = -1000.0;

            for (Element slope: elements) {
                double d = slope.current().signedDistanceTo(x, y); // * Slope

                if (d < -1) continue;

                double npi = slope.current().nearestPointIndex(x, y);
                double i = 0.0;
                    if (npi < 0) i = - npi;
                    if (npi > slope.current().length()) i = npi - slope.current().length();

                // If there is already a selected segment nearest, no need to look further
//                if (selectedSegment != null && d >= distance) continue;

                // No roof surface for straight segments
//                if (straightSegments.contains(slope.current())) continue;

                /*
                // "Cut" roof plane according to previous slope
                double previousDistance = slope.previous().signedDistanceTo(x,y);
                // Never discard slope if distances are equals (this may reject wanted slope)
                // Keep closest slope if convex, reject it if concave
//                if (previousDistance != d && (slope.detPrevious() > 0 ^ previousDistance < d))
                if (slope.detPrevious() > 0 && previousDistance < d)
                    continue;

                // "Cut" roof plane according to next slope
                double nextDistance = slope.next().signedDistanceTo(x,y);
                // Never discard slope if distances are equals (this may reject wanted slope)
                // Keep closest slope if convex, reject it if concave
//                if (nextDistance != d && (slope.detNext() > 0 ^ nextDistance < d))
                if (slope.detNext() > 0 && nextDistance < d)
                    continue;

                // Ok, now, we select segment if it has a lower distance than previously
                */
                if (i == 0 && d < distance) {
                    distance = d;
                    d2 = d;
                    selectedSegment = slope.current();
                }/*
                if (i > 0 && d - i > d2) {
                    distance = d;
                    d2 = d - i;
                    selectedSegment = slope.current();
                }*/
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
