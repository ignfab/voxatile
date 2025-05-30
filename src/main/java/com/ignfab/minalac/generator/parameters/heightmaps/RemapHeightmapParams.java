package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.UnaryHeightmapOperator;
import com.ignfab.minalac.generator.parameters.utils.IntegerIntervalParams;
import com.ignfab.minalac.generator.utils.IntegerInterval;

/**
 * Parameters for a {@link UnaryOperationHeightmapSpec} which remaps all heightmap values to a new one.
 * It returns the value associated with the first interval containing the original value.
 * If no match is found, the original value is returned.
 */
public class RemapHeightmapParams implements ReadableHeightmapParams {
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
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        LinkedHashMap<IntegerInterval, Integer> map = new LinkedHashMap<>();
        mapping.forEach(((intervalParams, integer) -> map.put(intervalParams.create(), integer)));
        IntUnaryOperator function = (i -> {
            for (Map.Entry<IntegerInterval, Integer> entry : map.entrySet())
                if (entry.getKey().contains(i))
                    return entry.getValue();
            return i;
        });
        return new UnaryOperationHeightmapSpec(remap.create(store), new UnaryHeightmapOperator.Simple(function));
    }

    static class ValuesKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return IntegerIntervalParams.FallbackParams.Deserializer.stringToFallbackParams(key);
        }
    }
}
