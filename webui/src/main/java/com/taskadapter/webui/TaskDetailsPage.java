package com.taskadapter.webui;

import com.taskadapter.PluginManager;
import com.taskadapter.config.ConfigStorage;
import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.PluginFactory;
import com.taskadapter.web.SettingsManager;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author Alexey Skorokhodov
 */
public class TaskDetailsPage extends Page {
    private TAFile file;
    private PageManager pageManager;
    private ConfigStorage storage;
    private PluginManager pluginManager;
    private EditorManager editorManager;
    private SettingsManager settingsManager;
    private Label name;
    private Button updateMSPLink;
    private Button link1to2;
    private Button link2to1;
    private Button cloneButton = new Button("Clone config");
    private VerticalLayout layout = new VerticalLayout();

    // TODO refactor this huge list of parameters!
    public TaskDetailsPage(TAFile file, PageManager pageManager, ConfigStorage storage, PluginManager pluginManager, EditorManager editorManager, SettingsManager settingsManager) {
        this.file = file;
        this.pageManager = pageManager;
        this.storage = storage;
        this.pluginManager = pluginManager;
        this.editorManager = editorManager;
        this.settingsManager = settingsManager;
        buildUI();
        setTask();
    }

    private void buildUI() {
        layout.setSpacing(true);
        name = new Label();
        layout.addComponent(name);

        Button configureButton = new Button("Configure");
        configureButton.setStyleName(BaseTheme.BUTTON_LINK);
        configureButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showConfigurePage();
            }
        });
        layout.addComponent(configureButton);

        cloneButton.setStyleName(BaseTheme.BUTTON_LINK);
        cloneButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().addWindow(new MessageDialog("Confirm Clone", "Clone the selected config?",
                        new MessageDialog.Callback() {
                            public void onDialogResult(boolean yes) {
                                if (yes) {
                                    storage.cloneConfig(file);
                                    pageManager.show(PageManager.TASKS);
                                }
                            }
                        }
                ));
            }
        });
        layout.addComponent(cloneButton);

        Button deleteButton = new Button("Delete config");
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK);
        deleteButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                showDeletePage();
            }
        });
        layout.addComponent(deleteButton);

        if (hasOneMSPConnector()) {
            updateMSPLink = new Button();
            updateMSPLink.setStyleName(BaseTheme.BUTTON_LINK);
            layout.addComponent(updateMSPLink);
        }
        link1to2 = new Button();
        link1to2.setStyleName(BaseTheme.BUTTON_LINK);

        link2to1 = new Button();
        link2to1.setStyleName(BaseTheme.BUTTON_LINK);

        layout.addComponent(link1to2);
        layout.addComponent(link2to1);

        updateLinks();
        setCompositionRoot(layout);
        createBox(file.getConnectorDataHolder1());
        createBox(file.getConnectorDataHolder2());
    }

    private void createBox(ConnectorDataHolder dataHolder) {
        Descriptor connector = getConnector(dataHolder);
        // TODO use the config
        ConnectorConfig config = (ConnectorConfig) dataHolder.getData();
        NativeButton button = new NativeButton(connector.getLabel());
        button.setWidth("250px");
        button.setHeight("110px");
        button.addStyleName("boxButton");
        layout.addComponent(button);
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
            }
        });

    }

    private Descriptor getConnector(ConnectorDataHolder desc) {
        return pluginManager.getDescriptor(desc.getType());
    }

    private void showDeletePage() {
        DeletePage page = new DeletePage(pageManager, storage, file);
        pageManager.show(page);
    }

    private void showConfigurePage() {
        ConfigureTaskPage page = new ConfigureTaskPage(file, editorManager, storage, settingsManager);
        pageManager.show(page);
    }

    private void setTask() {
        name.setValue("Name : " + file.getName());
    }

    private static String generateLinkText(String label, Descriptor connectorForDefaultLabel) {
        if (label == null || label.trim().isEmpty()) {
            label = connectorForDefaultLabel.getLabel();
        }
        return label;
    }

    private static void setLinkText(Button link, String labelFrom, String labelTo) {
        String text = "Export from " + labelFrom + " to " + labelTo;
        link.setCaption(text);
    }

    private void updateLinks() {
        final Connector connector1 = getRealConnector1();
        final Connector connector2 = getRealConnector2();

        String labelFrom = generateLinkText(connector1.getConfig().getLabel(), connector1.getDescriptor());
        String labelTo = generateLinkText(connector2.getConfig().getLabel(), connector2.getDescriptor());

        setLinkText(link1to2, labelFrom, labelTo);
        setLinkText(link2to1, labelTo, labelFrom);

//        removeSelListeners(link1to2);
//        removeSelListeners(link2to1);
        this.link1to2.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                ExportPage page = new ExportPage(connector1, connector2);
                pageManager.show(page);
            }
        });
        this.link2to1.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                ExportPage page = new ExportPage(connector2, connector1);
                pageManager.show(page);
            }
        });

        if (hasOneMSPConnector()) {
            createUpdateMSPLink();
            updateMSPLink();
        }
    }

    private static String MSP_ID = "Microsoft Project";

    private boolean hasOneMSPConnector() {
        // this looks weird, but we need to identify MSP connector somehow.
        // UI code does not know anything about any specific connector...
        // another option would be to add "isMSP" to the Connector Interface, which would be
        // even more weird.
        // See MSPDescriptorTest class: it has a test to verify the ID stays the same
        String type1 = file.getConnectorDataHolder1().getType();
        String type2 = file.getConnectorDataHolder2().getType();
        // only one of the connectors is MSP
        return (
                (type1.equals(MSP_ID) && (!type2.equals(MSP_ID)))
                        ||
                        (type2.equals(MSP_ID) && (!type1.equals(MSP_ID)))
        );
    }

    private Descriptor getOtherConnector() {
        return getConnector1().getID().equals(MSP_ID) ? getConnector2() : getConnector1();
    }

    private void updateMSPLink() {
        String otherConnectorName = getOtherConnector().getLabel();
        String text = "Update the MSP file with data from " + otherConnectorName;
        updateMSPLink.setCaption(text);
    }

    private void createUpdateMSPLink() {
        final Connector connector1 = getRealConnector1();
        final Connector connector2 = getRealConnector2();

        updateMSPLink.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UpdateFilePage page = new UpdateFilePage(connector1, connector2);
                pageManager.show(page);
            }
        });
    }

    private Descriptor getConnector1() {
        return pluginManager.getDescriptor(file.getConnectorDataHolder1().getType());
    }

    private Descriptor getConnector2() {
        return pluginManager.getDescriptor(file.getConnectorDataHolder2().getType());
    }

    private Connector getRealConnector1() {
        final PluginFactory factory1 = pluginManager.getPluginFactory(file.getConnectorDataHolder1().getType());
        final ConnectorConfig config1 = (ConnectorConfig) file.getConnectorDataHolder1().getData();
        return factory1.createConnector(config1);
    }

    private Connector getRealConnector2() {
        final PluginFactory factory2 = pluginManager.getPluginFactory(file.getConnectorDataHolder2().getType());
        final ConnectorConfig config2 = (ConnectorConfig) file.getConnectorDataHolder2().getData();
        return factory2.createConnector(config2);
    }

    @Override
    public String getNavigationPanelTitle() {
        return file.getName();
    }
}
