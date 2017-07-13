package com.taskadapter.connector.msp.write;

import org.junit.Test;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import static org.junit.Assert.*;
import static com.taskadapter.connector.msp.write.TaskEstimationMode.*;

public final class TaskFieldSetterTest {

    // TODO TA3 MSP tests

    /**
     * Tests estimation mode setting for a MSP task.
     * <p>
     * Following terms are used in assertions:
     * <ul>
     * <li><strong>Forced</strong> estimation is an estimation which must have a
     * defined (non-null) values. It is usually required to make some other
     * fields work. For example, done ratio does not work when estimated time is
     * not set. So, usage of "done ratio" forces estimation time to be set.
     * <li><strong>Explicit</strong> estimation is an estimation requested by a
     * user. This estimation may be not defined (if it is absent in a source
     * task).
     * </ul>
     */
/*    @Test
    public void testEstimationMode() {

        Mappings mappings = new Mappings();
        GTask task = new GTask();

        assertEquals("Estimation is not required", NO_ESTIMATE,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(22.3f);
        assertEquals("Estimation is not required", NO_ESTIMATE,
                getTaskEstimationMode(task, mappings));

        task = new GTask();
        mappings.setMapping(FIELD.ESTIMATED_TIME, true, "test", "default time");
        assertEquals("Explicit estimation must not be forced", NO_ESTIMATE,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(33.5f);
        assertEquals("Explicit estimation should be used", TASK_TIME,
                getTaskEstimationMode(task, mappings));


         * Check if estimated time is estimated when DONE_RATIO is required. MSP
         * ignores DONE_RATIO if estimated time is not set. So, usage of
         * estimated time FORCES estimated time to be set.
         *
        task = new GTask();
        mappings = new Mappings();
        mappings.setMapping(FIELD.DONE_RATIO, true, "test", "default done ratio");
        assertEquals("Done ratio is not set, no forcing", NO_ESTIMATE,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(33f);
        assertEquals("Done ratio is not set (but ET is), no forcing",
                NO_ESTIMATE, getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(null);
        task.setDoneRatio(33);
        assertEquals("Done ratio is set, ET is forced", WILD_GUESS,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(22.33f);
        assertEquals("Done ratio is set, ET is forced, use GTask value",
                TASK_TIME, getTaskEstimationMode(task, mappings));

        */
/*
         * Check estimation when both DONE_RATIO and ET is set for export. Done
         * ratio FORCES ET to be set even this value is absent in a task. See
         * also a previous comment.
         *//*

        task = new GTask();
        mappings = new Mappings();
        mappings.setMapping(FIELD.DONE_RATIO, true, "test", "default done ratio");
        mappings.setMapping(FIELD.ESTIMATED_TIME, true, "text", "default estimated time");

        assertEquals("Done ratio not set, no forcing", NO_ESTIMATE,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(1f);
        assertEquals("Done ratio is not set, use explicit ET", TASK_TIME,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(null);
        task.setDoneRatio(99);
        assertEquals("Done ratio is set, ET is forced", WILD_GUESS,
                getTaskEstimationMode(task, mappings));
        task.setEstimatedHours(1E-3F);
        assertEquals("Done ratio is set, ET is forced (use explicit value)",
                TASK_TIME, getTaskEstimationMode(task, mappings));
    }
*/
}
