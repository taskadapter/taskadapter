package com.taskadapter.connector.definition;

/**
 * This mimics the Eclipse IProgressMonitor. The goal of having this class is to get rid of Eclipse UI dependencies
 * for Data Connectors.
 *
 * @author Alexey Skorokhodov
 */
public interface ProgressMonitor {
    public void beginTask(String taskName, int total);

    public void worked(int work);

    public void done();
}
