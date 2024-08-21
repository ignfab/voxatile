package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.wfs.GML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * Data provider using WFS 1.1 and GML 3.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider implements Provider<SimpleFeature> {
    private final String baseURL;

    /**
     * Constructs a new {@code WFS1_1_GML3_1_DataProvider}.
     *
     * @param baseURL the base URL.
     */
    public WFS1_1_GML3_1_DataProvider(String baseURL) {
        this.baseURL = baseURL;
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide() throws GenerationFailedException, RetryableException {
        GML gml = new GML(GML.Version.GML3);

        URL url;
        try {
            url = new URL(baseURL);
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Malformed data source URL", e);
        }

        try {
            InputStream stream = url.openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots

            // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
            // This code is temporary and no attention is given to its (bad) performance
            // TODO: Improve that!
            byte[] bytes = stream.readAllBytes();
            stream.close();
            String string = new String(bytes).replace("http://BDTOPO_V3", "explicitly-invalid");
            InputStream replacedStream = new ByteArrayInputStream(string.getBytes());

            return new FeaturesResult(gml.decodeFeatureCollection(replacedStream).features(), replacedStream);
        } catch (IOException e) {
            throw new RetryableException(e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new GenerationFailedException("Unable to decode features", e);
        }
    }

    private record FeaturesResult(SimpleFeatureIterator features, InputStream stream) implements Result<SimpleFeature> {
        @Override
        public Iterator<SimpleFeature> iterator() {
            return new Iter();
        }

        @Override
        public void close() throws IOException {
            features.close();
            stream.close();
        }

        private final class Iter implements Iterator<SimpleFeature> {
            @Override
            public boolean hasNext() {
                return features.hasNext();
            }

            @Override
            public SimpleFeature next() {
                return features.next();
            }
        }
    }
}
