package com.taskadapter.webui;

import com.taskadapter.connector.definition.ProgressMonitor;
import com.vaadin.ui.ProgressIndicator;

/**
 * @author Alexey Skorokhodov
 */
public class MonitorWrapper implements ProgressMonitor {
    private ProgressIndicator vaadinProgressMonitor;
    private float total;
    private float current;

    public MonitorWrapper(ProgressIndicator vaadinProgressMonitor) {
        this.vaadinProgressMonitor = vaadinProgressMonitor;
    }

    @Override
    public void beginTask(String name, int total) {
        vaadinProgressMonitor.setCaption(name);
        this.total = (float) total;
    }

    @Override
    public void worked(int work) {
        this.current += work;
        System.out.println(current / total);
        vaadinProgressMonitor.setValue(current / total);
    }

    @Override
    public void done() {
//            vaadinProgressMonitor.done();
    }
}
