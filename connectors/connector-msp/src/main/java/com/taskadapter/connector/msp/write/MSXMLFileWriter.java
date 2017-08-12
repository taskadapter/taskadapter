package com.taskadapter.connector.msp.write;

import com.taskadapter.connector.Field;
import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.DefaultValueSetter;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.SaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.GTaskToMSP;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Task;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MSXMLFileWriter {

    private static final String ALIAS_REMOTE_ID = "TA Remote ID";
    private static final String ALIAS_ISSUE_TYPE = "TA Task Type";
    private static final String ALIAS_ISSUE_STATUS = "TA Task Status";
    private static final String ALIAS_TARGET_VERSION = "TA Target Version";
    private static final String ALIAS_ENVIRONMENT = "TA Environment";

    /**
     * MPXJ can't set NULL/undefined time for "duration" or "work" fields so we have to use
     * these text fields to indicate that "duration" or "work" is UNDEFINED and not "0"
     */
    private static final String ALIAS_IS_DURATION_UNDEFINED = "TA isDurationUndefined";

    /**
     * MPXJ can't set NULL/undefined time for "duration" or "work" fields so we have to use
     * these text fields to indicate that "duration" or "work" is UNDEFINED and not "0"
     */
    private static final String ALIAS_IS_WORK_UNDEFINED = "TA isWorkUndefined";
    private final Iterable<FieldRow> rows;

    public MSXMLFileWriter( Iterable<FieldRow> rows) {
        this.rows = rows;
    }

    public String write(String absoluteFilePath, SaveResultBuilder syncResult, List<GTask> tasks,
                        boolean keepTaskId) throws IOException, BadConfigException {

        // XXX load resources from existing MS file to cache here

        ProjectFile project = new ProjectFile();
        project.setAutoTaskID(true);
        project.setAutoTaskUniqueID(!keepTaskId);
        project.setAutoResourceID(true);
        project.setAutoResourceUniqueID(true);
        project.setAutoOutlineLevel(true);
        project.setAutoOutlineNumber(true);

        setAliases(project);
        addTasks(syncResult, project, null, tasks, keepTaskId);

        ProjectHeader header = project.getProjectHeader();
        project.addDefaultBaseCalendar();
        Date earliestTaskDate = DateFinder.findEarliestStartDate(project.getAllTasks());
        if (earliestTaskDate != null) {
            header.setStartDate(earliestTaskDate);
        }
        String taTag = "Project created by Task Adapter. http://www.taskadapter.com";
        /* setComments() does not work with MPXJ 4.0.0 and MS Project 2010 Prof.
          * I sent a bug report to MPXJ developer
          * header.setComments(taTag);
          */
        header.setSubject(taTag);

        return RealWriter.writeProject(absoluteFilePath, project);
    }

    private void addTasks(SaveResultBuilder syncResult, ProjectFile project,
                          Task parentMSPTask,
                          List<GTask> gTasks,
                          boolean keepTaskId) throws BadConfigException {
        for (GTask gTask : gTasks) {
            Task newMspTask;

            if (parentMSPTask == null) {
                newMspTask = project.addTask();
            } else {
                newMspTask = parentMSPTask.addTask();
            }
            GTask transformedTask = DefaultValueSetter.adapt(rows, gTask);
            GTaskToMSP gTaskToMSP = new GTaskToMSP(newMspTask, new ResourceManager(project));
            gTaskToMSP.setFields(transformedTask, keepTaskId);
            syncResult.addCreatedTask(new TaskId(gTask.getId(), gTask.getKey()),
                    new TaskId(newMspTask.getID(), newMspTask.getID() + ""));
            addTasks(syncResult, project, newMspTask, transformedTask.getChildren(), keepTaskId);
        }

    }

    void setAliases(ProjectFile project) {
        project.setTaskFieldAlias(MSPDefaultFields.FIELD_DURATION_UNDEFINED, ALIAS_IS_DURATION_UNDEFINED);
        project.setTaskFieldAlias(MSPDefaultFields.FIELD_WORK_UNDEFINED, ALIAS_IS_WORK_UNDEFINED);

//        setAliasIfMappingNotNULL(project, FIELD.REMOTE_ID, ALIAS_REMOTE_ID);
//        setAliasIfMappingNotNULL(project, Field.taskType, ALIAS_ISSUE_TYPE);
//        setAliasIfMappingNotNULL(project, FIELD.TASK_STATUS, ALIAS_ISSUE_STATUS);
//        setAliasIfMappingNotNULL(project, FIELD.ENVIRONMENT, ALIAS_ENVIRONMENT);
//        setAliasIfMappingNotNULL(project, FIELD.TARGET_VERSION, ALIAS_TARGET_VERSION);
    }

    private void setAliasIfMappingNotNULL(ProjectFile project, Field field, String aliasName) {
        String mspFileFieldName = field.name();
        if (mspFileFieldName != null) {
            /* it is NULL if the old Task Adapter config does not have a mapping for this field.
                * E.g. we added "task type" field in the new TA version and then we try running
                * export using the old config, which does not have "task type" mapped to anything.
                */
            project.setTaskFieldAlias(MSPUtils.getTaskFieldByName(mspFileFieldName), aliasName);
        }
    }
}
