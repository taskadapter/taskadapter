package com.taskadapter.connector.msp;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

class TimeCalculator {

    static Duration calculateTimeAlreadySpent(GTask gTask) {
        float doneRatioInPercents = getFloatNullSafe(gTask.getValue(AllFields.doneRatio));
        float hours = getFloatNullSafe(gTask.getValue(AllFields.estimatedTime));

        var doneRatio = doneRatioInPercents / 100f;

        var duration = doneRatio * hours;
        return Duration.getInstance(duration, TimeUnit.HOURS);
    }

    static Duration calculateRemainingTime(GTask gTask) {
        var timeAlreadySpent = calculateTimeAlreadySpent(gTask);
        float estimatedHours = getFloatNullSafe(gTask.getValue(AllFields.estimatedTime));
        var hoursLeft = estimatedHours - timeAlreadySpent.getDuration();
        return Duration.getInstance(hoursLeft, TimeUnit.HOURS);
    }

    private static float getFloatNullSafe(Float value) {
        return value == null ? 0 : value;
    }
}
