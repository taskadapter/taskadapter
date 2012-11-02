package com.taskadapter.webui.config;

import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.Navigator;
import com.taskadapter.webui.export.Exporter;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

public class ExportButtonsFragment extends VerticalLayout {
    private final Messages messages;
    private Services services;
    private final Navigator navigator;
    private final UISyncConfig syncConfig;

    public ExportButtonsFragment(Messages messages, Services services, Navigator navigator, UISyncConfig syncConfig) {
        this.messages = messages;
        this.services = services;
        this.navigator = navigator;
        this.syncConfig = syncConfig;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);
        addComponent(createButton(MappingSide.RIGHT));
        addComponent(createButton(MappingSide.LEFT));
    }

    private Button createButton(MappingSide exportDirection) {
        String imageFile;
        UISyncConfig config;

        switch (exportDirection) {
            case RIGHT:
                imageFile = "img/arrow_right.png";
                config = syncConfig;
                break;
            case LEFT:
                imageFile = "img/arrow_left.png";
                config = syncConfig.reverse();
                break;
            default:
                throw new IllegalArgumentException("Unsupported mapping direction " + exportDirection);
        }
        Button button = new Button();
        button.setIcon(new ThemeResource(imageFile));
        button.setStyleName(Runo.BUTTON_SMALL);
        button.addStyleName("exportLeftRightButton");

        final Exporter exporter = new Exporter(messages, services, navigator, config);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
        return button;
    }
}
