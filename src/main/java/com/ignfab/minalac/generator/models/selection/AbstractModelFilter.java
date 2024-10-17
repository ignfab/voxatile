package com.ignfab.minalac.generator.models.selection;

public abstract class AbstractModelFilter implements ModelFilter {
    protected final ModelFilter models;

    public AbstractModelFilter(ModelFilter models) {
        this.models = models;
    }
}
