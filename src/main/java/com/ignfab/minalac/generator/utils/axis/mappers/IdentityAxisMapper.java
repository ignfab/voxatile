package com.ignfab.minalac.generator.utils.axis.mappers;

/**
 * An {@link AxisMapper} that does an identity mapping.
 * <p>
 * It maps any position into interval number 0, at the same position.
 */
public class IdentityAxisMapper implements AxisMapper {
    private final int size;
    private final int[] intervals;

    /**
     * Creates a new {@code IdentityIndexMapper}.
     *
     * @param size Size of index mapper interval (interval starts at pos 0 for now)
     */
    public IdentityAxisMapper(int size) {
        if (size < 0)
            throw new IllegalArgumentException("length can not be negative");
        this.size = size;
        intervals = (size == 0) ? new int[0] : new int[] { size };
    }

    @Override
    public Mapped map(int coordinateValue) {
        // TODO-3 : Faut t-il mettre un offset ou considerer que tous les IndexMapper commencent à zéro?
        if (0 > coordinateValue || coordinateValue >= size)
            throw new IndexOutOfBoundsException("Provided position is out of bounds");
        return new Mapped(0, coordinateValue);
    }

    @Override
    public int[] intervals() {
        return intervals;
    }

    @Override
    public int size() {
        return size;
    }
}
