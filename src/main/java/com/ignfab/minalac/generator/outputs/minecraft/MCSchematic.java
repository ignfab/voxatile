package com.ignfab.minalac.generator.outputs.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.github.ensgijs.nbt.io.BinaryNbtHelpers;
import io.github.ensgijs.nbt.io.NamedTag;
import io.github.ensgijs.nbt.tag.CompoundTag;
import io.github.ensgijs.nbt.tag.IntTag;
import io.github.ensgijs.nbt.tag.ListTag;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

// TODO This might only be a way to create the structure instead of a full class implementing placeable
// The extra offset (called placementOffset here) should be generalized to every placeable during the placeable parameterization revamp!
public class MCSchematic implements Placeable {
    private final WorldCoords3d placementOffset;
    private final PlaceableStructure structure;

    public MCSchematic(File file, boolean excludeAir, WorldCoords3d placementOffset) {
        this.placementOffset = placementOffset;

        // https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-3.md
        try {
            CompoundTag root = BinaryNbtHelpers.read(file).getTagAutoCast();
            CompoundTag schematic = root.getCompoundTag("Schematic");

            int version = schematic.getInt("Version");
            if (version != 3)
                throw new IOException("Invalid schematic version (only version 3 is supported): " + version);

            // DataVersion could be checked to ensure it is compatible with current generated version,
            // but exact match would be too restrictive, and range is not straightforward...
            // So it's up to the user to give a schematic for the correct version of the game!

            // Metadata is irrelevant here

            int width = schematic.getShort("Width") & 0xFFFF;
            int height = schematic.getShort("Height") & 0xFFFF;
            int length = schematic.getShort("Length") & 0xFFFF;

            int[] offset = schematic.getIntArray("Offset");
            int offsetX = offset[0];
            int offsetY = offset[1];
            int offsetZ = offset[2];

            CompoundTag blocks = schematic.getCompoundTag("Blocks");
            Map<Integer, MCVoxel> palette = readBlockPalette(blocks.getCompoundTag("Palette"));
            int[] data = readVarInts(blocks.getByteArray("Data"));
            if (data.length != width * height * length)
                throw new IllegalArgumentException("Invalid data length: " + data.length + ". Expected: " + width * height * length);
            Map<WorldCoords3d, DirectBlockEntityVoxel> blockEntities = readBlockEntities(blocks.getCompoundList("BlockEntities"), offsetX, offsetY, offsetZ);

            PlaceableStructure structure = new PlaceableStructure();
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        int index = data[x + (y * length + z) * width];
                        MCVoxel voxel = palette.get(index);
                        if (voxel == null)
                            throw new IllegalArgumentException("Invalid palette index: " + index);
                        if (voxel.type.equals("void_air") || voxel.type.equals("minecraft:void_air"))
                            continue;
                        if (excludeAir && (voxel.type.equals("air") || voxel.type.equals("minecraft:air"))) // TODO Could rely on MCVoxel.DEFAULTVOXEL if equality was more reliable
                            continue;
                        structure.set(x + offsetX, -z - offsetZ, y + offsetY, voxel); // X/Z/-Y => X/Y/Z
                    }
                }
            }
            blockEntities.forEach((pos, blockEntity) -> structure.set(pos, blockEntity.merge((MCVoxel) structure.get(pos))));

            // Biomes and Entities are not handled here

            this.structure = structure;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read Minecraft schematic: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        if (!(tile instanceof MCVoxelTile))
            throw new IllegalArgumentException("Minecraft schematic can only be placed in a Minecraft world");
        structure.place(tile, x + placementOffset.x(), y + placementOffset.y(), z + placementOffset.z());
    }

    private Map<Integer, MCVoxel> readBlockPalette(CompoundTag paletteData) {
        Map<Integer, MCVoxel> palette = new HashMap<>(paletteData.size());
        for (NamedTag entry : paletteData) {
            palette.put(
                entry.<IntTag>getTagAutoCast().asInt(),
                MCVoxel.fromString(entry.getName())
            );
        }
        return palette;
    }

    private Map<WorldCoords3d, DirectBlockEntityVoxel> readBlockEntities(ListTag<CompoundTag> blockEntitiesData, int offsetX, int offsetY, int offsetZ) {
        Map<WorldCoords3d, DirectBlockEntityVoxel> blockEntities = new HashMap<>(blockEntitiesData.size());
        for (CompoundTag blockEntity : blockEntitiesData) {
            int[] pos = blockEntity.getIntArray("Pos");
            int posX = pos[0];
            int posY = pos[1];
            int posZ = pos[2];
            CompoundTag data = blockEntity.getCompoundTag("Data");
            data.remove("id");
            data.remove("keepPacked");
            data.remove("x");
            data.remove("y");
            data.remove("z");
            blockEntities.put(
                new WorldCoords3d(posX + offsetX, -posZ - offsetZ, posY + offsetY), // X/Z/-Y => X/Y/Z
                new DirectBlockEntityVoxel("", blockEntity.getString("Id"), data)
            );
        }
        return blockEntities;
    }

    private int[] readVarInts(byte[] varInts) {
        int[] result = new int[varIntsLength(varInts)];
        int index = 0;
        int i = 0;
        while (i < varInts.length) {
            int value = 0;
            int varIntLength = 0;

            byte b;
            do {
                b = varInts[i];
                i++;
                value |= (b & 0x7F) << (varIntLength * 7);
                varIntLength++;
                if (varIntLength > 5)
                    throw new IllegalArgumentException("Invalid VarInt data: " + Arrays.toString(varInts));
            } while ((b & 0x80) != 0);

            result[index] = value;
            index++;
        }
        return result;
    }

    private int varIntsLength(byte[] varInts) {
        int len = 0;
        for (byte varInt : varInts)
            if ((varInt & 0x80) == 0)
                len++;
        return len;
    }

    private static class DirectBlockEntityVoxel extends MCBlockEntityVoxel {
        private final CompoundTag data;

        DirectBlockEntityVoxel(String type, String id, CompoundTag data) {
            this(type, id, null, data);
        }

        DirectBlockEntityVoxel(String type, String id, Map<String, String> properties, CompoundTag data) {
            super(type, id, properties);
            this.data = data;
        }

        @Override
        protected void serialize(CompoundTag tag) {
            for (NamedTag namedTag : data)
                tag.put(namedTag);
        }

        public DirectBlockEntityVoxel merge(MCVoxel voxel) {
            return new DirectBlockEntityVoxel(voxel.type, id, voxel.properties, data);
        }
    }
}
