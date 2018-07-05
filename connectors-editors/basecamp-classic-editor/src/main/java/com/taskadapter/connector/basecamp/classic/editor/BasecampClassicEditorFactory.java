package com.taskadapter.connector.basecamp.classic.editor;

import com.taskadapter.connector.basecamp.classic.BasecampClassicConfig;
import com.taskadapter.connector.basecamp.classic.BasecampClassicConnector;
import com.taskadapter.connector.basecamp.classic.BasecampConfigValidator;
import com.taskadapter.connector.basecamp.classic.BasecampUtils;
import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.DroppingNotSupportedException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import scala.Option;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;

public class BasecampClassicEditorFactory implements PluginEditorFactory<BasecampClassicConfig, WebConnectorSetup> {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());
    private final ExceptionFormatter formatter = new BasecampErrorFormatter();
    private static final String BUNDLE_NAME = "com.taskadapter.connector.basecamp.classic.editor.messages";
    private static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        return ServerPanelFactory.withApiKeyAndLoginPassword(BasecampClassicConnector.ID(), BasecampClassicConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup() {
        return new WebConnectorSetup(BasecampClassicConnector.ID(), Option.empty(),
                "My Basecamp Classic", "https://-my-project-name-here-.basecamphq.com", "",
                "", true, "");
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, BasecampClassicConfig config, WebConnectorSetup setup) {

        Panel projectPanel = createProjectPanel(config, setup);

        GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setMargin(true);
        grid.setSpacing(true);

        grid.addComponent(projectPanel);
        return grid;
    }

    private Panel createProjectPanel(final BasecampClassicConfig config, WebConnectorSetup setup) {
        Panel projectPanel = new Panel("Project");

        GridLayout grid = new GridLayout();
        grid.setColumns(4);
        grid.setMargin(true);
        grid.setSpacing(true);

        projectPanel.setContent(grid);

        addProjectRow(config, setup, grid);
        addTodoKeyRow(config, setup, grid);
        return projectPanel;
    }

    private void addProjectRow(final BasecampClassicConfig config, WebConnectorSetup setup, GridLayout grid) {
        Label projectKeyLabel = new Label("Project key:");
        addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel);

        MethodProperty<String> projectKeyProperty = new MethodProperty<>(config, "projectKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(projectKeyProperty));
        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.loadProject(config, setup, formatter, factory);
                    }
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        DataProvider<List<? extends NamedKeyedObject>> projectProvider = () -> {
            List<BasecampProject> basecampProjects = BasecampUtils.loadProjects(factory, setup);
            List<NamedKeyedObject> objects = new ArrayList<>();
            for (BasecampProject project : basecampProjects) {
                objects.add(new NamedKeyedObjectImpl(project.getKey(), project.getName()));
            }
            return objects;
        };

        Button showProjectsButton = EditorUtil.createLookupButton(
                "...",
                "Show list of available projects on the server.",
                "Select project",
                "List of projects on the server",
                projectProvider,
                formatter,
                namedKeyedObject -> {
                    projectKeyProperty.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton);
    }

    private void addTodoKeyRow(final BasecampClassicConfig config, WebConnectorSetup setup, GridLayout grid) {
        Label todoListKey = new Label("Todo list key:");
        addTo(grid, Alignment.MIDDLE_LEFT, todoListKey);

        MethodProperty<String> todoKeyProperty = new MethodProperty<>(config, "todoKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(todoKeyProperty));

        Button infoButton = EditorUtil.createButton("Info", "View the todo list info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.showTodoListInfo(config, setup, formatter, factory);
                    }
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        DataProvider<List<? extends NamedKeyedObject>> todoListsProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                List<TodoList> todoLists = BasecampUtils.loadTodoLists(factory, config, setup);
                List<NamedKeyedObject> objects = new ArrayList<>();
                for (TodoList todoList : todoLists) {
                    objects.add(new NamedKeyedObjectImpl(todoList.getKey(), todoList.getName()));
                }
                return objects;
            }
        };

        Button showTodoListsButton = EditorUtil.createLookupButton(
                "...",
                "Show Todo Lists",
                "Select a Todo list",
                "Todo lists on the server",
                todoListsProvider,
                formatter,
                namedKeyedObject -> {
                    todoKeyProperty.setValue(namedKeyedObject.getKey());
                    return null;
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, showTodoListsButton);
    }

    @Override
    public void validateForSave(BasecampClassicConfig config, WebConnectorSetup setup, Seq<FieldMapping<?>> fieldMappings) throws BadConfigException {
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public Seq<BadConfigException> validateForLoad(BasecampClassicConfig config, WebConnectorSetup setup) {
        return BasecampConfigValidator.validateTodoListNoException(config);
    }

    @Override
    public String describeSourceLocation(BasecampClassicConfig config, WebConnectorSetup setup) {
        return setup.host();
    }

    @Override
    public String describeDestinationLocation(BasecampClassicConfig config, WebConnectorSetup setup) {
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

    @Override
    public WebConnectorSetup updateForSave(BasecampClassicConfig config, Sandbox sandbox, WebConnectorSetup setup,
                                           Seq<FieldMapping<?>> fieldMappings)
            throws BadConfigException {
        validateForSave(config, setup, fieldMappings);
        return setup;
    }

    @Override
    public void validateForDropInLoad(BasecampClassicConfig config)
            throws DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
