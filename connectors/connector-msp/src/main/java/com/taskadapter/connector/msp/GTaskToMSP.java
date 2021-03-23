package com.taskadapter.connector.msp;

import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.connector.msp.write.ResourceManager;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.AssigneeFullName;
import com.taskadapter.model.Description;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import com.taskadapter.model.Summary;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Duration;
import net.sf.mpxj.Priority;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class GTaskToMSP {
    private static Logger log = LoggerFactory.getLogger(GTaskToMSP.class);

    private Task mspTask;
    private ResourceManager resourceManager;

    public GTaskToMSP(Task mspTask, ResourceManager resourceManager) {
        this.mspTask = mspTask;
        this.resourceManager = resourceManager;
    }

    /**
     * "% done" field is used to calculate "actual work". this is more like a
     * hack used until Redmine REST API provides "time spent" serverInfo in
     * "issues list" response (see task http://www.redmine.org/issues/5303 )
     */
    void setFields(GTask gTask, boolean keepTaskId) throws FieldConversionException {
        mspTask.setMilestone(false);
        if (keepTaskId) { // Setting the old unique id, true only in "save external id" operation
            mspTask.setUniqueID(gTask.getId().intValue());
        }
        for (Map.Entry<Field<?>, Object> row : gTask.getFields().entrySet()) {
            try {
                processField(row.getKey(), row.getValue());
            } catch (Exception e) {
                throw new FieldConversionException(MSPConnector.ID, row.getKey(), row.getValue(), e.getMessage());
            }
        }
        // assignee is a special case because updating this field requires information from several fields
        processAssignee(gTask);
    }

    private void processAssignee(GTask gTask) {
        var assigneeFullName = gTask.getValue(AllFields.assigneeFullName);
        var value = CustomFieldConverter.getValueAsString(assigneeFullName).trim();
        var resource = resourceManager.getOrCreateResource(value);
        var ass = mspTask.addResourceAssignment(resource);
        ass.setUnits(100);
        // MUST set the remaining work to avoid this bug:
        //http://www.hostedredmine.com/issues/7780 "Duration" field is ignored when "Assignee" is set
        Float estimatedTime = gTask.getValue(AllFields.estimatedTime);
        if (estimatedTime != null && estimatedTime != 0) {
            ass.setRemainingWork(TimeCalculator.calculateRemainingTime(gTask));
            if (gTask.getValue(AllFields.doneRatio) != 0) {
                var timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask);
                ass.setActualWork(timeAlreadySpent);
            }
        }
    }

    private void processField(Field<?> field, Object value) {
        var stringBasedValue = CustomFieldConverter.getValueAsString(value)
                .trim();
        if (field instanceof Summary) {
            mspTask.setName(stringBasedValue);
            return;
        }
        if (field instanceof Description) {
            mspTask.setNotes(stringBasedValue);
            return;
        }
        if (field instanceof AssigneeFullName) {
            // skip - processed separately because it requires access to several fields
            return;
        }
        if (field.equals(MspField.mustStartOn)) {
            mspTask.setConstraintType(ConstraintType.MUST_START_ON);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.startAsSoonAsPossible)) {
            mspTask.setConstraintType(ConstraintType.AS_SOON_AS_POSSIBLE);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.startAsLateAsPossible)) {
            mspTask.setConstraintType(ConstraintType.AS_LATE_AS_POSSIBLE);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.startNoEarlierThan)) {
            mspTask.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.startNoLaterThan)) {
            mspTask.setConstraintType(ConstraintType.START_NO_LATER_THAN);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.mustFinishOn)) {
            mspTask.setConstraintType(ConstraintType.MUST_FINISH_ON);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.finishNoEarlierThan)) {
            mspTask.setConstraintType(ConstraintType.FINISH_NO_EARLIER_THAN);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.finishNoLaterThan)) {
            mspTask.setConstraintType(ConstraintType.FINISH_NO_LATER_THAN);
            mspTask.setConstraintDate((Date) value);
        }
        if (field.equals(MspField.finish) && value != null) {
            mspTask.setFinish((Date) value);
        }

        if (field.equals(MspField.deadline) && value != null) {
            mspTask.setDeadline((Date) value);
        }

        if (field instanceof com.taskadapter.model.Priority) {
            var priorityInt = value == null ? 0 : (int) value;
            var mspPriority = Priority.getInstance(priorityInt);
            mspTask.setPriority(mspPriority);
        }

        if (field.equals(MspField.taskDuration)) {
            mspTask.setDuration(Duration.getInstance(NumberConverters.getFloatNullSafe(value),
                    TimeUnit.HOURS));
        }

        if (field.equals(MspField.taskWork)) {
            mspTask.setWork(Duration.getInstance(NumberConverters.getFloatNullSafe(value),
                    TimeUnit.HOURS));
        }
        if (field.equals(MspField.actualWork)) {
            mspTask.setActualWork(Duration.getInstance(NumberConverters.getFloatNullSafe(value),
                    TimeUnit.HOURS));
        }
        if (field.equals(MspField.actualDuration)) {
            mspTask.setActualDuration(Duration.getInstance(NumberConverters.getFloatNullSafe(value), TimeUnit.HOURS));
        }
        if (field.equals(MspField.percentageComplete)) {
            mspTask.setPercentageComplete(NumberConverters.getFloatNullSafe(value));
        }
        if (field.equals(MspField.actualFinish)) {
            mspTask.setActualFinish((Date) value);
        }

        if (field.getFieldName().startsWith("Text")) {
            setFieldByName(field, stringBasedValue);
        }
        // ignore the rest of the fields
    }

    private void setFieldByName(Field<?> field, Object value) {
        var f = MSPUtils.getTaskFieldByName(field.getFieldName());
        mspTask.set(f, value);
    }
}
