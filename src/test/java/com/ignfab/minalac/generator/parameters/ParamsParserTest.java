package com.ignfab.minalac.generator.parameters;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.parameters.tasks.TestingTaskParams;

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
        format: testing
        """;

    private ParamsParser newParser() {
        ParamsParser parser = new ParamsParser();
        parser.registerFormat("testing", new OutputFormat(TestingVoxelWorld::new, TestingVoxelParams.class, TestingVoxelParams::new));
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
              "format": "testing"
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
        assertEquals(Collections.emptyMap(), params.forEachTile);
    }

    @Test
    public void testParseWithOptionalFields() {
        // optional tasks field deserialization is tested on testRegisterTasks()
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
        assertThrows(ParseException.class, () -> newParser().parse("format: testing"), "Absence of the area field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                longitude: 2.4
              extentX: 500
              extentY: 2500
            format: testing
            """
        ), "Absence of the latitude field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
              extentX: 500
              extentY: 2500
            format: testing
            """
        ), "Absence of the longitude field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
                longitude: 2.4
              extentY: 2500
            format: testing
            """
        ), "Absence of the extentX field should throw an exception");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area:
              center:
                latitude: 5.8
                longitude: 2.4
              extentX: 500
            format: testing
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
    public void testAnchors() {
        assertDoesNotThrow(() -> newParser().parse("""
            references:
              - &area
                center:
                  latitude: 5.8
                  longitude: 2.4
                extentX: 500
                extentY: 2500
                angle: 30
              - &format testing
            area: *area
            format: *format
            """
        ), "Anchors should be resolved");

        assertThrows(ParseException.class, () -> newParser().parse("""
            area: *area
            format: *format
            """
        ), "Unresolved anchors should throw an exception");

        ParamsParser parser = newParser();
        parser.registerParams("customIdentifier", TestingTaskParams.class);

        assertDoesNotThrow(() -> parser.parse("""
            forEachTile:
              smeargle1: &rdr
                type: customIdentifier
                requiredField: sketch
              smeargle2: *rdr
              smeargle3: *rdr
            """
            + MINIMAL_YAML
        ), "Repeating Yaml parameters should be parsed without generating anchors");
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
    public void testRegisterTasks() {
        // Testing explicit and implicit empty schedules
        GenerationParams paramsExplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "forEachTile: {}"), "Explicit empty forEachTile should not trigger exception");
        assertEquals(Collections.emptyMap(), paramsExplicit.forEachTile, "Explicit empty forEachTile should result in a empty map");
        // `forEachTile:` is by default deserialized as null
        GenerationParams paramsImplicit = assertDoesNotThrow(() -> newParser().parse(MINIMAL_YAML + "forEachTile:"), "Implicit empty forEachTile should not trigger exception");
        assertEquals(Collections.emptyMap(), paramsImplicit.forEachTile, "Implicit empty forEachTile should result in a empty map");

        // There is a field `type` needed for resolving the concrete class.
        assertThrows(ParseException.class, () -> newParser().parse("""
            forEachTile:
              someTaskName:
                someField: foo
            """
            + MINIMAL_YAML
        ), "Type field is missing");

        ParamsParser parser = newParser();
        parser.registerParams("customIdentifier", TestingTaskParams.class);

        GenerationParams genParams = assertDoesNotThrow(() -> parser.parse("""
            forEachTile:
              smeargle:
                type: customIdentifier
                requiredField: sketch
            """
            + MINIMAL_YAML
        ), "Task should be deserialized");

        // type field is not deserialized only used to find out the class that should be used for deserialization
        assertNotNull(genParams.forEachTile.get("smeargle"));
        assertNull(genParams.forEachTile.get("smeargle").type, "Type field should not be deserialized");

        TestingTaskParams taskParams = (TestingTaskParams) genParams.forEachTile.get("smeargle");
        assertEquals("sketch", taskParams.requiredField);
        assertEquals("defaultOptionalValue", taskParams.optionalField);

        assertThrows(ParseException.class, () -> parser.parse("""
            forEachTile:
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
        parser.registerParams("tragedy", TestingTaskParams.class);

        GenerationParams params = assertDoesNotThrow(() -> parser.parse("""
            references:
              - &girl juliet
              - &boy romeo
            forEachTile:
              verona:
                type: tragedy
                requiredField: *girl
                optionalField: *boy
            """
            + MINIMAL_YAML
        ));

        TestingTaskParams tasksParams = assertInstanceOf(TestingTaskParams.class, params.forEachTile.get("verona"));
        assertEquals("juliet", tasksParams.requiredField);
        assertEquals("romeo", tasksParams.optionalField);
    }

    @Test
    public void testParseNestedReferences() {
        ParamsParser parser = newParser();
        parser.registerParams("deathStar", TestingTaskParams.class);

        GenerationParams params = assertDoesNotThrow(() -> parser.parse("""
            references:
              - &sith vador
              - &jedi luke
            forEachTile:
              death-star-1: &death-star
                type: deathStar
                requiredField: *sith
                optionalField: *jedi
              death-star-2: *death-star
            """
            + MINIMAL_YAML
        ));

        TestingTaskParams tasksParams1 = assertInstanceOf(TestingTaskParams.class, params.forEachTile.get("death-star-1"));
        assertEquals("vador", tasksParams1.requiredField);
        assertEquals("luke", tasksParams1.optionalField);
        TestingTaskParams tasksParams2 = assertInstanceOf(TestingTaskParams.class, params.forEachTile.get("death-star-2"));
        assertEquals("vador", tasksParams2.requiredField);
        assertEquals("luke", tasksParams2.optionalField);
    }
}
