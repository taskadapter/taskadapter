package com.taskadapter.web.event;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventBusImplTest {
    @Test
    public void canPostAndAccept() {
        var processor = new EventProcessor();
        EventBusImpl.subscribe(SchedulerStatusChanged.class, processor);
        EventBusImpl.post(new SchedulerStatusChanged(true));
        assertThat(processor.currentValue).isTrue();
    }

    static class EventProcessor implements Subscriber<SchedulerStatusChanged> {
        boolean currentValue = false;

        @Override
        public void process(SchedulerStatusChanged event) {
            this.currentValue = event.isSchedulerEnabled();
        }
    }
}