package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.world.VoxelWorld;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(VoxelWorld world) {
        for (Model model : selection)
            if (cls.isInstance(model))
                render(cls.cast(model), world);
    }

    /**
     * Performs rendering for a given model.
     *
     * @param model the model to render
     * @param world world to render into
     */
    protected abstract void render(M model, VoxelWorld world);
}
