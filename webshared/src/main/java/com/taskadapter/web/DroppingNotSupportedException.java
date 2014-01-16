package com.taskadapter.web;

// TODO Alexey: I don't like using this exception.
// whether or not dropping is supported is a "flag", not an exceptional situation.
// so connectors should report this as a feature instead of throwing an exception.
public final class DroppingNotSupportedException extends Exception {
    public static final DroppingNotSupportedException INSTANCE = new DroppingNotSupportedException();
}
