package com.ignfab.minalac.generator.parameters.placeables.structures;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.*;

public class BlueprintStructureParamsTest {

    @Test
    void testValidate() {
        BlueprintPlaceableStructureParams params;

        // Minimal test
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, y, z ]
            with:
              'a': A
            blueprint:
              - - "aa"
                - "aa"
              - - "aa"
                - "aa"
            """
        ));

        assertDoesNotThrow(params::validate);

        // Maximal test
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            xOffset: 10
            yOffset: 5
            zOffset: -3
            axes: [ x, y, z ]
            with:
              'a': A
            blueprint:
              - - "aa"
                - "aa"
              - - "aa"
                - "aa"
            """
        ));
        assertDoesNotThrow(params::validate);

        // Missing axis
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, y ]
            with:
              'a': A
            blueprint:
              - - "aa"
                - "aa"
              - - "aa"
                - "aa"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: x
            with:
              'a': A
            blueprint:
              - "aa"
              - "aa"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // Missing dimension
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, y, z ]
            with:
              'a': A
            blueprint:
              - "aa"
              - "aa"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, y ]
            with:
              'a': A
            blueprint: "aa"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        // Same axis twice
        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, y, x ]
            with:
              'a': A
            blueprint:
              - - "a"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> ParamsTester.deserialize(
            BlueprintPlaceableStructureParams.class,
            """
            axes: [ x, x ]
            with:
              'a': A
            blueprint:
              - "a"
            """
        ));
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
