package com.taskadapter.connector.definition;

import java.util.Objects;

public class WebConnectorSetup implements ConnectorSetup {
    private String connectorId;
    private String id;
    private String label;
    private String host;
    private String userName;
    private String password;
    private boolean useApiKey;
    private String apiKey;

    public WebConnectorSetup() {
    }

    /**
     * @param connectorId is used to find all existing setups for, say, JIRA to show on "new config" page.
     */
    public static WebConnectorSetup apply(String connectorId,
                                          String label,
                                          String host,
                                          String userName,
                                          String password,
                                          boolean useApiKey,
                                          String apiKey) {
        return new WebConnectorSetup()
                .setConnectorId(connectorId)
                .setLabel(label)
                .setHost(host)
                .setUserName(userName)
                .setPassword(password)
                .setUseApiKey(useApiKey)
                .setApiKey(apiKey);
    }

    public String getConnectorId() {
        return connectorId;
    }

    public WebConnectorSetup setConnectorId(String connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public String getId() {
        return id;
    }

    public WebConnectorSetup setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public WebConnectorSetup setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getHost() {
        return host;
    }

    public WebConnectorSetup setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public WebConnectorSetup setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public WebConnectorSetup setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isUseApiKey() {
        return useApiKey;
    }

    public WebConnectorSetup setUseApiKey(boolean useApiKey) {
        this.useApiKey = useApiKey;
        return this;
    }

    public String getApiKey() {
        return apiKey;
    }

    public WebConnectorSetup setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebConnectorSetup that = (WebConnectorSetup) o;
        return useApiKey == that.useApiKey && Objects.equals(connectorId, that.connectorId) && Objects.equals(id, that.id) && Objects.equals(label, that.label) && Objects.equals(host, that.host) && Objects.equals(userName, that.userName) && Objects.equals(password, that.password) && Objects.equals(apiKey, that.apiKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, id, label, host, userName, password, useApiKey, apiKey);
    }

    @Override
    public String toString() {
        return "WebConnectorSetup{" +
                "connectorId='" + connectorId + '\'' +
                ", id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", host='" + host + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", useApiKey=" + useApiKey +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
