package com.ignfab.minalac.generator.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.models.ModelImpl;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ModelStore;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTaskTest {

    @Test
    public void testRender() {
        ModelStore modelStore = new ModelStore();

        modelStore.add("digit", new ModelImplTester('1'));
        modelStore.add("digit", new ModelImplTester('1'));
        modelStore.add("digit", new ModelImplTester('2'));
        modelStore.add("letter", new ModelImplTester('b'));
        modelStore.add("letter", new ModelImplTester('a'));

        ModelTaksImpl task = new ModelTaksImpl(modelStore, "digit");
        assertEquals(0, task.modelsRendered.size());

        // Not testing rendering area
        task.run(WorldBBox3d.EMPTY);

        assertTrue(task.modelsRendered.contains(new ModelImplTester('1')));
        assertTrue(task.modelsRendered.contains(new ModelImplTester('2')));
        assertEquals(2, Collections.frequency(task.modelsRendered, new ModelImplTester('1')));
        assertEquals(1, Collections.frequency(task.modelsRendered, new ModelImplTester('2')));
        assertEquals(3, task.modelsRendered.size());

        ModelTaksImpl idleTask = new ModelTaksImpl(modelStore, "specialCharacter");
        assertEquals(0, idleTask.modelsRendered.size());

        idleTask.run(WorldBBox3d.EMPTY);

        assertEquals(0, idleTask.modelsRendered.size());
    }

    private static class ModelTaksImpl extends ModelTask<ModelImpl> {
        private final List<ModelImpl> modelsRendered = new ArrayList<>();

        ModelTaksImpl(ModelStore store, String modelType) {
            super(ModelImpl.class, new ModelSelection(store, modelType, null));
        }

        @Override
        protected void run(ModelImpl model, WorldBBox3d bbox) {
            modelsRendered.add(model);
        }
    }

    private static class ModelImplTester extends ModelImpl {
        private final char type;

        ModelImplTester(char type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModelImplTester model = (ModelImplTester) o;
            return type == model.type;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type);
        }

        @Override
        public String salt() {
            throw new UnsupportedOperationException("Unimplemented method 'salt'");
        }
    }
}
