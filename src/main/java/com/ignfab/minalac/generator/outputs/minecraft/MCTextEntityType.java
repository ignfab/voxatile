package com.ignfab.minalac.generator.outputs.minecraft;

import net.querz.nbt.tag.CompoundTag;

/**
 * An in-game floating text.
 * This is only capable of handling a single line of text!
 *
 * @see com.ignfab.minalac.generator.world.VoxelTypeFactory#createText(String)
 * @see MCMultilineTextEntityType
 */
public class MCTextEntityType extends MCEntityType {
    private final String text;

    /**
     * Creates a new {@code MCTextEntityType}.
     *
     * @param world the {@link MCVoxelWorld} this text will be placed into
     * @param text the floating text line shown
     */
    public MCTextEntityType(MCVoxelWorld world, String text) {
        super(world, "minecraft:armor_stand");
        this.text = text;
    }

    @Override
    protected void serialize(CompoundTag tag) {
        tag.putString("CustomName", "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
        tag.putBoolean("CustomNameVisible", true);
        tag.putBoolean("NoGravity", true);
        tag.putByte("Invisible", (byte) 1);
        tag.putByte("Marker", (byte) 1);
    }

    @Override
    public void place(double x, double y, double z) {
        super.place(x, y, z - 0.4); // Offset because the armor stand's name appears above the specified position
    }
}
