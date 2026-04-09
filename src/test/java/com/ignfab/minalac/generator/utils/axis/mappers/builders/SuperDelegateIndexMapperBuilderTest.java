package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SuperDelegateIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertEquals(0, 0);

        // TODO: not finished
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(
            11,
            new OverlayAxisMapperBuilder(
                new TestingIndexMapperBuilder(10, 12),
                new TestingIndexMapperBuilder(10, 11),
                new TestingIndexMapperBuilder(10, 13)
            ).maxSizeUnder(15)
        );

        assertEquals(
            0,
            new OverlayAxisMapperBuilder(
                new TestingIndexMapperBuilder(0, 15),
                new TestingIndexMapperBuilder(0)
            ).maxSizeUnder(15)
        );
    }

    @Test
    public void testMinimumSize() {
        // TODO: not finished
    }
}
