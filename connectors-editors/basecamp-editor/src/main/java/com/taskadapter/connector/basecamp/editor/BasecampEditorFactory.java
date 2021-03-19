package com.taskadapter.connector.basecamp.editor;

import com.google.common.base.Strings;
import com.taskadapter.common.ui.FieldMapping;
import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampConnector;
import com.taskadapter.connector.basecamp.BasecampUtils;
import com.taskadapter.connector.basecamp.BasecampValidator;
import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.server.ServerPanelWithPasswordAndAPIKey;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.DefaultSavableComponent;
import com.taskadapter.web.uiapi.SavableComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig, WebConnectorSetup> {
    private static final String BUNDLE_NAME = "com.taskadapter.connector.basecamp.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);
    private static final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());
    private static final BasecampErrorFormatter formatter = new BasecampErrorFormatter();

    @Override
    public SavableComponent getMiniPanelContents(Sandbox sandbox, BasecampConfig config, WebConnectorSetup setup) {
        Binder<BasecampConfig> binder = new Binder<>(BasecampConfig.class);

        Component projectPanel = createProjectPanel(binder, config, setup);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("50em", 1),
                new FormLayout.ResponsiveStep("50em", 2));

        layout.add(projectPanel);
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
        return new ServerPanelWithPasswordAndAPIKey(BasecampConnector.ID, BasecampConnector.ID, setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup(Sandbox sandbox) {
        return WebConnectorSetup.apply(BasecampConnector.ID,
                "My Basecamp 2", ObjectAPI.BASECAMP_URL, "", "", false, "");
    }

    @Override
    public List<BadConfigException> validateForSave(BasecampConfig config, WebConnectorSetup setup, List<FieldMapping<?>> fieldMappings) {
        List<BadConfigException> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(config.getProjectKey())) {
            list.add(new ProjectNotSetException());
        }
        return list;
    }

    @Override
    public List<BadConfigException> validateForLoad(BasecampConfig config, WebConnectorSetup setup) {
        return BasecampValidator.validateConfig(config);
    }

    @Override
    public void validateForDropInLoad(BasecampConfig config) throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }

    @Override
    public String describeSourceLocation(BasecampConfig config, WebConnectorSetup setup) {
        return setup.getHost();
    }

    @Override
    public String describeDestinationLocation(BasecampConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public Messages fieldNames() {
        return MESSAGES;
    }

    @Override
    public String formatError(Throwable e) {
        return formatter.formatError(e);
    }


    private Component createProjectPanel(Binder<BasecampConfig> binder, BasecampConfig config, WebConnectorSetup setup) {

        Label accountIdLabel = new Label("Account Id");
        TextField accountIdField = EditorUtil.textInput(binder, "accountId");

        Label projectLabel = new Label("Project key");
        TextField projectField = EditorUtil.textInput(binder, "projectKey");
        Button projectInfoButton = EditorUtil.createButton("Info", "View the project info",
                event -> ShowInfoElement.loadProject(config, setup, formatter, factory)
        );
        DataProvider<List<? extends NamedKeyedObject>> projectProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                List<BasecampProject> basecampProjects = BasecampUtils.loadProjects(factory, config, setup);
                List<NamedKeyedObject> objects = new ArrayList<>();
                for (BasecampProject project : basecampProjects) {
                    objects.add(new NamedKeyedObjectImpl(project.getKey(), project.getName()));
                }
                return objects;
            }
        };

        Button showProjectsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available projects on the server.",
                "Select a project",
                projectProvider,
                formatter,
                namedKeyedObject -> {
                    projectField.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );

        Label todoListKeyLabel = new Label("Todo list key");
        TextField todoListKeyField = EditorUtil.textInput(binder, "todoKey");
        Button todoListInfoButton = EditorUtil.createButton("Info", "View the todo list info",
                event -> ShowInfoElement.showTodoListInfo(config, setup, formatter, factory)
        );

        DataProvider<List<? extends NamedKeyedObject>> todoListsProvider = () -> {
            List<TodoList> todoLists = BasecampUtils.loadTodoLists(factory, config, setup);
            List<NamedKeyedObject> objects = new ArrayList<>();
            for (TodoList todoList : todoLists) {
                objects.add(new NamedKeyedObjectImpl(todoList.getKey(), todoList.getName()));
            }
            return objects;
        };

        Button showTodoListsButton = EditorUtil.createLookupButton(
                "...",
                "Show Todo Lists",
                "Select a Todo list",
                todoListsProvider,
                formatter,
                namedKeyedObject -> {
                    todoListKeyField.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );

        Checkbox findUserByName = EditorUtil.checkbox("Find users based on assignee's name",
                "This option can be useful when you need to export a new MSP project file to Redmine/JIRA/MantisBT/....\n"
                        + "Task Adapter can load the system's users by resource names specified in the MSP file\n"
                        + "and assign the new tasks to them.\n"
                        + "Note: this operation usually requires 'Admin' permission in the system.",
                binder, "findUserByName");

        Checkbox loadCompletedItemsCheckbox = EditorUtil.checkbox("Load completed items",
                "",
                binder, "loadCompletedTodos");

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("20em", 1),
                new FormLayout.ResponsiveStep("60em", 2),
                new FormLayout.ResponsiveStep("20em", 3),
                new FormLayout.ResponsiveStep("20em", 4));

        layout.add(accountIdLabel);
        layout.add(accountIdField, 3);

        layout.add(projectLabel, projectField, projectInfoButton, showProjectsButton);

        layout.add(todoListKeyLabel, todoListKeyField, todoListInfoButton, showTodoListsButton);

        layout.add(loadCompletedItemsCheckbox, 4);
        layout.add(findUserByName, 4);

        return layout;
    }

}
