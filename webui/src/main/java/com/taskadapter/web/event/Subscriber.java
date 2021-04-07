package com.taskadapter.web.event;

public interface Subscriber<T> {
    void process(T event);
}
