package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfigDetailsPage extends Page {
    private TAFile file;
    private Label name;
    private VerticalLayout layout = new VerticalLayout();

    public ConfigDetailsPage() {
    }

    private void buildUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);
        name = new Label();
        layout.addComponent(name);
        layout.addComponent(new ConfigToolbarPanel(navigator, file));
        layout.addComponent(new ConfigButtonsPanel(navigator, file, services));
    }

    private void setTask() {
        name.setValue("Name : " + file.getConfigLabel());
    }

    public void setFile(TAFile file) {
        this.file = file;
    }

    @Override
    public String getPageTitle() {
        return file.getConfigLabel();
    }

    @Override
    public Component getUI() {
        buildUI();
        setTask();
        return layout;
    }
}
