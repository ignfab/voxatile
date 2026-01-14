package com.ignfab.minalac.generator;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AxiomBlueprint {
    // https://github.com/Moulberry/AxiomPaperPlugin/blob/master/src/main/java/com/moulberry/axiom/blueprint/BlueprintIo.java
    public static void main(String[] args) {
        //TextNbtSerializer snbt = new TextNbtSerializer(false);

        try (FileInputStream is = new FileInputStream("C:\\Users\\user\\Downloads\\kdo_indy.bp")) {
            DataInputStream dis = new DataInputStream(is);
            System.out.println(Integer.toHexString(dis.readInt())); // 0xAE5BB36

            /*dis.readInt();
            System.out.println(snbt.toString(BinaryNbtHelpers.read(dis, CompressionType.NONE)));

            int thumbnailLength = dis.readInt();
            dis.skipNBytes(thumbnailLength);

            dis.readInt();
            System.out.println(snbt.toString(BinaryNbtHelpers.read(dis, CompressionType.GZIP)));*/

            toFile("header.nbt", dis);
            toFile("thumbnail.png", dis);
            toFile("data.nbt", dis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void toFile(String name, DataInputStream dis) throws IOException {
        int length = dis.readInt();
        try (FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(dis.readNBytes(length));
        }
    }
}
