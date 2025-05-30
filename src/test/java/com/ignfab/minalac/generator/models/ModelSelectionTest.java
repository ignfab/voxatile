package com.ignfab.minalac.generator.models;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.models.filters.ModelFilterHasMetadata;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;

public class ModelSelectionTest {

    @Test
    public void testIterator() {
        TestingGenerationTile tile = new TestingGenerationTile(WorldBBox3d.EMPTY);

        Model modelA = new TestingModel("A", Map.of("a", 1));
        Model modelB = new TestingModel("B", Map.of("b", 2));
        Model modelC = new TestingModel("C");

        tile.models().add("X", modelA);
        tile.models().add("Y", modelA);
        tile.models().add("X", modelB);
        tile.models().add("X", modelA);
        tile.models().add("Y", modelC);

        assertBrowsesAllOnce(List.of(modelA, modelA, modelB), new ModelSelection("X", null).forTile(tile));

        assertBrowsesAllOnce(List.of(modelA, modelA), new ModelSelection("X", new ModelFilterHasMetadata("a")).forTile(tile));

        assertBrowsesAllOnce(List.of(modelA, modelC), new ModelSelection("Y", null).forTile(tile));

        assertEmpty(new ModelSelection("Y", new ModelFilterHasMetadata("b")).forTile(tile));

        assertEmpty(new ModelSelection("Z", null).forTile(tile));
    }
}
