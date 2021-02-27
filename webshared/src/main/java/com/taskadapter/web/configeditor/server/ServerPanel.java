package com.taskadapter.web.configeditor.server;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.configeditor.EditorUtil;
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

import java.util.Optional;

public class ServerPanel extends VerticalLayout implements ConnectorSetupPanel {

    private String connectorId;
    private WebConnectorSetup setup;
    private final HtmlLabel introTextLabel;
    private final Label passwordFieldLabel;
    private final HtmlLabel passwordHelpText;
    private TextField labelField;
    private TextField hostField;
    private PasswordField password;
    private Label errorMessageLabel;
    private TextField userLoginInput;

    public ServerPanel(String connectorId, String caption, WebConnectorSetup setup) {
        this.connectorId = connectorId;
        this.setup = setup;

        var binder = new Binder(WebConnectorSetup.class);
        var captionLabel = new Html("<b>" + caption + "</b>");

        labelField = ServerPanelUtil.label(binder);
        hostField = ServerPanelUtil.host(binder);
        hostField.setPlaceholder("https://myserver:3000/some_location");
        EditorUtil.setTooltip(hostField, Page.message("setupPanel.serverUrlHint"));
        userLoginInput = ServerPanelUtil.userName(binder);
        password = ServerPanelUtil.password(binder);

        errorMessageLabel = new Label();
        errorMessageLabel.addClassName("error-message-label");


        introTextLabel = new HtmlLabel("");

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

        passwordHelpText = new HtmlLabel("");
        form.add(passwordHelpText, 2);

        passwordFieldLabel = new Label(Page.message("setupPanel.password"));
        form.add(
                passwordFieldLabel,
                password);
        binder.readBean(setup);
        add(captionLabel, introTextLabel, form, errorMessageLabel);

    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Optional<String> validate() {
        if (Strings.isNullOrEmpty(setup.getLabel())) {
            return Optional.of(Page.message("newConfig.configure.nameRequired"));
        }
        if (Strings.isNullOrEmpty(setup.getHost())) {
            return Optional.of(Page.message("newConfig.configure.serverUrlRequired"));
        }
        return Optional.empty();
    }

    @Override
    public WebConnectorSetup getResult() {
        cleanup();
        return WebConnectorSetup.apply(connectorId,
                labelField.getValue(),
                hostField.getValue(),
                userLoginInput.getValue(),
                password.getValue(),
                false, "");
    }

    public void cleanup() {
        if (getHostString().endsWith("/")) {
            hostField.setValue(getHostString().substring(0, getHostString().length() - 1));
        }
    }

    private String getHostString() {
        return hostField.getValue();
    }

    public void showError(String string) {
        errorMessageLabel.setText(string);
        errorMessageLabel.setVisible(true);
    }

    public ServerPanel setIntroText(String text) {
        introTextLabel.setText(text);
        return this;
    }

    public ServerPanel setPasswordHelp(String text) {
        passwordHelpText.setText(text);
        return this;
    }

    public ServerPanel setPasswordFieldLabel(String text) {
        passwordFieldLabel.setText(text);
        return this;
    }
}
