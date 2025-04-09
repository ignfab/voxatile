package com.ignfab.minalac.generator.parameters;

import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.Position2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.ignfab.minalac.generator.generation.DataSource;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.renderers.Renderer;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * This class is used to create a new {@link Generation} from {@link GenerationParams} parameters.
 */
public final class GenerationCreator {

    private GenerationCreator() {}

    /**
     * Creates a new {@code Generation} from the specified parameters.
     *
     * @param params the parameters
     * @return the corresponding generation object
     */
    static Generation create(GenerationParams params) {
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

        Generation generation = new Generation(
            params.format.createWorld(),
            new Seed(params.seed),
            targetCrs,
            center[0],
            center[1],
            params.area.extentX,
            params.area.extentY,
            params.horizontalScale,
            params.verticalScale,
            Math.toRadians(params.area.angle)
        );

        // TODO: refactor when placeable deserialization is implemented
        params.heightmaps.forEach((name, heightmapParams) ->
            generation.heightmaps().add(
                name,
                heightmapParams.create(generation)
            )
        );

        // Data Sources scheduling
        params.sources.forEach((name, dataSourceParams) -> {
            DataSource dataSource = dataSourceParams.create(generation);
            generation.scheduler().schedule(
                "source:" + name,
                () -> dataSource.fetch(generation.world().limits()),
                dataSourceParams.after
            );
        });

        // Renderers scheduling
        params.renderers.forEach((name, rendererParams) -> {
            Renderer renderer = rendererParams.create(generation);
            generation.scheduler().schedule(
                "renderer:" + name,
                () -> renderer.render(generation.world()),
                rendererParams.after
            );
        });

        return generation;
    }
}
