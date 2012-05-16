package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.web.service.Services;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

public class ConfigActionsPanel extends VerticalLayout {
    private Navigator navigator;
    private TAFile file;
    private Services services;
    private HorizontalLayout horizontalLayout;

    public ConfigActionsPanel(Navigator navigator, TAFile file, Services services) {
        addStyleName("configs-single-panel");
        this.navigator = navigator;
        this.file = file;
        this.services = services;
        buildUI();
    }

    private void buildUI() {
        setSpacing(true);

        addDescription();

        horizontalLayout = new HorizontalLayout();
        horizontalLayout.addStyleName("configs-single-panel-inner");
        horizontalLayout.setSpacing(true);
        addComponent(horizontalLayout);

        createBox(file.getConnectorDataHolder1());
        createActionButtons();
        createBox(file.getConnectorDataHolder2());
    }

    private void addDescription() {
        Label description = new Label(file.getConfigLabel());
        addComponent(description);
        setComponentAlignment(description, Alignment.MIDDLE_CENTER);
    }

    private void createBox(final ConnectorDataHolder dataHolder) {
        final String label = dataHolder.getData().getLabel();
        NativeButton configBoxButton = new NativeButton(label);
        configBoxButton.addStyleName("boxButton");
        configBoxButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                navigator.showConfigureTaskPage(file, label);
            }
        });
        horizontalLayout.addComponent(configBoxButton);
    }

    private void createActionButtons() {
        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.addComponent(createButton("img/arrow_right.png", file.getConnectorDataHolder1(), file.getConnectorDataHolder2()));
        buttonsLayout.addComponent(createButton("img/arrow_left.png", file.getConnectorDataHolder2(), file.getConnectorDataHolder1()));
        horizontalLayout.addComponent(buttonsLayout);
    }

    private Button createButton(String label, final ConnectorDataHolder sourceDataHolder, final ConnectorDataHolder destinationDataHolder) {
        Button button = new NativeButton();
        button.setIcon(new ThemeResource(label));

        final Exporter exporter = new Exporter(navigator, services.getPluginManager(), sourceDataHolder, destinationDataHolder, file);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
        return button;
    }
}
