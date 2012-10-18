package com.taskadapter.web.service;

import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LastVersionLoader {
    /**
     * check the last TA version available for download on the website.
     */
    public static String loadLastVersion() {
        try {
            Properties properties = new Properties();
            InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(Resources.getResource("taskadapter.properties"));
            properties.load(inputSupplier.getInput());
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
