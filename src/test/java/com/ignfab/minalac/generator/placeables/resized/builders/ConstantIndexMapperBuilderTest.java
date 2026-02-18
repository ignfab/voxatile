package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;

import static org.junit.jupiter.api.Assertions.*;

public class ConstantIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(UnresizableStructureException.class, () -> new ConstantIndexMapperBuilder(7). build(8));
        assertThrows(UnresizableStructureException.class, () -> new ConstantIndexMapperBuilder(6). build(8));

        throw new RuntimeException("Not finished");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(-1, new ConstantIndexMapperBuilder(3).maxSizeUnder(-1));

        assertEquals(3, new ConstantIndexMapperBuilder(3).maxSizeUnder(4));
        assertEquals(3, new ConstantIndexMapperBuilder(3).maxSizeUnder(3));
        assertEquals(-1, new ConstantIndexMapperBuilder(3).maxSizeUnder(2));

        assertEquals(0, new ConstantIndexMapperBuilder(0).maxSizeUnder(0));
        assertEquals(0, new ConstantIndexMapperBuilder(0).maxSizeUnder(1));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(7, new ConstantIndexMapperBuilder(7).minimumSize());
        assertEquals(0, new ConstantIndexMapperBuilder(0).minimumSize());
    }
}
