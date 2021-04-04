package com.taskadapter.webui;

import com.taskadapter.reporting.ErrorReporter;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;

public class MyCustomErrorHandler extends DefaultErrorHandler {
    private final ErrorReporter errorReporter;

    public MyCustomErrorHandler(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public void error(ErrorEvent event) {
        Throwable t = event.getThrowable();
        errorReporter.reportIfAllowed(t);
        super.error(event);
    }
}