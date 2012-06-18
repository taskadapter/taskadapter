package com.taskadapter.connector.redmine;

import com.taskadapter.redmineapi.bean.Project;

// TODO rename the class, its name is TOO simlar to LoadProjectsJobResultListener
// TODO ?? make it generic (independent from Redmine Project), move to webshared - configeditor package
public interface LoadProjectJobResultListener {
    public void notifyProjectLoaded(Project project);
}
