package com.ignfab.minalac.generator.models.selection;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ModelMetadataListSelection extends AbstractModelFilter {
    private final String metadata;
    private final List<?> values;

    public ModelMetadataListSelection(ModelFilter models, String metadata, Object... values) {
        this(models, metadata, Arrays.asList(values));
    }

    public ModelMetadataListSelection(ModelFilter models, String metadata, List<?> values) {
        super(models);
        this.metadata = metadata;
        this.values = values;
    }

    @Override
    public Iterator<Model> iterator() {
        return Iterators.filter(models.iterator(), model -> values.contains(model.getMetadata(metadata)));
    }
}
