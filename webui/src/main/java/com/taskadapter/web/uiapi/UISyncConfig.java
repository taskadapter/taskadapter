package com.taskadapter.web.uiapi;

import com.google.common.base.Throwables;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.MappingBuilder;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.DropInConnector;
import com.taskadapter.connector.definition.ExportDirection;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskKeyMapping;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.core.Updater;
import com.taskadapter.model.GTask;
import com.taskadapter.web.TaskKeeperLocationStorage;
import com.taskadapter.webui.results.ExportResultFormat;
import scala.Option;
import scala.collection.JavaConverters;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UI model for a mapping config. All fields with complex mutable varues are
 * immutable. Simple fields or fields with immutable varues (like a String
 * fields) are mutable in this config.
 * <p>
 * There may be several instances of [[UISyncConfig]] for a same
 * "hard-copy". Moreover, that instances may differs from each other. Users of
 * this class should be aware of this behavior.
 */
public class UISyncConfig {
    private TaskKeeperLocationStorage taskKeeperLocationStorage;

    /**
     * Config identity. Unique "config-storage" id to distinguish between
     * configs. May be <code>null</code> for a new (non-saved) config.
     */
    private ConfigId configId;

    /**
     * Config label
     */
    private String label;

    /**
     * First connector config.
     */
    private UIConnectorConfig connector1;

    /**
     * Second connector config.
     */
    private UIConnectorConfig connector2;

    /**
     * Field mappings. Left side is connector1, right side is connector2.
     */
    private List<FieldMapping<?>> fieldMappings;

    /**
     * "Config is reversed" flag.
     */
    private boolean reversed;

    public UISyncConfig(TaskKeeperLocationStorage taskKeeperLocationStorage,
                        ConfigId configId,
                        String label,
                        UIConnectorConfig connector1,
                        UIConnectorConfig connector2,
                        List<FieldMapping<?>> fieldMappings,
                        boolean reversed) {
        this.taskKeeperLocationStorage = taskKeeperLocationStorage;
        this.configId = configId;
        this.label = label;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.fieldMappings = fieldMappings;
        this.reversed = reversed;
    }

