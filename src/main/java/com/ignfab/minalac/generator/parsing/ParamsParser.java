package com.ignfab.minalac.generator.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.outputs.minecraft.MCVoxelWorld;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelWorld;
import org.geotools.api.geometry.Position;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.geometry.Position2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A Json/Yaml parser able to decode parameters into Generation and World objects.
 */
public class ParamsParser {
    private final RawParams rawParams;
    private final CoordinateReferenceSystem targetCrs;
    private static final Map<String, Supplier<VoxelWorld>> FORMATS = Map.of(
        "minecraft", MCVoxelWorld::new,
        "minetest", MTVoxelWorld::new
    );

    /**
     * Builds a parser from a serialized string.
     *
     * @param serialized A string containing parameters data in Json or Yaml format
     */
    public ParamsParser(String serialized) throws ParseException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
        try {
            rawParams = mapper.readValue(serialized, RawParams.class);
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
        if (rawParams.verticalScale <= 0)
            throw new ParseException("The field verticalScale must be greater than 0");
        if (rawParams.horizontalScale <= 0)
            throw new ParseException("The field horizontalScale must be greater than 0");
        if (-90 > rawParams.area.center.latitude
            || rawParams.area.center.latitude > 90
            || -180 > rawParams.area.center.longitude
            || rawParams.area.center.longitude > 180)
            throw new ParseException("The coordinates of the center field are incorrect");
        if (rawParams.area.extendX <= 0)
            throw new ParseException("The field extendX must be greater than 0");
        if (rawParams.area.extendY <= 0)
            throw new ParseException("The field extendY must be greater than 0");
        if (!FORMATS.containsKey(rawParams.format))
            throw new ParseException("The provided format is not supported");
        try {
            // TODO : If not provided its default value should be calculated by finding the appropriated projected CRS for the provided center point.
            // At the moment the default value is EPSG:2154
            targetCrs = CRS.decode(rawParams.crs == null ? "EPSG:2154" : rawParams.crs);
        } catch (FactoryException e) {
            throw new ParseException(e);
        }
    }

    /**
     * Creates generation object from parameters.
     *
     * @return A Generation object corresponding to parameters given at construction
     */
    public Generation createGeneration() {
        // This conversion is temporary.
        // Should be removed when Generation class supports the conversion from WSG84 to targetCRS
        double[] convertedCoords;
        try {
            MathTransform mathTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, targetCrs);
            Position position = new Position2D(DefaultGeographicCRS.WGS84, rawParams.area.center.longitude, rawParams.area.center.latitude);
            convertedCoords = mathTransform.transform(position, position).getCoordinate();
        } catch (FactoryException | TransformException e) {
            throw new RuntimeException(e);
        }
        Generation generation = new Generation(
            targetCrs,
            convertedCoords[0],
            convertedCoords[1],
            rawParams.area.extendX,
            rawParams.area.extendY,
            rawParams.horizontalScale,
            rawParams.verticalScale
        );

        if (rawParams.heightmaps != null)
            for (RawParams.HeightmapParams heightmapParams : rawParams.heightmaps)
                generation.addHeightmap(heightmapParams.name, new Heightmap(generation.getWorldBBox2d(), heightmapParams.defaultValue));

        return generation;
    }

    /**
     * Creates voxel world object from parameters.
     *
     * @return A VoxelWorld object corresponding to parameters given at construction
     */
    public VoxelWorld createVoxelWorld() {
        VoxelWorld world = FORMATS.get(rawParams.format).get();
        WorldBBox3d maximumLimits = world.maxLimits();
        world.setLimits(new WorldBBox3d(
            -rawParams.area.extendX / 2,
            -rawParams.area.extendY / 2,
            maximumLimits.minZ(),
            rawParams.area.extendX,
            rawParams.area.extendY,
            maximumLimits.sizeZ()
        ));
        return world;
    }
}
