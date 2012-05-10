package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.service.Services;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

public class ConfigButtonsPanel extends HorizontalLayout {
    private Navigator navigator;
    private TAFile file;
    private Services services;

    public ConfigButtonsPanel(Navigator navigator, TAFile file, Services services) {
        this.navigator = navigator;
        this.file = file;
        this.services = services;
        buildUI();
    }

    private void buildUI() {
        createBox(file.getConnectorDataHolder1());
        createActionButtons();
        createBox(file.getConnectorDataHolder2());
    }

    private void createBox(ConnectorDataHolder dataHolder) {
        Descriptor descriptor = getDescriptor(dataHolder);
        Label label = new Label(descriptor.getLabel());
        label.addStyleName("connectorBoxLabel");
        addComponent(label);
    }

    private void createActionButtons() {
        VerticalLayout buttonsLayout = new VerticalLayout();
        buttonsLayout.setMargin(new MarginInfo(true, false, true, false));
        buttonsLayout.addComponent(createButton("img/arrow_right.png", file.getConnectorDataHolder1(), file.getConnectorDataHolder2()));
        buttonsLayout.addComponent(createButton("img/arrow_left.png", file.getConnectorDataHolder2(), file.getConnectorDataHolder1()));
        addComponent(buttonsLayout);
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

    private Descriptor getDescriptor(ConnectorDataHolder desc) {
        return services.getPluginManager().getDescriptor(desc.getType());
    }
//    private ExportAction createExportAction(ConnectorDataHolder holder1, ConnectorDataHolder holder2) {
//        ConfigSaver sourceConfigSaver = new MyConfigSaver(holder1);
//        ConfigSaver destinationConfigSaver = new MyConfigSaver(holder2);
//        return new ExportAction(sourceConfigSaver, holder1, destinationConfigSaver, holder2);
//    }

}
