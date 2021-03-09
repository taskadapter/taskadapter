package com.taskadapter.connector.trello.editor;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exception.FieldNotMappedException;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.LoginNameNotSpecifiedException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.connector.trello.TrelloClient;
import com.taskadapter.connector.trello.TrelloConfig;
import com.taskadapter.connector.trello.TrelloConnector;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanelWithKeyAndToken;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import scala.Option;
import scala.collection.JavaConverters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrelloEditorFactory implements PluginEditorFactory<TrelloConfig, WebConnectorSetup> {
    private static Messages messages = new Messages("com.taskadapter.connector.trello.messages");

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, TrelloConfig config, WebConnectorSetup setup) {
        VerticalLayout layout = new VerticalLayout();
//    layout.setWidth(600, PIXELS)
        TrelloClient client = new TrelloClient(setup.getPassword(), setup.getApiKey());
        Binder<TrelloConfig> binder = new Binder<>(TrelloConfig.class);
        ProjectPanel projectPanel = new ProjectPanel(
                binder,
                "boardName",
                Option.empty(),
                Option.empty(),
                () -> JavaConverters.seqAsJavaList(client.getBoards(setup.getUserName()))
                        .stream().map(b -> new NamedKeyedObjectImpl(b.getId(), b.getName()))
                        .collect(Collectors.toList()),
                null,
                null,
                this);
        projectPanel.setProjectKeyLabel(messages.get("projectPanel.projectLabel"));
        layout.add(projectPanel);

        binder.readBean(config);

        return new DefaultSavableComponent(projectPanel, () -> {
            try {
                binder.writeBean(config);
                return true;
            } catch (ValidationException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        return new ServerPanelWithKeyAndToken(TrelloConnector.ID(), TrelloConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(TrelloConnector.ID(), "My Trello",
                "https://api.trello.com", "", "", false, "");
    }

    @Override
    public List<BadConfigException> validateForSave(TrelloConfig config, WebConnectorSetup setup, List<FieldMapping<?>> fieldMappings) {
        List<BadConfigException> errors = new ArrayList<>();
        if (Strings.isNullOrEmpty(setup.getHost())) {
            errors.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(setup.getUserName())) {
            errors.add(new LoginNameNotSpecifiedException());
        }
        if (Strings.isNullOrEmpty(config.getBoardId())) {
            errors.add(new ProjectNotSetException());
        }

        errors.addAll(checkTrelloListIsMapped(fieldMappings));
        return errors;
    }

    private List<BadConfigException> checkTrelloListIsMapped(List<FieldMapping<?>> fieldMappings) {
        // "List Name" must be present on the right side and must be selected for export
        if (fieldMappings.stream()
                .noneMatch(m ->
                        m.getFieldInConnector2().isPresent() && m.getFieldInConnector2().get().equals(AllFields.taskStatus)
                                && m.isSelected())) {
            return Arrays.asList(new FieldNotMappedException("List Name"));
        }
        return new ArrayList<>();
    }

    @Override
    public List<BadConfigException> validateForLoad(TrelloConfig config, WebConnectorSetup setup) {
        List<BadConfigException> errors = new ArrayList<>();
        if (Strings.isNullOrEmpty(setup.getHost())) {
            errors.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getBoardId())) {
            errors.add(new ProjectNotSetException());
        }
        return errors;
    }

    @Override
    public void validateForDropInLoad(TrelloConfig config) throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

    @Override
    public String describeSourceLocation(TrelloConfig config, WebConnectorSetup setup) {
        return setup.getHost();
    }

    @Override
    public String describeDestinationLocation(TrelloConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return new Messages("com.taskadapter.connector.trello.field-names");
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ServerURLNotSetException) return messages.get("errors.serverURLNotSet");
        if (e instanceof LoginNameNotSpecifiedException) return messages.get("errors.loginNameNotSet");
        if (e instanceof ProjectNotSetException) return messages.get("errors.boardNotSet");
        if (e instanceof FieldNotMappedException) return messages.format("trello.error.requiredFieldNotMapped",
                ((FieldNotMappedException) e).getFieldName());
        if (!(e instanceof UnsupportedConnectorOperation)) {
            return e.getMessage();
        }
        UnsupportedConnectorOperation connEx = (UnsupportedConnectorOperation) e;
        if ("saveRelations".equals(connEx.getMessage())) {
            return messages.get("errors.unsupported.relations");
        }
        return e.getMessage();
    }
}
