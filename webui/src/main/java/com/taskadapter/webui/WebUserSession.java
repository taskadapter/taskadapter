package com.taskadapter.webui;

import java.util.Optional;

public class WebUserSession {

    Optional<String> currentLoginName = Optional.empty();

    public Optional<String> getCurrentUserName() {
        return currentLoginName;
    }

    public WebUserSession setCurrentUserName(String loginName) {
        this.currentLoginName = Optional.of(loginName);
        return this;
    }

    public void clear() {
        currentLoginName = Optional.empty();
    }
}
