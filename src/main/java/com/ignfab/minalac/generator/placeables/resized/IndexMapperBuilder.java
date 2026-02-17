package com.ignfab.minalac.generator.placeables.resized;

public interface IndexMapperBuilder {
    IndexMapper build(int size);
    // TODO: PE maxSizeunder(

    /**
     * Returns 0 if not possible
     * @param size
     * @return
     */
    int ask(int size);
    int minimumSize();
}
