package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampUtils;
import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.configeditor.server.ServerPanelWithAPIKey;
import com.taskadapter.web.service.Services;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.propertyInput;
import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig> {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());
    private final ExceptionFormatter formatter = new BasecampErrorFormatter();

    @Override
    public ComponentContainer getMiniPanelContents(WindowProvider windowProvider, Services services, BasecampConfig config) {

        Panel panel = createServerPanel(config);
        Panel projectPanel = createProjectPanel(windowProvider, config);

        GridLayout grid = new GridLayout();
        grid.setColumns(2);
        grid.setMargin(true);
        grid.setSpacing(true);

        grid.addComponent(panel);
        grid.addComponent(projectPanel);
        return grid;
    }

    private Panel createServerPanel(BasecampConfig config) {
        Panel panel = new Panel("Server Info");
        MethodProperty<String> serverURLProperty = new MethodProperty<String>(config, "serverUrl");
        serverURLProperty.setReadOnly(true);
        ServerPanelWithAPIKey redmineServerPanel = new ServerPanelWithAPIKey(new MethodProperty<String>(config, "label"),
                serverURLProperty,
                new MethodProperty<String>(config.getAuth(), "login"),
                new MethodProperty<String>(config.getAuth(), "password"),
                new MethodProperty<String>(config.getAuth(), "apiKey"),
                new MethodProperty<Boolean>(config.getAuth(), "useAPIKeyInsteadOfLoginPassword"));
        panel.addComponent(redmineServerPanel);
        return panel;
    }

    private Panel createProjectPanel(final WindowProvider windowProvider, final BasecampConfig config) {
        Panel projectPanel = new Panel("Project");

        GridLayout grid = new GridLayout();
        grid.setColumns(4);
        grid.setMargin(true);
        grid.setSpacing(true);

        projectPanel.addComponent(grid);

        addAccountIdRow(config, grid);
        addProjectRow(windowProvider, config, grid);
        addTodoKeyRow(windowProvider, config, grid);
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

    private void addProjectRow(final WindowProvider windowProvider, final BasecampConfig config, GridLayout grid) {
        Label projectKeyLabel = new Label("Project key:");
        addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel);

        MethodProperty<Object> projectKeyProperty = new MethodProperty<Object>(config, "projectKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(projectKeyProperty));
        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.loadProject(windowProvider, config, formatter, factory);
                    }
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        DataProvider<List<? extends NamedKeyedObject>> projectProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                List<BasecampProject> basecampProjects = BasecampUtils.loadProjects(factory, config);
                List<NamedKeyedObject> objects = new ArrayList<NamedKeyedObject>();
                for (BasecampProject project : basecampProjects) {
                    objects.add(new NamedKeyedObjectImpl(project.getKey(), project.getName()));
                }
                return objects;
            }
        };

        Button showProjectsButton = EditorUtil.createLookupButton(
                windowProvider,
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

    private void addTodoKeyRow(final WindowProvider windowProvider, final BasecampConfig config, GridLayout grid) {
        Label todoListKey = new Label("Todo list key:");
        addTo(grid, Alignment.MIDDLE_LEFT, todoListKey);

        MethodProperty<Object> todoKeyProperty = new MethodProperty<Object>(config, "todoKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(todoKeyProperty));

        Button infoButton = EditorUtil.createButton("Info", "View the todo list info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.showTodoListInfo(windowProvider, config, formatter, factory);
                    }
                }
        );
        addTo(grid, Alignment.MIDDLE_CENTER, infoButton);

        DataProvider<List<? extends NamedKeyedObject>> todoListsProvider = new DataProvider<List<? extends NamedKeyedObject>>() {
            @Override
            public List<? extends NamedKeyedObject> loadData() throws ConnectorException {
                List<TodoList> todoLists = BasecampUtils.loadTodoLists(factory, config);
                List<NamedKeyedObject> objects = new ArrayList<NamedKeyedObject>();
                for (TodoList todoList : todoLists) {
                    objects.add(new NamedKeyedObjectImpl(todoList.getKey(), todoList.getName()));
                }
                return objects;
            }
        };

        Button showTodoListsButton = EditorUtil.createLookupButton(
                windowProvider,
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
    public void validateForSave(BasecampConfig config) throws BadConfigException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void validateForLoad(BasecampConfig config) throws BadConfigException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String describeSourceLocation(BasecampConfig config) {
        return "basecamp.com";
    }

    @Override
    public String describeDestinationLocation(BasecampConfig config) {
        return "basecamp.com";
    }

    @Override
    public String formatError(Throwable e) {
        return formatter.formatError(e);
    }
}
