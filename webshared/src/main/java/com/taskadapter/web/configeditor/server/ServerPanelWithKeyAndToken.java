package com.taskadapter.web.configeditor.server;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
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

public class ServerPanelWithKeyAndToken implements ConnectorSetupPanel {
    private final Html captionLabel;
    private final VerticalLayout layout;
    private final TextField labelField;
    private final TextField hostField;
    private final TextField userLoginInput;
    private final PasswordField apiKeyField;
    private final Label errorMessageLabel;
    private final PasswordField tokenField;
    private String connectorId;

    public ServerPanelWithKeyAndToken(String connectorId, String caption, WebConnectorSetup setup) {
        this.connectorId = connectorId;

        captionLabel = new Html("<b>" + caption + "</b>");

        layout = new VerticalLayout();

        var binder = new Binder(WebConnectorSetup.class);

        labelField = ServerPanelUtil.label(binder);
        labelField.addClassName("server-panel-textfield");

        hostField = ServerPanelUtil.host(binder);
        userLoginInput = ServerPanelUtil.userName(binder);

        apiKeyField = ServerPanelUtil.apiKey(binder);
        apiKeyField.addClassName("server-panel-textfield");

        tokenField = ServerPanelUtil.password(binder);
        tokenField.addClassName("server-panel-textfield");

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

        form.add(
                new Label(Page.message("setupPanel.apiAccessKey")),
                apiKeyField);

        form.add(
                new Label(Page.message("setupPanel.token")),
                tokenField);

        layout.add(captionLabel, form, errorMessageLabel);
    }

    @Override
    public Component getUI() {
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
        return new WebConnectorSetup(connectorId, Option.empty(), labelField.getValue(),
                hostField.getValue(), userLoginInput.getValue(),
                apiKeyField.getValue(), true, tokenField.getValue());
    }

    @Override
    public void showError(String string) {
        errorMessageLabel.setText(string);
    }
}
