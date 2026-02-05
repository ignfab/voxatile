package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class SuperDelegateIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertEquals(0, 0);
        throw new RuntimeException("Not implemented");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(
            11,
            new SuperDelegateIndexMapperBuilder(
                new TestingIndexMapperBuilder(10, 12),
                new TestingIndexMapperBuilder(10, 11),
                new TestingIndexMapperBuilder(10, 13)
            ).maxSizeUnder(15)
        );

        assertEquals(
            0,
            new SuperDelegateIndexMapperBuilder(
                new TestingIndexMapperBuilder(0, 15),
                new TestingIndexMapperBuilder(0)
            ).maxSizeUnder(15)
        );
    }

    @Test
    public void testMinimumSize() {
        throw new RuntimeException("Not implemented");
    }
}
