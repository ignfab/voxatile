package com.ignfab.minalac.generator.outputs.minetest.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.ignfab.minalac.generator.outputs.minetest.Block;

/**
 * This class is responsible for providing the serialized {@code Block} needed by the {@code map.sqlite} file.
 * @see SQLiteMapWriter
 * @see com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld#save(java.io.File)
 */
public class Serializer {

    // We use a static buffer to convert int32 and int64 to bytes.
    // This avoids usage of DeflaterOutputStream.write(int) which performs something similar
    // but with a byte array instantiation for each call.
    private final byte[] buffer;

    // Using a static deflater avoids many useless instantiations.
    private final Deflater deflater;

    /**
     * Constructs a new {@code Serializer}.
     */
    public Serializer() {
        buffer = new byte[8192];
        deflater = new Deflater(Deflater.BEST_COMPRESSION);
    }

    private void write8Bits(OutputStream stream, int value) throws IOException {
        buffer[0] = (byte) (value & 0xFF);
        stream.write(buffer, 0, 1);
    }

    private void write16Bits(OutputStream stream, int value) throws IOException {
        buffer[0] = (byte) ((value >> 8) & 0xFF);
        buffer[1] = (byte) (value & 0xFF);
        stream.write(buffer, 0, 2);
    }

    private void write32Bits(OutputStream stream, int value) throws IOException {
        buffer[0] = (byte) ((value >> 24) & 0xFF);
        buffer[1] = (byte) ((value >> 16) & 0xFF);
        buffer[2] = (byte) ((value >> 8) & 0xFF);
        buffer[3] = (byte) (value & 0xFF);
        stream.write(buffer, 0, 4);
    }

    private void writeParam0IntoStream(OutputStream stream, Block block) throws IOException {
        int i = 0;
        for (short value : block.getParam0()) {
            buffer[i++] = (byte) ((value >> 8) & 0xFF);
            buffer[i++] = (byte) (value & 0xFF);
        }
        stream.write(buffer);
    }

    // See World Format Documentation for more information about block serialization
    // https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-timers
    private void generateNameIdMapping(OutputStream stream, Block block) throws IOException {
        // u8 name-id-mapping version
        write8Bits(stream, 0);

        // u16 num_name_id_mappings
        write16Bits(stream, block.getNameIdMapping().size());

        for (Map.Entry<Integer, String> entry : block.getNameIdMapping().entrySet()) {
            // u16 : id
            write16Bits(stream, entry.getKey());

            // u16 : name_len
            write16Bits(stream, entry.getValue().length());

            // u8[name_len]
            stream.write(entry.getValue().getBytes());
        }
    }

    /**
     * Serialize the specified {@link Block} for Minetest map version 28.
     *
     * @param block the block to serialize
     * @return a {@code byte[]} representing the serialized block
     * @throws IOException if an error occurs during serialization
     * @see <a href="https://github.com/minetest/minetest/blob/master/doc/world_format.md#mapblock-serialization-format">Minetest world format documentation</a>
     */
    public byte[] serialize(Block block) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(16384);

        // u8 : map version number
        write8Bits(stream, 28);

        // u8 : flags
        write8Bits(stream, 0);

        // u16 : lighting_complete
        write16Bits(stream, 0);

        // u8 : content_width
        write8Bits(stream, 2);

        // u8 : params_width
        write8Bits(stream, 2);

        // Zlib Node data
        DeflaterOutputStream nodeDataStream = new DeflaterOutputStream(stream, deflater);

        writeParam0IntoStream(nodeDataStream, block);
        nodeDataStream.write(block.getParam1());
        nodeDataStream.write(block.getParam2());

        nodeDataStream.flush();
        nodeDataStream.finish();
        deflater.reset();

        // Zlib Node metadata
        DeflaterOutputStream nodeMetadataStream = new DeflaterOutputStream(stream, deflater);

        // u8 : version
        write16Bits(nodeMetadataStream, 2);
        write16Bits(nodeMetadataStream, 0);
        nodeMetadataStream.flush();
        nodeMetadataStream.finish();
        deflater.reset();

        // u8 : static object version
        write8Bits(stream, 0);

        // u16 : static object count
        write16Bits(stream, 0);

        // u32 : timestamp
        write32Bits(stream, 0);

        // name id mapping
        generateNameIdMapping(stream, block);

        // u8 : length of the data of a single time
        write8Bits(stream, 10);

        // u16 : num_of_timers
        write16Bits(stream, 0);

        return stream.toByteArray();
    }
}
