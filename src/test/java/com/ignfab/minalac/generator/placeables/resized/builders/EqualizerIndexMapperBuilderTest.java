package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;

import static org.junit.jupiter.api.Assertions.*;

public class EqualizerIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnresizableStructureException.class, () -> new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 1).build(5));
        assertThrows(UnresizableStructureException.class, () -> new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 1).build(7));
        throw new RuntimeException("Not finished");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(6));
        assertEquals(9, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(10));

        assertEquals(-1, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(5));

        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 1).maxSizeUnder(0));
        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 1).maxSizeUnder(5));

        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3, 4), 2).maxSizeUnder(6));
        assertEquals(10, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3, 4), 2).maxSizeUnder(10));

        // TODO-12 : Lié au 12. Mais pose d'autre problemes
        // assertEquals(5, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0, 3), 1).maxSizeUnder(5));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0).minimumSize());
        assertEquals(3, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 1).minimumSize());
        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).minimumSize());

        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 2).minimumSize());
    }
}
