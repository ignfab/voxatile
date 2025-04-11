package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;

/**
 * The abstract {@code ModelRenderer} class represents a type of {@link Renderer} that renders a selection of models.
 *
 * @param <M> Model type for this renderer.
 */
public abstract class ModelRenderer<M> implements Renderer {
    private final Class<M> cls;
    private final ModelSelection selection;

    protected ModelRenderer(Class<M> cls, ModelSelection selection) {
        this.cls = cls;
        this.selection = selection;
    }

    @Override
    public void render(GenerationTile tile) {
        for (Model model : selection)
            if (cls.isInstance(model))
                render(cls.cast(model), tile);
    }

    /**
     * Performs rendering for a given model.
     *
     * @param model the model to render
     * @param tile tile to render into
     */
    protected abstract void render(M model, GenerationTile tile);
}
