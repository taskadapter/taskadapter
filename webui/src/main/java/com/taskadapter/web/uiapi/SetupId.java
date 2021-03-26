package com.taskadapter.web.uiapi;

import java.util.Objects;

/**
 * Class to uniquely identify a saved setup (like web server url, credentials, ...) in the store
 */
public class SetupId {

    final private String id;

    /**
     * * @param id string-based identifier. this is currently equal to file name where setup is stored.
     */
    public SetupId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetupId setupId = (SetupId) o;
        return Objects.equals(id, setupId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
