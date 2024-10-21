package com.ignfab.minalac.generator.utils.graph;

import java.io.File;
import java.io.IOException;

/**
 * An implementation of {@link GraphWriter}
 * using the Mermaid diagram language.
 */
public class MermaidGraphWriter extends FileGraphWriter {
    /**
     * Creates a new {@code MermaidGraphWriter}.
     * @param file the target file
     * @throws IOException if the file is invalid
     */
    public MermaidGraphWriter(File file) throws IOException {
        super(file);
    }

    @Override
    public void begin(boolean root, String title) {
        writer.printf(root ? """
            ---
            title: %s
            ---
            flowchart TB
            """ : "subgraph %s%n", title);
    }

    @Override
    public void addNode(String id, String label) {
        writer.println(id + "[`" + label + "`]");
    }

    @Override
    public void addEdge(String from, String to, boolean directed) {
        String link = directed ? " --> " : " --- ";
        writer.println(from + link + to);
    }

    @Override
    public void end(boolean root) {
        if (!root)
            writer.println("end");
        super.end(root);
    }
}
