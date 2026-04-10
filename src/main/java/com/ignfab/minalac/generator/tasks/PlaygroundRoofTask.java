package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
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

    private static Shape2d testingShapeRectangle() {
        return new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(5, 5),
            new WorldCoords2d(20, 5),
            new WorldCoords2d(20, -15),
            new WorldCoords2d(5, -15))
        );
    }

    private static Shape2d testingShapeBigRectangle() {
        return new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(5, 5),
            new WorldCoords2d(55, 5),
            new WorldCoords2d(55, -25),
            new WorldCoords2d(5, -25))
        );
    }

    private static Shape2d testingShapeSquare() {
        return new Polygon2d(LinearRing2d.fromPoints(
            new WorldCoords2d(5, 5),
            new WorldCoords2d(15, 5),
            new WorldCoords2d(15, -5),
            new WorldCoords2d(5, -5))
        );
    }

    private void drawMansard(Shape2d shape, GenerationTile tile, int altitude) {
        /// Ajout Barycentre
        int xG = 0;
        int yG = 0;
        int n = 0;
        for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            xG = xG + segment.start().x();
            yG = yG + segment.start().y();
            n++;
        }
        WorldCoords2d barycenter = new WorldCoords2d(xG / n, yG / n);

        double minDistanceToSegmentFromBarycenter = Double.MAX_VALUE;
        for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            double d = Math.abs(segment.signedDistanceTo(barycenter));
            minDistanceToSegmentFromBarycenter = Math.min(d, minDistanceToSegmentFromBarycenter);
        }
        //// Fin ajout

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();
            Double distance = null;

            for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {

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

                value = ff(distance, minDistanceToSegmentFromBarycenter * 0.5, 2.0, 1);

                int pValue = altitude + (int) Math.round(value);
                while (pValue > altitude) {
                    placeable.place(tile.voxels(), x, y, pValue);
                    pValue--;
                }
            }
        }
    }

    private static double f(double d, double max) {
        return max * (1 - Math.pow(1 - (d / max), 2));
    }

    private static double ff(double distance, double seuil, double slope1, double slope2) {
        double value;
        if (distance < seuil)
            value = slope1 * distance;
        else
            value = slope2 * (distance - seuil) + seuil * slope1;
        return value;
    }


    @Override
    public void run(GenerationTile tile) {
        int altitude = 5;

        Shape2d shape = testingShapeBigRectangle().toShape2d();

        drawMansard(shape, tile, altitude);
        //oldDrawHipped(shape, tile, altitude, 1);
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void oldDrawHipped(Shape2d shape, GenerationTile tile, int altitude, double slope) {
        /// Ajout Barycentre
        int xG = 0;
        int yG = 0;
        int n = 0;
        for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            xG = xG + segment.start().x();
            yG = yG + segment.start().y();
            n++;
        }
        WorldCoords2d barycenter = new WorldCoords2d(xG / n, yG / n);

        double max = Double.MAX_VALUE;
        for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {
            double distBary = Math.abs(segment.signedDistanceTo(barycenter));
            if (max > distBary)
                max = distBary;
        }

        if (applyF) {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA : " + max);
        }
        //// Fin ajout

        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            int x = voxel.coords().x();
            int y = voxel.coords().y();
            Double distance = null;

            for (Segment2d segment: Iterables.flatMap(shape.lineStrings(), (lineString) -> lineString.segments())) {

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
                    max = segment.signedDistanceTo(barycenter);
                    max = max * max;
                }
            }

            /// Modification
            if (distance != null) {
                distance = Math.sqrt(distance);
                double value;
                int iMax = 0;
                if (applyF) {
                    value = f(distance, Math.sqrt(max));
                    iMax = 0;
                } else {
                    value = slope * distance;
                }
                int pValue = altitude + (int) Math.round(value);
                for (int i = 0; i <= iMax; i ++)
                    placeable.place(tile.voxels(), x, y, pValue - i);
            }
        }
    }
}
