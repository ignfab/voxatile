package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class RepeatAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnbuildableException.class, () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).build(5));
        assertThrows(UnbuildableException.class, () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).build(7));
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).maxSizeUnder(6))
        );


        assertEquals(-1, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).maxSizeUnder(5))
        );

        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 1, Integer.MAX_VALUE).maxSizeUnder(0))
        );
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 1, Integer.MAX_VALUE).maxSizeUnder(5))
        );

        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3, 4), 2, Integer.MAX_VALUE).maxSizeUnder(6))
        );
        assertEquals(10, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3, 4), 2, Integer.MAX_VALUE).maxSizeUnder(10))
        );

        // TODO: Continue tests maximum occurences

        // Unresizable underlying structure
        // Max under 25 is 2 * 7 = 14
        assertEquals(14, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(7, 7), 2, 2).maxSizeUnder(25))
        );
        // Max under 14 is 2 * 7 = 14
        assertEquals(14, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(7, 7), 2, 2).maxSizeUnder(14))
        );

        // Resizable underlying structure (Can take any size from 2 to 8)
        // Max under 30 is max([2, 8]) * maxOccurences (3 here) -> 8 * 3 = 24
        assertEquals(24, assertDoesNotThrow(
            () -> new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(2, 8), 2, 3).maxSizeUnder(30))
        );

        // TODO-N-12: Revenir sur ça (Ca peut devenir compliqué car deux sources de variations de taille)
        // Ca serait bien d'avoir un TestingAxisBuilder prenant une liste de tailles possibles
        // Par exemple, taille possible de 6 et 8 avec minOccur de 2 et maxOccur de 3, on aurait maxUnder(25/24) = 24 et maxUnder(23) = 22
    }

    @Test
    public void testMinimumSize() {
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 0, Integer.MAX_VALUE).minimumSize())
        );
        assertEquals(3, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 1, Integer.MAX_VALUE).minimumSize())
        );
        assertEquals(6, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(3), 2, Integer.MAX_VALUE).minimumSize())
        );
        assertEquals(0, assertDoesNotThrow(
            () ->  new RepeatAxisMapperBuilder(new TestingAxisMapperBuilder(0), 2, Integer.MAX_VALUE).minimumSize())
        );
    }
}
