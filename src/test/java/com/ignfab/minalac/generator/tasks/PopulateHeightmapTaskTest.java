package com.ignfab.minalac.generator.tasks;

import java.util.List;

import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.generation.TestingGenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.TestingHeightmap;
import com.ignfab.minalac.generator.inputs.FloatArrayGeographicDataMatrix2d;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.FloatMatrixModel;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

import static org.junit.jupiter.api.Assertions.*;

public class PopulateHeightmapTaskTest {
    @Test
    public void test() throws TransformException {
        TestingGenerationTile tile = new TestingGenerationTile(new WorldBBox3d(-1, -2, 0, 3, 3, 1));
        TestingHeightmap heightmap = tile.newStoredHeightmap("heightmap", 0);

        MapToWorldConverter converter = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
        // Beware, Y is upside down in this matrix
        float[] values = {
            9.0f, 10.0f, 11.0f,
            7.0f, 8.0f, 9.0f,
            4.0f, 5.0f, 6.0f,
            1.0f, 2.0f, 3.0f
        };

        FloatGeographicDataMatrix2d data = new FloatArrayGeographicDataMatrix2d(values, 3, 4, 0, -1, 1.0, 1.0);
        FloatMatrixModel model = new FloatMatrixModel(data, converter);

        tile.models().add("matrix", List.of(model));
        ModelSelection selection = new ModelSelection("matrix", null);

        new PopulateHeightmapTask(selection, heightmap.spec(), 0.5).run(tile);

        assertValue(0, heightmap, -1, -2);
        assertValue(0, heightmap, 0, -2);
        assertValue(0, heightmap, 1, -2);
        assertValue(0, heightmap, -1, -1);
        assertValue(2, heightmap, 0, -1);
        assertValue(4, heightmap, 1, -1);
        assertValue(0, heightmap, -1, 0);
        assertValue(8, heightmap, 0, 0);
        assertValue(10, heightmap, 1, 0);
    }

    private void assertValue(int expected, ReadableHeightmap heightmap, int x, int y) {
        assertEquals(expected, heightmap.get(x, y), String.format("at (x = %d, y = %d)", x, y));
    }
}
