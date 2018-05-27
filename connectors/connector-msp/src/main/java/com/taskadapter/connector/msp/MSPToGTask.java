package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Description$;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Precedes$;
import com.taskadapter.model.Summary$;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import java.util.ArrayList;
import java.util.List;

class MSPToGTask {

    private ProjectProperties projectProperties;

    MSPToGTask(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
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

    private GTask convertToGenericTask(Task task) {
        GTask genericTask = new GTask();

        genericTask.setValue(Summary$.MODULE$, task.getName());
        genericTask.setId(task.getUniqueID().longValue());
        genericTask.setKey(task.getUniqueID() + "");
        genericTask.setSourceSystemId(new TaskId(task.getUniqueID(), task.getUniqueID() + ""));

        Task parent = task.getParentTask();
        if (parent != null && parent.getID() != 0 && parent.getUniqueID() != 0) {
            genericTask.setParentIdentity(new TaskId(parent.getUniqueID(), parent.getUniqueID() + ""));
        }

        genericTask.setValue(MspField.priority(), task.getPriority().getValue());

        if (task.getWork() != null) {
            genericTask.setValue(MspField.taskWork(), convertMspDurationToHours(task.getWork()));
        }
        if (task.getActualWork() != null) {
            genericTask.setValue(MspField.actualWork(), convertMspDurationToHours(task.getActualWork()));
        }

        if (task.getDuration() != null) {
            genericTask.setValue(MspField.taskDuration(), convertMspDurationToHours(task.getDuration()));
        }
        if (task.getActualDuration() != null) {
            genericTask.setValue(MspField.actualDuration(), convertMspDurationToHours(task.getActualDuration()));
        }

        if (task.getPercentageComplete() != null) {
            genericTask.setValue(MspField.percentageComplete(), task.getPercentageComplete().intValue());
        }

        // DATES
        ConstraintType type = task.getConstraintType();
        if (ConstraintType.START_NO_LATER_THAN.equals(type)) {
            genericTask.setValue(MspField.startNoLaterThan(), task.getConstraintDate());
        } else if (ConstraintType.START_NO_EARLIER_THAN.equals(type)) {
            genericTask.setValue(MspField.startNoEarlierThan(), task.getConstraintDate());
        } else if (ConstraintType.MUST_START_ON.equals(type)) {
            genericTask.setValue(MspField.mustStartOn(), task.getConstraintDate());
        } else if (ConstraintType.AS_SOON_AS_POSSIBLE.equals(type)) {
            genericTask.setValue(MspField.startAsSoonAsPossible(), task.getStart());
        } else if (ConstraintType.AS_LATE_AS_POSSIBLE.equals(type)) {
            genericTask.setValue(MspField.startAsLateAsPossible(), task.getStart());
        } else if (ConstraintType.MUST_FINISH_ON.equals(type)) {
            genericTask.setValue(MspField.mustFinishOn(), task.getStart());
        }
        genericTask.setValue(MspField.finish(), task.getFinish());
        genericTask.setValue(MspField.deadline(), task.getDeadline());

        genericTask.setValue(AssigneeFullName$.MODULE$, extractAssignee(task));
        genericTask.setValue(Description$.MODULE$, task.getNotes());

        for (int i = 1; i <= 30; i++) {
            if (task.getText(i) != null) {
                genericTask.setValue(new CustomString("Text" + i), task.getText(i));
            }
        }

        processRelations(task, genericTask);
        return genericTask;
    }

    private void processRelations(Task task, GTask genericTask) {
        List<Relation> relations = task.getSuccessors();
        if (relations != null) {
            relations.stream()
                    .filter(relation -> relation.getType().equals(RelationType.FINISH_START))
                    .forEach(relation -> {
                        Task sourceTask = relation.getSourceTask();
                        Task targetTask = relation.getTargetTask();
                        GRelation r = new GRelation(
                                new TaskId(sourceTask.getUniqueID(), sourceTask.getUniqueID() + ""),
                                new TaskId(targetTask.getUniqueID(), targetTask.getUniqueID() + ""),
                                Precedes$.MODULE$);
                        genericTask.getRelations().add(r);
                    });
        }
    }

    private String extractAssignee(Task task) {
        Resource r = getAssignee(task);
        if (r != null) {
            /*
                * it only makes sense to use the ID if we know it came from us,
                * otherwise we'd try creating tasks in Redmine/Jira/.. using this
                * MSP-specific ID.
                */
//            Integer id = null;
//            if (r.getUniqueID() != null && MSPUtils.isResourceOurs(r)) {
//                id = r.getUniqueID();
//            }
            return r.getName();
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
        Duration convertedToHoursDuration = mspDuration.convertUnits(TimeUnit.HOURS, projectProperties);
        return (float) convertedToHoursDuration.getDuration();
    }
}
