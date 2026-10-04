package fr.ign.voxatile.core.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RangeTestingAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(IllegalArgumentException.class, () -> new RangeTestingAxisMapperBuilder(5, 2, 0));
        assertDoesNotThrow(() -> new RangeTestingAxisMapperBuilder(2, 2, 0));
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(0, new RangeTestingAxisMapperBuilder(0).maxSizeFittingUnder(1));
        assertEquals(1, new RangeTestingAxisMapperBuilder(0, 2).maxSizeFittingUnder(1));

        assertEquals(-1, new RangeTestingAxisMapperBuilder(2, 5).maxSizeFittingUnder(1));

        assertEquals(2, new RangeTestingAxisMapperBuilder(2, 5).maxSizeFittingUnder(2));
        assertEquals(5, new RangeTestingAxisMapperBuilder(2, 5).maxSizeFittingUnder(5));
        assertEquals(3, new RangeTestingAxisMapperBuilder(2, 5).maxSizeFittingUnder(3));
        assertEquals(5, new RangeTestingAxisMapperBuilder(2, 5).maxSizeFittingUnder(6));

        assertEquals(2, new RangeTestingAxisMapperBuilder(2).maxSizeFittingUnder(3));
        assertEquals(2, new RangeTestingAxisMapperBuilder(2).maxSizeFittingUnder(2));
        assertEquals(-1, new RangeTestingAxisMapperBuilder(2).maxSizeFittingUnder(1));

    }

    @Test
    public void testMinimumSize() {
        assertEquals(2, new RangeTestingAxisMapperBuilder(2, 5).minimumSize());
        assertEquals(2, new RangeTestingAxisMapperBuilder(2).minimumSize());
    }
}
