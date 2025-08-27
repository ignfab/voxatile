package com.ignfab.minalac.generator.parameters.placeables.patterns;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RepeatPatternParamsTest {
    @Test
    public void testDeserialize() {
        // Minimal test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RepeatPatternParams.class,
            """
                repeatStructure:
                  with:
                    'B': default:stonebrick
                    'S': default:stone
                  axes: [ x, y ]
                  blueprint:
                    - 'SS'
                    - 'SB'
            """
        ));

        // Full test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RepeatPatternParams.class,
            """
                repeatStructure:
                  with:
                    'B': default:stonebrick
                    'S': default:stone
                  axes: [ x, y ]
                  blueprint:
                    - 'SS'
                    - 'SB'
                eachX:
                  shiftX: 1
                  shiftY: 1
                  shiftZ: 1
                eachY:
                  shiftX: 1
                  shiftY: 1
                  shiftZ: 1
                eachZ:
                  shiftX: 1
                  shiftY: 1
                  shiftZ: 1
            """
        ));
    }
}
