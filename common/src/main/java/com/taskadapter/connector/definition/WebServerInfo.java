package com.taskadapter.connector.definition;

import com.google.common.base.Strings;
import com.taskadapter.connector.common.Encryptor;
import com.taskadapter.connector.common.XorEncryptor;

public class WebServerInfo {

    public static final String DEFAULT_URL_PREFIX = "http://";

    private String label = "";
    private String host = "";
    private String userName = "";
    protected String password = "";

    private boolean useAPIKeyInsteadOfLoginPassword = false;
    private String apiKey = "";

    private transient Encryptor encryptor = new XorEncryptor();

    public WebServerInfo(String label, String host, String userName, String password, String apiKey) {
        this.label = label;
        this.host = host;
        this.userName = userName;
        this.password = encryptor.encrypt(password);
        this.apiKey = encryptor.encrypt(apiKey);
    }

    public WebServerInfo(String host, String userName, String password) {
        this("", host, userName, password, "");
    }

    public WebServerInfo() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return encryptor.decrypt(password);
    }

    public void setPassword(String newPassword) {
        this.password = encryptor.encrypt(newPassword);
    }

    public boolean isUseAPIKeyInsteadOfLoginPassword() {
        return useAPIKeyInsteadOfLoginPassword;
    }

    public void setUseAPIKeyInsteadOfLoginPassword(
            boolean useAPIKeyInsteadOfLoginPassword) {
        this.useAPIKeyInsteadOfLoginPassword = useAPIKeyInsteadOfLoginPassword;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apiKey == null) ? 0 : apiKey.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result
                + ((password == null) ? 0 : password.hashCode());
        result = prime * result
                + (useAPIKeyInsteadOfLoginPassword ? 1231 : 1237);
        result = prime * result
                + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WebServerInfo other = (WebServerInfo) obj;
        if (apiKey == null) {
            if (other.apiKey != null) {
                return false;
            }
        } else if (!apiKey.equals(other.apiKey)) {
            return false;
        }
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (useAPIKeyInsteadOfLoginPassword != other.useAPIKeyInsteadOfLoginPassword) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        return true;
    }

    public boolean isHostSet() {
        return (host != null && (!host.isEmpty()) && !host.equalsIgnoreCase(DEFAULT_URL_PREFIX));
    }

    /**
     * @return empty string if all is valid
     */
    public String validate() {
        if (Strings.isNullOrEmpty(label)) {
            return "Name is required";
        }
        if (!isHostSet()) {
            return "Host is required";
        }
        return "";
    }

    @Override
    public String toString() {
        return "WebServerInfo{" +
                "host='" + host + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
