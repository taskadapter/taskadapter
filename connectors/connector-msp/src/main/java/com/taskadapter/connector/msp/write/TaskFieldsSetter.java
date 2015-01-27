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

import static com.taskadapter.model.GTaskDescriptor.FIELD.*;

public class TaskFieldsSetter {
    private Mappings mappings;
    private Task mspTask;
    private ResourceManager resourceManager;
    private static final float DEFAULT_HOURS_FOR_NONESTIMATED_TASK = 8;

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
        processEstimatedTime(gTask);
        processDoneRatio(gTask);
        processAssignee(gTask);
        processPriority(gTask);
        processKey(gTask);
        processStartDate(gTask);
        processDueDate(gTask);
        processClosedDate(gTask);
        setFieldIfSelected(ENVIRONMENT, mspTask, gTask.getEnvironment());
        setFieldIfSelected(TARGET_VERSION, mspTask, gTask.getTargetVersionName());
    }

    private void processKey(GTask gTask) {
        setFieldIfSelected(REMOTE_ID, mspTask, gTask.getKey());
    }

    private void processStatus(GTask gTask) {
        setFieldIfSelected(TASK_STATUS, mspTask, gTask.getStatus());
    }

    private void processType(GTask gTask) {
        setFieldIfSelected(TASK_TYPE, mspTask, gTask.getType());
    }

    private void processSummary(GTask gTask) {
        if (mappings.isFieldSelected(SUMMARY)) {
            mspTask.setName(gTask.getSummary());
        }
    }

    private void processPriority(GTask gTask) {
        if (mappings.isFieldSelected(PRIORITY)) {
            Priority mspPriority = Priority.getInstance(gTask.getPriority());
            mspTask.setPriority(mspPriority);
        }
    }

    private void processDescription(GTask gTask) {
        if (mappings.isFieldSelected(DESCRIPTION)) {
            mspTask.setNotes(gTask.getDescription());
        }
    }

    private void processDueDate(GTask gTask) {
        if (gTask.getDueDate() != null && mappings.isFieldSelected(DUE_DATE)) {
            String dueDateValue = mappings.getMappedTo(DUE_DATE);
            if (dueDateValue.equals(TaskField.FINISH.toString())) {
                mspTask.set(TaskField.FINISH, gTask.getDueDate());
            } else if (dueDateValue.equals(TaskField.DEADLINE.toString())) {
                mspTask.set(TaskField.DEADLINE, gTask.getDueDate());
            }
        }
    }

    private void processStartDate(GTask gTask) {
        if (mappings.isFieldSelected(START_DATE)) {
            String constraint = mappings.getMappedTo(START_DATE);
            if (constraint == null || MSPUtils.NO_CONSTRAINT.equals(constraint)) {
                mspTask.setStart(gTask.getStartDate());
            } else {
                ConstraintType constraintType = ConstraintType.valueOf(constraint);
                mspTask.setConstraintType(constraintType);
                mspTask.setConstraintDate(gTask.getStartDate());
            }
        }
    }

    private void processClosedDate(GTask gTask) {
        if (mappings.isFieldSelected(CLOSE_DATE)) {
            String constraint = mappings.getMappedTo(CLOSE_DATE);
            if (TaskField.ACTUAL_FINISH.getName().equals(constraint)) {
                mspTask.setActualFinish(gTask.getClosedDate());
            }
        }
    }
    
    private void processAssignee(GTask gTask) {
        if (mappings.isFieldSelected(ASSIGNEE) && gTask.getAssignee() != null) {
            Resource resource = resourceManager.getOrCreateResource(gTask.getAssignee());
            ResourceAssignment ass = mspTask.addResourceAssignment(resource);
            ass.setUnits(100);
            /* MUST set the remaining work to avoid this bug:
            * http://www.hostedredmine.com/issues/7780 "Duration" field is ignored when "Assignee" is set
            */
            if (gTask.getEstimatedHours() != null) {
                ass.setRemainingWork(TimeCalculator.calculateRemainingTime(gTask));

                // the same "IF" as above, now for the resource assignment. this might need refactoring...
                if (gTask.getDoneRatio() != null && mappings.isFieldSelected(DONE_RATIO)) {
                    Duration timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask.getDoneRatio(), gTask.getEstimatedHours());
                    ass.setActualWork(timeAlreadySpent);
                }
            }
        }
    }

    private void processEstimatedTime(GTask gTask) throws BadConfigException {
        final Float estimatedTime = calculateTaskEstimatedTime(gTask);
        switch (getTaskEstimationMode(gTask)) {
        case TASK_TIME:
            setEstimatedHours(estimatedTime);
            break;
        case WILD_GUESS:
            setEstimatedHours(estimatedTime);
            // "estimated" means that the time was "approximate". it is shown by MSP as "?" next to the duration value.
            // like "8 hrs?"
            mspTask.setEstimated(true);
            break;
        case NO_ESTIMATE:
            mspTask.set(MSPDefaultFields.FIELD_DURATION_UNDEFINED, "true");
            mspTask.set(MSPDefaultFields.FIELD_WORK_UNDEFINED, "true");
            break;
        }
    }
    
    /**
     * Calculates a task estimated time. In some cases (for example, exporting
     * DONE_RATIO but no estimated time is set) we still need to use some
     * estimation. This method respects that cases and can provide general task
     * estimation. If task estimation is not required, returns null.
     * 
     * @param gTask
     *            task to estimate it time.
     * @return <code>null</code> if task does not require an estimated time.
     *         Otherwise estimated (or guessed) task time.
     */
    private Float calculateTaskEstimatedTime(GTask gTask)
            throws BadConfigException {
        final TaskEstimationMode estimationMode = getTaskEstimationMode(gTask);
        switch (estimationMode) {
        case TASK_TIME:
            return gTask.getEstimatedHours();
        case WILD_GUESS:
            return DEFAULT_HOURS_FOR_NONESTIMATED_TASK;
        case NO_ESTIMATE:
            return null;
        }
        throw new IncompatibleClassChangeError("Bad/unsupported estimation mode " + estimationMode);
    }

    /* Simple closure to call static calculator with proper arguments */
    private TaskEstimationMode getTaskEstimationMode(GTask gTask) throws BadConfigException {
        return getTaskEstimationMode(gTask, mappings);
    }

    static TaskEstimationMode getTaskEstimationMode(GTask gTask, final Mappings mappings) {
        /* Normal case, time is mapped and set. */
        if (gTask.getEstimatedHours() != null && mappings.isFieldSelected(ESTIMATED_TIME))
            return TaskEstimationMode.TASK_TIME;
        
        // "%% Done" is ignored by MSP if there's no estimate on task.
        // This makes sense, but unfortunately some users want "% done" to be transferred even when
        // there's no time estimate.
        if (mappings.isFieldSelected(DONE_RATIO) && gTask.getDoneRatio() != null) {
            /* Estimation time is set. Use it even if user does not ask to 
             * map estimated time. It is still more reasonable than 
             * "wild guess" estimation. */
            if (gTask.getEstimatedHours() != null)
                return TaskEstimationMode.TASK_TIME;
            /* We need some estimation, let's just guess it. */
            return TaskEstimationMode.WILD_GUESS;
        }
        return TaskEstimationMode.NO_ESTIMATE;
    }

    private void setEstimatedHours(float hours) throws BadConfigException {
        Duration estimatedValue = Duration.getInstance(hours, TimeUnit.HOURS);
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

        // processDoneRatio() logic used to be called from here until http://www.hostedredmine.com/issues/199534
        // was requested by Matt.
        // now I moved the call to the main setFields() so that Done Ratio is always set even if the estimated time is null.
    }

    private void processDoneRatio(GTask gTask) throws BadConfigException {
        if (gTask.getDoneRatio() != null && mappings.isFieldSelected(DONE_RATIO)) {
            final Float estimatedTime = calculateTaskEstimatedTime(gTask);
            final Duration timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask.getDoneRatio(), estimatedTime);
            if (MSPUtils.useWork(mappings)) {
                mspTask.setActualWork(timeAlreadySpent);
            } else {
                mspTask.setActualDuration(timeAlreadySpent);
            }
            mspTask.setPercentageComplete(gTask.getDoneRatio());
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
