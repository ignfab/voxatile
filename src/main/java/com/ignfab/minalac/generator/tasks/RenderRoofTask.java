package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link ModelTask} drawing roof over a surface.
 */
public class RenderRoofTask extends ModelTask<Shape2dConvertibleModel> {

    public enum RoofType {
        FLAT,
        HIPPED
    };

    private final Placeable placeable;
    private final RoofType type;
    private final String altitudeMetadata;
    private final String heightMetadata;

    private boolean applyF = false;

    private final Shape2dVoxelizer voxelizer = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param borders What to place on geometries borders
     */
    public RenderRoofTask(ModelSelection selection, RoofType type, String altitudeMetadata, String heightMetadata, Placeable placeable, boolean applyF) {
        super(Shape2dConvertibleModel.class, selection);
        this.type = type;
        this.placeable = placeable;
        this.altitudeMetadata = altitudeMetadata;
        this.heightMetadata = heightMetadata;
        this.applyF = applyF;
    }

    private void drawFlat(Shape2d shape, GenerationTile tile, int altitude) {
        for (Positioned2d voxel : tile.limits().to2d().filterInside(voxelizer.voxelize(shape))) {
            placeable.place(tile.voxels(),  voxel.coords().x(), voxel.coords().y(), altitude);
        }
    }

    private void drawHipped(Shape2d shape, GenerationTile tile, int altitude, double slope) {
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
            Double maxDistance = 0.0;

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
                    maxDistance = Math.max(distance, maxDistance);
                }
            }

            /// Modification
            if (distance != null) {
                distance = Math.sqrt(distance);
                maxDistance = Math.sqrt(maxDistance);
                if (applyF) {
                    System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB : " + maxDistance);
                }
                double value;
                int iMax = 0;
                if (applyF) {
                    value = f(distance, maxDistance);
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

    private static double f(double d, double max) {
        return max * (1 - Math.pow(1 - (d / max), 2));
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {

        if (model.getMetadata(altitudeMetadata) == null ||
            model.getMetadata(heightMetadata) == null)
            return;

        int altitude = (int) Math.round(
            ((Number) model.getMetadata(altitudeMetadata)).doubleValue() +
            ((Number) model.getMetadata(heightMetadata)).doubleValue() / tile.generation().getVerticalScale()
        );

        Shape2d shape = model.toShape2d();

        switch(type) {
            case FLAT:
                drawFlat(shape, tile, altitude);
                break;
            case HIPPED:
                drawHipped(shape, tile, altitude, 1);
                break;
            default:
                break;
        }
    }
}
