package com.ignfab.minalac.generator.outputs.minetest;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Random;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;
import com.ignfab.minalac.generator.world.VoxelTile;

public class MTSchematic implements Placeable {
    private final SchemLayer[] layers;
    private final WorldSize3d size;
    private final WorldCoords3d offset;
    private final Random random;

    public MTSchematic(File file, WorldCoords3d offset, Seed seed) {
        this.offset = offset;
        random = seed.createRandom();

        // https://docs.luanti.org/for-creators/luanti-schematic-file-format/
        try (DataInputStream stream = new DataInputStream(new FileInputStream(file))) {
            // u32 : signature 'MTSM'
            stream.skipNBytes(4);

            // u16 : version
            int version = stream.readUnsignedShort();
            if (version != 4)
                throw new IOException("Invalid schematic version (only version 4 is supported): " + version);

            // u16 : size_x
            int sizeX = stream.readUnsignedShort();
            // u16 : size_y
            int sizeY = stream.readUnsignedShort();
            // u16 : size_z
            int sizeZ = stream.readUnsignedShort();
            size = new WorldSize3d(sizeX, sizeZ, sizeY); // XZY => XYZ

            // u8[size_y] : slice probability
            byte[] sliceProbabilities = readExactlyNBytes(stream, sizeY, "slice probabilities");

            // u16 : name_id_len
            int nameIdLen = stream.readUnsignedShort();

            // name_id_table
            String[] names = new String[nameIdLen];
            for (int i = 0; i < nameIdLen; i++) {
                // u16 : name_len
                int nameLen = stream.readUnsignedShort();

                // u8[name_len] : name
                names[i] = new String(readExactlyNBytes(stream, nameLen, "node name"), StandardCharsets.UTF_8);
            }

            // Zlib
            Inflater inflater = new Inflater();
            InflaterInputStream decompressed = new InflaterInputStream(stream, inflater);

            // u16[size] : node_ids (param0)
            short[] param0 = new short[size.volume()];
            ByteBuffer.wrap(readExactlyNBytes(decompressed, 2 * size.volume(), "param0")).asShortBuffer().get(param0);

            // u8[size] : probability & force (param1)
            byte[] param1 = readExactlyNBytes(decompressed, size.volume(), "param1");

            // u8[size] : param2
            byte[] param2 = readExactlyNBytes(decompressed, size.volume(), "param2");

            inflater.end();

            layers = new SchemLayer[sizeY];
            for (int y = 0; y < sizeY; y++) {
                SchemLayer layer = new SchemLayer(sizeX * sizeZ, sliceProbabilities[y]);
                for (int z = 0; z < sizeZ; z++) {
                    for (int x = 0; x < sizeX; x++) {
                        int i = (sizeZ - z - 1) * sizeZ * sizeY + y * sizeX + x;
                        layer.voxels[z * sizeZ + x] = SchemVoxel.of(names[param0[i]], param1[i], param2[i]);
                    }
                }
                layers[y] = layer;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read Minetest schematic: " + file.getAbsolutePath(), e);
        }
    }

    private static byte[] readExactlyNBytes(InputStream stream, int size, String dataName) throws IOException {
        byte[] bytes = stream.readNBytes(size);
        if (bytes.length != size)
            throw new IOException("Incomplete %s data: expected %d bytes, got %d".formatted(dataName, size, bytes.length));
        return bytes;
    }

    @Override
    public void place(VoxelTile tile, int xOrigin, int yOrigin, int zOrigin) {
        if (!(tile instanceof MTVoxelTile mtTile))
            throw new IllegalArgumentException("Minetest schematic can only be placed in a Minetest world");
        random.setSeed(xOrigin, yOrigin, zOrigin);
        int z = 0;
        for (int l = 0; l < size.z(); l++) {
            SchemLayer layer = layers[l];
            if (random.nextDouble() >= layer.chance())
                continue;
            for (int y = 0; y < size.y(); y++) {
                for (int x = 0; x < size.x(); x++) {
                    SchemVoxel voxel = layer.voxels[y * size.y() + x];
                    if (random.nextDouble() >= voxel.chance())
                        continue;
                    int voxelX = xOrigin + x - offset.x();
                    int voxelY = yOrigin + y - offset.y();
                    int voxelZ = zOrigin + z - offset.z();
                    if (voxel.force() || mtTile.isAir(voxelX, voxelY, voxelZ))
                        mtTile.set(voxelX, voxelZ, voxelY, voxel.voxel()); // XYZ => XZY
                }
            }
            z++;
        }
    }

    private static double decodeChance(byte probability) {
        return (probability & 0x7F) / (double) 0x7F;
    }

    private record SchemVoxel(MTVoxel voxel, double chance, boolean force) {
        public static final SchemVoxel VOID = new SchemVoxel(new MTVoxel("air", (byte) 0, (byte) 0), 0, false);

        public static SchemVoxel of(String type, byte param1, byte param2) {
            return param1 == 0 ? VOID : new SchemVoxel(
                new MTVoxel(type, (byte) 0, param2),
                decodeChance(param1),
                (param1 & 0x80) != 0
            );
        }
    }

    private record SchemLayer(SchemVoxel[] voxels, double chance) {
        SchemLayer(int size, byte probability) {
            this(new SchemVoxel[size], decodeChance(probability));
        }
    }
}
