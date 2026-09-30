package com.ignfab.minalac.generator.fetchers;

import java.util.LinkedList;
import java.util.List;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;

/*
TODOS:
- Trouver comment gérer la bbox: pour une tuile, il faut fusionner les demandes (mais on ne les a pas toutes, ça va poser pb)
- Trouver comment gérer le passage d'une tuile à l'autre --> DONE, using Specs
*/

public class OverpassPoolSpec {
    // For all tiles:
    private final EnvelopeProvider envelopeProvider;
    private final List<String> queries = new LinkedList<>();
    private final HttpFetcher fetcher;

    /**
     *
     */
    public OverpassPoolSpec(
        HttpFetcher fetcher,
        EnvelopeProvider envelopeProvider
    ) {
        this.fetcher = fetcher;
        this.envelopeProvider = envelopeProvider;
    }

    /**
     * TODO: Missing a generic pattern for Spec(Generation level)/Data(Tile level)
     * We have the same pattern with Heightmaps
     */
    public OverpassPool getPoolForTile(GenerationTile tile) {
        return tile.overpassPool;
    }

    /**
     *
     */
    synchronized public int addQuery(String query) {
        queries.add(query);
        return queries.size() - 1;
    }

    /**
     *
     */
    public EnvelopeProvider envelopeProvider() {
        return envelopeProvider;
    }

    /**
     * TODO: queries could be passed to OverpassPoolSpec
     */
    public List<String> queries() {
        return queries;
    }

    /**
     * TODO: fetcher could be passed to OverpassPoolSpec
     */
    public HttpFetcher fetcher() {
        return fetcher;
    }

}
