package com.ignfab.minalac.generator.utils.coordinates;

import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.locationtech.jts.geom.util.AffineTransformation;

/**
 * A {@link MapToWorldConverter} for testing purposes.
 */
public final class TestingConverter extends MapToWorldConverter {
    /**
     * A testing {@code MapToWorldConverter} that does no transformation.
     */
    public static final TestingConverter IDENTITY = new TestingConverter(IdentityTransform.create(2), new AffineTransformation());

    private TestingConverter(MathTransform crsTransform, AffineTransformation postTransform) {
        super(crsTransform, postTransform);
    }
}
