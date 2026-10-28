package fr.ign.voxatile.core.inputs;

public record IntegerArrayGeographicDataMatrix2d(
    int[] data,
    int sizeX,
    int sizeY,
    double offsetX,
    double offsetY,
    double cellSizeX,
    double cellSizeY
) implements IntegerGeographicDataMatrix2d {

    /**
     * Creates a new {@code IntegerArrayGeographicDataMatrix2d} with empty data.
     *
     * @param sizeX size of data matrix on x-axis
     * @param sizeY size of data matrix on y-axis
     * @param offsetX geographic offset for x-axis (position of matrix point 0 on map)
     * @param offsetY geographic offset for y-axis (position of matrix point 0 on map)
     * @param cellSizeX geographic size on x-axis of a matrix cell
     * @param cellSizeY geographic size on y-axis of a matrix cell
     */
    public IntegerArrayGeographicDataMatrix2d(int sizeX, int sizeY, double offsetX, double offsetY, double cellSizeX, double cellSizeY) {
        this(new int[sizeX * sizeY], sizeX, sizeY, offsetX, offsetY, cellSizeX, cellSizeY);
    }

    @Override
    public int getInteger(int x, int y) {
        return data[y * sizeX + x];
    }
}
