package com.ignfab.minalac.generator.placeables.work_in_progress;

public interface IndexMapperBuilder {
    IndexMapper build(int size);
    int ask(int size);

    class Identity implements IndexMapperBuilder {
        @Override
        public IndexMapper build(int size) {
            return new IndexMapper.Identity(size);
        }

        @Override
        public int ask(int size) {
            throw new RuntimeException("Not implemented");
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
}
