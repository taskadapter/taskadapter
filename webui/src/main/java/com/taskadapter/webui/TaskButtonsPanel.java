package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

public class TaskButtonsPanel extends HorizontalLayout {
    private static final int ARROW_BUTTON_HEIGHT = 55;
    private static Resource ICON_LEFT = new ThemeResource("../../icons/left.png");
    private static Resource ICON_RIGHT = new ThemeResource("../../icons/right.png");

    private PluginManager pluginManager;
    private PageManager pageManager;
    private TAFile file;

    public TaskButtonsPanel(PluginManager pluginManager, PageManager pageManager, TAFile file) {
        this.pluginManager = pluginManager;
        this.pageManager = pageManager;
        this.file = file;
        buildUI();
    }

    private void buildUI() {
        createBox(file.getConnectorDataHolder1());
        createActionButtons();
        createBox(file.getConnectorDataHolder2());
    }

    private void createBox(ConnectorDataHolder dataHolder) {
        Descriptor descriptor = getDescriptor(dataHolder);
        // TODO use the config
        ConnectorConfig config = dataHolder.getData();
        NativeButton button = new NativeButton(descriptor.getLabel());
        button.addStyleName("boxButton");
        addComponent(button);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                // TODO implement this
            }
        });

    }

    private void createActionButtons() {
        Layout buttonsLayout = new VerticalLayout();
        buttonsLayout.addComponent(createButton(ICON_RIGHT, file.getConnectorDataHolder1(), file.getConnectorDataHolder2()));
        buttonsLayout.addComponent(createButton(ICON_LEFT, file.getConnectorDataHolder2(), file.getConnectorDataHolder1()));
        addComponent(buttonsLayout);
    }

    private Button createButton(Resource icon, final ConnectorDataHolder sourceDataHolder, final ConnectorDataHolder destinationDataHolder) {
        Button button = new NativeButton();
        button.setHeight(ARROW_BUTTON_HEIGHT, UNITS_PIXELS);
        button.setIcon(icon);
        final Exporter exporter = new Exporter(pageManager, pluginManager, sourceDataHolder, destinationDataHolder, file.getName());
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exporter.export();
            }
        });
        return button;
    }

    private Descriptor getDescriptor(ConnectorDataHolder desc) {
        return pluginManager.getDescriptor(desc.getType());
    }
//    private ExportAction createExportAction(ConnectorDataHolder holder1, ConnectorDataHolder holder2) {
//        ConfigSaver sourceConfigSaver = new MyConfigSaver(holder1);
//        ConfigSaver destinationConfigSaver = new MyConfigSaver(holder2);
//        return new ExportAction(sourceConfigSaver, holder1, destinationConfigSaver, holder2);
//    }

}
