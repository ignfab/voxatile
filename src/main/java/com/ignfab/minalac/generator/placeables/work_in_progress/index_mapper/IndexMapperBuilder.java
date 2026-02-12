package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

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
