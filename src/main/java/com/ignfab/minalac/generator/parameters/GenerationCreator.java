package com.ignfab.minalac.generator.parameters;

import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.Position2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.tasks.TileTask;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * This class is used to create a new {@link Generation} from {@link GenerationParams} parameters.
 */
public final class GenerationCreator {

    private GenerationCreator() {}

    /**
     * Creates a new {@code Generation} from the specified parameters.
     *
     * @param params the parameters
     * @param maxTileSize max tile size if tiling wanted, else null
     * @return the corresponding generation object
     */
    static Generation create(GenerationParams params, Integer maxTileSize) {
        // TODO : If not provided its default value should be calculated by finding the appropriated projected CRS for the provided center point.
        // At the moment the default value is EPSG:2154
        CoordinateReferenceSystem targetCrs;
        double[] center;
        try {
            targetCrs = CRS.decode(params.crs == null ? "EPSG:2154" : params.crs);
            MathTransform mathTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, targetCrs);
            Position position = new Position2D(DefaultGeographicCRS.WGS84, params.area.center.longitude, params.area.center.latitude);
            center = mathTransform.transform(position, position).getCoordinate();
        } catch (FactoryException e) {
            throw new RuntimeException("Wasn't able to decode the CRS code", e);
        } catch (TransformException e) {
            throw new RuntimeException("Wasn't able to convert the coordinates", e);
        }

        VoxelWorld world = params.format.createWorld();
        world.getMetadata().setWorldName(params.worldName);
        Generation generation = new Generation(
            world,
            new Seed(params.seed),
            targetCrs,
            center[0],
            center[1],
            params.area.extentX,
            params.area.extentY,
            params.horizontalScale,
            params.verticalScale,
            Math.toRadians(params.area.angle),
            maxTileSize == null || maxTileSize <= 0 ? Math.max(params.area.extentX, params.area.extentY) : maxTileSize
        );

        params.heightmaps.forEach((name, heightmapParams) ->
            generation.heightmaps().add(heightmapParams.create(name))
        );

        // ForEachTile scheduling
        params.forEachTile.forEach((name, taskParams) -> {
            TileTask task = taskParams.create(generation);
            generation.scheduler().schedule(name, task);
            taskParams.addMarginsTo.forEach((modelType) -> {
                // Add placement margins to implied models
                generation.includeModelTypeMargins(modelType, task.placementMargins());
            });
        });
        params.forEachTile.forEach((name, taskParams) -> {
            for (String depName : taskParams.after)
                generation.scheduler().addDependency(name, depName);
        });

        return generation;
    }
}
