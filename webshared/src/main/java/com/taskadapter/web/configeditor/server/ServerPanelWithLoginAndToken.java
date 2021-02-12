package com.taskadapter.web.configeditor.server;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.ui.HtmlLabel;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import scala.Option;

import java.util.Optional;

public class ServerPanelWithLoginAndToken implements ConnectorSetupPanel {

    private final String connectorId;
    private final String tokenDescription;

    private final TextField labelField;
    private final TextField hostField;
    private final TextField userLoginInput;
    private final PasswordField apiTokenField;
    private final VerticalLayout layout;
    private final Html captionLabel;
    private final Label errorMessageLabel;

    public ServerPanelWithLoginAndToken(String connectorId, String caption,
                                        WebConnectorSetup setup, String tokenDescription) {
        this.connectorId = connectorId;
        this.tokenDescription = tokenDescription;

        layout = new VerticalLayout();
        captionLabel = new Html("<b>" + caption + "</b>");

        var binder = new Binder(WebConnectorSetup.class);

        labelField = ServerPanelUtil.label(binder);
        labelField.addClassName("server-panel-textfield");

        hostField = ServerPanelUtil.host(binder);
        hostField.addClassName("server-panel-textfield");
        hostField.setPlaceholder("https://myserver:3000/some_location");

        userLoginInput = ServerPanelUtil.userName(binder);
        userLoginInput.addClassName("server-panel-textfield");

        apiTokenField = ServerPanelUtil.apiKey(binder);
        apiTokenField.addClassName("server-panel-textfield");

        binder.readBean(setup);

        errorMessageLabel = new Label();
        errorMessageLabel.addClassName("error-message-label");

        buildUI();
    }

    private void buildUI() {
        var form = new FormLayout();

        form.add(
                new Label(Page.message("setupPanel.name")),
                labelField);

        form.add(
                new Label(Page.message("setupPanel.serverUrl")),
                hostField);

        form.add(
                new Label(Page.message("setupPanel.login")),
                userLoginInput);

        var emptyLabelHeight = "10px";
        var emptyLabel = createEmptyLabel(emptyLabelHeight);
        form.add(
                emptyLabel,
                emptyLabel);

        form.add(new HtmlLabel(tokenDescription), 2);

        form.add(
                new HtmlLabel(Page.message("setupPanel.token")),
                apiTokenField);

        layout.add(captionLabel, form, errorMessageLabel);
    }


    private Label createEmptyLabel(String height) {
        var label = new HtmlLabel("&nbsp;");
        label.setHeight(height);
        return label;
    }

    @Override
    public Component getComponent() {
        return layout;
    }

    @Override
    public Optional<String> validate() {
        if (Strings.isNullOrEmpty(labelField.getValue())) {
            return Optional.of(Page.message("newConfig.configure.nameRequired"));
        }
        var host = hostField.getValue();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(ServerPanelConstants.defaultUrlPrefix)) {
            return Optional.of(Page.message("newConfig.configure.serverUrlRequired"));
        }
        return Optional.empty();
    }

    @Override
    public WebConnectorSetup getResult() {
        return new WebConnectorSetup(connectorId, Option.empty(), labelField.getValue(), hostField.getValue(),
                userLoginInput.getValue(),
                "", true, apiTokenField.getValue());
    }

    @Override
    public void showError(String string) {
        errorMessageLabel.setText(string);
    }
}
