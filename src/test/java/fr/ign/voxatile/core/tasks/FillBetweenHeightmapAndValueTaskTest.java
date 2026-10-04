package fr.ign.voxatile.core.tasks;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.generation.TestingGenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.TestingHeightmap;
import fr.ign.voxatile.core.models.Model;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.TestingRectangleShape2dModel;
import fr.ign.voxatile.core.models.values.MetadataValue;
import fr.ign.voxatile.core.utils.world2d.WorldCoords2d;
import fr.ign.voxatile.core.utils.world3d.WorldBBox3d;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.world.TestingVoxel;

import static org.junit.jupiter.api.Assertions.*;

public class FillBetweenHeightmapAndValueTaskTest {
    @Test
    void testFlattenSurface() {
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(0, 0, 0, 10, 10, 10));
        TestingHeightmap heightmap = tile.newStoredHeightmap("heightmap", 0);

        // Prepare a diagonal heightmap
        for (WorldCoords2d pos : heightmap.bbox())
            heightmap.set(pos, pos.x());

        // Prepare a single square model that has the same size as the tile.
        Model model = new TestingRectangleShape2dModel(tile.limits().to2d());
        int zMetadata = 5;
        model.setMetadata("zTest", zMetadata);
        tile.models().add("model", List.of(model));

        // Run leveling
        assertDoesNotThrow(() -> new FillBetweenHeightmapAndValueTask(
            new ModelSelection("model", null),
            heightmap.spec(),
            new MetadataValue("zTest"),
            new TestingVoxel("A"),
            new TestingVoxel("B")
        ).run(tile));

        // Verify filling
        for (WorldCoords3d c : tile.limits()) {
            int zHeightmap = heightmap.get(c.to2d());

            if (zHeightmap <= c.z() && c.z() <= zMetadata)
                tile.voxels().assertVoxel("B", c);
            if (c.z() > zMetadata && zHeightmap >= c.z())
                tile.voxels().assertVoxel("A", c);
        }
    }
}
