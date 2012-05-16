package com.taskadapter.web.service;

public class SessionInfo {
    private boolean requestCameFromLocalhost;

    public boolean isRequestCameFromLocalhost() {
        return requestCameFromLocalhost;
    }

    public void setRequestCameFromLocalhost(boolean requestCameFromLocalhost) {
        this.requestCameFromLocalhost = requestCameFromLocalhost;
    }
}
