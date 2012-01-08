package com.taskadapter.connector;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PrioritiesTest {
    @Test
    public void defaultPriorityNumberForUnknownText() {
        assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, getSamplePriorities().getPriorityByText("unknown value"));
    }

    @Test
    public void priorityTextFoundByExactNumber() {
        assertEquals("High", getSamplePriorities().getPriorityByMSP(700));
    }

    @Test
    public void priorityNameFoundInRange() {
        assertEquals("Normal", getSamplePriorities().getPriorityByMSP(400));
    }

    @Test
    public void lowestPriorityFoundForNegativeNumber() {
        assertEquals("Low", getSamplePriorities().getPriorityByMSP(-1));
    }

    @Test
    public void highestPriorityFoundForLargeNumber() {
        assertEquals("Immediate", getSamplePriorities().getPriorityByMSP(99999));
    }
    private Priorities getSamplePriorities() {
        return new Priorities(new HashMap<String, Integer>() {
            private static final long serialVersionUID = 516389048716909610L;
            {
                put("Low", 100);
                put("Normal", 500);
                put("High", 700);
                put("Urgent", 800);
                put("Immediate", 1000);
            }
        });
    }
}
