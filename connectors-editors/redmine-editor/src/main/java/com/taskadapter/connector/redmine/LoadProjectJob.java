package com.taskadapter.connector.redmine;

import org.redmine.ta.RedmineManager;
import org.redmine.ta.beans.Project;

/**
 * @author Alexey Skorokhodov
 */
public class LoadProjectJob {

    private LoadProjectJobResultListener resultsListener;
    private RedmineManager mgr;
    private String projectKey;
    private Project project = null;

    public LoadProjectJob(LoadProjectJobResultListener resultsListener,
                          RedmineManager mgr, String projectKey) {
        this.resultsListener = resultsListener;
        this.mgr = mgr;
        this.projectKey = projectKey;
    }

    public void run() {
        try {
            this.project = mgr.getProjectByKey(projectKey);
        } catch (final Exception e) {
            System.out.println(e);
        }
        resultsListener.notifyProjectLoaded(project);
    }

}