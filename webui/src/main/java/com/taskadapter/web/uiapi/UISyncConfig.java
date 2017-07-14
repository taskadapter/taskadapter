package com.taskadapter.web.uiapi;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.MappingBuilder;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.RemoteIdUpdater;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.core.Updater;
import com.taskadapter.model.GTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * UI model for a mapping config. All fields with complex mutable values are
 * immutable. Simple fields or fields with immutable values (like a String
 * fields) are mutable in this config.
 * <p>
 * There may be several instances of {@link UISyncConfig} for a same
 * "hard-copy". Moreover, that instances may differs from each other. Users of
 * this class should be aware of this behavior.
 * 
 */
public final class UISyncConfig {

    public static class TaskExportResult {
        public final TaskSaveResult saveResult;
        public final Exception remoteIdUpdateException;

        public TaskExportResult(TaskSaveResult saveResult,
                Exception remoteIdUpdateException) {
            this.saveResult = saveResult;
            this.remoteIdUpdateException = remoteIdUpdateException;
        }
    }

    /**
     * Config identity. Unique "config-storage" id to distinguish between
     * configs. May be <code>null</code> for a new (non-saved) config.
     */
    private final String identity;

    /** Name of the user who owns this config. */
    private final String owner;

    /**
     * Config label
     */
    private String label;

    /**
     * First connector config.
     */
    private final UIConnectorConfig connector1;

    /**
     * Second connector config.
     */
    private final UIConnectorConfig connector2;

    /**
     * Field mappings. Left side is connector1, right size is connector2.
     */
    private final List<FieldMapping> fieldMappings;

    /**
     * "Config is reversed" flag.
     */
    private boolean reversed;

    UISyncConfig(String identity, String owner, String label,
            UIConnectorConfig connector1, UIConnectorConfig connector2,
            List<FieldMapping> fieldMappings, boolean reversed) {
        this.identity = identity;
        this.owner = owner;
        this.label = label;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.fieldMappings = fieldMappings;
        this.reversed = reversed;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public UIConnectorConfig getConnector1() {
        return connector1;
    }

    public UIConnectorConfig getConnector2() {
        return connector2;
    }

    public List<FieldMapping> getNewMappings() {
        return fieldMappings;
    }

    /** Returns name of the user who owns this config. */
    public String getOwnerName() {
        return owner;
    }

    String getIdentity() {
        return identity;
    }

    /**
     * Creates a "reversed" version of a config. Reversed version shares
     * connector configurations and config identity with this config, but have
     * new mapping instance.
     * 
     * @return "reversed" (back-order) configuration.
     */
    public UISyncConfig reverse() {
        return new UISyncConfig(identity, owner, label, connector2, connector1,
                reverse(fieldMappings), !reversed);
    }

    /**
     * Returns a "normalized" (canonical) form of this config.
     * 
     * @return normalized (canonical) version of this config.
     */
    public UISyncConfig normalized() {
        return reversed ? reverse() : this;
    }

    /**
     * Checks, if config is reversed (i.e. connector1/connector2 are opposite
     * from a stored data).
     * 
     * @return <code>true</code> iff config is reversed.
     */
    public boolean isReversed() {
        return reversed;
    }

    private static List<FieldMapping> reverse(List<FieldMapping> fieldMappings) {
        List<FieldMapping> result = new ArrayList<>();
        for (FieldMapping mapping : fieldMappings) {
            result.add(reverse(mapping));
        }
        return result;
    }

    private static FieldMapping reverse(FieldMapping mapping) {
        return new FieldMapping(mapping.fieldInConnector2(),
                mapping.fieldInConnector1(), mapping.selected(), mapping.defaultValue());
    }

    /**
     * Generates a source mappings. Returned mappings is snapshot of a current
     * state and are not updated when newMappings changes.
     * 
     * @return source mappings
     */
    Iterable<FieldRow> generateFieldRowsToExportLeft() {
        return MappingBuilder.build(fieldMappings, ExportDirection.LEFT);
    }

    Iterable<FieldRow> generateFieldRowsToExportRight() {
        return MappingBuilder.build(fieldMappings, ExportDirection.RIGHT);
    }

    /**
     * Loads tasks from connector1. This is invoked directly from UI layer when user clicks "Go" to load tasks.
     * 
     * @param taskLimit number of tasks to load.
     * @return loaded tasks.
     */
    public List<GTask> loadTasks(int taskLimit) throws ConnectorException {
        return TaskLoader.loadTasks(taskLimit, getConnector1().createConnectorInstance(), getConnector1().getLabel(),
                ProgressMonitorUtils.DUMMY_MONITOR);
    }

    public List<GTask> loadDropInTasks(File tempFile, int taskLimit)
            throws ConnectorException {
        // TODO TA3 drag-n-drop
        throw new RuntimeException("not implemented");
/*
        return TaskLoader.loadDropInTasks(taskLimit,
                (DropInConnector) getConnector1().createConnectorInstance(),
                tempFile, generateFieldRowsToExportLeft(),
                ProgressMonitorUtils.DUMMY_MONITOR);
*/
    }

    public TaskExportResult saveTasks(List<GTask> tasks, ProgressMonitor progress) {
        final NewConnector connectorInstance = getConnector2().createConnectorInstance();
        final String destinationLocation = getConnector2().getDestinationLocation();
        final Iterable<FieldRow> rows = generateFieldRowsToExportRight();
        final TaskSaveResult result = TaskSaver.save(connectorInstance, destinationLocation, rows, tasks, progress);
        try {
            RemoteIdUpdater.updateRemoteIds(result.getIdToRemoteKeyMap(),
                    getConnector1().createConnectorInstance());
            return new TaskExportResult(result, null);
        } catch (ConnectorException e) {
            return new TaskExportResult(result, e);
        }

    }

    public TaskExportResult onlySaveTasks(List<GTask> tasks,
            ProgressMonitor progress) {
        final TaskSaveResult result = TaskSaver.save(getConnector2()
                .createConnectorInstance(), getConnector2()
                .getDestinationLocation(), generateFieldRowsToExportRight(), tasks,
                progress);
        return new TaskExportResult(result, null);
    }

    /**
     * Loads tasks for update.
     * 
     * @return list of tasks to update.
     * @throws ConnectorException
     *             if something goes wrong.
     */
    public List<GTask> loadTasksForUpdate()
            throws ConnectorException {
        final Updater updater = makeUpdater();
        updater.loadTasks();
        updater.removeTasksWithoutRemoteIds();
        return updater.getExistingTasks();
    }

    private Updater makeUpdater() {
        final NewConnector sourceConnector = getConnector1()
                .createConnectorInstance();
        final NewConnector destinationConnector = getConnector2()
                .createConnectorInstance();
        final Updater updater = new Updater(destinationConnector,
                generateFieldRowsToExportRight(), sourceConnector,
                getConnector1().getDestinationLocation());
        return updater;
    }

    /**
     * Updates tasks.
     * 
     * @param selectedTasks
     *            tasks to update.
     * @param progress
     *            progress monitor.
     * @return number of updated tasks.
     * @throws ConnectorException
     *             if something goes wrong.
     */
    public int updateTasks(List<GTask> selectedTasks, ProgressMonitor progress)
            throws ConnectorException {
        final Updater updater = makeUpdater();
        updater.setConfirmedTasks(selectedTasks);
        updater.setMonitor(progress);
        updater.loadExternalTasks();
        updater.saveFile();
        return updater.getNumberOfUpdatedTasks();
    }

}
