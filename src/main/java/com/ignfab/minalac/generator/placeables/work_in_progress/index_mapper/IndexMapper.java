package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import java.util.Iterator;
import java.util.stream.IntStream;

public interface IndexMapper {
    PlaceableIndex placeable(int coordinateValue);
    SizedIterable<StructureIndex> structure();

    /**
     * {Returns the total size of this index axis mapper.}
     */
    int size();

    record PlaceableIndex(int index, int coordinateValue){};
    // TODO: length -> size
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

        @Override
        public int size() {
            return length;
        }
    }

    class Equalizer implements IndexMapper {
        int[] length;
        int totalLength;

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
            this.totalLength = totalLength;
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
            return new SizedIterable<>() {
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

        @Override
        public int size() {
            return totalLength;
        }
    }

    class Stretcher implements IndexMapper {
        int stretchableCoord;
        int lengthAtRest;
        int length;

        // lengthAtRest is the "original size"
        // 3 cas : taille demande = taille de la struct;
        // taille demandé = taille struct - 1 (la colonne disparait)
        // taille demandé > taille struct (la colonne est repete)
        public Stretcher(int stretchableCoord, int lengthAtRest, int length) {
            this.stretchableCoord = stretchableCoord;
            this.lengthAtRest = lengthAtRest;
            this.length = length;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            int r = length - lengthAtRest;
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

        @Override
        public int size() {
            return length;
        }
    }

    class MiddleTakesAll implements IndexMapper {
        int totalLength;
        int breakPointLeft;
        int breakPointRight;
        int[] tempTab;

        public MiddleTakesAll(int totalLength, int edgeLength) {
            this.totalLength = totalLength;
            this.breakPointLeft = edgeLength;
            this.breakPointRight = totalLength - edgeLength;
            tempTab = new int[3];
            tempTab[0] = edgeLength;
            tempTab[2] = edgeLength;
            tempTab[1] = totalLength - 2 * edgeLength;
        }

        @Override
        public PlaceableIndex placeable(int coordinateValue) {
            if (coordinateValue < breakPointLeft)
                return new PlaceableIndex(0, coordinateValue);
            else if (coordinateValue < breakPointRight)
                return new PlaceableIndex(1, coordinateValue - breakPointLeft);
            else
                return new PlaceableIndex(2, coordinateValue - breakPointRight);
        }

        @Override
        public SizedIterable<StructureIndex> structure() {
            return new SizedIterable<>() {
                @Override
                public int size() {
                    return 3;
                }

                @Override
                public Iterator<StructureIndex> iterator() {
                    return IntStream.range(0, 3).mapToObj( i -> new StructureIndex(i, tempTab[i])).toList().iterator();
                }
            };
        }

        @Override
        public int size() {
            return totalLength;
        }
    }

    public static void main(String[] args) {
        int total = 2;
        // IndexMapper map = new MiddleTakesAll(total, 2);
        IndexMapper map = new Stretcher(0, 3, total);
        for (int c = 0; c < total; c++) {
            map.placeable(c);
            System.out.printf("%d -> %s %n", c, map.placeable(c));
        }

        for(StructureIndex s : map.structure()){
            System.out.println(s);
        }
    }
}
