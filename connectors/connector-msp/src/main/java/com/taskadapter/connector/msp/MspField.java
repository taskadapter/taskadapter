package com.taskadapter.connector.msp;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomDate;
import com.taskadapter.model.CustomFloat;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;
import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.TaskField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MspField {
    public static final Field<Date> closedOn = new CustomDate(TaskField.ACTUAL_FINISH.getName());
    public static final Field<Integer> priority = AllFields.priority;
    public static final Field<Float> percentageComplete = AllFields.doneRatio;
    public static final CustomFloat taskDuration = new CustomFloat(TaskField.DURATION.getName());
    public static final CustomFloat taskWork = new CustomFloat(TaskField.WORK.getName());

    public static final CustomFloat actualWork = new CustomFloat(TaskField.ACTUAL_WORK.getName());
    public static final CustomFloat actualDuration = new CustomFloat(TaskField.ACTUAL_DURATION.getName());
    public static final Field<Date> actualFinish = new CustomDate(TaskField.ACTUAL_FINISH.getName());

    public static final Field<Date> startAsSoonAsPossible = new CustomDate(ConstraintType.AS_SOON_AS_POSSIBLE.name());
    public static final Field<Date> startAsLateAsPossible = new CustomDate(ConstraintType.AS_LATE_AS_POSSIBLE.name());
    public static final Field<Date> mustStartOn = new CustomDate(ConstraintType.MUST_START_ON.name());
    public static final Field<Date> mustFinishOn = new CustomDate(ConstraintType.MUST_FINISH_ON.name());
    public static final Field<Date> startNoEarlierThan = new CustomDate(ConstraintType.START_NO_EARLIER_THAN.name());
    public static final Field<Date> startNoLaterThan = new CustomDate(ConstraintType.START_NO_LATER_THAN.name());
    public static final Field<Date> finishNoEarlierThan = new CustomDate(ConstraintType.FINISH_NO_EARLIER_THAN.name());
    public static final Field<Date> finishNoLaterThan = new CustomDate(ConstraintType.FINISH_NO_LATER_THAN.name());

    public static final Field<Date> finish = new CustomDate(TaskField.FINISH.name());
    public static final Field<Date> deadline = new CustomDate(TaskField.DEADLINE.name());

    public static final List<Field<?>> textFields;
    public static final List<Field<?>> fields;

    static {
        textFields = IntStream.rangeClosed(1, 30).mapToObj(i -> new CustomString("Text" + i)).collect(Collectors.toList());

        ArrayList<Field<?>> tmp = new ArrayList<>(List.of(actualDuration, actualWork, actualFinish,
                AllFields.summary,
                AllFields.description,
                AllFields.assigneeFullName,
                closedOn,
                finishNoEarlierThan,
                finishNoLaterThan,
                mustStartOn, mustFinishOn,
                priority, percentageComplete,
                startNoEarlierThan,
                startNoLaterThan,
                startAsSoonAsPossible,
                startAsLateAsPossible,
                taskDuration, taskWork,
                finish, deadline
        ));
        tmp.addAll(textFields);
        fields = tmp;
    }
}
