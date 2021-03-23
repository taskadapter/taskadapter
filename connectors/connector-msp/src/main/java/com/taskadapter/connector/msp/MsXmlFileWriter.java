package com.taskadapter.connector.msp;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.msp.write.DateFinder;
import com.taskadapter.connector.msp.write.MSPDefaultFields;
import com.taskadapter.connector.msp.write.RealWriter;
import com.taskadapter.connector.msp.write.ResourceManager;
import com.taskadapter.model.DefaultValueSetter;
import com.taskadapter.model.GTask;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;

import java.io.IOException;
import java.util.List;

public class MsXmlFileWriter {
//    private static final String ALIAS_REMOTE_ID = "TA Remote ID";
//    private static final String ALIAS_ISSUE_TYPE = "TA Task Type";
//    private static final String ALIAS_ISSUE_STATUS = "TA Task Status";
//    private static final String ALIAS_TARGET_VERSION = "TA Target Version";
//    private static final String ALIAS_ENVIRONMENT = "TA Environment";
    /**
     * MPXJ can't set NULL/undefined time for "duration" or "work" fields so we have to use
     * these text fields to indicate that "duration" or "work" is UNDEFINED and not "0"
     */
    private static final String ALIAS_IS_DURATION_UNDEFINED = "TA isDurationUndefined";
    private static final String ALIAS_IS_WORK_UNDEFINED = "TA isWorkUndefined";

    private Iterable<FieldRow<?>> rows;

    public MsXmlFileWriter(Iterable<FieldRow<?>> rows) {
        this.rows = rows;
    }

    public String write(String absoluteFilePath, SaveResultBuilder syncResult, List<GTask> tasks,
                        boolean keepTaskId) throws IOException, BadConfigException { // XXX load resources from existing MS file to cache here
        var project = new ProjectFile();
        var config = project.getProjectConfig();
        config.setAutoTaskID(true);
        config.setAutoTaskUniqueID(!keepTaskId);
        config.setAutoResourceID(true);
        config.setAutoResourceUniqueID(true);
        config.setAutoOutlineLevel(true);
        config.setAutoOutlineNumber(true);
        setAliases(project);
        addTasks(syncResult, project, null, tasks, keepTaskId);
        var properties = project.getProjectProperties();
        project.addDefaultBaseCalendar();
        var earliestTaskDate = DateFinder.findEarliestStartDate(project.getTasks());
        if (earliestTaskDate != null) properties.setStartDate(earliestTaskDate);
        var taTag = "Project created by Task Adapter. http://www.taskadapter.com";
        /* setComments() does not work with MPXJ 4.0.0 and MS Project 2010 Prof.
         * I sent a bug report to MPXJ developer
         * properties.setComments(taTag);
         */
        properties.setSubject(taTag);
        return RealWriter.writeProject(absoluteFilePath, project);
    }

    private void addTasks(SaveResultBuilder syncResult,
                          ProjectFile project,
                          Task parentMSPTask,
                          List<GTask> gTasks,
                          boolean keepTaskId) throws BadConfigException {
        for (GTask gTask : gTasks) {
            try {
                var newMspTask = parentMSPTask == null ? project.addTask()
                        : parentMSPTask.addTask();

                var transformedTask = DefaultValueSetter.adapt(rows, gTask);
                var gTaskToMSP = new GTaskToMSP(newMspTask, new ResourceManager(project));
                gTaskToMSP.setFields(transformedTask, keepTaskId);
                syncResult.addCreatedTask(new TaskId(gTask.getId(), gTask.getKey()),
                        new TaskId(newMspTask.getID().longValue(), newMspTask.getID() + ""));
                addTasks(syncResult, project, newMspTask, transformedTask.getChildren(), keepTaskId);
            } catch (ConnectorException e) {
                syncResult.addTaskError(gTask, e);
            } catch (Exception t) {
                syncResult.addTaskError(gTask, t);
                t.printStackTrace();
            }
        }
    }

    private void setAliases(ProjectFile project) {
        var fields = project.getCustomFields();
        fields.getCustomField(MSPDefaultFields.FIELD_DURATION_UNDEFINED).setAlias(ALIAS_IS_DURATION_UNDEFINED);
        fields.getCustomField(MSPDefaultFields.FIELD_WORK_UNDEFINED).setAlias(ALIAS_IS_WORK_UNDEFINED);
        //        setAliasIfMappingNotNULL(project, FIELD.REMOTE_ID, ALIAS_REMOTE_ID);
        //        setAliasIfMappingNotNULL(project, Field.taskType, ALIAS_ISSUE_TYPE);
        //        setAliasIfMappingNotNULL(project, FIELD.TASK_STATUS, ALIAS_ISSUE_STATUS);
        //        setAliasIfMappingNotNULL(project, FIELD.ENVIRONMENT, ALIAS_ENVIRONMENT);
        //        setAliasIfMappingNotNULL(project, FIELD.TARGET_VERSION, ALIAS_TARGET_VERSION);
    }

//  private def setAliasIfMappingNotNULL(fieldsContainer: CustomFieldContainer, field: Field<_>, aliasName: String): Unit = {
//    var mspFileFieldName = field.name
//    if (mspFileFieldName != null) {
//      /* it is NULL if the old Task Adapter config does not have a mapping for this field.
//                     * E.g. we added "task type" field in the new TA version and then we try running
//                     * export using the old config, which does not have "task type" mapped to anything.
//                     */
//      fieldsContainer.getCustomField(MSPUtils.getTaskFieldByName(mspFileFieldName)).setAlias(aliasName)
//    }
//  }
}
