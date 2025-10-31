package com.ignfab.minalac.generator.parameters.processors.post;

import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetadataConvertPostProcessorParamsTest {

    @Test
    public void testCreate() {
        assertDoesNotThrow(() ->
            new MetadataConvertPostProcessorParams(
                "metadata",
                MetadataConvertPostProcessorParams.ConversionFunctionParams.HORIZONTAL_DISTANCE
            )
        );
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(new MetadataConvertPostProcessorParams("metadata", MetadataConvertPostProcessorParams.ConversionFunctionParams.ALTITUDE)::validate);
        assertThrows(IllegalArgumentException.class, new MetadataConvertPostProcessorParams("", MetadataConvertPostProcessorParams.ConversionFunctionParams.ALTITUDE)::validate);
    }

    @Test
    public void testDeserialize() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerSubtypes(new NamedType(MetadataConvertPostProcessorParams.class, "conversion"));

        MetadataConvertPostProcessorParams min = assertDoesNotThrow(() -> mapper.readValue(
            """
                type: conversion
                metadata: height
                convertAs: verticalDistance
                ifMissing: ignore
                ifConversionFail: discardModel
                """,
            MetadataConvertPostProcessorParams.class
        ));
        assertEquals("height", min.metadata);
        assertEquals(MetadataConvertPostProcessorParams.ConversionFunctionParams.VERTICAL_DISTANCE, min.convertAs);
        assertEquals(FailurePolicyParams.IGNORE, min.ifMissing);
        assertEquals(FailurePolicyParams.DISCARD_MODEL, min.ifConversionFail);

        MetadataConvertPostProcessorParams max = assertDoesNotThrow(() -> mapper.readValue(
            """
            type: conversion
            metadata: height
            convertAs: altitude
            """,
            MetadataConvertPostProcessorParams.class
        ));
        assertEquals("height", max.metadata);
        assertEquals(MetadataConvertPostProcessorParams.ConversionFunctionParams.ALTITUDE, max.convertAs);
        assertEquals(FailurePolicyParams.ERROR, max.ifMissing);
        assertEquals(FailurePolicyParams.ERROR, max.ifConversionFail);
    }

    @Test
    public void testCreateConversionFunctionParams() throws FactoryException {
        MapToWorldConverter converter = new MapToWorldConverter(
            CRS.decode("EPSG:2154"),
            CRS.decode("EPSG:2154"),
            0,
            0,
            // horizontalScale
            3,
            // verticalScale
            0.5,
            0,
            1
        );
        Model model = new TestingModel(converter);

        Function<Object, Integer> horizontal = assertDoesNotThrow(() -> MetadataConvertPostProcessorParams.ConversionFunctionParams.HORIZONTAL_DISTANCE.create(model));
        // This is to make sure the function is properly created.
        assertThrows(IllegalArgumentException.class, () -> horizontal.apply("60.34"));
        assertEquals(181, horizontal.apply(60.34));

        Function<Object, Integer> vertical = assertDoesNotThrow(() -> MetadataConvertPostProcessorParams.ConversionFunctionParams.VERTICAL_DISTANCE.create(model));
        assertEquals(70, vertical.apply(141.7));
    }
}
