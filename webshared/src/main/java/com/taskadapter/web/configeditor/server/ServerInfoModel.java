package com.taskadapter.web.configeditor.server;

import java.util.List;

/**
 * Server information model. Provides information for server completion.
 * 
 */
public interface ServerInfoModel {
    /**
     * Returns all available serer URLs.
     * 
     * @return all available serer URLs.
     */
    public List<String> getServers();

    /**
     * Returns logins for a specified server.
     * 
     * @param url
     *            server URL.
     * @return server login.
     */
    public List<String> getLogins(String url);
    
    /**
     * Retreive any possible logins.
     * @return any possible logins.
     */
    public List<String> getLogins();

    /**
     * Returns password for a specified server and login. If there is no a
     * single password for a given combination, returns null.
     * 
     * @param url
     *            server URL.
     * @param login
     *            server login.
     * @return single unique password for a given server.
     */
    public String getPassword(String url, String login);
    
    
    /**
     * Returns password for a specified login. If there is no a
     * single password for a given user, returns null.
     * 
     * @param login
     *            user login.
     * @return single unique password for a given server.
     */
    public String getPassword(String login);
}
