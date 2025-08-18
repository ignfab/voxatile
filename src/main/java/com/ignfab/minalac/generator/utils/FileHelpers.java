package com.ignfab.minalac.generator.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A utility class for files operations.
 */
public final class FileHelpers {
    private FileHelpers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Tests if the given file is a regular readable file.
     * Shortcut for {@code FileHelpers.isReadableRegularFile(file.toPath())}
     * @param file the file to test
     * @return {@code true} if the file is regular and readable, {@code false} otherwise
     * @see #isReadableRegularFile(Path)
     */
    public static boolean isReadableRegularFile(File file) {
        return isReadableRegularFile(file.toPath());
    }

    /**
     * Tests if the given path points to a regular readable file.
     * @param path the path to test
     * @return {@code true} if the target file is regular and readable, {@code false} otherwise
     */
    public static boolean isReadableRegularFile(Path path) {
        return Files.isRegularFile(path) && Files.isReadable(path);
    }

    /**
     * Puts the given content into the file, creating any necessary directories.
     * @param file the file to write content to
     * @param content the content to write as UTF-8 into the file
     * @throws IOException if content cannot be written to the file for any reason
     */
    public static void write(File file, String content) throws IOException {
        File parent = file.getParentFile();
        if (parent.isDirectory() || parent.mkdirs())
            Files.writeString(file.toPath(), content);
        else
            throw new IOException("Unable to create parent directories to write to file: " + file.getAbsolutePath());
    }
}
