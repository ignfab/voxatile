package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A fake heightmap for testing.
 * <p>
 * It embarks both a mocking {@code HeightmapDeclaration} and a {@code WritableHeightmapSpec}
 * making it only usable in "mono-tile" environment.
 */
public class TestingHeightmap extends Heightmap {
    private final Declaration declaration;
    private final Spec spec;
    private final String name;

    /**
     * Creates a new storable {@code TestingHeightmap}.
     *
     * @param name Name of that heightmap (allows it to be stored)
     * @param bbox Area of that heightmap
     * @param defaultValue Default value for this heightmap
     */
    public TestingHeightmap(String name, WorldBBox2d bbox, int defaultValue) {
        super(bbox, defaultValue);
        this.name = name;
        this.declaration = new Declaration(this, defaultValue);
        this.spec = new Spec(this);
    }

    /**
     * Creates a new {@code TestingHeightmap}.
     *
     * @param bbox Area of that heightmap
     * @param defaultValue Default value for this heightmap
     */
    public TestingHeightmap(WorldBBox2d bbox, int defaultValue) {
        super(bbox, defaultValue);
        this.name = "";
        this.declaration = new Declaration(this, defaultValue);
        this.spec = new Spec(this);
    }

    /**
     * Returns specs corresponding to this heightmap usage as readable/writable heightmap.
     *
     * @return spec
     */
    public Spec spec() {
        return spec;
    }

    /**
     * Returns declaration corresponding to this heightmap.
     *
     * @return declaration
     */
    public Declaration declaration() {
        return declaration;
    }

    /**
     * Mocking class for {@link WritableHeightmapSpec}.
     */
    public static class Spec extends WritableHeightmapSpec {
        private final TestingHeightmap created;

        Spec(TestingHeightmap created) {
            this.created = created;
        }

        // Beware when testing, this should be called only on "mono-tile" environment.
        @Override
        protected WritableHeightmap create(HeightmapStore store) {
            return created;
        }
    }

    /**
     * Mocking class for {@link HeightmapDeclaration}.
     */
    public static class Declaration extends HeightmapDeclaration {
        private final TestingHeightmap created;

        Declaration(TestingHeightmap created, int defaultValue) {
            super(created.name, defaultValue);
            this.created = created;
        }

        /**
         * Returns a mocking testing heightmap.
         * Beware when testing, this should be called only on "mono-tile" environment.
         *
         * @param bbox ignored
         * @return the testing heightmap.
         */
        @Override
        public TestingHeightmap create(WorldBBox2d bbox) {
            return created;
        }
    }
}
