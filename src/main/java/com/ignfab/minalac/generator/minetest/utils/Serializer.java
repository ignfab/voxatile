package com.ignfab.minalac.generator.minetest.utils;

import com.ignfab.minalac.generator.minetest.Block;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

public class Serializer {

    private void write8Bits(OutputStream stream, int value) throws IOException {
        stream.write((byte) (value & 0xFF));
    }

    private void write16Bits(OutputStream stream, int value) throws IOException {
        stream.write((byte) ((value >> 8) & 0xFF));
        stream.write((byte) (value & 0xFF));
    }

    private void write32Bits(OutputStream stream, int value) throws IOException {
        stream.write((byte) ((value >> 24) & 0xFF));
        stream.write((byte) ((value >> 16) & 0xFF));
        stream.write((byte) ((value >> 8) & 0xFF));
        stream.write((byte) (value & 0xFF));
    }

    private void writeParam0IntoStream(OutputStream stream, Block block) throws IOException {
        for (short param0 : block.getParam0()) {
            write16Bits(stream, param0);
        }
    }

    //See World Format Documentation for more information about block serialization
    //https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-timers
    private void generateNameIdMapping(OutputStream stream, Block block) throws IOException {
        //u8 name-id-mapping version
        write8Bits(stream, 0);

        //u16 num_name_id_mappings
        write16Bits(stream, block.getNameIdMapping().size());

        for (Map.Entry<Integer, String> entry : block.getNameIdMapping().entrySet()) {
            //u16 : id
            write16Bits(stream, entry.getKey());

            //u16 : name_len
            write16Bits(stream, entry.getValue().length());

            //u8[name_len]
            stream.write(entry.getValue().getBytes());
        }
    }

    //Serializer for map version 28
    //See World Format Documentation for version 28
    //https://github.com/minetest/minetest/blob/master/doc/world_format.md
    public byte[] serialize(Block block) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //u8 : map version number
        write8Bits(stream, 28);

        //u8 : flags
        write8Bits(stream, 0);

        //u16 : lighting_complete
        write16Bits(stream, 0);

        //u8 : content_width
        write8Bits(stream, 2);

        //u8 : params_width
        write8Bits(stream, 2);

        //Zlib Node data
        DeflaterOutputStream nodeDataStream = new DeflaterOutputStream(stream);
        writeParam0IntoStream(nodeDataStream, block);
        nodeDataStream.write(block.getParam1());
        nodeDataStream.write(block.getParam2());
        nodeDataStream.flush();
        nodeDataStream.finish();

        //Zlib Node metadata
        DeflaterOutputStream nodeMetadataStream = new DeflaterOutputStream(stream);
        //u8 : version
        write16Bits(nodeMetadataStream, 2);
        write16Bits(nodeMetadataStream, 0);
        nodeMetadataStream.flush();
        nodeMetadataStream.finish();

        //u8 : static object version
        write8Bits(stream, 0);

        //u16 : static object count
        write16Bits(stream, 0);

        //u32 : timestamp
        write32Bits(stream, 0);

        //name id mapping
        generateNameIdMapping(stream, block);

        //u8 : length of the data of a single time
        write8Bits(stream, 10);

        //u16 : num_of_timers
        write16Bits(stream, 0);

        return stream.toByteArray();
    }
}