package com.ignfab.minalac.generator.outputs.minecraft;

import com.ignfab.minalac.generator.world.MultilineTextEntityVerticalAnchor;

/**
 * An in-game floating text.
 * This can handle multiple lines of text.
 *
 * @see com.ignfab.minalac.generator.world.VoxelTypeFactory#createText(String, MultilineTextEntityVerticalAnchor)
 * @see MCTextEntityType
 */
public class MCMultilineTextEntityType extends MCTextEntityType {
    private final MCTextEntityType[] lines;
    private final MultilineTextEntityVerticalAnchor anchor;

    private static final double LINE_HEIGHT = 0.3;

    /**
     * Creates a new {@code MCMultilineTextEntityType}.
     *
     * @param world the {@link MCVoxelWorld} this text will be placed into
     * @param anchor the vertical anchor to place lines
     * @param lines the floating text lines shown
     */
    public MCMultilineTextEntityType(MCVoxelWorld world, MultilineTextEntityVerticalAnchor anchor, String... lines) {
        super(world, "");
        this.anchor = anchor;
        this.lines = new MCTextEntityType[lines.length];
        for (int i = 0; i < lines.length; i++)
            this.lines[i] = new MCTextEntityType(world, lines[i]);
    }

    @Override
    public void place(double x, double y, double z) {
        double offset = switch (anchor) {
            case TOP -> 0;
            case MIDDLE -> LINE_HEIGHT * (lines.length - 1) / 2;
            case BOTTOM -> LINE_HEIGHT * (lines.length - 1);
        };
        for (int i = 0; i < lines.length; i++)
            lines[i].place(x, y, z + offset - LINE_HEIGHT * i);
    }
}
