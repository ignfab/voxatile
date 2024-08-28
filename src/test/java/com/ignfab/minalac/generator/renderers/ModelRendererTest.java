package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ModelRendererTest {

    @Test
    public void testRender() {
        ModelStore modelStore = new ModelStore();

        modelStore.add("digit", new ModelImpl('1'));
        modelStore.add("digit", new ModelImpl('1'));
        modelStore.add("digit", new ModelImpl('2'));
        modelStore.add("letter", new ModelImpl('b'));
        modelStore.add("letter", new ModelImpl('a'));

        ModelRendererImpl renderer = new ModelRendererImpl(modelStore, "digit");
        assertEquals(0, renderer.modelsRendered.size());

        // Not testing rendering area
        renderer.render(WorldBBox3d.EMPTY);

        assertTrue(renderer.modelsRendered.contains(new ModelImpl('1')));
        assertTrue(renderer.modelsRendered.contains(new ModelImpl('2')));
        assertEquals(2, Collections.frequency(renderer.modelsRendered, new ModelImpl('1')));
        assertEquals(1, Collections.frequency(renderer.modelsRendered, new ModelImpl('2')));
        assertEquals(3, renderer.modelsRendered.size());

        ModelRendererImpl idleRenderer = new ModelRendererImpl(modelStore, "specialCharacter");
        assertEquals(0, idleRenderer.modelsRendered.size());

        idleRenderer.render(WorldBBox3d.EMPTY);

        assertEquals(0, idleRenderer.modelsRendered.size());
    }

    @SuppressWarnings("checkstyle:VisibilityModifier")
    private static class ModelRendererImpl extends ModelRenderer {
        List<ModelImpl> modelsRendered = new ArrayList<>();

        ModelRendererImpl(ModelStore store, String modelType) {
            super(new ModelSelection(store, modelType));
        }

        @Override
        protected void render(Model model, WorldBBox3d bbox) {
            ModelImpl modelImpl = (ModelImpl) model;
            modelsRendered.add(modelImpl);
        }
    }

    @SuppressWarnings("checkstyle:VisibilityModifier")
    private static class ModelImpl extends Model {
        public char type;

        ModelImpl(char type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModelImpl model = (ModelImpl) o;
            return type == model.type;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type);
        }
    }
}
