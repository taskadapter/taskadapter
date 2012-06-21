package com.taskadapter.web.service;

public class UpdateManager {

    private String lastVersion;
    private String currentVersion;

    public UpdateManager() {
        // TODO hardcoded current version
        currentVersion = "2.0.2";

// disabled for now to not generate a lot of useless "check version" requests to the server.
// we'll enable before the release.
//    loadLastVersion();
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
        return currentVersion;
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
        this.currentVersion = currentVersion;
    }

    void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }
}
