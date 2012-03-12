package com.taskadapter.connector.definition;

import com.taskadapter.connector.common.XorUtils;

public class WebServerInfo {
//	private static final String DEFAULT_HOST_VALUE = "http://";

    private String host = "";// = DEFAULT_HOST_VALUE;
    private String userName = "";
    private String password = "";
    private boolean useAPIKeyInsteadOfLoginPassword = false;
    private String apiKey = "";

    public WebServerInfo(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = XorUtils.XORMark + XorUtils.stringXOR.encode (password, XorUtils.XORKey);
    }
    

    public WebServerInfo() {
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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
        if (password.startsWith(XorUtils.XORMark)) {
            return XorUtils.stringXOR.decode (password.substring(1), XorUtils.XORKey);
        }else{
            return password;
        }
    }

    public void setPassword(String password) {
        this.password = password;
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
        return !(host == null || host.isEmpty());
    }
}
