package com.taskadapter.connector.basecamp.editor;

import com.taskadapter.connector.basecamp.BasecampConfig;
import com.taskadapter.connector.basecamp.BasecampUtils;
import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.web.ExceptionFormatter;
import com.taskadapter.webui.NotificationUtil;
import com.vaadin.flow.component.notification.Notification;

import static com.taskadapter.web.ui.MessageUtils.nvl;

public final class ShowInfoElement {
    static void loadProject(BasecampConfig config, WebConnectorSetup setup, ExceptionFormatter exceptionFormatter, ObjectAPIFactory factory) {
        try {
            BasecampProject project = BasecampUtils.loadProject(factory, config, setup);
            showProjectInfo(project);

        } catch (BadConfigException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = exceptionFormatter.formatError(e);
            NotificationUtil.showError(localizedMessage);
        }
    }

    private static void showProjectInfo(BasecampProject project) {
        String msg = "<BR>Key:  " + project.getKey()
                + "<BR>Name: " + project.getName()
                + "<BR>Description: " + nvl(project.getDescription())
                + "<BR>Completed Todo lists: " + project.getCompletedTodolists()
                + "<BR>Remaining Todo lists: " + project.getRemainingTodolists();

        Notification.show(msg);
    }

    public static void showTodoListInfo(BasecampConfig config, WebConnectorSetup setup, ExceptionFormatter formatter, ObjectAPIFactory factory) {
        try {
            TodoList todoList = BasecampUtils.loadTodoList(factory, config, setup);
            showTodoListInfoPopup(todoList);

        } catch (BadConfigException e) {
            String localizedMessage = formatter.formatError(e);
            Notification.show(localizedMessage);
        } catch (ConnectorException e) {
            String localizedMessage = formatter.formatError(e);
            // TODO show error
            Notification.show(localizedMessage);
        }
    }

    private static void showTodoListInfoPopup(TodoList todoList) {
        String msg = "<BR>Key:  " + todoList.getKey()
                + "<BR>Name: " + todoList.getName()
                + "<BR>Description: " + nvl(todoList.getDescription())
                + "<BR>Completed Todos: " + todoList.getCompletedCount()
                + "<BR>Remaining Todos: " + todoList.getRemainingCount();

        Notification.show(msg);
    }
}
