package com.taskadapter.data;

public final class States {

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
