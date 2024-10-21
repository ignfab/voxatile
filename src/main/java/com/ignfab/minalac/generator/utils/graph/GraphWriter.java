package com.ignfab.minalac.generator.utils.graph;

// TODO improve "(nested) current graph" handling (remove "root" arg from begin / end methods)
/**
 * A {@code GraphWriter} provides a way to create basic graph.
 */
public interface GraphWriter {
    /**
     * Starts a new graph or subgraph.
     * @param root whether this is the root graph or a subgraph
     * @param title the name to give to the graph or subgraph
     */
    void begin(boolean root, String title);

    /**
     * Adds a node to the current graph.
     * @param id the unique identifier of the node
     * @param label the descriptive label to show on the node
     */
    void addNode(String id, String label);

    /**
     * Links two nodes together.
     * @param from the origin of the relationship
     * @param to the destination of the relationship
     * @param directed whether this is a directed or undirected edge
     */
    void addEdge(String from, String to, boolean directed);

    /**
     * Finishes the current graph or subgraph.
     * @param root whether the current graph is the root graph
     */
    void end(boolean root);
}
