package com.taskadapter.web.configeditor;

import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

public class MiniPanel extends HorizontalLayout {
    private WindowProvider windowProvider;
    private UIConnectorConfig config;
    private Window newWindow;
    private Label connectorLabel;

    public MiniPanel(WindowProvider windowProvider, UIConnectorConfig connectorConfig) {
        this.windowProvider = windowProvider;
        this.config = connectorConfig;
        buildUI();
        addListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
                showEditConnectorDialog();
            }
        });
    }

    private void buildUI() {
        setHeight(37, Sizeable.UNITS_PIXELS);
        setWidth(293, Sizeable.UNITS_PIXELS);
        
        final HorizontalLayout inner = new HorizontalLayout();
        inner.setHeight(37, Sizeable.UNITS_PIXELS);
        inner.setWidth(293, Sizeable.UNITS_PIXELS);
        inner.setMargin(false, true, false, true);

        addStyleName("editableConnectorTitle");
        connectorLabel = new Label();
        inner.addComponent(connectorLabel);
        
        final Embedded editIcon = new Embedded(null, new ThemeResource("img/edit.png"));
        inner.addComponent(editIcon);
        
        inner.setComponentAlignment(editIcon, Alignment.MIDDLE_RIGHT);
        inner.setComponentAlignment(connectorLabel, Alignment.MIDDLE_LEFT);
        inner.setExpandRatio(connectorLabel, 1.0f);
        addComponent(inner);
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
