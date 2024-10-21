package com.ignfab.minalac.generator.utils.graph;

import java.io.File;
import java.io.IOException;

/**
 * An implementation of {@link GraphWriter}
 * using the DOT graph language.
 */
public class DOTGraphWriter extends FileGraphWriter {
    /**
     * Creates a new {@code DOTGraphWriter}.
     * @param file the target file
     * @throws IOException if the file is invalid
     */
    public DOTGraphWriter(File file) throws IOException {
        super(file);
    }

    @Override
    public void begin(boolean root, String title) {
        writer.printf(root ? """
            graph {
            label="%s"
            rankdir="TB"
            """ : "subgraph %s {%n", title);
    }

    @Override
    public void addNode(String id, String label) {
        writer.println(simplifyId(id) + " [label=\"" + label + "\"]");
    }

    @Override
    public void addEdge(String from, String to, boolean directed) {
        writer.printf("%s -- %s [dir=%s, arrowhead=normal]%n", simplifyId(from), simplifyId(to), directed ? "forward" : "none");
    }

    @Override
    public void end(boolean root) {
        writer.println("}");
        super.end(root);
    }

    private String simplifyId(String id) {
        return id.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
