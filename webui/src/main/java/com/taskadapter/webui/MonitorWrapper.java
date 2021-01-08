package com.taskadapter.webui;

import com.taskadapter.connector.definition.ProgressMonitor;
import com.vaadin.flow.component.progressbar.ProgressBar;


public class MonitorWrapper implements ProgressMonitor {
    private ProgressBar progressBar;
    private float total;
    private float current;

    public MonitorWrapper(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void beginTask(String name, int total) {
//        vaadinProgressMonitor.setCaption(name);
        this.total = (float) total;
    }

    @Override
    public void worked(int work) {
        this.current += work;
//        vaadinProgressMonitor.setProgress(current / total);
    }

    @Override
    public void done() {
//            progressBar.done();
    }
}
