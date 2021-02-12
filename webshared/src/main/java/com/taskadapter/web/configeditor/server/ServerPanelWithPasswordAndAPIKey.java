package com.taskadapter.web.configeditor.server;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.webui.Page;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import scala.Option;

import java.util.Optional;

public class ServerPanelWithPasswordAndAPIKey implements ConnectorSetupPanel {
    private final TextField labelField;
    private final TextField serverURL;
    private final PasswordField apiKeyField;
    private final TextField login;
    private final PasswordField password;
    private final Label errorMessageLabel;
    private final RadioButtonGroup<String> authOptionsGroup;
    private final VerticalLayout layout;
    private final Html captionLabel;

    private String connectorId;
    private WebConnectorSetup setup;

    public ServerPanelWithPasswordAndAPIKey(String connectorId, String caption, WebConnectorSetup setup) {
        this.connectorId = connectorId;
        this.setup = setup;
        layout = new VerticalLayout();
        captionLabel = new Html("<b>" + caption + "</b>");
        layout.add(caption);

        var binder = new Binder(WebConnectorSetup.class);

        labelField = EditorUtil.textInput(binder, "label");
        serverURL = EditorUtil.textInput(binder, "host");
        serverURL.setPlaceholder("http://myserver:3000/some_location");

        apiKeyField = EditorUtil.passwordInput(binder, "apiKey");
        login = EditorUtil.textInput(binder, "userName");
        password = EditorUtil.passwordInput(binder, "password");

        errorMessageLabel = new Label();
        errorMessageLabel.addClassName("error-message-label");

        authOptionsGroup = new RadioButtonGroup<String>();

        buildUI();
        binder.readBean(setup);

    }

    private void buildUI() {
        var form = new FormLayout();

        form.add(
                new Label(Page.message("setupPanel.name")),
                labelField);

        form.add(
                new Label(Page.message("setupPanel.serverUrl")),
                serverURL);

        authOptionsGroup.setLabel(Page.message("setupPanel.authorization"));
        authOptionsGroup.setItems(Page.message("setupPanel.useApiKey"),
                Page.message("setupPanel.useLogin"));
        var booleanValue = setup.useApiKey();
        var valueToSet = booleanValue ? Page.message("setupPanel.useApiKey")
                : Page.message("setupPanel.useLogin");

        authOptionsGroup.setValue(valueToSet);
        authOptionsGroup.addThemeVariants(RadioGroupVariant.MATERIAL_VERTICAL);
        authOptionsGroup.addValueChangeListener(value -> {
            var useAPIOptionSelected = shouldUseApiKey();
            setAuthOptionsState(useAPIOptionSelected);
        });

        form.add(authOptionsGroup, 2);

        form.add(
                new Label(Page.message("setupPanel.apiAccessKey")),
                apiKeyField);

        form.add(
                new Label(Page.message("setupPanel.login")),
                login);

        form.add(
                new Label(Page.message("setupPanel.password")),
                password);

        layout.add(captionLabel, form, errorMessageLabel);
    }

    private void setAuthOptionsState(boolean useAPIKey) {
        apiKeyField.setEnabled(useAPIKey);
        login.setEnabled(!useAPIKey);
        password.setEnabled(!useAPIKey);
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
        var host = serverURL.getValue();
        if (host == null || host.isEmpty() || host.equalsIgnoreCase(ServerPanelConstants.defaultUrlPrefix)) {
            return Optional.of(Page.message("newConfig.configure.serverUrlRequired"));
        }
        return Optional.empty();
    }

    @Override
    public WebConnectorSetup getResult() {
        return new WebConnectorSetup(connectorId, Option.empty(), labelField.getValue(),
                serverURL.getValue(), login.getValue(),
                password.getValue(), shouldUseApiKey(), apiKeyField.getValue());
    }

    @Override
    public void showError(String string) {
        errorMessageLabel.setText(string);
    }

    private boolean shouldUseApiKey() {
        return authOptionsGroup.getValue().equals(Page.message("setupPanel.useApiKey"));
    }
}
