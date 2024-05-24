package com.ignfab.minalac.generator.utils.network;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Trust manager that does not validate certificate chains.
 */
public class HttpTrustAllSSL extends X509ExtendedTrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {}

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {}

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    /**
     * Disable SSL checks by applying a trust-all policy.
     */
    public static void applyGlobally() {
        try {
            // Create & install the all-trusting trust manager
            TrustManager[] trustAllCerts = { new HttpTrustAllSSL() };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Create & install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Unable to apply all-trusting policy", e);
        }
    }
}
