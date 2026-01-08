package com.ignfab.minalac.generator.extensions.minetest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.SoftReferenceObjectPool;

/**
 * This class is responsible for providing the serialized {@code Block} needed by the {@code map.sqlite} file.
 * @see SQLiteMapWriter
 * @see com.ignfab.minalac.generator.extensions.minetest.MTVoxelTile#save()
 */
public class Serializer implements AutoCloseable {
    // Shared pool of internal state objects to minimize memory allocations.
    // When serializing blocks sequentially (by default), only one object
    // will be created and reused, but parallel serialization will still
    // be possible, by creating more objects from the pool.
    // Soft-reference allows garbage collection of idle pool objects.
    private final ObjectPool<InternalState> pool = new SoftReferenceObjectPool<>(new InternalStatePoolFactory());

    // Pool factory manages pool objects
    private static final class InternalStatePoolFactory extends BasePooledObjectFactory<InternalState> {
        @Override
        public InternalState create() {
            return new InternalState();
        }

        @Override
        public void activateObject(PooledObject<InternalState> p) {
            p.getObject().stream.reset();
        }

        @Override
        public void destroyObject(PooledObject<InternalState> p) {
            p.getObject().deflater.end();
        }

        @Override
        public PooledObject<InternalState> wrap(InternalState obj) {
            return new DefaultPooledObject<>(obj);
        }
    }

    private static final class InternalState {
        // Reusing the same stream to avoid instantiating a new byte array each time.
        private final ByteArrayOutputStream stream = new ByteArrayOutputStream(16384);

        // Using a single deflater avoids many useless instantiations.
        private final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

        // We use a pre-allocated buffer to convert int32 and int64 to bytes.
        // This avoids usage of DeflaterOutputStream#write(int) which performs
        // something similar but with a byte array instantiation for each call.
        private final byte[] buffer = new byte[8192];

        public byte[] data() {
            return stream.toByteArray();
        }

        public void write8Bits(int value) throws IOException {
            write8Bits(stream, value);
        }

        public void write8Bits(OutputStream stream, int value) throws IOException {
            buffer[0] = (byte) (value & 0xFF);
            stream.write(buffer, 0, 1);
        }

        public void write16Bits(int value) throws IOException {
            write16Bits(stream, value);
        }

        public void write16Bits(OutputStream stream, int value) throws IOException {
            buffer[0] = (byte) ((value >> 8) & 0xFF);
            buffer[1] = (byte) (value & 0xFF);
            stream.write(buffer, 0, 2);
        }

        public void write32Bits(int value) {
            buffer[0] = (byte) ((value >> 24) & 0xFF);
            buffer[1] = (byte) ((value >> 16) & 0xFF);
            buffer[2] = (byte) ((value >> 8) & 0xFF);
            buffer[3] = (byte) (value & 0xFF);
            stream.write(buffer, 0, 4);
        }

        public void writeParam0IntoStream(OutputStream stream, Block block) throws IOException {
            int i = 0;
            for (short value : block.getParam0()) {
                buffer[i++] = (byte) ((value >> 8) & 0xFF);
                buffer[i++] = (byte) (value & 0xFF);
            }
            stream.write(buffer);
        }

        // See World Format Documentation for more information about block serialization
        // https://github.com/minetest/minetest/blob/master/doc/world_format.md#node-timers
        public void generateNameIdMapping(Block block) throws IOException {
            // u8 name-id-mapping version
            write8Bits(0);

            // u16 num_name_id_mappings
            write16Bits(block.getNameIdMapping().size());

            for (Map.Entry<Integer, String> entry : block.getNameIdMapping().entrySet()) {
                // u16 : id
                write16Bits(entry.getKey());

                // u16 : name_len
                write16Bits(entry.getValue().length());

                // u8[name_len]
                stream.write(entry.getValue().getBytes());
            }
        }

        public void compressed(CompressedWriter writer) throws IOException {
            DeflaterOutputStream compressedStream = new DeflaterOutputStream(stream, deflater);
            writer.write(compressedStream);
            compressedStream.finish();
            deflater.reset();
        }

        @FunctionalInterface
        private interface CompressedWriter {
            void write(DeflaterOutputStream stream) throws IOException;
        }
    }

    /**
     * Serialize the specified {@link Block} for Minetest map version 28.
     *
     * @param block the block to serialize
     * @return a {@code byte[]} representing the serialized block
     * @throws IOException if an error occurs during serialization
     * @see <a href="https://github.com/luanti-org/luanti/blob/master/doc/world_format.md#mapblock-serialization-format">Minetest world format documentation</a>
     */
    public byte[] serialize(Block block) throws IOException {
        InternalState state;
        try {
            state = pool.borrowObject();
        } catch (Exception e) {
            throw new IOException("Unable to get an internal state from the pool", e);
        }

        try {
            // u8 : map version number
            state.write8Bits(28);

            // u8 : flags
            state.write8Bits(0);

            // u16 : lighting_complete
            state.write16Bits(0);

            // u8 : content_width
            state.write8Bits(2);

            // u8 : params_width
            state.write8Bits(2);

            // Zlib Node data
            state.compressed(stream -> {
                state.writeParam0IntoStream(stream, block);
                stream.write(block.getParam1());
                stream.write(block.getParam2());
            });

            // Zlib Node metadata
            state.compressed(stream -> {
                // u8 : version
                state.write8Bits(stream, 2);
                // u16 : count of metadata
                state.write16Bits(stream, 0);
            });

            // u8 : static object version
            state.write8Bits(0);

            // u16 : static object count
            state.write16Bits(0);

            // u32 : timestamp
            state.write32Bits(0);

            // name id mapping
            state.generateNameIdMapping(block);

            // u8 : length of the data of a single time
            state.write8Bits(10);

            // u16 : num_of_timers
            state.write16Bits(0);
        } catch (IOException e) {
            try {
                pool.invalidateObject(state);
            } catch (Exception suppressed) {
                e.addSuppressed(suppressed);
            }
            throw e;
        }

        byte[] data = state.data();

        try {
            pool.returnObject(state);
        } catch (Exception e) {
            throw new IOException("Unable to return the internal state to the pool", e);
        }
        return data;
    }

    @Override
    public void close() {
        pool.close();
    }
}
