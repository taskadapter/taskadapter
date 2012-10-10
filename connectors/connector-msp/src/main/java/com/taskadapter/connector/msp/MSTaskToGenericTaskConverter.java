package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.write.MSXMLFileWriter;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import net.sf.mpxj.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class MSTaskToGenericTaskConverter {

    private ProjectHeader header;
    private Mappings mappings;

    MSTaskToGenericTaskConverter(Mappings mappings) {
        this.mappings = mappings;
    }

    public void setHeader(ProjectHeader header) {
        this.header = header;
    }

    public List<GTask> convertToGenericTaskList(List<Task> tasks) throws BadConfigException {
        List<GTask> genericTasks = new ArrayList<GTask>();

        for (Task task : tasks) {
            if (task.getName() == null) {
                // skip empty lines in MSP XML files
                continue;
            }
            GTask genericTask = convertToGenericTask(task);
            genericTasks.add(genericTask);
        }
        return genericTasks;
    }

    public GTask convertToGenericTask(Task task) throws BadConfigException {
        GTask genericTask = new GTask();
        genericTask.setType(extractType(task));

        genericTask.setSummary(task.getName());
        genericTask.setId(task.getUniqueID());
        // TODO Add test for this
        genericTask.setKey(task.getUniqueID() + "");

        Task parent = task.getParentTask();
        if (parent != null) {
            genericTask.setParentKey(parent.getUniqueID() + "");
        }

        genericTask.setRemoteId(extractRemoteId(task));

        genericTask.setPriority(task.getPriority().getValue());

        genericTask.setStatus(extractStatus(task));

        genericTask.setEstimatedHours(extractEstimatedHours(task));
        if (task.getPercentageComplete() != null) {
            genericTask.setDoneRatio(task.getPercentageComplete().intValue());
        }

        // DATES
        ConstraintType type = task.getConstraintType();
        if (type != null
                && ((type.equals(ConstraintType.START_NO_LATER_THAN) || (type.
                equals(ConstraintType.MUST_START_ON))))) {
            genericTask.setStartDate(task.getConstraintDate());
        } else {
            genericTask.setStartDate(task.getStart());
        }

        // DUE DATE
		final String dueDateField = mappings.getMappedTo(GTaskDescriptor.FIELD.DUE_DATE);
        if (dueDateField != null) {
            Date mspDueDate = null;
            if (dueDateField.equals(TaskField.FINISH.toString())) {
                mspDueDate = task.getFinish();
            } else if (dueDateField.equals(TaskField.DEADLINE.toString())) {
                mspDueDate = task.getDeadline();
            }
            genericTask.setDueDate(mspDueDate);
        }

        genericTask.setAssignee(extractAssignee(task));
        genericTask.setDescription(task.getNotes());

        processRelations(task, genericTask);
        return genericTask;
    }

    private void processRelations(Task task, GTask genericTask) {
        List<Relation> relations = task.getSuccessors();
        if (relations != null) {
            for (Relation relation : relations) {
                if (relation.getType().equals(RelationType.FINISH_START)) {
                    GRelation r = new GRelation(Integer.toString(relation
                            .getSourceTask().getUniqueID()),
                            Integer.toString(relation.getTargetTask()
                                    .getUniqueID()), GRelation.TYPE.precedes);
                    genericTask.getRelations().add(r);
                }
            }
        }
    }

    GUser extractAssignee(Task task) {
        GUser genericAssignee = new GUser();
        Resource r = getAssignee(task);
        if (r != null) {
            /*
                * it only makes sense to use the ID if we know it came from us,
                * otherwise we'd try creating tasks in Redmine/Jira/.. using this
                * MSP-specific ID.
                */
            if (r.getUniqueID() != null && MSPUtils.isResourceOurs(r)) {
                genericAssignee.setId(r.getUniqueID());
            }
            genericAssignee.setDisplayName(r.getName());
            return genericAssignee;
        }
        return null;
    }

    /**
     * @return NULL if there's no assignee
     */
    private static Resource getAssignee(Task task) {
        List<ResourceAssignment> assignments = task.getResourceAssignments();
        if ((assignments != null) && (assignments.size() > 0)) {
            // just use the 1st one. see improvement request:
            // https://www.hostedredmine.com/issues/7772
            ResourceAssignment ass = assignments.get(0);
            return ass.getResource();
        }
        return null;
    }

    Float extractEstimatedHours(Task task) throws BadConfigException {
        Duration useAsEstimatedTime = null;

        if (MSPUtils.useWork(mappings)) {
            String isUndefinedString = (String) task.getCurrentValue(MSXMLFileWriter.FIELD_WORK_UNDEFINED);
            boolean isUndefined = Boolean.parseBoolean(isUndefinedString);
            // this is to differentiate "0" and "undefined". unfortunately, MPXJ does not do this for us.
            if (!isUndefined) {
                useAsEstimatedTime = task.getWork();
            }
        } else {
            String isUndefinedString = (String) task.getCurrentValue(MSXMLFileWriter.FIELD_DURATION_UNDEFINED);

            boolean isUndefined = Boolean.parseBoolean(isUndefinedString);
            // this is to differentiate "0" and "undefined". unfortunately, MPXJ does not do this for us.
            if (!isUndefined) {
                useAsEstimatedTime = task.getDuration();
            }
        }

        if (useAsEstimatedTime != null) {
            Duration convertedToHoursDuration = useAsEstimatedTime.convertUnits(TimeUnit.HOURS, header);
            return (float) convertedToHoursDuration.getDuration();
        }

        return null;
    }

    String extractRemoteId(Task mspTask) {
        Object remoteIdObj = getValue(mspTask, FIELD.REMOTE_ID);
        if ((remoteIdObj != null) && !(remoteIdObj.toString().isEmpty())) {
            return remoteIdObj.toString();
        }
        return null;
    }

    Object getValue(Task mspTask, FIELD field) {
        String v = mappings.getMappedTo(field);
        if (v != null) {
            TaskField f = MSPUtils.getTaskFieldByName(v);

            return mspTask.getCurrentValue(f);
        }
        return null;
    }

    String extractType(Task mspTask) {
        // GTask TYPE: bug/task/feature/review/... (using custom field from MSP file)
        Object obj = getValue(mspTask, FIELD.TASK_TYPE);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    String extractStatus(Task mspTask) {
        // GTask STATUS: new/assigned/resolved/... (using custom field from MSP file)
        Object obj = getValue(mspTask, FIELD.TASK_STATUS);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }
}
