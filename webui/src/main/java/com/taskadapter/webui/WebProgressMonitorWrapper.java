package com.taskadapter.webui;

import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.ui.HtmlLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.server.Command;

public class WebProgressMonitorWrapper extends VerticalLayout implements ProgressMonitor {
    private final HtmlLabel caption;
    private ProgressBar progressBar;
    private float total;
    private float current;
    private volatile boolean taskStopped = false;

    public WebProgressMonitorWrapper(ProgressBar progressBar, String captionText, int total) {
        this.progressBar = progressBar;
        caption = EditorUtil.createCaption(captionText);
        progressBar.setMin(0);
        progressBar.setMax(total);
        add(caption,
                progressBar);
    }

    // TODO 14 cleanup: keep the task label and total in the constructor only, remove this method completely
    @Override
    public void beginTask(String label, int total) {
        this.total = (float) total;
        inUi(() -> caption.setText(label));
    }

    @Override
    public void worked(int work) {
        this.current += work;
        inUi(() -> progressBar.setValue(current));
    }

    @Override
    public void done() {
        inUi(() -> progressBar.setValue(total));
    }

    @Override
    public void stopTask() {
        this.taskStopped = true;
    }

    @Override
    public boolean isStopped() {
        return taskStopped;
    }

    private void inUi(Command command) {
        progressBar.getUI().get().access(command::execute);
    }
}
