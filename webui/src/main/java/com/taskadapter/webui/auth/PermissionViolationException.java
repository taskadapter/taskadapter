package com.taskadapter.webui.auth;

import java.util.Arrays;

public class PermissionViolationException extends RuntimeException {
    public PermissionViolationException(String... permissions) {
        String.format("One of '%s' permissions is required to proceed.", Arrays.toString(permissions));
    }
}