    public TaskKeeperLocationStorage getTaskKeeperLocationStorage() {
        return taskKeeperLocationStorage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ConfigId getConfigId() {
        return configId;
    }

    public UIConnectorConfig getConnector1() {
        return connector1;
    }

    public UIConnectorConfig getConnector2() {
        return connector2;
    }

    public List<FieldMapping<?>> getNewMappings() {
        return fieldMappings;
    }

    public List<FieldMapping<?>> getFieldMappings() {
        return fieldMappings;
    }

    /**
     * Returns name of the user who owns this config.
     */
    public String getOwnerName() {
        return configId.ownerName();
    }

    /**
     * Creates a "reversed" version of a config. Reversed version shares
     * connector configurations and config identity with this config, but have
     * new mapping instance.
     *
     * @return "reversed" (back-order) configuration.
     */
    public UISyncConfig reverse() {
        return new UISyncConfig(taskKeeperLocationStorage, configId, label, connector2, connector1,
                reverse(fieldMappings), !reversed);
    }

    /**
     * Returns a "normalized" (canonical) form of this config.
     *
     * @return normalized (canonical) version of this config.
     */
    public UISyncConfig normalized() {
        if (reversed) {
            return reverse();
        }
        return this;
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

    /**
     * @return number of updated tasks.
     */
    public int updateTasks(List<GTask> selectedTasks, ProgressMonitor progress) throws ConnectorException {
        var updater = makeUpdater();
        updater.setConfirmedTasks(selectedTasks);
        updater.setMonitor(progress);
        updater.loadExternalTasks();
        updater.saveFile();
        return updater.getNumberOfUpdatedTasks();
    }

    private Updater makeUpdater() {
        var sourceConnector = getConnector1().createConnectorInstance();
        var destinationConnector = getConnector2().createConnectorInstance();
        var updater = new Updater(destinationConnector, generateFieldRowsToExportRight(), sourceConnector, getConnector1().getDestinationLocation());
        return updater;
    }

    public List<GTask> loadTasksForUpdate() throws ConnectorException {
        var updater = makeUpdater();
        updater.loadTasks();
        updater.removeTasksWithoutRemoteIds();
        return updater.getExistingTasks();
    }

    public List<GTask> loadDropInTasks(File tempFile, int taskLimit) throws ConnectorException {
        return TaskLoader.loadDropInTasks(taskLimit,
                (DropInConnector) getConnector1().createConnectorInstance(),
                tempFile, ProgressMonitorUtils.DUMMY_MONITOR);
    }

    /**
     * Generates a source mappings. Returned mappings is snapshot of a current
     * state and are not updated when newMappings changes.
     *
     * @return source mappings
     */
    private List<FieldRow<?>> generateFieldRowsToExportLeft() {
        return JavaConverters.seqAsJavaList(MappingBuilder.build(
                JavaConverters.asScalaBuffer(fieldMappings), ExportDirection.LEFT));
    }

    private List<FieldRow<?>> generateFieldRowsToExportRight() {
        return JavaConverters.seqAsJavaList(MappingBuilder.build(
                JavaConverters.asScalaBuffer(fieldMappings), ExportDirection.RIGHT));
    }

    public ExportResultFormat saveTasks(List<GTask> tasks, ProgressMonitor progressMonitor) {
        var start = System.currentTimeMillis();
        var connectorTo = getConnector2().createConnectorInstance();
        var destinationLocation = getConnector2().getDestinationLocation();
        var rows = generateFieldRowsToExportRight();

        var location1 = getConnector1().getSourceLocation();
        var location2 = getConnector2().getSourceLocation();
        var previouslyCreatedTasksResolver = taskKeeperLocationStorage.loadTasks(location1, location2);
        var result = TaskSaver.save(previouslyCreatedTasksResolver, connectorTo, destinationLocation,
                rows, tasks, progressMonitor);
        if (reversed) {
            List<TaskKeyMapping> reversedTuplesList = JavaConverters.seqAsJavaList(result.keyToRemoteKeyList())
                    .stream()
                    .map(pair -> new TaskKeyMapping(pair.newId, pair.originalId))
                    .collect(Collectors.toList());
            taskKeeperLocationStorage.store(location2, location1,
                    JavaConverters.asScalaBuffer(reversedTuplesList)
            );
        } else {
            taskKeeperLocationStorage.store(location1, location2, result.keyToRemoteKeyList());
        }
        var finish = System.currentTimeMillis();

        var resultId = String.valueOf(finish);

        var decodedGeneralErrors = JavaConverters.seqAsJavaList(result.generalErrors())
                .stream()
                .map(e -> getConnector2().decodeException(e))
                .collect(Collectors.toList());

        var decodedTaskErrors = JavaConverters.seqAsJavaList(result.taskErrors())
                .stream()
                .map(e -> new DecodedTaskError(e.getTask().getSourceSystemId(),
                        getConnector2().decodeException(e.getError()),
                        Throwables.getStackTraceAsString(e.getError()))
                )
                .collect(Collectors.toList());

        var finalResult = new ExportResultFormat(resultId, configId, label, getConnector1().getSourceLocation(),
                destinationLocation,
                Option.apply(result.targetFileAbsolutePath()),
                result.updatedTasksNumber(), result.createdTasksNumber(),
                JavaConverters.asScalaBuffer(decodedGeneralErrors),
                JavaConverters.asScalaBuffer(decodedTaskErrors),
                new Date(start),
                (int) ((finish - start) / 1000)
        );
        return finalResult;
    }

    public PreviouslyCreatedTasksResolver getPreviouslyCreatedTasksResolver() {
        var location1 = getConnector1().getSourceLocation();
        var location2 = getConnector2().getSourceLocation();
        return taskKeeperLocationStorage.loadTasks(location1, location2);
    }

    /**
     * Loads tasks from connector1. This is invoked directly from UI layer when user clicks "Go" to load tasks.
     * This method also decorates loaded tasks with "remote Ids" for known tasks (as defined by "tasks cache");
     *
     * @param taskLimit max number of tasks to load.
     * @return loaded tasks.
     */
    public static List<GTask> loadTasks(UISyncConfig config, int taskLimit) throws ConnectorException {
        var loadedTasks = TaskLoader.loadTasks(taskLimit, config.getConnector1().createConnectorInstance(),
                config.getConnector1().getLabel(), ProgressMonitorUtils.DUMMY_MONITOR);
        return loadedTasks;
    }

    public static List<FieldMapping<?>> reverse(List<FieldMapping<?>> fieldMappings) {
        List<FieldMapping<?>> list = fieldMappings
                .stream()
                .map(UISyncConfig::reverse)
                .collect(Collectors.toList());
        return list;
    }

    private static FieldMapping<?> reverse(FieldMapping<?> mapping) {
        return new FieldMapping(mapping.fieldInConnector2(),
                mapping.fieldInConnector1(),
                mapping.selected(),
                mapping.defaultValue());
    }
}
