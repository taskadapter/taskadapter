package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.vaadin.ui.Window;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LookupJob extends Thread {

    private Window window;
    private LookupResultListener resultsListener;
    private LookupOperation operation;

    public LookupJob(Window window, LookupOperation operation,
                     LookupResultListener resultsListener) {
        this.window = window;
        this.resultsListener = resultsListener;
        this.operation = operation;
    }

    @Override
    public void run() {
        try {
            final List<? extends NamedKeyedObject> objects = operation.run();
            if (objects.isEmpty()) {
                EditorUtil.show(window, "No objects", "No objects have been found");
            }
            // must synchronize changes over application
            synchronized (window.getApplication()) {
                resultsListener.notifyDone(objects);
            }
        } catch (ValidationException e) {
            EditorUtil.show(window, "Validation failed", e);
        } catch (Exception e) {
            EditorUtil.show(window, "Operation failed", e);
        }
    }
}
