package com.ignfab.minalac.generator.placeables.work_in_progress;

import java.util.Iterator;
import java.util.stream.IntStream;

public interface IndexMapper {
    int tmp_localCoordinate_getStructImpl(int i);
    int tmp_order_index_getStructImpl(int i);
    int tmp_length();
    int tmp_number();

    SizedIterable<StructureIndex> structureIndex__toBeChanged();

    record StructureIndex(int order, int length){};

    class Identity implements IndexMapper {
        int length;

        public Identity(int length) {
            this.length = length;
        }

        @Override
        public int tmp_localCoordinate_getStructImpl(int i) {
            return i;
        }

        @Override
        public int tmp_order_index_getStructImpl(int i) {
            return 0;
        }

        @Override
        public int tmp_length() {
            if (true)
                throw new RuntimeException("Not tested");
            return length;
        }

        @Override
        public int tmp_number() {
            return 1;
        }

        @Override
        public SizedIterable<StructureIndex> structureIndex__toBeChanged() {
            return new SizedIterable<StructureIndex>() {
                @Override
                public int size() {
                    return 1;
                }

                @Override
                public Iterator<StructureIndex> iterator() {
                    return IntStream.range(0, 1).mapToObj(i -> new StructureIndex(0, length)).toList().iterator();
                }
            };
        }
    }

    class LastAll implements IndexMapper {
        int length;
        int breakpoint;

        public LastAll(int length) {
            this.length = length;
            this.breakpoint = length / 2;
        }

        @Override
        public int tmp_localCoordinate_getStructImpl(int i) {
            if (i < breakpoint)
                return i;
            return i - breakpoint;
        }

        @Override
        public int tmp_order_index_getStructImpl(int i) {
            if (i < breakpoint)
                return 0;
            return 1;
        }

        @Override
        public int tmp_length() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int tmp_number() {
            return 2;
        }

        public SizedIterable<StructureIndex> structureIndex__toBeChanged() {
            return new SizedIterable<StructureIndex>() {
                @Override
                public int size() {
                    return 2;
                }

                @Override
                public Iterator<StructureIndex> iterator() {
                    return IntStream.range(0, 2).mapToObj(i -> new StructureIndex(i, breakpoint + i * (length - 2 * breakpoint))).toList().iterator();
                }
            };
        }
    }
}
