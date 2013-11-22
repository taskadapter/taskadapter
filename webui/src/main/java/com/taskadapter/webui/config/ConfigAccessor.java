package com.taskadapter.webui.config;

import java.util.List;

import com.taskadapter.web.uiapi.UISyncConfig;

/** Accessor to configurations. */
public interface ConfigAccessor {

    /** Returns display name of the given config. */
    public String nameOf(UISyncConfig config);

    /** Returns all configs available to users. */
    public List<UISyncConfig> getConfigs();
}
