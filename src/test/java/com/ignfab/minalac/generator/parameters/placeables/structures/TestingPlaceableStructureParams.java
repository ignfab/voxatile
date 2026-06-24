package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.util.List;

import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;

/**
 * Class providing valid and invalid {@link PlaceableStructureParams} for tests.
 */
public final class TestingPlaceableStructureParams {
    /**
     * A valid {@link PlaceableStructureParams}.
     */
    public static final PlaceableStructureParams VALID;

    /**
     * An invalid {@link PlaceableStructureParams}.
     */
    public static final PlaceableStructureParams INVALID;

    static {
        VALID = new PlaceableStructureParams(List.of(new Valid()));
        INVALID = new PlaceableStructureParams(List.of(new Invalid()));
    }

    private static final class Valid extends PlaceableStructureParams.Variant {
        @Override
        public void apply(Seed seed, PlaceableStructure structure) {}
    }

    private static final class Invalid extends PlaceableStructureParams.Variant {
        public void validate() {
            throw new IllegalArgumentException("Invalid structure");
        }

        @Override
        public void apply(Seed seed, PlaceableStructure structure) {}
    }

    private TestingPlaceableStructureParams() {}
}
