package com.ignfab.minalac.generator;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.github.luben.zstd.Zstd;
import org.bson.BsonBinaryReader;
import org.bson.BsonDocument;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;

public class HytaleWorldFormatExploration {
    public static void main(String[] args) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream("C:\\Users\\user\\AppData\\Roaming\\Hytale\\UserData\\Saves\\HytaleWorld\\universe\\worlds\\default\\chunks\\0.0.region.bin"))) {
            System.out.println("Magic: " + new String(dis.readNBytes(20)));
            System.out.println("Version: " + dis.readInt());
            int blobCount = dis.readInt();
            System.out.println("Blob count: " + blobCount);
            int segmentSize = dis.readInt();
            System.out.println("Segment size: " + segmentSize);

            int firstSegmentIndex = dis.readInt();
            System.out.println("First segment index: " + firstSegmentIndex);
            dis.skipNBytes((blobCount - 1) * 4L);
            dis.skipNBytes((firstSegmentIndex - 1) * (long) segmentSize);

            int srcLength = dis.readInt();
            System.out.println("Src length: " + srcLength);
            int compressedLength = dis.readInt();
            System.out.println("Compressed length: " + compressedLength);

            byte[] data = Zstd.decompress(dis.readNBytes(compressedLength), srcLength);
            BsonDocument bson = new BsonDocumentCodec().decode(new BsonBinaryReader(ByteBuffer.wrap(data)), DecoderContext.builder().build());
            //System.out.println(bson.toJson());

            /*DataInputStream environmentChunkData = new DataInputStream(new ByteArrayInputStream(bson.getDocument("Components").getDocument("EnvironmentChunk").getBinary("Data").getData()));
            int mappingCount = environmentChunkData.readInt();
            System.out.println("Mapping count: " + mappingCount);
            for (int i = 0; i < mappingCount; i++) {
                int serialId = environmentChunkData.readInt();
                String key = environmentChunkData.readUTF();
                System.out.println("- " + serialId + ": " + key);
            }*/

            /*DataInputStream blockChunkData = new DataInputStream(new ByteArrayInputStream(bson.getDocument("Components").getDocument("BlockChunk").getBinary("Data").getData()));
            System.out.println("Needs physics: " + blockChunkData.readBoolean());*/

        }
    }

    /*public static void main0(String[] args) throws IOException {
        try (IndexedStorageFile file = IndexedStorageFile.open(Path.of("C:\\Users\\user\\Downloads\\HytaleWorld\\universe\\worlds\\default\\chunks\\0.0.region.bin"))) {
            BsonDocument bsonDocument = BsonUtil.readFromBuffer(file.readBlob(0));
            System.out.println(bsonDocument.toJson());
        }
    }*/
}
