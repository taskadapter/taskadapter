package com.taskadapter.web;

import com.taskadapter.connector.definition.ConnectorSetup;
import com.vaadin.flow.component.Component;

import java.util.Optional;

public interface ConnectorSetupPanel {
    Component getComponent();

    /**
     * @return Empty if no errors. localized error text otherwise
     */
    Optional<String> validate();

    void showError(String string);

    ConnectorSetup getResult();
}
