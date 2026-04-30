package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that maps position into one stretched interval.
 */
public class StretcherIndexMapper implements AxisMapper {
    private final int size;
    private final int[] intervals;
    private final int stretchablePosition;
    private final int stretchSize;
    private final int minimum;

    // lengthAtRest is the "original size"
    // 3 cas : taille demande = taille de la struct;
    // taille demandé = taille struct - 1 (la colonne disparait)
    // taille demandé > taille struct (la colonne est repete)
    /**
     * Creates a new {@code AxisMapper}.
     *
     * @param stretchablePosition Position where the underlying interval is stetched (must be in the underlying interval)
     * @param baseSize Size of the underlying interval
     * @param size Stretched size (size of this mapper)
     */
    public StretcherIndexMapper(int origin, int stretchablePosition, int baseSize, int size) {
        if (baseSize <= 0)
            throw new IllegalArgumentException("Base size can not be negative or zero");
        if (size < 0)
            throw new IllegalArgumentException("Size can not be negative");
        if (size - baseSize < - 1)
            throw new IllegalArgumentException("Can not be squeezed more than 1");
        if (stretchablePosition < origin || stretchablePosition >= origin + baseSize)
            throw new IllegalArgumentException("Stretchable coordinate out of base interval");

        this.stretchablePosition = stretchablePosition;
        this.stretchSize = size - baseSize;
        this.size = size;
        this.minimum = origin;

        // TODO: See what to do for size = 0 (seems `if (size - baseSize < -1)` prevents that case)
        intervals = new int[]{ baseSize };
    }

    @Override
    public Mapped map(int position) {
        if (position < minimum || position >= minimum + size)
            throw new IndexOutOfBoundsException("Position out of index mapper size");
        return new Mapped(0,
           position < stretchablePosition ? position : Math.max(stretchablePosition, position - stretchSize)
        );
    }

    @Override
    public int[] intervals() {
        return intervals;
    }

    @Override
    public int minimum() {
        return minimum;
    }

    @Override
    public int size() {
        return size;
    }
}
