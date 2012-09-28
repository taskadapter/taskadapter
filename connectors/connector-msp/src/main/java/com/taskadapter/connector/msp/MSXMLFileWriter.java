package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import net.sf.mpxj.*;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.writer.ProjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MSXMLFileWriter {

    private static final String ALIAS_REMOTE_ID = "TA Remote ID";

    private static final String ALIAS_ISSUE_TYPE = "TA Task Type";

    private static final String ALIAS_ISSUE_STATUS = "TA Task Status";

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

    public static final TaskField FIELD_DURATION_UNDEFINED = TaskField.TEXT20;
    public static final TaskField FIELD_WORK_UNDEFINED = TaskField.TEXT21;

    private MSPConfig config;

    public MSXMLFileWriter(MSPConfig config) {
        this.config = config;
    }

    public String write(TaskSaveResultBuilder syncResult, List<GTask> rows,
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
        addTasks(syncResult, project, null, rows, keepTaskId);

        ProjectHeader header = project.getProjectHeader();
        project.addDefaultBaseCalendar();
        Date earliestTaskDate = findEarliestStartDate(rows);
        if (earliestTaskDate != null) {
            header.setStartDate(earliestTaskDate);
        }
        String taTag = "Project created by Task Adapter. http://www.taskadapter.com";
        /* setComments() does not work with MPXJ 4.0.0 and MS Project 2010 Prof.
          * I sent a bug report to MPXJ developer
          * header.setComments(taTag);
          */
        header.setSubject(taTag);

        return writeProject(project);
    }

    private static Date findEarliestStartDate(List<GTask> tasks) {
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 9999);
        Date earliestDate = maxCal.getTime();
        boolean atLeast1StartDateSet = false;
        for (GTask gTask : tasks) {
            if (gTask.getStartDate() != null && earliestDate.after(gTask.getStartDate())) {
                earliestDate = gTask.getStartDate();
                atLeast1StartDateSet = true;
            }
        }
        if (atLeast1StartDateSet) {
            return earliestDate;
        }
        return null;
    }

    private void addTasks(TaskSaveResultBuilder syncResult, ProjectFile project,
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
            setTaskFields(project, newMspTask, gTask, keepTaskId);
            syncResult.addCreatedTask(gTask.getId(), newMspTask.getID() + "");

//            gTask.setId(newMspTask.getID()); // TODO not needed anymore?

            addTasks(syncResult, project, newMspTask, gTask.getChildren(), keepTaskId);
        }

    }

    private Resource getOrCreateResource(ProjectFile project,
                                         GUser assignee) {
        Resource resource = project.getResourceByUniqueID(assignee.getId());
        if (resource == null) {
            // we assume all resources are already in the 'cache' (map)
            resource = project.addResource();
            resource.setName(assignee.getDisplayName());
            resource.setType(ResourceType.WORK);

            if (assignee.getId() != null) {
                resource.setUniqueID(assignee.getId());
            }
            MSPUtils.markResourceAsOurs(resource);
        }
        return resource;
    }

    /**
     * @param project project
     * @return absolute file path
     * @throws java.io.IOException exception
     */
    public String writeProject(ProjectFile project)
            throws IOException {
        String mspFileName = config.getOutputAbsoluteFilePath();
        File folder = new File(mspFileName).getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        File realFile = new File(mspFileName);

        ProjectWriter writer = new MSPDIWriter();
        writer.write(project, realFile);
        return realFile.getAbsolutePath();
    }

    void setAliases(ProjectFile project) {
        project.setTaskFieldAlias(FIELD_DURATION_UNDEFINED, ALIAS_IS_DURATION_UNDEFINED);
        project.setTaskFieldAlias(FIELD_WORK_UNDEFINED, ALIAS_IS_WORK_UNDEFINED);

        setAliasIfMappingNotNULL(project, FIELD.REMOTE_ID, ALIAS_REMOTE_ID);
        setAliasIfMappingNotNULL(project, FIELD.TASK_TYPE, ALIAS_ISSUE_TYPE);
        setAliasIfMappingNotNULL(project, FIELD.TASK_STATUS, ALIAS_ISSUE_STATUS);
    }

    private void setAliasIfMappingNotNULL(ProjectFile project, FIELD field, String aliasName) {
        String mspFileFieldName = config.getFieldMappings().getMappedTo(field);
        if (mspFileFieldName != null) {
            /* it is NULL if the old Task Adapter config does not have a mapping for this field.
                * E.g. we added "task type" field in the new TA version and then we try running
                * export using the old config, which does not have "task type" mapped to anything.
                */
            project.setTaskFieldAlias(MSPUtils.getTaskFieldByName(mspFileFieldName), aliasName);
        }
    }

    /**
     * "% done" field is used to calculate "actual work". this is more like a
     * hack used until Redmine REST API provides "time spent" serverInfo in
     * "issues list" response (see task http://www.redmine.org/issues/5303 )
     * @throws BadConfigException 
     */
    protected void setTaskFields(ProjectFile project, Task mspTask,
                                 GTask gTask, boolean keepTaskId) throws BadConfigException {
    	final Mappings mapping = config.getFieldMappings();
        mspTask.setMilestone(false);

        if (mapping.isFieldSelected(FIELD.SUMMARY)) {
            mspTask.setName(gTask.getSummary());
        }

        if (mapping.isFieldSelected(FIELD.PRIORITY)) {
            Priority mspPriority = Priority.getInstance(gTask.getPriority());
            mspTask.setPriority(mspPriority);
        }

        if (keepTaskId) { // Setting the old unique id, true only in
            // "save external id" operation
            mspTask.setUniqueID(gTask.getId());
        }

        setFieldIfSelected(FIELD.TASK_TYPE, mspTask, gTask.getType());
        setFieldIfSelected(FIELD.TASK_STATUS, mspTask, gTask.getStatus());

        // ESTIMATED TIME and DONE RATIO
        if (gTask.getEstimatedHours() != null && mapping.isFieldSelected(FIELD.ESTIMATED_TIME)) {
            Duration estimatedValue = Duration.getInstance(
                    gTask.getEstimatedHours(), TimeUnit.HOURS);
            if (MSPUtils.useWork(config)) {
                mspTask.setWork(estimatedValue);
                // need to explicitly clear it because we can have previously created
                // tasks with this field set to TRUE
                mspTask.set(FIELD_WORK_UNDEFINED, "false");
            } else {
                // need to explicitly clear it because we can have previously created
                // tasks with this field set to TRUE
                mspTask.setDuration(estimatedValue);
                mspTask.set(FIELD_DURATION_UNDEFINED, "false");
            }

            if (gTask.getDoneRatio() != null && mapping.isFieldSelected(FIELD.DONE_RATIO)) {
                Duration timeAlreadySpent = calculateTimeAlreadySpent(gTask);
                // time already spent
                if (MSPUtils.useWork(config)) {
                    mspTask.setActualWork(timeAlreadySpent);
                    mspTask.setPercentageWorkComplete(gTask.getDoneRatio());
                } else {
                    mspTask.setActualDuration(timeAlreadySpent);
                    mspTask.setPercentageComplete(gTask.getDoneRatio());
                }
            }
        } else {
            mspTask.set(FIELD_DURATION_UNDEFINED, "true");
            mspTask.set(FIELD_WORK_UNDEFINED, "true");
        }

        // ASSIGNEE
        if (mapping.isFieldSelected(FIELD.ASSIGNEE) && gTask.getAssignee() != null) {
            Resource resource = getOrCreateResource(project, gTask.getAssignee());
            ResourceAssignment ass = mspTask.addResourceAssignment(resource);
            ass.setUnits(100);
            /* MUST set the remaining work to avoid this bug:
            * http://www.hostedredmine.com/issues/7780 "Duration" field is ignored when "Assignee" is set
            */
            if (gTask.getEstimatedHours() != null) {
                ass.setRemainingWork(calculateRemainingTime(gTask));

                // the same "IF" as above, now for the resource assignment. this might need refactoring...
                if (gTask.getDoneRatio() != null && mapping.isFieldSelected(FIELD.DONE_RATIO)) {
                    Duration timeAlreadySpent = calculateTimeAlreadySpent(gTask);
                    ass.setActualWork(timeAlreadySpent);
                }
            }
        }


        if (gTask.getPriority() != null) {
            mspTask.setPriority(Priority.getInstance(gTask.getPriority()));
        }

        setFieldIfSelected(FIELD.REMOTE_ID, mspTask, gTask.getRemoteId());

        // START DATE
        if (mapping.isFieldSelected(FIELD.START_DATE)) {
            String constraint = mapping.getMappedTo(FIELD.START_DATE);
            if (constraint == null || MSPUtils.NO_CONSTRAINT.equals(constraint)) {
                mspTask.setStart(gTask.getStartDate());
            } else {
                ConstraintType constraintType = ConstraintType.valueOf(constraint);
                mspTask.setConstraintType(constraintType);
                mspTask.setConstraintDate(gTask.getStartDate());
            }
        }

        // DUE DATE
        if (gTask.getDueDate() != null && mapping.isFieldSelected(FIELD.DUE_DATE)) {
            String dueDateValue = mapping.getMappedTo(FIELD.DUE_DATE);
            if (dueDateValue.equals(TaskField.FINISH.toString())) {
                mspTask.set(TaskField.FINISH, gTask.getDueDate());
            } else if (dueDateValue.equals(TaskField.DEADLINE.toString())) {
                mspTask.set(TaskField.DEADLINE, gTask.getDueDate());
            }
        }

        if (mapping.isFieldSelected(FIELD.DESCRIPTION)) {
            mspTask.setNotes(gTask.getDescription());
        }
    }

    private Duration calculateTimeAlreadySpent(GTask gTask) {
        float doneRatio = gTask.getDoneRatio() / 100f;
        return Duration.getInstance(doneRatio * gTask.getEstimatedHours(),
                TimeUnit.HOURS);
    }

    private Duration calculateRemainingTime(GTask gTask) {
        float doneRatio;
        if (gTask.getDoneRatio() != null) {
            doneRatio = gTask.getDoneRatio() / 100f;
        } else {
            doneRatio = 0;
        }
        return Duration.getInstance((1 - doneRatio) * gTask.getEstimatedHours(),
                TimeUnit.HOURS);
    }

    private void setFieldIfSelected(FIELD field, Task mspTask, Object value) {
        final Mappings mapping = config.getFieldMappings();
		if (mapping.isFieldSelected(field)) {
            String v = mapping.getMappedTo(field);
            TaskField f = MSPUtils.getTaskFieldByName(v);
            mspTask.set(f, value);
        }

    }

}
