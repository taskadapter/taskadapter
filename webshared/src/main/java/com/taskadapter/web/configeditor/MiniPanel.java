package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.web.WindowProvider;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

public class MiniPanel extends GridLayout {
    private static final int COLUMNS_NUMBER = 3;

    private WindowProvider windowProvider;
    private String connectorType;
    private ConnectorConfig config;
    private Window newWindow;
    private Label dataLocationLabel;

    public MiniPanel(WindowProvider windowProvider, String connectorType, ConnectorConfig config) {
        this.windowProvider = windowProvider;
        this.connectorType = connectorType;
        this.config = config;
        buildU();
    }

    private void buildU() {
        setColumns(COLUMNS_NUMBER);
        setMargin(true);
        setSpacing(true);

        dataLocationLabel = new Label();
        addComponent(dataLocationLabel);
        refreshLabel();

        Button editServerButton = EditorUtil.createButton("Edit", "Edit the server settings",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        showEditServerDialog();
                    }
                }
        );
        addComponent(editServerButton);

        configureEditServerWindow();
    }

    private void configureEditServerWindow() {
        newWindow = new Window();
        newWindow.setCaption("Edit " + connectorType + " settings");
        newWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        newWindow.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent e) {
                refreshLabel();
            }
        });
    }

    private void refreshLabel() {
        Property connectorLabel = EditorUtil.wrapNulls(new MethodProperty<String>(config, "label"));
        dataLocationLabel.setPropertyDataSource(connectorLabel);
    }

    private void showEditServerDialog() {
        newWindow.center();
        newWindow.setModal(true);

        windowProvider.getWindow().addWindow(newWindow);
        newWindow.focus();
    }

    public void setPanelContents(ComponentContainer serverPanel) {
        newWindow.setContent(serverPanel);
    }
}
