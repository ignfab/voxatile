package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * The abstract {@code ModelRenderer} class represents a type of {@link Renderer} that renders a selection of models.
 */
public abstract class ModelRenderer implements Renderer {
    private final ModelFilter selection;

    protected ModelRenderer(ModelFilter selection) {
        this.selection = selection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(WorldBBox3d bbox) {
        if (selection != null)
            for (Model model : selection)
                render(model, bbox);
    }

    /**
     * Performs rendering for a given model.
     *
     * @param model the model to render
     * @param bbox the limits of the rendering area.
     */
    protected abstract void render(Model model, WorldBBox3d bbox);
}
