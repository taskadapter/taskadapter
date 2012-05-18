package com.taskadapter.webui;

import com.taskadapter.web.configeditor.DefaultPanel;
import com.vaadin.ui.*;

/**
 * @author Alexey Skorokhodov
 */
public class ConfirmationPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private String questionText;
    private Button.ClickListener actionListener;

    private void buildUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);
        layout.setMargin(true);

        Label label = new Label(questionText);
        label.setWidth(DefaultPanel.WIDE_PANEL_WIDTH);
        Button deleteButton = new Button("Yes");
        deleteButton.addListener(actionListener);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(deleteButton);
        horizontalLayout.addComponent(createBackButton("Cancel"));

        layout.addComponent(label);
        layout.addComponent(horizontalLayout);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "confirmation";
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
