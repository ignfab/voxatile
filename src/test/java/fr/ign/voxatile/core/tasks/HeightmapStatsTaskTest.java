package fr.ign.voxatile.core.tasks;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.TestingHeightmap;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.TestingRectangleShape2dModel;
import fr.ign.voxatile.core.utils.world2d.WorldBBox2d;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapStatsTaskTest {
    private int heightFormula(WorldCoords2d pos) {
        return (int) Math.floor(pos.x() * .3 + pos.y() * .7);
    }

    @Test
    void testComputeMinMaxHeightmap() {
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(-1, -2, -20, 7, 6, 40));
        TestingHeightmap heightmap = tile.newStoredHeightmap("heightmap", 0);

        // Prepare a non flat Heightmap
        for (WorldCoords2d pos : heightmap.bbox())
            heightmap.set(pos, heightFormula(pos));

        // Prepare a single square model
        Model modelA = new TestingRectangleShape2dModel(new WorldBBox2d(0, -1, 5, 3));
        Model modelB = new TestingRectangleShape2dModel(new WorldBBox2d(3, 3, 4, 3));
        tile.models().add("model", List.of(modelA, modelB));

        // Try to compute
        assertDoesNotThrow(() -> new HeightmapStatsTask(
            new ModelSelection("model", null),
            heightmap.spec(),
            "minimum",
            "maximum"
        ).run(tile));

        assertEquals(-1, (int) modelA.getMetadata("minimum"));
        assertEquals(1, (int) modelA.getMetadata("maximum"));
        assertEquals(2, (int) modelB.getMetadata("minimum"));
        assertEquals(3, (int) modelB.getMetadata("maximum"));
    }

    @Test
    void testComputeMinMaxEmptyHeightmap() {
        TestingGenerationTile tile = new TestingGenerationTile(WorldBBox3d.EMPTY);
        TestingHeightmap ground = tile.newStoredHeightmap("heightmap", 0);

        Model model = new TestingRectangleShape2dModel(new WorldBBox2d(0, -1, 5, 3));
        tile.models().add("model", List.of(model));

        assertDoesNotThrow(() -> new HeightmapStatsTask(
            new ModelSelection("model", null),
            ground.spec(),
            "minimum",
            "maximum"
        ).run(tile));

        assertNull(model.getMetadata("minimum"));
        assertNull(model.getMetadata("maximum"));
    }
}
