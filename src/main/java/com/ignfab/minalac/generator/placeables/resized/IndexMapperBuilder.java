package com.ignfab.minalac.generator.placeables.resized;

public interface IndexMapperBuilder {
    // TODO-10 : Doit lever une exception UnresizableStructureException ?
    IndexMapper build(int size);
    /**
     * Returns -1 if not possible
     * @param size
     * @return
     */
    int maxSizeUnder(int size);
    int minimumSize();
}
