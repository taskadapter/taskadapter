package com.taskadapter.web.uiapi;

import java.util.Objects;

/**
 * Class to uniquely identify a config in the store
 */
public class ConfigId {
    private final String ownerName;
    private final Integer id;

    /**
     * @param ownerName login name of the owner
     * @param id        numeric identifier
     */
    public ConfigId(String ownerName, Integer id) {
        this.ownerName = ownerName;
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigId configId = (ConfigId) o;
        return Objects.equals(ownerName, configId.ownerName) && Objects.equals(id, configId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerName, id);
    }
}
