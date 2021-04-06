package com.taskadapter.webui.pages.config;

import com.taskadapter.common.ui.ReloadableComponent;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.config.EditConfigPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FieldMappingPanel extends VerticalLayout implements ReloadableComponent {
    private EditConfigPage editConfigPage;

    public FieldMappingPanel(UISyncConfig config, ConfigOperations configOps) {
        var emptyErrorText = "";
        editConfigPage = new EditConfigPage(configOps, Page.MESSAGES, emptyErrorText, config);
        add(editConfigPage.getUI());
    }

    public void showError(String error) {
        editConfigPage.showError(error);
    }

    @Override
    public void reload() {
        // nothing yet
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
