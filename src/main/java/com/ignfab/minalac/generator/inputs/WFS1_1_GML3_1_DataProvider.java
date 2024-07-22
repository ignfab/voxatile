package com.ignfab.minalac.generator.inputs;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.wfs.GML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Data provider using WFS 1.1 and GML 3.1.
 */
@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider {
    private final String baseURL;

    /**
     * Constructs a new {@code WFS1_1_GML3_1_DataProvider}.
     *
     * @param baseURL the base URL.
     */
    public WFS1_1_GML3_1_DataProvider(String baseURL) {
        this.baseURL = baseURL;
    }


    /**
     * Retrieves a collection of features from the data provider.
     *
     * @return a collection of features
     */
    public SimpleFeatureCollection getFeatures() throws IOException, ParserConfigurationException, SAXException {
        GML gml = new GML(GML.Version.GML3);

        URL url = new URL(baseURL);

        InputStream stream = url.openStream();

        // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
        // This code is temporary and no attention is given to its (bad) performance
        // TODO: Improve that!
        byte[] bytes = stream.readAllBytes();
        stream.close();
        String string = new String(bytes).replace("http://BDTOPO_V3", "explicitly-invalid");
        InputStream replacedStream = new ByteArrayInputStream(string.getBytes());

        return gml.decodeFeatureCollection(replacedStream);
   }
}
