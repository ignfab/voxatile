package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.wfs.GML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;

/**
 * Data provider using WFS 1.1 and GML 3.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider implements Provider<SimpleFeature> {
    private static final String SERVICE = "WFS";
    private static final String VERSION = "2.0.0";

    private final String baseURL;
    private final String type;
    private final ReferencedEnvelope envelope;

    /**
     * Constructs a new {@code WFS1_1_GML3_1_DataProvider}.
     *
     * @param baseURL the base URL
     * @param type Name of the WFS feature type to query
     * @param envelope Limit of area to fetch
     */
    public WFS1_1_GML3_1_DataProvider(String baseURL, String type, ReferencedEnvelope envelope) {
        this.baseURL = baseURL;
        this.type = type;
        this.envelope = envelope;
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide() throws GenerationFailedException, RetryableException {

        String srsname = CRS.toSRS(envelope.getCoordinateReferenceSystem());
        if (srsname == null)
            throw new GenerationFailedException("Could not retrieve SRS name for layer");

        URLBuilder url = new URLBuilder(baseURL);
        url.addQueryParameter("SERVICE", SERVICE);
        url.addQueryParameter("VERSION", VERSION);
        url.addQueryParameter("REQUEST", "GetFeature");
        url.addQueryParameter("OUTPUTFORMAT", "GML3");
        url.addQueryParameter("TYPENAMES", type);
        url.addQueryParameter("SRSNAME", srsname);
        url.addQueryParameter("BBOX", envelope.getMinX() + "," + envelope.getMinY() + "," + envelope.getMaxX() + "," + envelope.getMaxY() + "," + srsname);
        url.addQueryParameter("STARTINDEX", 0);
        url.addQueryParameter("COUNT", 1000);

        InputStream stream;
        try {
            System.out.println(url.toURL());
            stream = url.toURL().openStream(); // TODO Replace with an HTTP requesting tool to allow unit testing and snapshots
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        // This code is temporary and no attention is given to its (bad) performance
        // TODO: Improve that!
        byte[] bytes;
        try {
            bytes = stream.readAllBytes();
            stream.close();
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
        String string = new String(bytes)
            .replace("http://BDTOPO_V3", "explicitly-invalid")
            .replace("http://RPG.LATEST", "explicitly-invalid");
        InputStream replacedStream = new ByteArrayInputStream(string.getBytes());

        try {
            return new FeaturesResult(new GML(GML.Version.GML3).decodeFeatureCollection(replacedStream).features(), replacedStream);
        } catch (IOException e) {
            throw new RetryableException("Error fetching data", e);
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

    @Override
    public CoordinateReferenceSystem crs() {
        return envelope.getCoordinateReferenceSystem();
    }
}
