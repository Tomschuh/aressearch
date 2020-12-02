package cz.agentes.aressearch.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

    private static HttpURLConnection httpURLConnection;

    public static HttpURLConnection sendGetRequest(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);

        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(false);

        return httpURLConnection;
    }
}
