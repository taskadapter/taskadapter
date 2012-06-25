package com.taskadapter.web.service;

import com.taskadapter.web.AppInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class UpdateManager {

    private static final String VERSION_PROPERTIES_FILE_NAME = "/version.properties";
    private final Logger logger = LoggerFactory.getLogger(UpdateManager.class);

    private String lastVersion;
    private AppInfo appInfo = new AppInfo();

    public UpdateManager() {
        loadAppInfo();

// disabled for now to not generate a lot of useless "check version" requests to the server.
// we'll enable before the release.
//    loadLastVersion();
    }

    private void loadAppInfo() {
        Properties properties = new Properties();
        try {
            properties.load(UpdateManager.class.getResourceAsStream(VERSION_PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            logger.error("Error loading " + VERSION_PROPERTIES_FILE_NAME + ": " + e.getMessage(), e);
        }
        appInfo.setVersion(properties.getProperty("version"));
        appInfo.setBuildDate(properties.getProperty("buildDate"));
    }

/*    private void loadLastVersion() {
        try {
            Properties properties = new Properties();
            properties.load(MyIOUtils.getResourceAsStream("taskadapter.properties"));
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
  */

    public String getCurrentVersion() {
        return appInfo.getVersion();
    }

    /**
     * check the last TA version available for download on the website.
     */
    public String getLatestAvailableVersion() {
        return lastVersion;
    }

    public boolean isCurrentVersionOutdated() {
        int res = getCurrentVersion().compareTo(getLatestAvailableVersion());
        return res < 0;
    }

    void setCurrentVersionForTesting(String currentVersion) {
        this.appInfo.setVersion(currentVersion);
    }

    void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }
}
