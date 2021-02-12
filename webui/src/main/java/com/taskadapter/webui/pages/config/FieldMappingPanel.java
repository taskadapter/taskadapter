package com.taskadapter.webui.pages.config;

import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.config.EditConfigPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FieldMappingPanel extends VerticalLayout {
    public FieldMappingPanel(UISyncConfig config, ConfigOperations configOps) {
        add(getConfigEditor(config, configOps));
    }

    private Component getConfigEditor(UISyncConfig config, ConfigOperations configOps) {
        var error = "";
        var editor = new EditConfigPage(configOps, Page.MESSAGES, error, config);
        return editor.getUI();
    }
}
