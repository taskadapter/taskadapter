package com.taskadapter.webui.pages.config.wizard;

import com.taskadapter.webui.pages.WizardStep;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Function;

import static com.taskadapter.webui.Page.message;

public class NewConfigGiveDescription extends FormLayout implements WizardStep<String> {

    public NewConfigGiveDescription(Function<String, Void> saveClicked) {
        setResponsiveSteps(
                new FormLayout.ResponsiveStep("50em", 1));

        var descriptionTextField = new TextField(message("createConfigPage.description"));
        descriptionTextField.setRequired(true);
        descriptionTextField.setWidth("400px");

        var createButton = new Button(message("createConfigPage.create"),
                e -> saveClicked.apply(descriptionTextField.getValue()));

        createButton.setEnabled(!descriptionTextField.getValue().isEmpty());
        descriptionTextField.addValueChangeListener(e ->
                createButton.setEnabled(!e.getValue().isEmpty()));

        add(descriptionTextField,
                createButton);
    }

    @Override
    public String getResult() {
        // does not matter for this final step
        return null;
    }

    @Override
    public Component ui(Object config) {
        return this;
    }
}
