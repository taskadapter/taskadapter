package com.taskadapter.connector.msp;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeCalculatorTest {
    // time already spent
    @Test
    public void zeroOnEmptyTask() {
        timeAlreadySpent(new GTask(), 0);
    }

    @Test
    public void zeroOnProvidedDoneRatioButEmptyEstimatedTime() {
        timeAlreadySpent(new GTask().setValue(AllFields.doneRatio, 10f),
                0);
    }

    @Test
    public void zeroOnProvidedTimeAndEmptyDoneRatio() {
        timeAlreadySpent(new GTask().setValue(AllFields.estimatedTime, 10f),
                0);
    }

    @Test
    public void is3point5hWhenEstimatedTime7hAndDoneRatio50percent() {
        timeAlreadySpent(new GTask()
                        .setValue(AllFields.estimatedTime, 7f)
                        .setValue(AllFields.doneRatio, 50f),
                3.5F);
    }

    @Test
    public void is5hWhenEstimatedTime5hAndDoneRatio100percent() {
        timeAlreadySpent(new GTask()
                        .setValue(AllFields.estimatedTime, 5f)
                        .setValue(AllFields.doneRatio, 100f),
                5);
    }

    @Test
    public void is0WhenEstimatedTime5hAndDoneRatio0percent() {
        timeAlreadySpent(new GTask()
                        .setValue(AllFields.estimatedTime, 5f)
                        .setValue(AllFields.doneRatio, 0f),
                0);
    }

    // remaining time
    @Test
    public void isZeroOnEmptyTask() {
        remainingTime(new GTask(), 0);
    }

    @Test
    public void isZeroOnProvidedDoneRatioButEmptyEstimatedTime() {
        remainingTime(new GTask().setValue(AllFields.doneRatio, 10f),
                0);
    }

    @Test
    public void is10WhenEstimatedTime10hAndEmptyDoneRatio() {
        remainingTime(new GTask().setValue(AllFields.estimatedTime, 10F),
                10);
    }

    @Test
    public void is5hWhenEstimatedTime5hAndDoneRatio0percent() {
        remainingTime(new GTask()
                        .setValue(AllFields.estimatedTime, 5f)
                        .setValue(AllFields.doneRatio, 0f),
                5);
    }

    @Test
    public void is2point5hWhenEstimatedTime10hAandDoneRatio75percent() {
        remainingTime(new GTask()
                        .setValue(AllFields.estimatedTime, 10f)
                        .setValue(AllFields.doneRatio, 75f),
                2.5F);
    }

    private static void timeAlreadySpent(GTask task, float value) {
        assertThat(TimeCalculator.calculateTimeAlreadySpent(task)
                .getDuration()).isEqualTo(value);
    }

    private static void remainingTime(GTask task, float value) {
        assertThat(TimeCalculator.calculateRemainingTime(task)
                .getDuration()).isEqualTo(value);
    }
}
