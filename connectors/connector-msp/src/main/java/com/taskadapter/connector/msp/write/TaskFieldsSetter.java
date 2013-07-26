package com.taskadapter.connector.msp.write;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.msp.MSPUtils;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.Priority;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskField;
import net.sf.mpxj.TimeUnit;

public class TaskFieldsSetter {
    private Mappings mappings;
    private Task mspTask;
    private ResourceManager resourceManager;

    public TaskFieldsSetter(Mappings mappings, Task mspTask, ResourceManager resourceManager) {
        this.mappings = mappings;
        this.mspTask = mspTask;
        this.resourceManager = resourceManager;
    }

    /**
     * "% done" field is used to calculate "actual work". this is more like a
     * hack used until Redmine REST API provides "time spent" serverInfo in
     * "issues list" response (see task http://www.redmine.org/issues/5303 )
     *
     * @throws com.taskadapter.connector.definition.exceptions.BadConfigException
     */
    // TODO should not throw the exception because the config should have been verified by now.
    public void setFields(GTask gTask, boolean keepTaskId) throws BadConfigException {
        mspTask.setMilestone(false);

        // TODO eliminate the boolean argument. split this function in two.
        if (keepTaskId) {
            // Setting the old unique id, true only in "save external id" operation
            mspTask.setUniqueID(gTask.getId());
        }

        processSummary(gTask);
        processDescription(gTask);
        processType(gTask);
        processStatus(gTask);
        processTime(gTask);
        processAssignee(gTask);
        processPriority(gTask);
        processKey(gTask);
        processStartDate(gTask);
        processDueDate(gTask);
    }

    private void processKey(GTask gTask) {
        setFieldIfSelected(GTaskDescriptor.FIELD.REMOTE_ID, mspTask, gTask.getKey());
    }

    private void processStatus(GTask gTask) {
        setFieldIfSelected(GTaskDescriptor.FIELD.TASK_STATUS, mspTask, gTask.getStatus());
    }

    private void processType(GTask gTask) {
        setFieldIfSelected(GTaskDescriptor.FIELD.TASK_TYPE, mspTask, gTask.getType());
    }

    private void processSummary(GTask gTask) {
        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.SUMMARY)) {
            mspTask.setName(gTask.getSummary());
        }
    }

    private void processPriority(GTask gTask) {
        // TODO Bug!!!
        if (gTask.getPriority() != null) {
            mspTask.setPriority(Priority.getInstance(gTask.getPriority()));
        }

        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.PRIORITY)) {
            Priority mspPriority = Priority.getInstance(gTask.getPriority());
            mspTask.setPriority(mspPriority);
        }

    }

    private void processDescription(GTask gTask) {
        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.DESCRIPTION)) {
            mspTask.setNotes(gTask.getDescription());
        }
    }

    private void processDueDate(GTask gTask) {
        // DUE DATE
        if (gTask.getDueDate() != null && mappings.isFieldSelected(GTaskDescriptor.FIELD.DUE_DATE)) {
            String dueDateValue = mappings.getMappedTo(GTaskDescriptor.FIELD.DUE_DATE);
            if (dueDateValue.equals(TaskField.FINISH.toString())) {
                mspTask.set(TaskField.FINISH, gTask.getDueDate());
            } else if (dueDateValue.equals(TaskField.DEADLINE.toString())) {
                mspTask.set(TaskField.DEADLINE, gTask.getDueDate());
            }
        }
    }

    private void processStartDate(GTask gTask) {
        // START DATE
        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.START_DATE)) {
            String constraint = mappings.getMappedTo(GTaskDescriptor.FIELD.START_DATE);
            if (constraint == null || MSPUtils.NO_CONSTRAINT.equals(constraint)) {
                mspTask.setStart(gTask.getStartDate());
            } else {
                ConstraintType constraintType = ConstraintType.valueOf(constraint);
                mspTask.setConstraintType(constraintType);
                mspTask.setConstraintDate(gTask.getStartDate());
            }
        }
    }

    private void processAssignee(GTask gTask) {
        // ASSIGNEE
        if (mappings.isFieldSelected(GTaskDescriptor.FIELD.ASSIGNEE) && gTask.getAssignee() != null) {
            Resource resource = resourceManager.getOrCreateResource(gTask.getAssignee());
            ResourceAssignment ass = mspTask.addResourceAssignment(resource);
            ass.setUnits(100);
            /* MUST set the remaining work to avoid this bug:
            * http://www.hostedredmine.com/issues/7780 "Duration" field is ignored when "Assignee" is set
            */
            if (gTask.getEstimatedHours() != null) {
                ass.setRemainingWork(TimeCalculator.calculateRemainingTime(gTask));

                // the same "IF" as above, now for the resource assignment. this might need refactoring...
                if (gTask.getDoneRatio() != null && mappings.isFieldSelected(GTaskDescriptor.FIELD.DONE_RATIO)) {
                    Duration timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask);
                    ass.setActualWork(timeAlreadySpent);
                }
            }
        }
    }

    private void processTime(GTask gTask) throws BadConfigException {
        // ESTIMATED TIME and DONE RATIO
        if (gTask.getEstimatedHours() != null && mappings.isFieldSelected(GTaskDescriptor.FIELD.ESTIMATED_TIME)) {
            Duration estimatedValue = Duration.getInstance(
                    gTask.getEstimatedHours(), TimeUnit.HOURS);
            if (MSPUtils.useWork(mappings)) {
                mspTask.setWork(estimatedValue);
                // need to explicitly clear it because we can have previously created
                // tasks with this field set to TRUE
                mspTask.set(MSPDefaultFields.FIELD_WORK_UNDEFINED, "false");
            } else {
                // need to explicitly clear it because we can have previously created
                // tasks with this field set to TRUE
                mspTask.setDuration(estimatedValue);
                mspTask.set(MSPDefaultFields.FIELD_DURATION_UNDEFINED, "false");
            }

            if (gTask.getDoneRatio() != null && mappings.isFieldSelected(GTaskDescriptor.FIELD.DONE_RATIO)) {
                Duration timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask);
                // time already spent
                if (MSPUtils.useWork(mappings)) {
                    mspTask.setActualWork(timeAlreadySpent);
                    mspTask.setPercentageWorkComplete(gTask.getDoneRatio());
                } else {
                    mspTask.setActualDuration(timeAlreadySpent);
                    mspTask.setPercentageComplete(gTask.getDoneRatio());
                }
            }
        } else {
            mspTask.set(MSPDefaultFields.FIELD_DURATION_UNDEFINED, "true");
            mspTask.set(MSPDefaultFields.FIELD_WORK_UNDEFINED, "true");
        }
    }

    private void setFieldIfSelected(GTaskDescriptor.FIELD field, Task mspTask, Object value) {
        if (mappings.isFieldSelected(field)) {
            String v = mappings.getMappedTo(field);
            TaskField f = MSPUtils.getTaskFieldByName(v);
            mspTask.set(f, value);
        }

    }
}
