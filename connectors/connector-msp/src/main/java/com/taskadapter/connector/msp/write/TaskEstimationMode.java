package com.taskadapter.connector.msp.write;

/**
 * Task estimation mode. Describes how to estimate a task time.
 */
public enum TaskEstimationMode {
    /**
     * Use GTask.estimatedTime as time estimation.
     */
    TASK_TIME, 
    /**
     * Guess estimation based on a task structure but not on explicitly set
     * estimated time.
     * Task estimator may use any of the following (but not limited to) 
     * approaches:
     * <ul>
     *   <li>Use fixed some default value
     *   <li>Use value configured by a user
     *   <li>Estimate time using estimated time for subtasks.
     * </ul>
     */
    WILD_GUESS,     
    /**
     * Task requires no estimated time.
     */
    NO_ESTIMATE
}
