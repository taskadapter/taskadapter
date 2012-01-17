package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.*;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

import java.util.Arrays;

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

    private Button createButton(Resource icon, final ConnectorDataHolder dataHolderFrom, final ConnectorDataHolder destinationDataHolder) {
        Button button = new NativeButton();
        button.setHeight(ARROW_BUTTON_HEIGHT, UNITS_PIXELS);
        button.setIcon(icon);
        button.addListener(createListener(dataHolderFrom, destinationDataHolder));
        return button;
    }

    private Button.ClickListener createListener(final ConnectorDataHolder sourceDataHolder, final ConnectorDataHolder destinationDataHolder) {
        return new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                processBasedOnDestinationConnectorType();
            }

            private void processBasedOnDestinationConnectorType() {
                Connector<ConnectorConfig> destinationConnector = getConnector(destinationDataHolder);
                if (destinationConnector instanceof FileBasedConnector) {
                    processFile((FileBasedConnector) destinationConnector);
                } else {
                    startRegularExport();
                }
            }

            final String TEXT_UPDATE = "Only update tasks present in the file";
            final String OVERWRITE = "Overwrite";
            final String CANCEL = "Cancel";

            private void processFile(FileBasedConnector connectorTo) {
                if (connectorTo.fileExists()) {

                    getWindow().addWindow(new MessageDialog("Choose operation", "Destination file already exists:" +
                            "\n" + connectorTo.getAbsoluteOutputFileName(), Arrays.asList(TEXT_UPDATE, OVERWRITE, CANCEL),
                            new MessageDialog.Callback() {
                                public void onDialogResult(String answer) {
                                    processFileAction(answer);
                                }
                            }));
                } else {
                    processFileAction(OVERWRITE);
                }
            }

            private void processFileAction(String action) {
                if (action.equals(TEXT_UPDATE)) {
                    startUpdateFile();
                } else if (action.equals(OVERWRITE)) {
                    startRegularExport();
                } else {
                    System.out.println("canceled");
                }
            }

            private void startUpdateFile() {
                UpdateFilePage page = new UpdateFilePage(getConnector(sourceDataHolder), getConnector(destinationDataHolder));
                pageManager.show(page);
            }

            private void startRegularExport() {
                ExportPage page = new ExportPage(getConnector(sourceDataHolder), getConnector(destinationDataHolder));
                pageManager.show(page);
            }

        };
    }

    private Descriptor getDescriptor(ConnectorDataHolder desc) {
        return pluginManager.getDescriptor(desc.getType());
    }

    private Connector getConnector(ConnectorDataHolder connectorDataHolder) {
        final PluginFactory factory = pluginManager.getPluginFactory(connectorDataHolder.getType());
        final ConnectorConfig config = connectorDataHolder.getData();
        return factory.createConnector(config);
    }

//    private ExportAction createExportAction(ConnectorDataHolder holder1, ConnectorDataHolder holder2) {
//        ConfigSaver sourceConfigSaver = new MyConfigSaver(holder1);
//        ConfigSaver destinationConfigSaver = new MyConfigSaver(holder2);
//        return new ExportAction(sourceConfigSaver, holder1, destinationConfigSaver, holder2);
//    }

}
