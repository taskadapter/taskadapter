package com.taskadapter.http;

import com.google.gson.Gson;
import com.taskadapter.connector.common.ConfigUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpCaller {
    private static HttpClient httpclient = new DefaultHttpClient();
    private static Gson gson = ConfigUtils.createDefaultGson();

    public static String getAsString(String url) throws IOException {
        var request = new HttpGet(url);
        var httpResponse = httpclient.execute(request);
        var responseEntity = httpResponse.getEntity();
        return EntityUtils.toString(responseEntity);
    }

    public static <C> C get(String url, Class<C> c) throws IOException {
        var request = new HttpGet(url);

        var httpResponse = httpclient.execute(request);
        var responseEntity = httpResponse.getEntity();
        var responseBody = EntityUtils.toString(responseEntity);
        var result = gson.fromJson(responseBody, c);
        return result;
    }

    public static String post(String url, String obj) throws IOException {
        var entity = new StringEntity(obj);
        var request = new HttpPost(url);
        request.setEntity(entity);
        return execute(request, String.class);
    }

    public static <C> C post(String url, Class<C> c) throws IOException {
        var request = new HttpPost(url);
        return execute(request, c);
    }

    public static <C> C put(String url, Class<C> c) throws IOException {
        var request = new HttpPut(url);
        return execute(request, c);
    }

    public static <C> C execute(HttpRequestBase request, Class<C> c) throws IOException {
        var httpResponse = httpclient.execute(request);
        var responseEntity = httpResponse.getEntity();
        var responseBody = EntityUtils.toString(responseEntity);
        var result = gson.fromJson(responseBody, c);
        return result;
    }
}
