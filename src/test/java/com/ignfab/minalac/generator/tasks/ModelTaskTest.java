package com.ignfab.minalac.generator.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.models.ModelImpl;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTaskTest {

    @Test
    public void testRun() {
        GenerationTile tile = new TestingGenerationTile(WorldBBox3d.ORIGIN);

        tile.models().add("digit", new ModelImplTester('1'));
        tile.models().add("digit", new ModelImplTester('1'));
        tile.models().add("digit", new ModelImplTester('2'));
        tile.models().add("letter", new ModelImplTester('b'));
        tile.models().add("letter", new ModelImplTester('a'));

        ModelTaskImpl task = new ModelTaskImpl("digit");
        assertEquals(0, task.modelsRendered.size());

        // Not testing rendering area
        task.run(tile);

        assertTrue(task.modelsRendered.contains(new ModelImplTester('1')));
        assertTrue(task.modelsRendered.contains(new ModelImplTester('2')));
        assertEquals(2, Collections.frequency(task.modelsRendered, new ModelImplTester('1')));
        assertEquals(1, Collections.frequency(task.modelsRendered, new ModelImplTester('2')));
        assertEquals(3, task.modelsRendered.size());

        ModelTaskImpl idleTask = new ModelTaskImpl("specialCharacter");
        assertEquals(0, idleTask.modelsRendered.size());

        idleTask.run(tile);

        assertEquals(0, idleTask.modelsRendered.size());
    }

    private static class ModelTaskImpl extends ModelTask<ModelImpl> {
        private final List<ModelImpl> modelsRendered = new ArrayList<>();

        ModelTaskImpl(String modelType) {
            super(ModelImpl.class, new ModelSelection(modelType, null));
        }

        @Override
        protected void run(ModelImpl model, GenerationTile tile) {
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
