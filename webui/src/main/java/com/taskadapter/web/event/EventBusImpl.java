package com.taskadapter.web.event;

import rx.lang.scala.Observable;
import rx.lang.scala.Subscriber;
import rx.lang.scala.Subscription;
import rx.lang.scala.subjects.PublishSubject;
import rx.lang.scala.subjects.PublishSubject$;
import rx.lang.scala.subjects.SerializedSubject;
import rx.lang.scala.subjects.SerializedSubject$;

public class EventBusImpl {
    private static final PublishSubject<Event> subject = PublishSubject$.MODULE$.apply();
    private static final SerializedSubject<Event> bus = SerializedSubject$.MODULE$.apply(subject);

    public static void post(Event event) {
        if (bus.hasObservers()) {
            bus.onNext(event);
        }
    }

    public static <T> Observable<T> observable(Class<T> eventClass) {
        rx.lang.scala.Observable<Event> observable = bus.filter(o -> o != null) // Filter out null objects, better safe than sorry
                // We're only interested in a specific event class
                .filter(o -> eventClass.isInstance(o));
        // Cast it for easier usage
        return (Observable<T>) observable;
    }

    /**
     * Subscribe to the event, run the provided action when the event is received.
     *
     * @param clazz event class to process
     * @param f the function to apply when an event of [T] class arrived
     * @return the subscription
     */
    public static <T extends Event> Subscription subscribe(Class<T> clazz, Subscriber<T> f) {
        var subscription = observable(clazz).subscribe(e -> {
            f.onNext(e);
            return null;
        });
        return subscription;
    }
}
