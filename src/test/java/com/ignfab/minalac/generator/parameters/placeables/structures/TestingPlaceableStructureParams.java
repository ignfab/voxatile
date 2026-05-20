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
        VALID = new PlaceableStructureParams();
        VALID.params = List.of(new Valid());
        INVALID = new PlaceableStructureParams();
        INVALID.params = List.of(new Invalid());
    }

    private static final class Valid extends PlaceableStructureParams.Variant {
        public void apply(Seed seed, PlaceableStructure.Builder structureBuilder) {}
    }

    private static final class Invalid extends PlaceableStructureParams.Variant {
        public void validate() {
            throw new IllegalArgumentException("Invalid structure");
        }
        public void apply(Seed seed, PlaceableStructure.Builder structureBuilder) {}
    }

    private TestingPlaceableStructureParams() {}
}
