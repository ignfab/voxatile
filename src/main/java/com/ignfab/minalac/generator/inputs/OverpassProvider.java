package com.ignfab.minalac.generator.inputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;

import com.slimjars.dist.gnu.trove.iterator.TLongObjectIterator;
import com.slimjars.dist.gnu.trove.map.TLongObjectMap;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;
import org.geotools.api.referencing.FactoryException;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for Overpass.
 */
public class OverpassProvider implements Provider<OsmData> {
    private final URI url;
    private final EnvelopeProvider envelopeProvider;
    private final String query;

    /**
     * Creates a new {@code OverpassProvider}.
     *
     * @param url Overpass URL
     * @param query Overpass query. Each element will be processed as a separate model.
     * @param envelopeProvider the envelope provider to filter elements.
     */
    public OverpassProvider(URI url, String query, EnvelopeProvider envelopeProvider) {
        this.url = url;
        this.query = query;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<OsmData> providedType() {
        return OsmData.class;
    }

    @Override
    public SimpleResult<OsmData> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(OsmData.CRS, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        // Fetch data from query, within bbox, including sub-ways and nodes but getting rid of eventual tags.
        // (we will rely on tags to distinguish wanted data from constituting ways and nodes)
        String data = String.format(Locale.US, "[bbox:%f,%f,%f,%f];%s;out;>;out skel;",
            envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY(), query);

        String body = "data=" + URLEncoder.encode(data, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(url)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<InputStream> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            throw new RetryableException("Error opening connection", e);
        }

        int status = response.statusCode();

        if (status >= 400) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
                System.err.printf("%d status code from Overpass%n", status);
                System.err.println(reader.lines().collect(Collectors.joining("\n")));
            } catch (IOException ignored) {}

            throw new RetryableException("%d status code from Overpass".formatted(status));
        }

        InMemoryMapDataSet dataset;
        try (InputStream stream = response.body()) {
            dataset = MapDataSetLoader.read(new OsmXmlReader(stream, false), true, true, true);
        } catch (OsmInputException e) {
            throw new GenerationFailedException(e);
        } catch (IOException e) {
            throw new RetryableException(e);
        }

        return new SimpleResult<>(OsmData.CRS,
            Iterators.remap(
                Iterators.union(
                    new OsmEntityIterator(dataset.getNodes()),
                    new OsmEntityIterator(dataset.getWays()),
                    new OsmEntityIterator(dataset.getRelations())
                ),
                (entity) -> new OsmData(dataset, entity)
            )
        );
    }

    /**
     * An iterator for a {@code TLongObjectMap} of {@code OsmEntity} objects.
     * <p>
     * This iterator wraps a {@code TLongObjectIterator} over {@code OsmEntity} subtypes.
     * It casts back results into {@code OsmEntity} type and skips entities with no tags
     * (the provider query strips tags from entities that are only component of features geometries).
     */
    private static class OsmEntityIterator implements Iterator<OsmEntity> {
        private final TLongObjectIterator<? extends OsmEntity> iterator;
        private OsmEntity next;

        OsmEntityIterator(TLongObjectMap<? extends OsmEntity> map) {
            iterator = map.iterator();
            moveOn();
        }

        public void moveOn() {
            next = null;
            while (next == null && iterator.hasNext()) {
                iterator.advance();
                if (iterator.value().getNumberOfTags() > 0)
                    next = iterator.value();
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public OsmEntity next() {
            OsmEntity result = next;
            moveOn();
            return result;
        }
    }
}
