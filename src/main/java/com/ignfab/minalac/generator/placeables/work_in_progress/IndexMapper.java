package com.ignfab.minalac.generator.placeables.work_in_progress;

import java.util.Iterator;
import java.util.stream.IntStream;

public interface IndexMapper {
    PlaceableIndex placeable(int coordinateValue);
    SizedIterable<StructureIndex> structure();

    record PlaceableIndex(int index, int coordinateValue){};
    record StructureIndex(int index, int length){};

    class Identity implements IndexMapper {
        int length;

        public Identity(int length) {
            this.length = length;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            return new PlaceableIndex(0, coordinateValue);
        }

        @Override
        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<>() {
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
        public PlaceableIndex placeable(int coordinateValue) {
            if (coordinateValue < breakpoint)
                return new PlaceableIndex(0, coordinateValue);
            return new PlaceableIndex(1, coordinateValue - breakpoint);
        }

        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<>() {
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

    class Equalizer implements IndexMapper {
        int[] length;

        public Equalizer(int totalLength) {
            this(totalLength, totalLength);
        }
        public Equalizer(int size, int totalLength) {
            int r = totalLength % size;
            int n = totalLength / size;
            int p = (r % n == 0) ? r / n : (r / n) + 1; // Arrondi sup if reste
            int[] tab = new int[n];
            // TODO: POC naif
            for (int i = 0; i < n; i++) {
                tab[i] = size + p;
                r = r - p;
                if (r <= 0)
                    p = 0;
                else if (r < p)
                    p = r;
            }

            this.length = tab;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            // TODO: POC naif
            int sum = 0;
            for (int j = 0; j < length.length; j++) {
                if (sum <= coordinateValue && coordinateValue < sum + length[j]) {
                    return new PlaceableIndex(j, (coordinateValue - sum) % length[j]);
                }
                sum = sum + length[j];
            }
            throw new IndexOutOfBoundsException("Equalizer Mapper speaking");
        }

        @Override
        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<StructureIndex>() {
                @Override
                public int size() {
                    return length.length;
                }

                @Override
                public Iterator<StructureIndex> iterator() {
                    return IntStream.range(0, length.length)
                        .mapToObj(i -> new StructureIndex(i, length[i]))
                        .toList().iterator();
                }
            };
        }
    }

    class Divider implements IndexMapper {
        int breakpoint;
        int length;

        public Divider(int length) {
            this.length = length;
            breakpoint = length / 2;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            if (coordinateValue < breakpoint)
                return new PlaceableIndex(0, coordinateValue);
            return new PlaceableIndex(1, coordinateValue - breakpoint);
        }

        @Override
        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<>() {
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

    class Stretcher implements IndexMapper {
        int stretchableCoord;
        int minimumLength;
        int length;

        public Stretcher(int stretchableCoord, int minimumLength, int length) {
            this.stretchableCoord = stretchableCoord;
            this.minimumLength = minimumLength;
            this.length = length;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            int r = Math.max(length - (minimumLength + 1), -1);
            if (coordinateValue < stretchableCoord) {
                return new PlaceableIndex(0, coordinateValue);
            } else if (coordinateValue <= stretchableCoord + r) {
                return new PlaceableIndex(0, stretchableCoord);
            } else {
                return new PlaceableIndex(0, coordinateValue - r);
            }
        }

        @Override
        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<>() {
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
}
