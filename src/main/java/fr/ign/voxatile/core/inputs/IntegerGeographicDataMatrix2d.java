package fr.ign.voxatile.core.inputs;

public interface IntegerGeographicDataMatrix2d extends GeographicDataMatrix2d<Integer> {

    @Override
    default Integer get(int x, int y) {
        return getInteger(x, y);
    }

    /**
     * Returns an integer value at a given coordinates in the matrix.
     *
     * @param x x-coordinate from {@code 0} to {@code sizeX() - 1}
     * @param y y-coordinate from {@code 0} to {@code sizeY() - 1}
     *
     * @return integer value at given coordinates
     */
    int getInteger(int x, int y);
}
