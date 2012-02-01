package com.taskadapter.web.configeditor;

/**
 * @author Alexey Skorokhodov
 */

import java.util.Map;

public interface LoadProjectsJobResultListener {
    public void notifyProjectsLoaded(Map<String, String> namesToKeysMap);
}

