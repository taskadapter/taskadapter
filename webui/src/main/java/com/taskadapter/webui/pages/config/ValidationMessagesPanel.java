package com.taskadapter.webui.pages.config;

import com.taskadapter.webui.Page;
import com.taskadapter.webui.pages.ValidationErrorTextWithProcessor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class ValidationMessagesPanel extends VerticalLayout {
    private final String caption;

    public ValidationMessagesPanel(String caption) {
        this.caption = caption;
    }

    public void show(List<ValidationErrorTextWithProcessor> errors) {
        removeAll();
        setVisible(!errors.isEmpty());
        if (!errors.isEmpty()) {
            var captionLabel = new Label(caption);
            captionLabel.addClassName("validationPanelCaption");
            add(captionLabel);
            errors.forEach(this::showMessage);
        }
    }

    public void showMessage(ValidationErrorTextWithProcessor error) {
        var row = new HorizontalLayout();
        var decoratedMessage = "* " + error.getText();
        var errorMessageLabel = new Label(decoratedMessage);
        errorMessageLabel.addClassName("error-message-label");
        errorMessageLabel.setWidth("600px");
        errorMessageLabel.addClassName("wrap");

        var fixButton = new Button(Page.message("configSummary.fixButtonCaption"),
                e -> error.getProcessor().run());
        row.add(errorMessageLabel);
        row.add(fixButton);
        add(row);
    }
}
