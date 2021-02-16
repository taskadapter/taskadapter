package com.taskadapter.model;

import java.util.List;

/**
 * Generic "Project" object. Internal representation for "projects".
 */
public class GProject implements NamedKeyedObject {
    private Long id;
    private String name;
    private String key;
    private String description;
    private String homepage;
    private List<GTask> tasks;

    public String getHomepage() {
        return homepage;
    }

    public GProject setHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    public Long getId() {
        return id;
    }

    public GProject setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GProject setName(String name) {
        this.name = name;
        return this;
    }

    public String getKey() {
        return key;
    }

    public GProject setKey(String key) {
        this.key = key;
        return this;
    }

    public List<GTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<GTask> tasks) {
        this.tasks = tasks;
    }

    public String getDescription() {
        return description;
    }

    public GProject setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((homepage == null) ? 0 : homepage.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
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
        GProject other = (GProject) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (homepage == null) {
            if (other.homepage != null) {
                return false;
            }
        } else if (!homepage.equals(other.homepage)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (tasks == null) {
            if (other.tasks != null) {
                return false;
            }
        } else if (!tasks.equals(other.tasks)) {
            return false;
        }
        return true;
    }

}
