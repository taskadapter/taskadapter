package com.taskadapter.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default mutable state.
 * 
 * @param <T>
 *            type of the state.
 */
public final class MutableState<T> implements State<T> {

    /**
     * State value.
     */
    private T value;

    /**
     * Listeners list.
     */
    private final List<Runnable> listeners = new CopyOnWriteArrayList<Runnable>();

    /**
     * Creates a new mutable state with a given value.
     * 
     * @param value
     *            initial state value.
     */
    public MutableState(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /**
     * Sets a new value. If value equals to current value, performs nothing.
     * 
     * @param newValue
     *            new state value.
     */
    public void set(T newValue) {
        T prevValue = value;
        if (prevValue == null && newValue == null)
            return;
        if (prevValue != null && prevValue.equals(newValue))
            return;

        value = newValue;
        for (Runnable r : listeners)
            r.run();
    }
}
