package com.taskadapter.webui;

import com.taskadapter.reporting.ErrorReporter;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;

public class MyCustomErrorHandler extends DefaultErrorHandler {
    @Override
    public void error(ErrorEvent event) {
        Throwable t = event.getThrowable();
        ErrorReporter.reportIfAllowed(t);
        doDefault(event);
    }
}