package com.ignfab.minalac.generator.placeables.work_in_progress;

public interface IndexMapperBuilder {
    IndexMapper build(int size);
    default int ask(int size) {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

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
        public int ask(int size) {
            return allowedSize;
        }
    }

    class LastAll implements IndexMapperBuilder {
        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.LastAll(size);
        }

        @Override
        public int ask(int size) {
            return size;
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
    }
}
