package com.taskadapter.connector.basecamp.classic.editor;

import com.taskadapter.connector.basecamp.classic.BasecampConfig;
import com.taskadapter.connector.basecamp.classic.BasecampConfigValidator;
import com.taskadapter.connector.basecamp.classic.BasecampUtils;
import com.taskadapter.connector.basecamp.classic.beans.BasecampProject;
import com.taskadapter.connector.basecamp.classic.beans.TodoList;
import com.taskadapter.connector.basecamp.classic.transport.BaseCommunicator;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.NamedKeyedObject;
import com.taskadapter.model.NamedKeyedObjectImpl;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.callbacks.DataProvider;
import com.taskadapter.web.configeditor.EditorUtil;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.web.configeditor.EditorUtil.passwordInput;
import static com.taskadapter.web.configeditor.EditorUtil.textInput;
import static com.taskadapter.web.ui.Grids.addTo;

public class BasecampEditorFactory implements PluginEditorFactory<BasecampConfig> {

    private final ObjectAPIFactory factory = new ObjectAPIFactory(new BaseCommunicator());
    private final ExceptionFormatter formatter = new BasecampErrorFormatter();

    @Override
    public ComponentContainer getMiniPanelContents(Sandbox sandbox, BasecampConfig config) {

        Panel panel = createServerPanel(config);
        Panel projectPanel = createProjectPanel(config);

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
        GridLayout grid = new GridLayout();
        grid.setColumns(2);

        Label descriptionLabel = new Label("Description:");
        addTo(grid, Alignment.MIDDLE_LEFT, descriptionLabel);
        MethodProperty<String> descriptionProperty = new MethodProperty<String>(config, "label");
        TextField descriptionField = textInput(descriptionProperty);
        descriptionField.addStyleName("server-panel-textfield");
        addTo(grid, Alignment.MIDDLE_LEFT, descriptionField);

        Label serverURLLabel = new Label("Server URL:");
        addTo(grid, Alignment.MIDDLE_LEFT, serverURLLabel);
        MethodProperty<String> serverURLProperty = new MethodProperty<String>(config, "serverUrl");
        TextField urlField = textInput(serverURLProperty);
        urlField.addStyleName("server-panel-textfield");
        addTo(grid, Alignment.MIDDLE_LEFT, urlField);

        Label apiKeyLabel = new Label("API access key:");
        addTo(grid, Alignment.MIDDLE_LEFT, apiKeyLabel);
        MethodProperty<Object> apiKey = new MethodProperty<Object>(config, "apiKey");
        PasswordField passwordField = passwordInput(apiKey);
        passwordField.addStyleName("server-panel-textfield");
        addTo(grid, Alignment.MIDDLE_LEFT, passwordField);

        panel.setContent(grid);
        return panel;
    }

    private Panel createProjectPanel(final BasecampConfig config) {
        Panel projectPanel = new Panel("Project");

        GridLayout grid = new GridLayout();
        grid.setColumns(4);
        grid.setMargin(true);
        grid.setSpacing(true);

        projectPanel.setContent(grid);

        addProjectRow(config, grid);
        addTodoKeyRow(config, grid);
        return projectPanel;
    }

    private void addProjectRow(final BasecampConfig config, GridLayout grid) {
        Label projectKeyLabel = new Label("Project key:");
        addTo(grid, Alignment.MIDDLE_LEFT, projectKeyLabel);

        MethodProperty<Object> projectKeyProperty = new MethodProperty<Object>(config, "projectKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(projectKeyProperty));
        Button infoButton = EditorUtil.createButton("Info", "View the project info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.loadProject(config, formatter, factory);
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

    private void addTodoKeyRow(final BasecampConfig config, GridLayout grid) {
        Label todoListKey = new Label("Todo list key:");
        addTo(grid, Alignment.MIDDLE_LEFT, todoListKey);

        MethodProperty<Object> todoKeyProperty = new MethodProperty<Object>(config, "todoKey");
        addTo(grid, Alignment.MIDDLE_LEFT, textInput(todoKeyProperty));

        Button infoButton = EditorUtil.createButton("Info", "View the todo list info",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        ShowInfoElement.showTodoListInfo(config, formatter, factory);
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
        BasecampConfigValidator.validateTodoList(config);
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
