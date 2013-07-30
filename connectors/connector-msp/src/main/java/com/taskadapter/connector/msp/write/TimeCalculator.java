package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;
import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public class TimeCalculator {
    static Duration calculateTimeAlreadySpent(float doneRatioInPercents, float hours) {
        float doneRatio = doneRatioInPercents / 100f;
        double duration = doneRatio * hours;
        return Duration.getInstance(duration, TimeUnit.HOURS);
    }

    static Duration calculateRemainingTime(GTask gTask) {
        float doneRatio;
        if (gTask.getDoneRatio() != null) {
            doneRatio = gTask.getDoneRatio() / 100f;
        } else {
            doneRatio = 0;
        }
        return Duration.getInstance((1 - doneRatio) * gTask.getEstimatedHours(),
                TimeUnit.HOURS);
    }
}
