package com.taskadapter.data;

/**
 * State utilities.
 */
public final class States {
    public static <T> State<T> fixed(final T t) {
        return new State<T>() {
            @Override
            public T get() {
                return t;
            }

            @Override
            public void addListener(Runnable listener) {
                // Not supported.
            }

            @Override
            public void removeListener(Runnable listener) {
                // Not supported.
            }
        };
    }

    /**
     * Invokes callback on value and all new values.
     * 
     * @param state
     *            state.
     * @param callback
     *            callback to onvoke on all values.
     */
    public static <E, T extends E> void onValue(final State<T> state,
            final DataCallback<E> callback) {
        final Runnable updater = new Runnable() {
            @Override
            public void run() {
                callback.callBack(state.get());
            }
        };
        
        state.addListener(updater);
        updater.run();
    }
}
