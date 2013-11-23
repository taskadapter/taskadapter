package com.taskadapter.data;

/**
 * Some state which can change over time. Not thread safe. However, all
 * instances must support subscription/unsubscription during listeners update.
 * 
 * @param <T>
 *            type of the state.
 */
public interface State<T> {
    /**
     * Returns current state.
     * 
     * @return current state.
     */
    public T get();

    /**
     * Adds a state listener.
     * 
     * @param listener
     *            listener to run on state change.
     */
    public void addListener(Runnable listener);

    /**
     * Removes a listener. If listener is not registered, does nothing.
     * 
     * @param listener
     *            listener to remove.
     */
    public void removeListener(Runnable listener);
}
