package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.generation.Store;
import com.ignfab.minalac.generator.generation.heightmaps.UnaryHeightmapOperator;
import com.ignfab.minalac.generator.generation.heightmaps.UnaryOperatorHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;
import com.ignfab.minalac.generator.parameters.utils.IntegerIntervalParams;
import com.ignfab.minalac.generator.utils.IntegerInterval;

/**
 * Remaps a heightmap value with a new value.
 * It returns the value associated with the first interval containing the original value.
 * If no match is found, the original value is returned.
 */
public class RemapHeightmapParams extends CustomReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams remap;
    /**
     * The mapping associating intervals with values (required).
     */
    @JsonSetter(
        nulls = Nulls.FAIL,
        contentNulls = Nulls.FAIL
    )
    @JsonDeserialize(keyUsing = RemapHeightmapParams.ValuesKeyDeserializer.class)
    // LinkedHashMap is used because interval declaration order is important for remapping.
    public LinkedHashMap<IntegerIntervalParams, Integer> mapping;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param remap the base heightmap.
     * @param mapping the interval mapping.
     */
    @ConstructorProperties({"remap", "mapping"})
    public RemapHeightmapParams(ReadableHeightmapParams remap, LinkedHashMap<IntegerIntervalParams, Integer> mapping) {
        this.remap = remap;
        this.mapping = mapping;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        remap.validate();
        mapping.keySet().forEach(IntegerIntervalParams::validate);
    }

    @Override
    public UnboundReadableHeightmap create(Store<UnboundHeightmap> store) {
        LinkedHashMap<IntegerInterval, Integer> map = new LinkedHashMap<>();
        mapping.forEach(((intervalParams, integer) -> map.put(intervalParams.create(), integer)));
        IntUnaryOperator function = (i -> {
            for (Map.Entry<IntegerInterval, Integer> entry : map.entrySet())
                if (entry.getKey().contains(i))
                    return entry.getValue();
            return i;
        });
        return new UnaryOperatorHeightmap(remap.create(store), new UnaryHeightmapOperator.Simple(function));
    }

    static class ValuesKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
            return IntegerIntervalParams.FallbackParams.Deserializer.stringToFallbackParams(s);
        }
    }
}
