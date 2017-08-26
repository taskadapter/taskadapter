package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampConnector;
import com.taskadapter.connector.basecamp.BasecampUtils;
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
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.server.ServerPanelFactory;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.propertyInput;
import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig, WebConnectorSetup> {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());
    private final ExceptionFormatter formatter = new BasecampErrorFormatter();

    @Override
    public boolean isWebConnector() {
        return true;
    }

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, BasecampConfig config, WebConnectorSetup setup) {

        Panel projectPanel = createProjectPanel(config, setup);
        GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setMargin(true);
        grid.setSpacing(true);
        grid.addComponent(projectPanel);
        return grid;
    }

    @Override
    public ConnectorSetupPanel getEditSetupPanel(Sandbox sandbox, WebConnectorSetup setup) {
        return ServerPanelFactory.withApiKeyAndLoginPassword(BasecampConnector.ID(), BasecampConnector.ID(), setup);
    }

    @Override
    public WebConnectorSetup createDefaultSetup() {
        return new WebConnectorSetup(BasecampConnector.ID(), Option.empty(), "My Basecamp 2", ObjectAPI.BASECAMP_URL, "",
                "", false, "");
    }

    private Panel createProjectPanel(BasecampConfig config, WebConnectorSetup setup) {
        Panel projectPanel = new Panel("Project");

        GridLayout grid = new GridLayout();
        grid.setColumns(4);
        grid.setMargin(true);
        grid.setSpacing(true);

        projectPanel.setContent(grid);

        addAccountIdRow(config, grid);
        addProjectRow(config, setup, grid);
        addTodoKeyRow(config, setup, grid);
        addCompletedCheckboxRow(config, grid);

        return projectPanel;
    }

    private void addCompletedCheckboxRow(BasecampConfig config, GridLayout grid) {
        CheckBox loadCompletedTasksCheckbox = new CheckBox("Load completed items");
        loadCompletedTasksCheckbox.setPropertyDataSource(new MethodProperty<String>(config, "loadCompletedTodos"));
        grid.addComponent(loadCompletedTasksCheckbox);
        grid.addComponent(new Label(""));
        grid.addComponent(new Label(""));
        grid.addComponent(new Label(""));
    }

    private void addAccountIdRow(BasecampConfig config, GridLayout grid) {
        Label accountIdLabel = new Label("Account Id:");
        grid.addComponent(accountIdLabel);
        TextField accountIdField = propertyInput(config, "accountId");
        grid.addComponent(accountIdField);
        grid.addComponent(new Label(""));
        grid.addComponent(new Label(""));
    }

    private void addProjectRow(final BasecampConfig config, WebConnectorSetup setup, GridLayout grid) {
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
                "Select project",
                "List of projects on the server",
                projectProvider,
                projectKeyProperty,
                false, formatter
        );
        addTo(grid, Alignment.MIDDLE_CENTER, showProjectsButton);
    }

    private void addTodoKeyRow(final BasecampConfig config, WebConnectorSetup setup, GridLayout grid) {
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
                "Todo lists on the server",
                todoListsProvider,
                todoKeyProperty,
                false, formatter
        );
        addTo(grid, Alignment.MIDDLE_CENTER, showTodoListsButton);
    }

    @Override
    public void validateForSave(BasecampConfig config, WebConnectorSetup setup) throws BadConfigException {
        if (config.getProjectKey() == null || config.getProjectKey().isEmpty()) {
            throw new ProjectNotSetException();
        }
    }

    @Override
    public void validateForLoad(BasecampConfig config, WebConnectorSetup setup) {
    }

    @Override
    public String describeSourceLocation(BasecampConfig config, WebConnectorSetup setup) {
        return setup.host();
    }

    @Override
    public String describeDestinationLocation(BasecampConfig config, WebConnectorSetup setup) {
        return describeSourceLocation(config, setup);
    }

    @Override
    public String formatError(Throwable e) {
        return formatter.formatError(e);
    }

    @Override
    public WebConnectorSetup updateForSave(BasecampConfig config, Sandbox sandbox, WebConnectorSetup setup)
            throws BadConfigException {
        validateForSave(config, setup);
        return setup;
    }


    @Override
    public void validateForDropInLoad(BasecampConfig config)
            throws BadConfigException, DroppingNotSupportedException {
        throw DroppingNotSupportedException.INSTANCE;
    }
}
