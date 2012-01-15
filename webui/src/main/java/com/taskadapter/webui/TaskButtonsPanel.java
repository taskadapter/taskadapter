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
    private Resource ICON_LEFT = new ThemeResource("../../icons/left.png");
    private Resource ICON_RIGHT = new ThemeResource("../../icons/right.png");

    private PluginManager pluginManager;
    private TAFile file;
    private Button buttonRight;
    private Button buttonLeft;

    public TaskButtonsPanel(PluginManager pluginManager, TAFile file) {
        this.pluginManager = pluginManager;
        this.file = file;
        buildUI();
    }

    private void buildUI() {
        createBox(file.getConnectorDataHolder1());
        createActionButtons();
        createBox(file.getConnectorDataHolder2());
    }

    private void createBox(ConnectorDataHolder dataHolder) {
        Descriptor connector = getConnector(dataHolder);
        // TODO use the config
        ConnectorConfig config = dataHolder.getData();
        NativeButton button = new NativeButton(connector.getLabel());
        button.setWidth("250px");
        button.setHeight("110px");
        button.addStyleName("boxButton");
        addComponent(button);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
            }
        });

    }

    private void createActionButtons() {
        Layout buttonsLayout = new VerticalLayout();
        buttonRight = new NativeButton();
        buttonRight.setHeight(ARROW_BUTTON_HEIGHT, UNITS_PIXELS);
        buttonRight.setIcon(ICON_RIGHT);
        buttonRight.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
//                createExportAction(file.getConnectorDataHolder1(), file.getConnectorDataHolder2()).startExport();
            }
        });
        buttonsLayout.addComponent(buttonRight);

        buttonLeft = new NativeButton();
        buttonLeft.setHeight(ARROW_BUTTON_HEIGHT, UNITS_PIXELS);
        buttonLeft.setIcon(ICON_LEFT);
        buttonLeft.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
//                createExportAction(file.getConnectorDataHolder2(), file.getConnectorDataHolder1()).startExport();
            }
        });
        buttonsLayout.addComponent(buttonLeft);
        addComponent(buttonsLayout);
    }

    private Descriptor getConnector(ConnectorDataHolder desc) {
        return pluginManager.getDescriptor(desc.getType());
    }

//    private ExportAction createExportAction(ConnectorDataHolder holder1, ConnectorDataHolder holder2) {
//        ConfigSaver sourceConfigSaver = new MyConfigSaver(holder1);
//        ConfigSaver destinationConfigSaver = new MyConfigSaver(holder2);
//        return new ExportAction(sourceConfigSaver, holder1, destinationConfigSaver, holder2);
//    }

}
