package com.taskadapter.webui.pages;

import com.vaadin.flow.component.Component;

public interface WizardStep<RES> {
    RES getResult();

    Component ui(Object config);
}
