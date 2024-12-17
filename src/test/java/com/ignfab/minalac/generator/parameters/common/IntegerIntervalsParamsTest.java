package com.ignfab.minalac.generator.parameters.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;
import com.ignfab.minalac.generator.utils.IntegerInterval;
import com.ignfab.minalac.generator.utils.IntegerIntervals;

public class IntegerIntervalsParamsTest {
    @Test
    void testSingleValue() {
        IntegerIntervalsParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalsParams.class, "1"));
        assertDoesNotThrow(params::validate);
        IntegerIntervals intervals = assertDoesNotThrow(() -> params.create());
        Iterator<IntegerInterval> iterator = intervals.iterator();
        assertDoesNotThrow(iterator::next);
        assertFalse(iterator.hasNext());

    }

    @Test
    void testList() {
        IntegerIntervalsParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalsParams.class, "[ 1, 2, 3 ]"));
        assertDoesNotThrow(params::validate);
        IntegerIntervals intervals = assertDoesNotThrow(() -> params.create());
        Iterator<IntegerInterval> iterator = intervals.iterator();
        assertDoesNotThrow(iterator::next);
        assertDoesNotThrow(iterator::next);
        assertDoesNotThrow(iterator::next);
        assertFalse(iterator.hasNext());
    }

    @Test
    void testEmpty() {
        IntegerIntervalsParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalsParams.class, "[]"));
        assertDoesNotThrow(params::validate);
        IntegerIntervals intervals = assertDoesNotThrow(() -> params.create());
        Iterator<IntegerInterval> iterator = intervals.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testInvalid() {
        IntegerIntervalsParams params = assertDoesNotThrow(() -> ParamsTester.deserialize(IntegerIntervalsParams.class, "[ 4..3 ]"));
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
