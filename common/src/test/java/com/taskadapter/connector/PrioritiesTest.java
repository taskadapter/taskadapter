package com.taskadapter.connector;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PrioritiesTest {
    @Test
    public void defaultPriorityNumberForUnknownText() {
        assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, getSamplePriorities().getPriorityByText("unknown value"));
    }

    @Test
    public void priorityTextFoundForMaxNumber() {
        assertEquals("Immediate", getSamplePriorities().getPriorityByMSP(Priorities.MAX_PRIORITY_VALUE));
    }

    @Test
    public void defaultPriorityTextForNULLNumber() {
        assertEquals("Normal", getSamplePriorities().getPriorityByMSP(null));
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
                put("Immediate", Priorities.MAX_PRIORITY_VALUE);
            }
        });
    }

    @Test
    public void copyConstructorCopiesFields() {
        Priorities samplePriorities = getSamplePriorities();
        Priorities cloned = new Priorities(samplePriorities);
        assertEquals(Integer.valueOf(700), cloned.getPriorityByText("High"));
        assertEquals(Integer.valueOf(Priorities.MAX_PRIORITY_VALUE), cloned.getPriorityByText("Immediate"));
    }

    @Test
    public void copyConstructorIsDeepIndeed() {
        Priorities samplePriorities = getSamplePriorities();
        Priorities cloned = new Priorities(samplePriorities);
        assertEquals(Integer.valueOf(700), cloned.getPriorityByText("High"));
        samplePriorities.setPriority("High", 950);

        assertEquals(Integer.valueOf(950), samplePriorities.getPriorityByText("High"));
        assertEquals(Integer.valueOf(700), cloned.getPriorityByText("High"));
    }
}
