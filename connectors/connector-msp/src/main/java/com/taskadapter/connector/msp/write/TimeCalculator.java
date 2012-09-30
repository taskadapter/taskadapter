package com.taskadapter.connector.msp.write;

import com.taskadapter.model.GTask;
import net.sf.mpxj.Duration;
import net.sf.mpxj.TimeUnit;

public class TimeCalculator {
    static Duration calculateTimeAlreadySpent(GTask gTask) {
        float doneRatio = gTask.getDoneRatio() / 100f;
        return Duration.getInstance(doneRatio * gTask.getEstimatedHours(),
                TimeUnit.HOURS);
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
