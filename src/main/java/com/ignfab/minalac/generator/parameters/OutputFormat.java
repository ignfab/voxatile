package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Output format parameters.
 * Actually, deserialized from a string containing the registered format name.
 */
public class OutputFormat {
    private Supplier<VoxelWorld> worldCreator;
    private Class<? extends PlaceableParams> voxelParams;
    private Function<String, PlaceableParams> shortcutVoxelParams;

    /**
     * Creates a new {@code OutputFormat}.
     *
     * @param worldCreator A static method creating a new VoxelWorld for this format
     * @param voxelParams {@code PlaceableParams} class to use for deserialization when no {@code type} parameter given
     * @param shortcutVoxelParams Method creating a {@code PlaceableParams} from a String
     */
    public OutputFormat(
        Supplier<VoxelWorld> worldCreator,
        Class<? extends PlaceableParams> voxelParams,
        Function<String, PlaceableParams> shortcutVoxelParams
    ) {
        this.worldCreator = worldCreator;
        this.voxelParams = voxelParams;
        this.shortcutVoxelParams = shortcutVoxelParams;
    }

    /**
     * Creates a {@code VoxelWorld} for this format.
     *
     * @return A new {@code VoxelWorld}
     */
    public VoxelWorld createWorld() {
        return worldCreator.get();
    }

    /**
     * Creates a new {@code PlaceableParams} out of a {@code String} if a {@code shortcutVoxelParams} method have been given to constructor.
     *
     * @param text String to interpret as {@code PlaceableParams}
     *
     * @return A new {@code PlaceableParams}
     */
    public PlaceableParams createVoxelParams(String text) {
        if (shortcutVoxelParams == null)
            throw new IllegalArgumentException("Selected format does not allow deserialization from simple string");

        return shortcutVoxelParams.apply(text);
    }

    /**
     * Creates a new {@code PlaceableParams} out of a {@code JsonNode} if a {@code voxelParams} class have been given to constructor.
     *
     * @param node {@code JsonNode} to interpret as {@code PlaceableParams}
     * @param context Context to use for deserialization
     *
     * @return A new {@code PlaceableParams}
     */
    public PlaceableParams createVoxelParams(JsonNode node, DeserializationContext context) throws JacksonException {
        if (voxelParams == null)
            throw new IllegalArgumentException("Selected format does not have a default voxel type structure, add `type` attribute");

        return context.readTreeAsValue(node, voxelParams);
    }

    /**
     * Register stuff needed for format deserialization.
     *
     * @param mapperBuilder {@code MapperBuilder} into which register stuff
     */
    public void registerPlaceableDeserializer(MapperBuilder<?, ?> mapperBuilder) {
        // Register format specific deserializer
        SimpleModule module = new SimpleModule("OutputFormatModule");
        module.addDeserializer(PlaceableParams.class, new PlaceableParams.Deserializer(this));
        mapperBuilder.addModule(module);

        // Register base "voxel" type
        if (voxelParams != null)
            mapperBuilder.registerSubtypes(new NamedType(voxelParams, "voxel"));
    }

    /**
     * A custom deserializer for {@code OutputFormat}s.
     *
     * It maps format name to a {@code OutputFormat} object.
     */
    static class Deserializer extends ValueDeserializer<OutputFormat> {

        private final Map<String, OutputFormat> formats = new HashMap<>();

        public void registerFormat(String name, OutputFormat format) {
            formats.put(name, format);
        }

        @Override
        public OutputFormat deserialize(JsonParser parser, DeserializationContext context) throws DatabindException {

            String name = parser.readValueAs(String.class);
            if (!formats.containsKey(name))
                throw DatabindException.from(parser, "Invalid value for the \"format\" property. Should be one of %s.".formatted(formats.keySet()));

            return formats.get(name);
        }
    }
}
