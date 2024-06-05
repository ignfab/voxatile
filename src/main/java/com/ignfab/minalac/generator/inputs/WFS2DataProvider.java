package com.ignfab.minalac.generator.inputs;

import org.apache.poi.util.ReplacingInputStream;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.wfs.GML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WFS2DataProvider {
    private final String baseURL;

    public WFS2DataProvider(String baseURL) {
        this.baseURL = baseURL;
    }

    public SimpleFeatureCollection getFeatures() throws IOException, ParserConfigurationException, SAXException {
        GML gml = new GML(GML.Version.GML3);

        URL url = new URL(baseURL);

        InputStream stream = url.openStream();

        InputStream replacedStream = new ReplacingInputStream(new ReplacingInputStream(new ReplacingInputStream(new ReplacingInputStream(stream,
                "</wfs:member>\n<wfs:member>", ""),
                "wfs:member", "gml:featureMembers"),
                "http://BDTOPO_V3", "http://BDTOPOV3"),
                "xmlns:gml=\"http://www.opengis.net/gml/3.2\"", "xmlns:gml=\"http://www.opengis.net/gml\"");

        return gml.decodeFeatureCollection(replacedStream);
   }
}
