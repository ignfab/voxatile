package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

public interface IndexMapperBuilder {
    IndexMapper build(int size);
    default int ask(int size) {
        throw new RuntimeException("NOT IMPLEMENTED");
    }
    int minimalLength();

    class Identity implements IndexMapperBuilder {
        int allowedSize;

        public Identity(int allowedSize) {
            this.allowedSize = allowedSize;
        }

        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.Identity(size);
        }

        @Override
        public int minimalLength() {
            return allowedSize;
        }

        @Override
        public int ask(int size) {
            return allowedSize;
        }
    }

    // TODO: je met adaptatif car ça peut reduire
    class AdaptativeStretcher implements IndexMapperBuilder {
        int stretchableCoord;
        int lengthAtRest;

        public AdaptativeStretcher(int stretchableCoord, int lengthAtRest) {
            this.stretchableCoord = stretchableCoord;
            this.lengthAtRest = lengthAtRest;
        }

        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.Stretcher(stretchableCoord, lengthAtRest, size);
        }

        @Override
        public int minimalLength() {
            // TODO: Il y a un soucis ici sur la définition
            return lengthAtRest - 1;
        }

        @Override
        public int ask(int size) {
            return Math.max(lengthAtRest - 1, size);
        }
    }

    // TODO : a voir si utile
    class Delegater implements IndexMapperBuilder {
        IndexMapperBuilder delegatee;

        public Delegater(IndexMapperBuilder delegatee) {
            this.delegatee = delegatee;
        }

        @Override
        public IndexMapper build(int size) {
            return delegatee.build(size);
        }

        @Override
        public int minimalLength() {
            return delegatee.minimalLength();
        }

        @Override
        public int ask(int size) {
            return delegatee.ask(size);
        }
    }

    class Equalizer implements IndexMapperBuilder {
        int preferredSubLength;

        public Equalizer(int preferredSubLength) {
            this.preferredSubLength = preferredSubLength;
        }

        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.Equalizer(preferredSubLength, size);
        }

        @Override
        public int minimalLength() {
            return preferredSubLength;
            // TODO: Not tested
        }

        @Override
        public int ask(int size) {
            return Math.max(size, preferredSubLength);
        }
    }

    class MiddleTakesAll implements IndexMapperBuilder {
        int edgeLength;

        public MiddleTakesAll(int edgeLength) {
            this.edgeLength = edgeLength;
        }

        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.MiddleTakesAll(size, edgeLength);
        }

        @Override
        public int ask(int size) {
            if (size - 2 * edgeLength > 0)
                return size;
            return 2 * edgeLength + 1;
        }

        @Override
        public int minimalLength() {
            return 2 * edgeLength + 1;
        }
    }
}
