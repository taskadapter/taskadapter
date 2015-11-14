package com.taskadapter.webui.config;

import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.uiapi.UIConnectorConfig;
import com.taskadapter.webui.ImageLoader;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class MiniPanel extends HorizontalLayout {
    private UIConnectorConfig config;
    private Window newWindow;
    private Label connectorLabel;

    public MiniPanel(UIConnectorConfig connectorConfig) {
        this.config = connectorConfig;
        buildUI();
        addLayoutClickListener((LayoutEvents.LayoutClickListener) event -> showEditConnectorDialog());
    }

    private void buildUI() {
        setHeight(37, Unit.PIXELS);
        setWidth(293, Unit.PIXELS);

        final HorizontalLayout inner = new HorizontalLayout();
        inner.setHeight(37, Unit.PIXELS);
        inner.setWidth(293, Unit.PIXELS);
        inner.setMargin(new MarginInfo(false, true, false, true));

        addStyleName("editableConnectorTitle");
        connectorLabel = new Label();
        inner.addComponent(connectorLabel);

        final Embedded editIcon = new Embedded(null, ImageLoader.getImage("edit.png"));
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
        newWindow.setCaption("Edit " + config.getConnectorTypeId()
                + " settings");
        newWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE);
        newWindow.addCloseListener((Window.CloseListener) e -> refreshLabel());
    }

    private void refreshLabel() {
        final Property<String> connectorLabel = new MethodProperty<>(config, "vaalabel");
        this.connectorLabel.setPropertyDataSource(connectorLabel);
    }

    public void showEditConnectorDialog() {
        newWindow.center();
        newWindow.setModal(true);

        getUI().addWindow(newWindow);
        newWindow.focus();
    }

    public void setPanelContents(ComponentContainer serverPanel) {
        newWindow.setContent(serverPanel);
    }
}
