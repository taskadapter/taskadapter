package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import com.taskadapter.model.Precedes$;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import java.util.ArrayList;
import java.util.List;

class MSPToGTask {

    private ProjectHeader header;

    void setHeader(ProjectHeader header) {
        this.header = header;
    }

    List<GTask> convertToGenericTaskList(List<Task> tasks) {
        List<GTask> genericTasks = new ArrayList<>();

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

    GTask convertToGenericTask(Task task) {
        GTask genericTask = new GTask();
//        genericTask.setType(extractField(task, TASK_TYPE));

        genericTask.setValue(MspField.summary(), task.getName());
        genericTask.setId(task.getUniqueID().longValue());
        genericTask.setKey(task.getUniqueID() + "");
        genericTask.setSourceSystemId(task.getUniqueID()+"");

        Task parent = task.getParentTask();
        if (parent != null && parent.getID() != 0 && parent.getUniqueID() != 0) {
            genericTask.setParentIdentity(new TaskId(parent.getUniqueID(), parent.getUniqueID() + ""));
        }

        genericTask.setValue(MspField.priority(), task.getPriority().getValue());

//        genericTask.setValue(MFextractField(task, TASK_STATUS));

        if (task.getWork() != null) {
            genericTask.setValue(MspField.taskWork(), convertMspDurationToHours(task.getWork()));
        }

        if (task.getDuration() != null) {
            genericTask.setValue(MspField.taskDuration(), convertMspDurationToHours(task.getDuration()));
        }
/*
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
        final String dueDateField = mappings.getMappedTo(DUE_DATE);
        if (dueDateField != null) {
            Date mspDueDate = null;
            if (dueDateField.equals(TaskField.FINISH.toString())) {
                mspDueDate = task.getFinish();
            } else if (dueDateField.equals(TaskField.DEADLINE.toString())) {
                mspDueDate = task.getDeadline();
            }
            genericTask.setDueDate(mspDueDate);
        }

*/
//        genericTask.setAssignee(extractAssignee(task));
        genericTask.setValue(MspField.description(), task.getNotes());

        processRelations(task, genericTask);
        return genericTask;
    }
/*

    String extractField(Task task, FIELD field) {
        Object value = getValue(task, field);
        if ((value != null) && !(value.toString().isEmpty())) {
            return value.toString();
        }
        return null;
    }
*/

    private void processRelations(Task task, GTask genericTask) {
        List<Relation> relations = task.getSuccessors();
        if (relations != null) {
            relations.stream()
                    .filter(relation -> relation.getType().equals(RelationType.FINISH_START))
                    .forEach(relation -> {
                        Task sourceTask = relation.getSourceTask();
                        Task targetTask = relation.getTargetTask();
                        GRelation r = new GRelation(
                                new TaskId(sourceTask.getUniqueID(), sourceTask.getUniqueID()+""),
                                new TaskId(targetTask.getUniqueID(), targetTask.getUniqueID()+""),
                                Precedes$.MODULE$);
                        genericTask.getRelations().add(r);
                    });
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

    private Float convertMspDurationToHours(Duration mspDuration) {
        Duration convertedToHoursDuration = mspDuration.convertUnits(TimeUnit.HOURS, header);
        return (float) convertedToHoursDuration.getDuration();
    }

/*    Float extractEstimatedHours(Task task) throws BadConfigException {
        Duration useAsEstimatedTime = null;

        if (MSPUtils.useWork(mappings)) {
            String isUndefinedString = (String) task.getCurrentValue(MSPDefaultFields.FIELD_WORK_UNDEFINED);
            boolean isUndefined = Boolean.parseBoolean(isUndefinedString);
            // this is to differentiate "0" and "undefined". unfortunately, MPXJ does not do this for us.
            if (!isUndefined) {
                useAsEstimatedTime = task.getWork();
            }
        } else {
            String isUndefinedString = (String) task.getCurrentValue(MSPDefaultFields.FIELD_DURATION_UNDEFINED);

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

/*    Object getValue(Task mspTask, FIELD field) {
        String v = mappings.getMappedTo(field);
        if (v != null) {
            TaskField f = MSPUtils.getTaskFieldByName(v);

            return mspTask.getCurrentValue(f);
        }
        return null;
    }*/

}
