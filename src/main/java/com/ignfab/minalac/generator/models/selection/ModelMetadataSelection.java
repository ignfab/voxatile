package com.ignfab.minalac.generator.models.selection;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.iterator.Iterators;

import java.util.Iterator;
import java.util.Objects;

public class ModelMetadataSelection extends AbstractModelFilter {
    private final String metadata;
    private final Object value;

    public ModelMetadataSelection(ModelFilter models, String metadata, Object value) {
        super(models);
        this.metadata = metadata;
        this.value = value;
    }

    @Override
    public Iterator<Model> iterator() {
        return Iterators.filter(models.iterator(), model -> Objects.equals(model.getMetadata(metadata), value));
    }
}
