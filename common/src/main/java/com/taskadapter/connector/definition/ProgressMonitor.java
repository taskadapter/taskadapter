package com.taskadapter.connector.definition;

/**
 * This mimics the Eclipse IProgressMonitor. The goal of having this class is to get rid of Eclipse UI dependencies
 * for Data Connectors.
 *
 * @author Alexey Skorokhodov
 */
public interface ProgressMonitor {
    void beginTask(String taskName, int total);

    void worked(int work);

    void done();

    void stopTask();

    boolean isStopped();
}
