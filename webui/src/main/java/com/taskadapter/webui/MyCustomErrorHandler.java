package com.taskadapter.webui;

import com.taskadapter.reporting.ErrorReporter;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;

public class MyCustomErrorHandler extends DefaultErrorHandler {
    @Override
    public void error(ErrorEvent event) {
        Throwable t = event.getThrowable();
        ErrorReporter.reportIfAllowed(t);
        super.error(event);
    }
}