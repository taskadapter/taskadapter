package com.taskadapter.web.service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Information about current user.
 * 
 */
public final class EditableCurrentUserInfo extends CurrentUserInfo {
    private String userName;
    private Collection<LoginEventListener> listeners = new ArrayList<LoginEventListener>();

    @Override
    public boolean isLoggedIn() {
        return userName != null;
    }

    @Override
    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String name) {
        if (this.userName == null && name == null || this.userName != null
                && this.userName.equals(name)) {
            return;
        }
        this.userName = name;
        notifyListeners();
    }

    @Override
    public void addChangeEventListener(LoginEventListener listener) {
        this.listeners.add(listener);
    }

    /* FIXME: TODO !!! This class does not handle listeners addition/removal
     * during a listeners firing. Nobody ever tries to look into a swing/awt 
     * and other core listeners implementations :(
     */
    private void notifyListeners() {
        for (LoginEventListener listener : listeners) {
            listener.userLoginInfoChanged();
        }
    }

}
