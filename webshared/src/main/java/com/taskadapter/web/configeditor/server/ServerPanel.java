package com.taskadapter.web.configeditor.server;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import scala.Option;

import java.util.Optional;

public class ServerPanel implements ConnectorSetupPanel {

    private final VerticalLayout layout;
    private final Label errorMessageLabel;
    private String connectorId;
    private WebConnectorSetup setup;
    private final Html captionLabel;

    public ServerPanel(String connectorId, String caption, WebConnectorSetup setup) {
        this.connectorId = connectorId;
        this.setup = setup;

        captionLabel = new Html("<b>" + caption + "</b>");
        var serverContainer = new ServerContainer(setup);
        errorMessageLabel = new Label();
        errorMessageLabel.setVisible(false);
        layout = new VerticalLayout(captionLabel, serverContainer, errorMessageLabel);
        layout.setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
    }

    @Override
    public Component getUI() {
        return layout;
    }

    @Override
    public Optional<String> validate() {
        if (Strings.isNullOrEmpty(setup.label())) {
            return Optional.of(Page.message("newConfig.configure.nameRequired"));
        }
        if (Strings.isNullOrEmpty(setup.host())) {
            return Optional.of(Page.message("newConfig.configure.serverUrlRequired"));
        }
        return Optional.empty();
    }

    @Override
    public WebConnectorSetup getResult() {
        return new WebConnectorSetup(connectorId, Option.empty(), setup.label(), setup.host(), setup.userName(),
                setup.password(), false, "");
    }

    public void showError(String string) {
        errorMessageLabel.setText(string);
        errorMessageLabel.setVisible(true);
    }
}
