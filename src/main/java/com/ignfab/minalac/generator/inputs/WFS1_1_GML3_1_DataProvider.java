package com.ignfab.minalac.generator.inputs;

import org.apache.poi.util.ReplacingInputStream;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.wfs.GML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@SuppressWarnings("checkstyle:TypeName") // Underscore character used to better identify "WFS 1.1" and "GML 3.1"
public class WFS1_1_GML3_1_DataProvider {
    private final String baseURL;

    public WFS1_1_GML3_1_DataProvider(String baseURL) {
        this.baseURL = baseURL;
    }

    public SimpleFeatureCollection getFeatures() throws IOException, ParserConfigurationException, SAXException {
        GML gml = new GML(GML.Version.GML3);

        URL url = new URL(baseURL);

        InputStream stream = url.openStream();

        // Invalidate schema declaration because GML version 3.1 is not used in this schema (version is unspecified, defaulting to 3.2)
        InputStream replacedStream = new ReplacingInputStream(stream, "http://BDTOPO_V3", "explicitly-invalid");

        return gml.decodeFeatureCollection(replacedStream);
   }
}
