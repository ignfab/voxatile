package fr.ign.voxatile.core.parameters.providers;

import java.beans.ConstructorProperties;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.OsmData;
import fr.ign.voxatile.core.inputs.OverpassProvider;
import fr.ign.voxatile.core.inputs.Provider;
import fr.ign.voxatile.core.parameters.processors.OsmProcessorParams;
import fr.ign.voxatile.core.parameters.processors.ProcessorParams;

/**
 * Parameters for Overpass provider.
 */
public class OverpassProviderParams extends ProviderParams {
    /**
     * URL of Overpass API to use (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String url;

    /**
     * Overpass query (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String query;

    /**
     * Creates a new OverpassProviderParams with mandatory fields.
     *
     * @param url URL of Overpass API to use.
     * @param query Overpass query
     */
    @ConstructorProperties({"url", "query"})
    public OverpassProviderParams(String url, String query) {
        this.url = url;
        this.query = query;
    }

    @Override
    public Provider<OsmData> create(Generation generation) {
        // (Trailing ";" are removed from query)
        return new OverpassProvider(URI.create(url), query.replaceAll(";+$", ""), generation::getEnvelopeForCRS);
    }

    @Override
    public ProcessorParams defaultProcessor() {
        return new OsmProcessorParams();
    }
}
