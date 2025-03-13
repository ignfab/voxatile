package com.ignfab.minalac.generator.parameters;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.parameters.placeables.voxels.MTVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.renderers.TestingRendererParams;

import static org.junit.jupiter.api.Assertions.*;

public class ParamsParserTest {
    private static final String MINIMAL_YAML = """
        area:
          center:
            latitude: 5.8
            longitude: 2.4
          extentX: 500
          extentY: 2500
          angle: 30
        format: minetest
        """;

    private ParamsParser newParser() {
        ParamsParser parser = new ParamsParser();
        parser.registerFormat("minetest", new OutputFormat(MTVoxelWorld::new, MTVoxelTypeParams.class, MTVoxelTypeParams::new));
        return parser;
    }

    @Test
    public void testParseFormat() {
        assertDoesNotThrow(() -> newParser().parse("""
            {
              "area": {
                "center": {
                  "latitude": 5.8,
                  "longitude": 2.4
                },
                "extentX": 500,
                "extentY": 2500
              },
              "format": "minetest"
            }
            """
        ), "Should be able to parse JSON format");

        assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML), "Should be able to parse YAML format");
    }

    @Test
    public void testParseWithoutOptionalFields() {
        GenerationParams params = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML));

        // Checks if the configuration for default values was properly done, should equal to default values
        assertEquals(1.0, params.verticalScale);
        assertEquals(1.0, params.horizontalScale);
        assertEquals(Collections.emptyMap(), params.heightmaps);
        assertEquals(Collections.emptyMap(), params.renderers);
    }

    @Test
    public void testParseWithOptionalFields() {
        // optional renderers field deserialization is tested on testRegisterRenderer()
        GenerationParams params = assertDoesNotThrow(() -> newParser().parse("""
            verticalScale: 2.5
            horizontalScale: 5.2
            heightmaps:
              someHeightmap:
                default: 3
            """
            + MINIMAL_YAML
        ));

        assertEquals(2.5, params.verticalScale);
        assertEquals(5.2, params.horizontalScale);
        assertEquals(30, params.area.angle);
        assertEquals(1, params.heightmaps.size());
        assertEquals(3, params.heightmaps.get("someHeightmap").defaultValue);
    }

    @Test
    public void testParseMissingRequiredField() {
        assertThrows(ParseException.class, () -> newParser().parse("format: minetest"), "Absence of the area field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                longitude: 2.4
              extentX: 500
              extentY: 2500
            format: minetest
            """
        ), "Absence of the latitude field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
              extentX: 500
              extentY: 2500
            format: minetest
            """
        ), "Absence of the longitude field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
                longitude: 2.4
              extentY: 2500
            format: minetest
            """
        ), "Absence of the extentX field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
                longitude: 2.4
              extentX: 500
            format: minetest
            """
        ), "Absence of the extentY field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
                longitude: 2.4
              extentX: 500
              extentY: 2500
            """
        ), "Absence of the format field should throw an exception");
    }

    @Test
    public void testParseHeightmaps() {
        // By default, jackson doesn't check the existence of duplicates keys: last occurrence takes precedence.
        // Checks if configuration was properly done so an exception is thrown.
        assertThrows(ParseException.class, () -> newParser().parse("""
            heightmaps:
              ground:
                default: -9
              ground:
                default: 8
            """
            + MINIMAL_YAML
        ), "Presence of duplicate keys should throw an exception");

        // Testing explicit and implicit empty heightmaps
        GenerationParams paramsExplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "heightmaps: {}"), "Explicit empty heightmaps should not throw an exception");
        assertEquals(Collections.emptyMap(), paramsExplicit.heightmaps, "Explicit empty heightmaps should result in a empty map");
        // `heightmaps:` is by default deserialized as null
        GenerationParams paramsImplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "heightmaps:"), "Implicit empty heightmaps should not throw an exception");
        assertEquals(Collections.emptyMap(), paramsImplicit.heightmaps, "Implicit empty heightmaps should result in a empty map");

        // By default, jackson sets null values for empty elements.
        // Checks if configuration was properly done, so an exception is thrown.
        assertThrows(ParseException.class, () -> newParser().parse("""
            heightmaps:
              altitude:
                default: -9
              ground:
            """
            + MINIMAL_YAML
        ), "Empty ground heightmap should throw an exception");

        // To ensure that an illegal value for the default is caught and thrown as a ParseException when deserialized
        assertThrows(ParseException.class, () -> newParser().parse("""
            heightmaps:
              altitude:
                default: illegal
            """
            + MINIMAL_YAML
        ), "Illegal value for default field");
    }

    @Test
    public void testParseRenderers() {
        // Testing explicit and implicit empty renderers
        GenerationParams paramsExplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "renderers: {}"), "Explicit empty renderers should not trigger exception");
        assertEquals(Collections.emptyMap(), paramsExplicit.renderers, "Explicit empty renderers should result in a empty map");
        // `renderers:` is by default deserialized as null
        GenerationParams paramsImplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "renderers:"), "Implicit empty renderers should not trigger exception");
        assertEquals(Collections.emptyMap(), paramsImplicit.renderers, "Implicit empty renderers should result in a empty map");

        // There is a field `type` needed for resolving the concrete class.
        assertThrows(ParseException.class, () -> newParser().parse("""
            renderers:
              someRendererName:
                someField: foo
            """
            + MINIMAL_YAML
        ), "Type field is missing");

        ParamsParser parser = newParser();
        parser.registerParams("customIdentifier", TestingRendererParams.class);

        GenerationParams genParams = assertDoesNotThrow(() -> parser.parse("""
            renderers:
              smeargle:
                type: customIdentifier
                requiredField: sketch
            """
            + MINIMAL_YAML
        ), "Renderer should be deserialized");

        // type field is not deserialized only used to find out the class that should be used for deserialization
        assertNotNull(genParams.renderers.get("smeargle"));
        assertNull(genParams.renderers.get("smeargle").type, "Type field should not be deserialized");

        TestingRendererParams rendererParams = (TestingRendererParams) genParams.renderers.get("smeargle");
        assertEquals("sketch", rendererParams.requiredField);
        assertEquals("defaultOptionalValue", rendererParams.optionalField);

        assertThrows(ParseException.class, () -> parser.parse("""
            renderers:
              zorua:
                type: wrongIdentifier
                requiredField: rest
            """
            + MINIMAL_YAML
        ), "Wrong type field value");
    }

    @Test
    public void testParseReferences() {
        ParamsParser parser = newParser();
        parser.registerParams("tragedy", TestingRendererParams.class);

        GenerationParams params = assertDoesNotThrow(() -> parser.parse("""
            references:
              - &girl juliet
              - &boy romeo
            renderers:
              verona:
                type: tragedy
                requiredField: *girl
                optionalField: *boy
            """
            + MINIMAL_YAML
        ));

        TestingRendererParams rendererParams = assertInstanceOf(TestingRendererParams.class, params.renderers.get("verona"));
        assertEquals("juliet", rendererParams.requiredField);
        assertEquals("romeo", rendererParams.optionalField);
    }
}
