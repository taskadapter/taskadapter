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
        WebConnectorSetup setup = new WebConnectorSetup();
        setup.setConnectorId(connectorId);
        setup.setLabel(label);
        setup.setHost(host);
        setup.setPassword(password);
        setup.setUseApiKey(useApiKey);
        setup.setApiKey(apiKey);
        setup.setUserName(userName);
        return setup;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseApiKey() {
        return useApiKey;
    }

    public void setUseApiKey(boolean useApiKey) {
        this.useApiKey = useApiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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
                ", password=<REDACTED>'" +
                ", useApiKey=" + useApiKey +
                ", apiKey=<REDACTED>" +
                '}';
    }
}
