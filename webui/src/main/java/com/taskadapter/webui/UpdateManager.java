package com.taskadapter.webui;

import com.taskadapter.util.MyIOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.redmine.ta.internal.HttpUtil;

import java.io.IOException;
import java.util.Properties;

public class UpdateManager {

    private String lastVersion;

    public UpdateManager() {
// disabled for now to not generate a lot of useless "check version" requests to the server.
// we'll enable before the release.
//    loadLastVersion();
        setHardcodedLastVersionForTesting();
    }

    private void setHardcodedLastVersionForTesting() {
        lastVersion = "2.0.0_dev";
    }

    private void loadLastVersion() {
        try {
            Properties properties = new Properties();
            properties.load(MyIOUtils.getResourceAsStream("bat.properties"));
            String url = properties.getProperty("update_site_url");
            DefaultHttpClient httpclient = HttpUtil.getNewHttpClient();

            HttpGet request = new HttpGet(url);

            HttpResponse httpResponse = httpclient.execute(request);
            HttpEntity responseEntity = httpResponse.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);
            this.lastVersion = responseBody.trim();
        } catch (IOException e) {
            throw new RuntimeException(e.toString(), e);
        }

    }

    public String getCurrentVersion() {
        return "1.0.1";
    }

    /**
     * check the last TA version available for download on the website.
     *
     * @return
     */
    public String getLatestAvailableVersion() {
        return lastVersion;
    }

    public static boolean isOutdated(String currentVersion, String lastAvailableVersion) {
        int res = currentVersion.compareTo(lastAvailableVersion);
        return res < 0;
    }


}
