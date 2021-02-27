package com.taskadapter.connector.mantis.editor;

import com.google.common.base.Strings;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.mantis.MantisConnector;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.configeditor.ProjectPanel;
import com.taskadapter.web.configeditor.server.ServerPanel;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

public class MantisEditorFactory implements PluginEditorFactory<MantisConfig, WebConnectorSetup> {

    private static String BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages";
    private static Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, MantisConfig config, WebConnectorSetup setup) {
        Binder<MantisConfig> binder = new Binder<>(MantisConfig.class);
        ProjectPanel projectPanel = new ProjectPanel(
                binder,
                "projectKey",
                Option.apply("queryId"),
                Option.empty(),
                new MantisProjectsListLoader(setup),
                null,
                new MantisQueryListLoader(config, setup),
                this);

        VerticalLayout layout = new VerticalLayout();
        layout.add(projectPanel,
                new OtherMantisFieldsPanel(binder));
        binder.readBean(config);

        return new DefaultSavableComponent(layout, () -> {
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
        return new ServerPanel(MantisConnector.ID(), MantisConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(MantisConnector.ID(),
                "My MantisBT", "http://", "", "", false, "");
    }

    @Override
    public List<BadConfigException> validateForSave(MantisConfig config, WebConnectorSetup setup,
                                                    List<FieldMapping<?>> fieldMappings) {
        List<BadConfigException> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(setup.getHost())) {
            list.add(new ServerURLNotSetException());
        }
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            list.add(new ProjectNotSetException());
        }
        return list;
    }

    @Override
    public List<BadConfigException> validateForLoad(MantisConfig config, WebConnectorSetup setup) {
        List<BadConfigException> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(setup.getHost())) {
            list.add(new ServerURLNotSetException());
        }
        if (Strings.isNullOrEmpty(config.getProjectKey()) && config.getQueryId() == null) {
            list.add(new BothProjectKeyAndQueryIsAreMissingException());
        }
        return list;
    }

    @Override
    public void validateForDropInLoad(MantisConfig config) throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

    @Override
    public String describeSourceLocation(MantisConfig config, WebConnectorSetup setup) {
        return setup.getHost();
    }

    @Override
    public String describeDestinationLocation(MantisConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }

    @Override
    public String formatError(Throwable e) {
        if (e instanceof ProjectNotSetException) return MESSAGES.get("error.projectNotSet");
        if (e instanceof ServerURLNotSetException) return MESSAGES.get("error.serverUrlNotSet");
        if (e instanceof BothProjectKeyAndQueryIsAreMissingException)
            return MESSAGES.get("mantisbt.error.bothProjectKeyAndQueryIdAreMissing");
        if (e instanceof UnsupportedOperationException) {
            UnsupportedOperationException uop = (UnsupportedOperationException) e;
            if ("saveRelations".equals(uop.getMessage())) return MESSAGES.get("error.unsupported.relations");
        }
        return e.toString();
    }
}
