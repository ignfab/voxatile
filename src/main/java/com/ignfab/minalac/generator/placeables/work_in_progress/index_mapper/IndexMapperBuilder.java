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

    class Stretcher implements IndexMapperBuilder {
        int stretchableCoord;
        int minLength;

        public Stretcher(int stretchableCoord, int minLength) {
            this.stretchableCoord = stretchableCoord;
            this.minLength = minLength;
        }

        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.Stretcher(stretchableCoord, minLength, size);
        }

        @Override
        public int minimalLength() {
            // TODO: Il y a un soucis ici sur la définition
            return minLength + 1;
        }

        @Override
        public int ask(int size) {
            return Math.max(minLength + 1, size);
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
