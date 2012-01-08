package com.taskadapter.connector.redmine;

import org.redmine.ta.beans.Project;

/**
 * @author Alexey Skorokhodov
 */
// TODO rename the class, its name is TOO simlar to LoadProjectsJobResultListener
// TODO ?? make it generic (independent from Redmine Project), move to webshared - configeditor package
public interface LoadProjectJobResultListener {
    public void notifyProjectLoaded(Project project);
}
