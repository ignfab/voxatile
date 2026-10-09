package fr.ign.voxatile.core.inputs.decoders;

import java.util.function.IntToDoubleFunction;

/**
 * Decoder for Terrarium format RGB values to float elevation.
 */
public class TerrariumFloatDecoder implements IntToDoubleFunction {

    @Override
    public double applyAsDouble(int value) {
        /*
        Terrarium format documentation: 
            https://github.com/tilezen/joerd/blob/master/docs/formats.md#terrarium
        */
        int r = (value >> 16) & 0xFF;
        int g = (value >> 8) & 0xFF;
        int b = value & 0xFF;
        return (r * 256.0f + g + b / 256.0f) - 32768.0f;
    }
}
