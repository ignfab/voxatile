package com.ignfab.minalac.generator.parameters.common;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.parameters.utils.IntegerIntervalParams;
import com.ignfab.minalac.generator.utils.IntegerInterval;

public class IntegerIntervalParamsTest {
    @Test
    void testCanonicalFromTo() {
        IntegerIntervalParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalParams.class,
            """
                from: 3
                to: 4
            """));
        assertDoesNotThrow(params::validate);
        IntegerInterval interval = assertDoesNotThrow(() -> params.create());
        assertEquals(3, interval.begin());
        assertEquals(4, interval.end());
    }

    @Test
    void testCanonicalValue() {
        IntegerIntervalParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalParams.class,
            """
                value: 2
            """));
        assertDoesNotThrow(params::validate);
        IntegerInterval interval = assertDoesNotThrow(() -> params.create());
        assertEquals(2, interval.begin());
        assertEquals(2, interval.end());
    }

    @Test
    void testShortenedInterval() {
        IntegerIntervalParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalParams.class, "-4..-3"));
        assertDoesNotThrow(params::validate);
        IntegerInterval interval = assertDoesNotThrow(() -> params.create());
        assertEquals(-4, interval.begin());
        assertEquals(-3, interval.end());
    }

    @Test
    void testShortenedValue() {
        IntegerIntervalParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalParams.class, "-5"));
        assertDoesNotThrow(params::validate);
        IntegerInterval interval = assertDoesNotThrow(() -> params.create());
        assertEquals(-5, interval.begin());
        assertEquals(-5, interval.end());
    }

    @Test
    void testInvalidReverse() {
        IntegerIntervalParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalParams.class, "3..1"));
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
