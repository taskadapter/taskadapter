package com.taskadapter.webui;

import com.taskadapter.connector.PropertiesUtf8Loader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Properties;

public class LastVersionLoader {
    /**
     * check the last TA version available for download on the website.
     */
    public static String loadLastVersion() {
        try {
            Properties properties = PropertiesUtf8Loader.load("taskadapter.properties");
            String url = properties.getProperty("update_site_url");
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);

            HttpResponse httpResponse = httpclient.execute(request);
            HttpEntity responseEntity = httpResponse.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
            return responseBody.trim();
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

}
