package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import com.taskadapter.model.GTask;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;

import java.util.List;
import java.util.stream.Collectors;

public class MSPToGTask {

    private final ProjectProperties projectProperties;

    public MSPToGTask(ProjectProperties projectProperties) {
        this.projectProperties = projectProperties;
    }

    List<GTask> convertToGenericTaskList(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getName() != null) // skip empty lines in MSP XML files
                .map(task -> convertToGenericTask(task))
                .collect(Collectors.toList());
    }

    private GTask convertToGenericTask(Task task) {
        var genericTask = new GTask();
        genericTask.setValue(AllFields.summary, task.getName());
        genericTask.setId(task.getUniqueID().longValue());
        genericTask.setKey(task.getUniqueID() + "");
        genericTask.setSourceSystemId(new TaskId(task.getUniqueID().longValue(), task.getUniqueID() + ""));
        var parent = task.getParentTask();
        if (parent != null && (parent.getID() != 0) && (parent.getUniqueID() != 0)) {
            genericTask.setParentIdentity(
                    new TaskId(parent.getUniqueID().longValue(), parent.getUniqueID() + ""));
        }
        genericTask.setValue(MspField.priority, task.getPriority().getValue());
        if (task.getWork() != null) {
            genericTask.setValue(MspField.taskWork, convertMspDurationToHours(task.getWork()));
        }
        if (task.getActualWork() != null) {
            genericTask.setValue(MspField.actualWork, convertMspDurationToHours(task.getActualWork()));
        }
        if (task.getDuration() != null) {
            genericTask.setValue(MspField.taskDuration, convertMspDurationToHours(task.getDuration()));
        }
        if (task.getActualDuration() != null) {
            genericTask.setValue(MspField.actualDuration, convertMspDurationToHours(task.getActualDuration()));
        }
        if (task.getPercentageComplete() != null) genericTask.setValue(MspField.percentageComplete,
                task.getPercentageComplete().floatValue());
        // DATES
        var type = task.getConstraintType();

        var constraintDate = task.getConstraintDate();
        switch (type) {
            case START_NO_LATER_THAN -> genericTask.setValue(MspField.startNoLaterThan, constraintDate);
            case START_NO_EARLIER_THAN -> genericTask.setValue(MspField.startNoEarlierThan, constraintDate);
            case MUST_START_ON -> genericTask.setValue(MspField.mustStartOn, constraintDate);
            case AS_SOON_AS_POSSIBLE -> genericTask.setValue(MspField.startAsSoonAsPossible, task.getStart());
            case AS_LATE_AS_POSSIBLE -> genericTask.setValue(MspField.startAsLateAsPossible, task.getStart());
            case MUST_FINISH_ON -> genericTask.setValue(MspField.mustFinishOn, task.getStart());
        }
        genericTask.setValue(MspField.finish, task.getFinish());
        genericTask.setValue(MspField.deadline, task.getDeadline());
        genericTask.setValue(AllFields.assigneeFullName, extractAssignee(task));
        genericTask.setValue(AllFields.description, task.getNotes());
        for (int i = 1; i <= 30; i++) {
            if (task.getText(i) != null) {
                genericTask.setValue(new CustomString("Text" + i), task.getText(i));
            }
        }
        processRelations(task, genericTask);
        return genericTask;
    }

    private void processRelations(Task task, GTask genericTask) {
        var relations = task.getSuccessors();
        if (relations == null) {
            return;
        }

        relations.stream()
                .filter(relation -> relation.getType().equals(RelationType.FINISH_START))
                .forEach(relation -> {
                    var sourceTask = relation.getSourceTask();
                    var targetTask = relation.getTargetTask();
                    var r = new GRelation(
                            new TaskId(sourceTask.getUniqueID().longValue(), sourceTask.getUniqueID() + ""),
                            new TaskId(targetTask.getUniqueID().longValue(), targetTask.getUniqueID() + ""),
                            GRelationType.precedes);
                    genericTask.getRelations().add(r);
                });
    }

    private String extractAssignee(Task task) {
        var r = MSPToGTask.getAssignee(task);
        if (r != null) {
            /*
             * it only makes sense to use the ID if we know it came from us,
             * otherwise we'd try creating tasks in Redmine/Jira/.. using this
             * MSP-specific ID.
             */
            //Integer id = null;
            //if (r.getUniqueID() != null && MSPUtils.isResourceOurs(r)) {       //id = r.getUniqueID();       //}
            return r.getName();
        }
        return null;
    }

    private Float convertMspDurationToHours(Duration mspDuration) {
        var convertedToHoursDuration = mspDuration.convertUnits(TimeUnit.HOURS, projectProperties);
        return (float) convertedToHoursDuration.getDuration();
    }

    /**
     * @return NULL if there's no assignee
     */
    private static Resource getAssignee(Task task) {
        var assignments = task.getResourceAssignments();
        if ((assignments != null) && (assignments.size() > 0)) { // just use the 1st one. see improvement request:
            // https://www.hostedredmine.com/issues/7772
            var ass = assignments.get(0);
            return ass.getResource();
        }
        return null;
    }
}
