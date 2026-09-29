package com.ignfab.minalac.generator.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.slimjars.dist.gnu.trove.iterator.TLongObjectIterator;
import com.slimjars.dist.gnu.trove.map.TLongObjectMap;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.fetchers.Fetcher;
import com.ignfab.minalac.generator.geodata.OsmData;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider for Overpass.
 */
public class OsmXmlProvider implements Provider<OsmData> {
    private final Fetcher fetcher;

    /**
     * Creates a new {@code OverpassProvider}.
     *
     * @param fetcher fetcher to use to get OSM-encoded data
     */
    public OsmXmlProvider(Fetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public Class<OsmData> providedType() {
        return OsmData.class;
    }

    @Override
    public SimpleResult<OsmData> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        List<InMemoryMapDataSet> datasets = new LinkedList<>();

        Fetcher.FetchResult results = fetcher.fetch(bbox);

        while (results.hasNext()) {
            try (InputStream stream = results.next()) {
                datasets.add(MapDataSetLoader.read(new OsmXmlReader(stream, false), true, true, true));
            } catch (OsmInputException e) {
                throw new GenerationFailedException("Unable to decode OSM data", e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new RetryableException("Error fetching data", e);
            }
        }

        return new SimpleResult<OsmData>(OsmData.CRS,
            Iterators.unwrap(
                Iterators.remap(
                    datasets.iterator(),
                    (dataset) -> Iterators.remap(
                        Iterators.union(
                            new OsmEntityIterator(dataset.getNodes()),
                            new OsmEntityIterator(dataset.getWays()),
                            new OsmEntityIterator(dataset.getRelations())
                        ),
                        (entity) -> new OsmData(dataset, entity)
                    )
                )
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
    public final class OsmEntityIterator implements Iterator<OsmEntity> {
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
