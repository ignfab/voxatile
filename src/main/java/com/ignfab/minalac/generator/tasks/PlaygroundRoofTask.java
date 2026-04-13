package com.ignfab.minalac.generator.tasks;

import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.algorithm.Distance;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.WorldSize2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineString2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LinearRing2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Polygon2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

public class PlaygroundRoofTask extends ModelTask<Shape2dConvertibleModel> {
    private final Placeable placeable;
    private final RenderRoofTask.RoofType type;
    private final String altitudeMetadata;
    private final String heightMetadata;
    private final Shape2dVoxelizer voxelizer = new SurfaceVoxelizer2d();

    private boolean applyF = false;

    public PlaygroundRoofTask(RenderRoofTask.RoofType type, String altitudeMetadata, String heightMetadata, Placeable placeable, boolean applyF) {
        super(Shape2dConvertibleModel.class, null);
        this.type = type;
        this.placeable = placeable;
        this.altitudeMetadata = altitudeMetadata;
        this.heightMetadata = heightMetadata;
        this.applyF = applyF;
    }

    private static Shape2d testingShapeRectangle(WorldCoords2d start, WorldSize2d size) {
        return new Polygon2d(LinearRing2d.fromPoints(
            start,
            new WorldCoords2d(start.x(), start.y() + size.y()),
            new WorldCoords2d(start.x() + size.x(), start.y() + size.y()),
            new WorldCoords2d(start.x() + size.x(), start.y())
        ));
    }

    private static Shape2d testingShapeRectangle(int xS, int yS, WorldSize2d size) {
        return testingShapeRectangle(new WorldCoords2d(xS, yS), size);
    }

    private void drawMansard(Shape2d shape, GenerationTile tile, int altitude) {
        /// Ajout Barycentre
        int xG = 0;
        int yG = 0;
        int n = 0;
        for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            xG = xG + segment.start().x();
            yG = yG + segment.start().y();
            n++;
        }
        WorldCoords2d barycenter = new WorldCoords2d(xG / n, yG / n);

