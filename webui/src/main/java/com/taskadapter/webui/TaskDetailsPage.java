package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class TaskDetailsPage extends Page {
    private TAFile file;
    private Label name;
    private VerticalLayout layout = new VerticalLayout();

    public TaskDetailsPage() {
    }

    private void buildUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);
        name = new Label();
        layout.addComponent(name);
        layout.addComponent(new TaskToolbarPanel(navigator, file, services));
        layout.addComponent(new TaskButtonsPanel(navigator, file, services));
    }

    private void setTask() {
        name.setValue("Name : " + file.getName());
    }

    public void setFile(TAFile file) {
        this.file = file;
    }

    @Override
    public String getPageTitle() {
        return file.getName();
    }

    @Override
    public Component getUI() {
        buildUI();
        setTask();
        return layout;
    }
}
