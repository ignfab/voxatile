package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that maps position into one stretched interval.
 */
public class StretcherAxisMapper extends AxisMapper {
    private final int stretchablePosition;
    private final int stretchSize;

    /**
     * Creates a new {@code StretcherAxisMapper}.
     *
     * @param origin the origin of this mapper
     * @param stretchablePosition Position where the underlying interval is stretched (must be in the underlying interval)
     * @param baseSize Size of the underlying interval
     * @param size Stretched size (size of this mapper)
     */
    public StretcherAxisMapper(int origin, int stretchablePosition, int baseSize, int size) {
        super(origin, size, new int[] { baseSize });

        if (baseSize <= 0)
            throw new IllegalArgumentException("Base size can not be negative or zero");
        if (size - baseSize < -1)
            throw new IllegalArgumentException("Can not be squeezed more than 1");
        if (stretchablePosition < origin || stretchablePosition >= origin + baseSize)
            throw new IllegalArgumentException("Stretchable coordinate out of base interval");

        this.stretchablePosition = stretchablePosition;
        this.stretchSize = size - baseSize;
    }

    @Override
    protected Mapped mapUnchecked(int position) {
        return new Mapped(0,
           position < stretchablePosition ? position : Math.max(stretchablePosition, position - stretchSize)
        );
    }
}
