package com.taskadapter.web.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBusImpl {
    private static final Map<Class, List<Subscriber>> subscribers = new HashMap<>();

    public static void post(Event event) {
        if (!subscribers.containsKey(event.getClass())) {
            return;
        }
        subscribers.get(event.getClass())
                .forEach(subscriber -> subscriber.process(event));
    }

    /**
     * Subscribe to the event, run the provided action when the event is received.
     *
     * @param clazz event class to process
     * @param f     the function to apply when an event of [T] class arrived
     */
    public static synchronized <T extends Event> void subscribe(Class<T> clazz, Subscriber<T> f) {
        final List<Subscriber> list = EventBusImpl.subscribers.get(clazz);
        if (list == null) {
            var arrayList = new ArrayList<Subscriber>();
            arrayList.add(f);
            subscribers.put(clazz, arrayList);
        } else {
            list.add(f);
        }
    }
}
