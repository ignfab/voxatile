package com.ignfab.minalac.generator.utils.graph;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A {@link GraphWriter} based on a {@link File}.
 * Implementing methods should use the provided {@link #writer}
 * to write to the target file.
 */
public abstract class FileGraphWriter implements GraphWriter {
    /**
     * The target file.
     */
    protected final File file;
    /**
     * The writer to write to the file.
     */
    protected final PrintStream writer;

    /**
     * Creates a new {@code FileGraphWriter}.
     * @param file the target file to write the graph to
     * @throws IOException if the file is not valid
     */
    public FileGraphWriter(File file) throws IOException {
        this.file = file;
        writer = new PrintStream(file);
    }

    @Override
    public void end(boolean root) {
        if (root)
            writer.close();
    }
}
