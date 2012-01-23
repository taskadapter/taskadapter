package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.web.WindowProvider;

import java.util.List;

/**
 * @author Alexey Skorokhodov
 */
public class LookupJob extends Thread {

    private WindowProvider windowProvider;
    private LookupResultListener resultsListener;
    private LookupOperation operation;

    public LookupJob(WindowProvider windowProvider, LookupOperation operation,
                     LookupResultListener resultsListener) {
        this.windowProvider = windowProvider;
        this.resultsListener = resultsListener;
        this.operation = operation;
    }

    @Override
    public void run() {
        try {
            final List<? extends NamedKeyedObject> objects = operation.run();
            if (objects.isEmpty()) {
                windowProvider.getWindow().showNotification("No objects", "No objects have been found");
            }
            // must synchronize changes over application
            synchronized (windowProvider.getWindow().getApplication()) {
                resultsListener.notifyDone(objects);
            }
        } catch (ValidationException e) {
            EditorUtil.show(windowProvider.getWindow(), "Validation failed", e);
        } catch (Exception e) {
            EditorUtil.show(windowProvider.getWindow(), "Operation failed", e);
        }
    }
}
