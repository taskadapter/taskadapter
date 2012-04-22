package com.taskadapter.webui;

import com.taskadapter.config.TAFile;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Alexey Skorokhodov
 */
public class ConfirmationPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private TAFile file;
    private String questionText;
    private Button.ClickListener actionListener;

    private void buildUI() {
        layout.removeAllComponents();
        layout.addComponent(new Label(questionText));
        Button deleteButton = new Button("Yes");
        deleteButton.addListener(actionListener);
        layout.addComponent(deleteButton);
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
        return layout;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setActionListener(Button.ClickListener actionListener) {
        this.actionListener = actionListener;
    }
}
