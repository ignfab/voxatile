package com.ignfab.minalac.generator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class FileHelpersTest {
    @Test
    public void testIsReadableRegularFile(@TempDir File tmp) throws IOException {
        File missing = new File(tmp, "missing");
        assertFalse(FileHelpers.isReadableRegularFile(missing));
        assertFalse(FileHelpers.isReadableRegularFile(missing.toPath()));

        File dir = new File(tmp, "dir");
        if (!dir.mkdir())
            fail("Unable to setup test data");
        assertFalse(FileHelpers.isReadableRegularFile(dir));

        File unreadable = new File(tmp, "unreadable");
        if (!unreadable.createNewFile())
            fail("Unable to setup test data");
        Path unreadablePath = unreadable.toPath();
        if (unreadablePath.getFileSystem().supportedFileAttributeViews().contains("acl")) { // Windows file-system
            Files.setAttribute(unreadablePath, "acl:acl", List.of(AclEntry.newBuilder()
                .setType(AclEntryType.DENY)
                .setPermissions(AclEntryPermission.READ_DATA)
                .setPrincipal(Files.getOwner(unreadablePath))
                .build()));
        } else if (!unreadable.setReadable(false))
            fail("Unable to setup test data");
        assertFalse(FileHelpers.isReadableRegularFile(unreadable));

        File readable = new File(tmp, "readable");
        if (!readable.createNewFile())
            fail("Unable to setup test data");
        assertTrue(FileHelpers.isReadableRegularFile(readable));
    }

    @Test
    public void testWrite(@TempDir File tmp) throws IOException {
        File simple = new File(tmp, "simple.txt");
        String simpleContent = "Simple content";
        FileHelpers.write(simple, simpleContent);
        assertEquals(simpleContent, Files.readString(simple.toPath()));

        File nested = new File(tmp, "deeply/nested/file.txt");
        String multilineContent = """
            Multiline
            content
            """;
        FileHelpers.write(nested, multilineContent);
        assertEquals(multilineContent, Files.readString(nested.toPath()));

        File dir = new File(tmp, "dir");
        if (!dir.mkdir())
            fail("Unable to setup test data");
        assertThrows(IOException.class, () -> FileHelpers.write(dir, "invalid"));
        assertThrows(IOException.class, () -> FileHelpers.write(new File(simple, "illegal/parent.txt"), "invalid"));
    }
}
