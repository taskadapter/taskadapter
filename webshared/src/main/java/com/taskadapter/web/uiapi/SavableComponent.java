package com.taskadapter.web.uiapi;

import com.vaadin.flow.component.Component;

public interface SavableComponent {
    Component getComponent();

    /**
     * save this  component. return a boolean flag showing whether or not the save was successful
     *
     * @return true if the save operation was successful. false if there were some validation or save errors
     */
    boolean save();
}
