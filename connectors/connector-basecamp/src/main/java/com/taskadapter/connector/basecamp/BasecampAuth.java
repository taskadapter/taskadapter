package com.taskadapter.connector.basecamp;

/**
 * Basecamp authentication data.
 * TODO: this class can be used by other connectors as well.
 */
public class BasecampAuth {

    private String login = "";
    private String password = "";
    private String apiKey = "";

    private boolean useAPIKeyInsteadOfLoginPassword = true;

    public boolean isUseAPIKeyInsteadOfLoginPassword() {
        return useAPIKeyInsteadOfLoginPassword;
    }

    public void setUseAPIKeyInsteadOfLoginPassword(boolean useAPIKeyInsteadOfLoginPassword) {
        this.useAPIKeyInsteadOfLoginPassword = useAPIKeyInsteadOfLoginPassword;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
