package com.ignfab.minalac.generator.placeables.work_in_progress;

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
            throw new RuntimeException("DUNNO");
        }
    }
}
