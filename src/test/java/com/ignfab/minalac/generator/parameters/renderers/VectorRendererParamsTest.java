package com.ignfab.minalac.generator.parameters.renderers;

import com.ignfab.minalac.generator.world.SemanticType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VectorRendererParamsTest {
    @Test
    public void testValidate() {
        VectorRendererParams paramsWithoutType = new VectorRendererParams("", "ground", SemanticType.GRASS, SemanticType.BRICK);
        assertThrows(IllegalArgumentException.class, paramsWithoutType::validate);

        VectorRendererParams paramsWithoutHeightmap = new VectorRendererParams("building", "", SemanticType.GRASS, SemanticType.BRICK);
        assertThrows(IllegalArgumentException.class, paramsWithoutHeightmap::validate);

        VectorRendererParams params = new VectorRendererParams("building", "ground", SemanticType.GRASS, SemanticType.BRICK);
        assertDoesNotThrow(params::validate);
    }
}
