package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.referencing.crs.DefaultGeographicCRS;

/**
 * A {@link MapToWorldConverter} for testing purposes.
 */
public final class TestingConverter extends MapToWorldConverter {
    /**
     * A testing {@code MapToWorldConverter} that does no transformation.
     */
    public static final TestingConverter IDENTITY = new TestingConverter();
    /**
     * An unused testing {@code MapToWorldConverter}.
     */
    public static final TestingConverter UNUSED = null;

    private TestingConverter() {
        super(
            DefaultGeographicCRS.WGS84,
            DefaultGeographicCRS.WGS84,
            0,
            0,
            1.0,
            1.0,
            0,
            0
        );
    }
}