        double minDistanceToSegmentFromBarycenter = Double.MAX_VALUE;
        for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            double d = Math.abs(segment.signedDistanceTo(barycenter));
            minDistanceToSegmentFromBarycenter = Math.min(d, minDistanceToSegmentFromBarycenter);
        }
        //// Fin ajout

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();
            Double distance = null;

            for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {

                double index = segment.nearestPointIndex(x, y);
                double extra = 0;
                double d;
                if (index < 0) {
                    d = segment.start().squareDistanceTo(x, y);
                    extra = -index;
                } else if (index > segment.length()) {
                    d = segment.end().squareDistanceTo(x, y);
                    extra = index - segment.length();
                } else {
                    d = segment.signedDistanceTo(x, y);
                    d = d * d;
                }

                if (d >= (extra * extra) && (distance == null || distance > d)) {
                    distance = d;
                }
            }

            /// Modification
            if (distance != null) {
                distance = Math.sqrt(distance);
                double value;

                value = doubleSlopeF(distance, minDistanceToSegmentFromBarycenter * 0.5, 2.0, 1);

                int pValue = altitude + (int) Math.round(value);
                while (pValue > altitude) {
                    placeable.place(tile.voxels(), x, y, pValue);
                    pValue--;
                }
            }
        }
    }

    private void drawGabled(Shape2d shape, GenerationTile tile, int altitude) {
        int xG = 0;
        int yG = 0;
        int n = 0;
        for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            xG = xG + segment.start().x();
            yG = yG + segment.start().y();
            n++;
        }
        WorldCoords2d barycenter = new WorldCoords2d(xG / n, yG / n);

        Segment2d min = null;
        for (Segment2d seg : Iterables.flatMap(shape.lineStrings(), LineString2d::segments)) {
            if (min == null)
                min = seg;
            else if (seg.length() < min.length())
                min = seg;
        }

        Vector2d v = min.normal().multiply(10).add(new Vector2d(barycenter.x(), barycenter.y()));
        WorldCoords2d end = new WorldCoords2d((int) v.x(), (int) v.y());

        Segment2d faite = new Segment2d(barycenter, end);

        Segment2d opp = null;
        for (Segment2d seg : Iterables.flatMap(shape.lineStrings(), LineString2d::segments)) {
            if (!seg.equals(min) && Math.abs(seg.direction().dot(faite.direction())) < 0.2)
                opp = seg;
        }

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();

            // TODO: A revoir (plein de supposition faites sur la géometrie)
            int d = (int) (opp.length() - Math.abs(faite.nearestPointIndex(x, y)));

            placeable.place(tile.voxels(), x, y, altitude + d);
        }
    }

    private static double f(double d, double max) {
        return max * (1 - Math.pow(1 - (d / max), 2));
    }

    private static double doubleSlopeF(double distance, double seuil, double slope1, double slope2) {
        double value;
        if (distance < seuil)
            value = slope1 * distance;
        else
            value = slope2 * (distance - seuil) + seuil * slope1;
        return value;
    }

    private static double distanceToCircle(double distance, double slope, double max) {
        if (slope != 1)
            throw new UnsupportedOperationException("tmp");
        /*
        // int[] tab = {1, 3, 3, 5, 6, 8, 8, 9, 9, 12, 13, 13, 14, 15, 15, 15};
        int[] tab = {1, 5, 5, 6, 6, 6, 8, 8, 10, 12, 14, 14, 14, 15, 15, 15};
        return tab[(int) distance];

         */
        double r = max - distance;
        return Math.sqrt(max * max - r * r);
    }

    private static double distanceToEllipse(double distance, double slope, double max) {
        if ( slope <= 0)
            throw new UnsupportedOperationException("Slope can't be negative");
        double u = distance - max;
        // double b_ellipse = slope * max;
        // See written notes
        return Math.sqrt(slope * slope * (max * max - u * u));
    }


    @Override
    public void run(GenerationTile tile) {
        int altitude = 5;

        WorldSize2d size = new WorldSize2d(50, 30);

        //drawMansard(testingShapeRectangle(5, 0, size), tile, altitude);
        drawHipped(testingShapeRectangle(5, 40, new WorldSize2d(68, 100)), tile, altitude, 1.75);
        //drawGabled(testingShapeRectangle(5, 80, size), tile, altitude);
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void drawHipped(Shape2d shape, GenerationTile tile, int altitude, double slope) {
        /// Ajout Barycentre
        int xG = 0;
        int yG = 0;
        int n = 0;
        for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), LineString2d::segments)) {
            xG = xG + segment.start().x();
            yG = yG + segment.start().y();
            n++;
        }
        WorldCoords2d barycenter = new WorldCoords2d(xG / n, yG / n);

        double minDistanceToSegmentFromBarycenter = Double.MAX_VALUE;
        for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), LineString2d::segments)) {
            double d = Math.abs(segment.signedDistanceTo(barycenter));
            minDistanceToSegmentFromBarycenter = Math.min(d, minDistanceToSegmentFromBarycenter);
        }
        //// Fin ajout
        Set<Double> debug = new HashSet<>();
        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();
            Double distance = null;
            for (Segment2d segment : Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
                double index = segment.nearestPointIndex(x, y);
                double extra = 0;
                double d;
                if (index < 0) {
                    d = segment.start().squareDistanceTo(x, y);
                    extra = -index;
                } else if (index > segment.length()) {
                    d = segment.end().squareDistanceTo(x, y);
                    extra = index - segment.length();
                } else {
                    d = segment.signedDistanceTo(x, y);
                    d = d * d;
                }

                if (d >= (extra * extra) && (distance == null || distance > d))
                    distance = d;
            }
            if (distance != null) {
                distance = Math.sqrt(distance);
                double value;

                debug.add(distance);

                // TODO: apply formule circle
                //if (true)
                //    throw new RuntimeException(" : " + minDistanceToSegmentFromBarycenter);
                value = distanceToEllipse(distance, slope, minDistanceToSegmentFromBarycenter);
                // value = distance * slope;

                int pValue = altitude + (int) Math.round(value);
                while (pValue > altitude) {
                    placeable.place(tile.voxels(), x, y, pValue);
                    pValue--;
                }
            }
        }
        if (false) {
            System.out.println(debug);
            throw new RuntimeException();
        }
    }
}
