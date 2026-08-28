package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllowlistTestingAxisMapperBuilderTest {
    @Test
    public void testMaxSizeUnder() {
        AllowlistTestingAxisMapperBuilder allowlist = new AllowlistTestingAxisMapperBuilder(new int[]{2, 5, 50});
        assertEquals(50, allowlist.maxSizeFittingUnder(50));
        assertEquals(50, allowlist.maxSizeFittingUnder(60));
        assertEquals(5, allowlist.maxSizeFittingUnder(49));
        assertEquals(2, allowlist.maxSizeFittingUnder(3));
        assertEquals(2, allowlist.maxSizeFittingUnder(2));
        assertEquals(-1, allowlist.maxSizeFittingUnder(1));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(1, new AllowlistTestingAxisMapperBuilder(new int[]{1, 5, 6}).minimumSize());
        assertEquals(2, new AllowlistTestingAxisMapperBuilder(new int[]{7, 2, 6, 3}).minimumSize());
        assertEquals(7, new AllowlistTestingAxisMapperBuilder(new int[]{7, 7, 7}).minimumSize());
    }
}
