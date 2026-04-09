package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnbuildableException.class, () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1).build(5));
        assertThrows(UnbuildableException.class, () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1).build(7));
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2).maxSizeUnder(6))
        );
        assertEquals(9, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2).maxSizeUnder(10))
        );

        assertEquals(-1, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2).maxSizeUnder(5))
        );

        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 1).maxSizeUnder(0))
        );
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 1).maxSizeUnder(5))
        );

        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3, 4), 2).maxSizeUnder(6))
        );
        assertEquals(10, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3, 4), 2).maxSizeUnder(10))
        );

        // TODO-12 : Lié au 12. Mais pose d'autre problemes
        // assertEquals(5, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0, 3), 1).maxSizeUnder(5));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 0).minimumSize())
        );
        assertEquals(3, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1).minimumSize())
        );
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2).minimumSize())
        );
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 2).minimumSize())
        );
    }
}
