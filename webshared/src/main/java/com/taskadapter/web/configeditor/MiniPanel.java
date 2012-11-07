package com.taskadapter.web.configeditor;

import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

public class MiniPanel extends GridLayout {
    private static final int COLUMNS_NUMBER = 3;

    private WindowProvider windowProvider;
    private UIConnectorConfig config;
    private Window newWindow;
    private Label connectorLabel;

    public MiniPanel(WindowProvider windowProvider, UIConnectorConfig connectorConfig) {
        this.windowProvider = windowProvider;
        this.config = connectorConfig;
        buildUI();
    }

    private void buildUI() {
        setColumns(COLUMNS_NUMBER);
        setMargin(true);
        setSpacing(true);

        connectorLabel = new Label();
        connectorLabel.addStyleName("connectorLabelInBlueBox");
        addComponent(connectorLabel);
        refreshLabel();
        configureEditServerWindow();
    }

    private void configureEditServerWindow() {
        newWindow = new Window();
        newWindow.setCaption("Edit " + config.getConnectorTypeId() + " settings");
        newWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        newWindow.addListener(new Window.CloseListener() {
            @Override
            public void windowClose(Window.CloseEvent e) {
                refreshLabel();
            }
        });
    }

    private void refreshLabel() {
        Property connectorLabel = EditorUtil.wrapNulls(new MethodProperty<String>(config.getRawConfig(), "label"));
        this.connectorLabel.setPropertyDataSource(connectorLabel);
    }

    public void showEditConnectorDialog() {
        newWindow.center();
        newWindow.setModal(true);

        windowProvider.getWindow().addWindow(newWindow);
        newWindow.focus();
    }

    public void setPanelContents(ComponentContainer serverPanel) {
        newWindow.setContent(serverPanel);
    }
}
