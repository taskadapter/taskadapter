package com.taskadapter.model;

/**
 * Generic User.
 *
 * @author Alexey Skorokhodov
 */
public class GUser {

    /**
     * Database ID.
     */
    private Integer id;

    /**
     * String-based User Identifier (aka "login" in web-based systems)
     */
    private String loginName;

    /**
     * Usually the Full Name of the User.
     */
    private String displayName;

    /**
     * @param loginName the string-based user identifier aka "login" in web-based systems
     */
    public GUser(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @param id        database ID
     * @param loginName the string-based user identifier aka "login" in web-based systems
     */
    public GUser(Integer id, String loginName) {
        this.id = id;
        this.loginName = loginName;
    }

    public GUser() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((displayName == null) ? 0 : displayName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((loginName == null) ? 0 : loginName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GUser other = (GUser) obj;
        if (displayName == null) {
            if (other.displayName != null)
                return false;
        } else if (!displayName.equals(other.displayName))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (loginName == null) {
            if (other.loginName != null)
                return false;
        } else if (!loginName.equals(other.loginName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GUser [id=" + id + ", loginName=" + loginName
                + ", displayName=" + displayName + "]";
    }

}
